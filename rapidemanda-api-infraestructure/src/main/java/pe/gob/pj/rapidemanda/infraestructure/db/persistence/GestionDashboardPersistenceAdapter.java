package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pe.gob.pj.rapidemanda.domain.model.servicio.*;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDashboardPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemanda;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandado;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandante;
import pe.gob.pj.rapidemanda.infraestructure.mapper.DemandadoEntityMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.DemandanteEntityMapper;

@Slf4j
@Component("gestionDashboardPersistencePort")
public class GestionDashboardPersistenceAdapter implements GestionDashboardPersistencePort {

    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sf;

    @Autowired
    private DemandanteEntityMapper demandanteEntityMapper;

    @Autowired
    private DemandadoEntityMapper demandadoEntityMapper;

    @Override
    public DashboardResumen obtenerResumen(String cuo) throws Exception {
        DashboardResumen res = new DashboardResumen();
        try {
            Long total = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md", Long.class)
                    .getSingleResult();

            Long registrados = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.estadoDemanda.bEstadoDemanda = :estado", Long.class)
                    .setParameter("estado", "P")
                    .getSingleResult();

            Long tipoM = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoPresentacion.bTipoPresentacion = :tipo", Long.class)
                    .setParameter("tipo", "M")
                    .getSingleResult();

            Long tipoF = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoPresentacion.bTipoPresentacion = :tipo", Long.class)
                    .setParameter("tipo", "F")
                    .getSingleResult();

            res.setTotalDemandas(total != null ? total : 0);
            res.setTotalRegistrados(registrados != null ? registrados : 0);
            res.setTotalPresentacionM(tipoM != null ? tipoM : 0);
            res.setTotalPresentacionF(tipoF != null ? tipoF : 0);
        } catch (Exception e) {
            log.error("{} Error obteniendo resumen dashboard: {}", cuo, e.getMessage());
            throw e;
        }
        return res;
    }

    @Override
    public List<DemandaResumen> listarDemandasRecientes(String cuo, int limite) throws Exception {
        List<DemandaResumen> lista = new ArrayList<>();
        try {
            TypedQuery<MovDemanda> q = sf.getCurrentSession().createQuery(
                    "SELECT md FROM MovDemanda md JOIN md.estadoDemanda ed JOIN md.tipoPresentacion tp WHERE md.estadoDemanda.bEstadoDemanda IN (:estados) ORDER BY md.fechaRegistro DESC",
                    MovDemanda.class);
            q.setParameter("estados", List.of("C", "P"));
            q.setMaxResults(limite);

            q.getResultStream().forEach(md -> {
                DemandaResumen d = new DemandaResumen();
                d.setId(md.getId());
                d.setSumilla(md.getSumilla());
                d.setTipoRecepcion(md.getTipoRecepcion());
                try {
                    d.setFechaRegistro(ProjectUtils.convertDateToString(md.getFechaRegistro(), ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM_SS));
                } catch (Exception ex) {
                    d.setFechaRegistro("");
                }
                try {
                    d.setFechaRecepcion(ProjectUtils.convertDateToString(md.getFechaRecepcion(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
                } catch (Exception ex) {
                    d.setFechaRecepcion("");
                }
                try {
                    d.setEstado(md.getEstadoDemanda() != null ? md.getEstadoDemanda().getBEstadoDemanda() : null);
                } catch (Exception ex) {
                    d.setEstado(null);
                }
                try {
                    d.setTipoPresentacion(md.getTipoPresentacion() != null ? md.getTipoPresentacion().getBTipoPresentacion() : null);
                } catch (Exception ex) {
                    d.setTipoPresentacion(null);
                }

                // demandantes: si existen varios listar el demandante con apoderadoComun = '1'
                try {
                    List<Demandante> demandantes = new ArrayList<>();
                    List<MovDemandante> mdDemandantes = md.getDemandantes();
                    if (mdDemandantes != null && !mdDemandantes.isEmpty()) {
                        demandantes = mdDemandantes.stream()
                                .filter(x -> {
                                    try {
                                        return x.getApoderadoComun() != null && "1".equals(x.getApoderadoComun());
                                    } catch (Exception e) {
                                        return false;
                                    }
                                })
                                .map(demandanteEntityMapper::toModel)
                                .toList();
                    }
                    d.setDemandantes(demandantes);
                } catch (Exception ex) {
                    d.setDemandantes(new ArrayList<>());
                }

                // demandados: si existen varios elegir el primero
                try {
                    List<Demandado> demandados = new ArrayList<>();
                    List<MovDemandado> mdDemandados = md.getDemandados();
                    if (mdDemandados != null && !mdDemandados.isEmpty()) {
                        Demandado primero = demandadoEntityMapper.toModel(mdDemandados.get(0));
                        if (primero != null) {
                            demandados.add(primero);
                        }
                    }
                    d.setDemandados(demandados);
                } catch (Exception ex) {
                    d.setDemandados(new ArrayList<>());
                }
                lista.add(d);
            });
        } catch (Exception e) {
            log.error("{} Error listando demandas recientes: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public PetitorioConteos contarPetitorios(String cuo) throws Exception {
        PetitorioConteos res = new PetitorioConteos();
        try {
            // Lista única de conteo de similitudes por combinación de cuatro campos
            List<Object[]> rows = sf.getCurrentSession()
                    .createQuery(
                            "SELECT p.tipo, p.pretensionPrincipal, p.concepto, p.pretensionAccesoria, COUNT(p) " +
                                    "FROM MovPetitorio p " +
                                    "GROUP BY p.tipo, p.pretensionPrincipal, p.concepto, p.pretensionAccesoria " +
                                    "ORDER BY COUNT(p) DESC", Object[].class)
                    .getResultList();

            List<ConteoPetitorioSimilitudItem> similitudes = new ArrayList<>();
            for (Object[] r : rows) {
                ConteoPetitorioSimilitudItem item = new ConteoPetitorioSimilitudItem();
                item.setTipo((String) r[0]);
                item.setPretensionPrincipal((String) r[1]);
                item.setConcepto((String) r[2]);
                item.setPretensionAccesoria((String) r[3]);
                item.setTotal(r[4] != null ? (Long) r[4] : 0L);
                similitudes.add(item);
            }
            res.setSimilitudes(similitudes);
        } catch (Exception e) {
            log.error("{} Error contando petitorios: {}", cuo, e.getMessage());
            throw e;
        }
        return res;
    }

    @Override
    public DemandanteConteos contarDemandantesSexoEdad(String cuo) throws Exception {
        DemandanteConteos res = new DemandanteConteos();
        try {
            // Por sexo
            List<Object[]> sexo = sf.getCurrentSession()
                    .createQuery("SELECT d.genero, COUNT(d) FROM MovDemandante d GROUP BY d.genero", Object[].class)
                    .getResultList();
            res.setPorSexo(mapConteos(sexo));

            // Por edad rango (cálculo en memoria)
            TypedQuery<Date> qFechas = sf.getCurrentSession()
                    .createQuery("SELECT d.fechaNacimiento FROM MovDemandante d", Date.class);
            List<Date> data = qFechas.getResultList();

            Map<String, Long> buckets = new HashMap<>();
            buckets.put("18-65", 0L);
            buckets.put("65+", 0L);

            Date now = new Date();
            for (Date fn : data) {
                if (fn == null) continue;
                int edad = calcularEdad(fn, now);
                String rango = obtenerRangoEdad(edad);
                buckets.put(rango, buckets.get(rango) + 1);
            }

            List<ConteoItem> rangos = new ArrayList<>();
            buckets.forEach((k, v) -> {
                ConteoItem c = new ConteoItem();
                c.setValor(k);
                c.setTotal(v);
                rangos.add(c);
            });
            res.setPorEdadRango(rangos);
        } catch (Exception e) {
            log.error("{} Error contando demandantes (sexo/edad): {}", cuo, e.getMessage());
            throw e;
        }
        return res;
    }

    @Override
    public List<ConteoParItem> contarDemandaPorTipoPresentacionYEstado(String cuo) throws Exception {
        List<ConteoParItem> lista = new ArrayList<>();
        try {
            // Conteos por estado (X) y tipo de presentación (series)
            List<Object[]> rows = sf.getCurrentSession()
                    .createQuery("SELECT md.estadoDemanda.bEstadoDemanda, md.tipoPresentacion.bTipoPresentacion, COUNT(md) FROM MovDemanda md GROUP BY md.estadoDemanda.bEstadoDemanda, md.tipoPresentacion.bTipoPresentacion", Object[].class)
                    .getResultList();
            rows.forEach(r -> {
                ConteoParItem item = new ConteoParItem();
                item.setClave1((String) r[0]); // estado: B, C, P
                item.setClave2((String) r[1]); // tipo: F, M
                item.setTotal((Long) r[2]);
                lista.add(item);
            });

            // Agregar categoría "Total" (X) por tipo de presentación (series)
            List<Object[]> totals = sf.getCurrentSession()
                    .createQuery("SELECT md.tipoPresentacion.bTipoPresentacion, COUNT(md) FROM MovDemanda md GROUP BY md.tipoPresentacion.bTipoPresentacion", Object[].class)
                    .getResultList();
            totals.forEach(r -> {
                ConteoParItem item = new ConteoParItem();
                item.setClave1("Total");
                item.setClave2((String) r[0]); // tipo: F, M
                item.setTotal((Long) r[1]);
                lista.add(item);
            });
        } catch (Exception e) {
            log.error("{} Error contando demanda por estado y tipoPresentacion: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public List<ConteoItem> contarDemandaPorTipoPresentacion(String cuo) throws Exception {
        List<ConteoItem> lista = new ArrayList<>();
        try {
            List<Object[]> rows = sf.getCurrentSession()
                    .createQuery("SELECT md.tipoPresentacion.bTipoPresentacion, COUNT(md) FROM MovDemanda md GROUP BY md.tipoPresentacion.bTipoPresentacion", Object[].class)
                    .getResultList();
            lista = mapConteos(rows);
        } catch (Exception e) {
            log.error("{} Error contando demanda por tipoPresentacion: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    private List<ConteoItem> mapConteos(List<Object[]> rows) {
        List<ConteoItem> lista = new ArrayList<>();
        for (Object[] r : rows) {
            ConteoItem c = new ConteoItem();
            c.setValor(r[0] != null ? String.valueOf(r[0]) : null);
            c.setTotal(r[1] != null ? (Long) r[1] : 0);
            lista.add(c);
        }
        return lista;
    }

    private int calcularEdad(Date nacimiento, Date ahora) {
        Calendar birth = Calendar.getInstance();
        birth.setTime(nacimiento);
        Calendar today = Calendar.getInstance();
        today.setTime(ahora);
        int years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            years--;
        }
        return Math.max(years, 0);
    }

    private String obtenerRangoEdad(int edad) {
        if (edad <= 65) return "18-65";
        return "65+";
    }
}