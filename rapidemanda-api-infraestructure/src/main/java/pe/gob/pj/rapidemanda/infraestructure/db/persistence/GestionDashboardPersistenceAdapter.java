package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
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
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionConceptoUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPetitorioUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPretensionAccesoriaUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPretensionPrincipalUseCasePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemanda;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandado;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandante;
import pe.gob.pj.rapidemanda.infraestructure.mapper.DemandadoEntityMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.DemandanteEntityMapper;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoConcepto;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionAccesoria;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionPrincipal;

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

    @Autowired
    @Qualifier("gestionPetitorioUseCasePort")
    private GestionPetitorioUseCasePort gestionPetitorioUseCasePort;

    @Autowired
    @Qualifier("gestionPretensionPrincipalUseCasePort")
    private GestionPretensionPrincipalUseCasePort gestionPretensionPrincipalUseCasePort;

    @Autowired
    @Qualifier("gestionConceptoUseCasePort")
    private GestionConceptoUseCasePort gestionConceptoUseCasePort;

    @Autowired
    @Qualifier("gestionPretensionAccesoriaUseCasePort")
    private GestionPretensionAccesoriaUseCasePort gestionPretensionAccesoriaUseCasePort;

    private final Map<String, String> cachePetitorios = new ConcurrentHashMap<>();
    private final Map<String, String> cachePretensionesPrincipales = new ConcurrentHashMap<>();
    private final Map<String, String> cacheConceptos = new ConcurrentHashMap<>();
    private final Map<String, String> cachePretensionesAccesorias = new ConcurrentHashMap<>();

    @Override
    public DashboardResumen obtenerResumen(String cuo) throws Exception {
        DashboardResumen res = new DashboardResumen();
        try {
            // Calcular rangos de fechas: mes actual y mes anterior
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date inicioMesActual = cal.getTime();

            Calendar calFinActual = (Calendar) cal.clone();
            calFinActual.add(Calendar.MONTH, 1);
            calFinActual.add(Calendar.MILLISECOND, -1);
            Date finMesActual = calFinActual.getTime();

            Calendar calInicioAnterior = (Calendar) cal.clone();
            calInicioAnterior.add(Calendar.MONTH, -1);
            Date inicioMesAnterior = calInicioAnterior.getTime();

            Calendar calFinAnterior = (Calendar) cal.clone();
            calFinAnterior.add(Calendar.MILLISECOND, -1);
            Date finMesAnterior = calFinAnterior.getTime();

            // Totales históricos (sin filtros de fecha)
            Long totalHistorico = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md", Long.class)
                    .getSingleResult();

            Long registradosHistorico = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.estadoDemanda.bEstadoDemanda = :estado", Long.class)
                    .setParameter("estado", "P")
                    .getSingleResult();

            Long tipoMHistorico = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoRecepcion = :tipo", Long.class)
                    .setParameter("tipo", "VIRTUAL")
                    .getSingleResult();

            Long tipoFHistorico = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoRecepcion = :tipo", Long.class)
                    .setParameter("tipo", "FISICA")
                    .getSingleResult();

            // Conteos mensuales para delta
            Long totalActual = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.fechaCompletado BETWEEN :ini AND :fin", Long.class)
                    .setParameter("ini", inicioMesActual)
                    .setParameter("fin", finMesActual)
                    .getSingleResult();

            Long totalAnterior = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.fechaCompletado BETWEEN :ini AND :fin", Long.class)
                    .setParameter("ini", inicioMesAnterior)
                    .setParameter("fin", finMesAnterior)
                    .getSingleResult();

            Long registradosActual = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.estadoDemanda.bEstadoDemanda = :estado AND md.fechaRecepcion BETWEEN :ini AND :fin", Long.class)
                    .setParameter("estado", "P")
                    .setParameter("ini", inicioMesActual)
                    .setParameter("fin", finMesActual)
                    .getSingleResult();

            Long registradosAnterior = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.estadoDemanda.bEstadoDemanda = :estado AND md.fechaRecepcion BETWEEN :ini AND :fin", Long.class)
                    .setParameter("estado", "P")
                    .setParameter("ini", inicioMesAnterior)
                    .setParameter("fin", finMesAnterior)
                    .getSingleResult();

            Long tipoMActual = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoRecepcion = :tipo AND md.fechaRecepcion BETWEEN :ini AND :fin", Long.class)
                    .setParameter("tipo", "VIRTUAL")
                    .setParameter("ini", inicioMesActual)
                    .setParameter("fin", finMesActual)
                    .getSingleResult();

            Long tipoMAnterior = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoRecepcion = :tipo AND md.fechaRecepcion BETWEEN :ini AND :fin", Long.class)
                    .setParameter("tipo", "VIRTUAL")
                    .setParameter("ini", inicioMesAnterior)
                    .setParameter("fin", finMesAnterior)
                    .getSingleResult();

            Long tipoFActual = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoRecepcion = :tipo AND md.fechaRecepcion BETWEEN :ini AND :fin", Long.class)
                    .setParameter("tipo", "FISICA")
                    .setParameter("ini", inicioMesActual)
                    .setParameter("fin", finMesActual)
                    .getSingleResult();

            Long tipoFAnterior = sf.getCurrentSession()
                    .createQuery("SELECT COUNT(md) FROM MovDemanda md WHERE md.tipoRecepcion = :tipo AND md.fechaRecepcion BETWEEN :ini AND :fin", Long.class)
                    .setParameter("tipo", "FISICA")
                    .setParameter("ini", inicioMesAnterior)
                    .setParameter("fin", finMesAnterior)
                    .getSingleResult();

            long valTotalHistorico = totalHistorico != null ? totalHistorico : 0;
            long valRegistradosHistorico = registradosHistorico != null ? registradosHistorico : 0;
            long valMHistorico = tipoMHistorico != null ? tipoMHistorico : 0;
            long valFHistorico = tipoFHistorico != null ? tipoFHistorico : 0;

            long valTotalActual = totalActual != null ? totalActual : 0;
            long valTotalAnterior = totalAnterior != null ? totalAnterior : 0;
            long valRegistradosActual = registradosActual != null ? registradosActual : 0;
            long valRegistradosAnterior = registradosAnterior != null ? registradosAnterior : 0;
            long valMActual = tipoMActual != null ? tipoMActual : 0;
            long valMAnterior = tipoMAnterior != null ? tipoMAnterior : 0;
            long valFActual = tipoFActual != null ? tipoFActual : 0;
            long valFAnterior = tipoFAnterior != null ? tipoFAnterior : 0;

            // Asignar totales históricos y deltas mensuales
            res.setTotalDemandas(valTotalHistorico);
            res.setTotalDemandasDeltaMensual(valTotalActual - valTotalAnterior);

            res.setTotalRegistrados(valRegistradosHistorico);
            res.setTotalRegistradosDeltaMensual(valRegistradosActual - valRegistradosAnterior);

            res.setTotalPresentacionM(valMHistorico);
            res.setTotalPresentacionMDeltaMensual(valMActual - valMAnterior);

            res.setTotalPresentacionF(valFHistorico);
            res.setTotalPresentacionFDeltaMensual(valFActual - valFAnterior);
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
                    "SELECT md FROM MovDemanda md JOIN md.estadoDemanda ed JOIN md.tipoPresentacion tp WHERE md.estadoDemanda.bEstadoDemanda IN (:estados) ORDER BY md.fechaCompletado DESC",
                    MovDemanda.class);
            q.setParameter("estados", List.of("C", "P"));
            q.setMaxResults(limite);

            q.getResultStream().forEach(md -> {
                DemandaResumen d = new DemandaResumen();
                d.setId(md.getId());
                d.setSumilla(md.getSumilla());
                d.setTipoRecepcion(md.getTipoRecepcion());
                try {
                    d.setFechaCompletado(ProjectUtils.convertDateToString(md.getFechaCompletado(), ProjectConstants.Formato.FECHA_DD_MM_YYYY_HH_MM_SS));
                } catch (Exception ex) {
                    d.setFechaCompletado("");
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
    public List<ConteoPetitorioSimilitudItem> contarPetitorios(String cuo) throws Exception {
        List<ConteoPetitorioSimilitudItem> lista = new ArrayList<>();
        try {
            // Lista única de conteo de similitudes por combinación de cuatro campos
            List<Object[]> rows = sf.getCurrentSession()
                    .createQuery(
                            "SELECT p.tipo, p.pretensionPrincipal, p.concepto, p.pretensionAccesoria, COUNT(p) " +
                                    "FROM MovPetitorio p " +
                                    "GROUP BY p.tipo, p.pretensionPrincipal, p.concepto, p.pretensionAccesoria " +
                                    "ORDER BY COUNT(p) DESC", Object[].class)
                    .getResultList();
            for (Object[] r : rows) {
                ConteoPetitorioSimilitudItem item = new ConteoPetitorioSimilitudItem();
                item.setTipo(obtenerNombrePetitorio(cuo, (String) r[0]));
                item.setPretensionPrincipal(obtenerNombrePretensionPrincipal(cuo, (String) r[1]));
                item.setConcepto(obtenerNombreConcepto(cuo, (String) r[2]));
                item.setPretensionAccesoria(obtenerNombrePretensionAccesoria(cuo, (String) r[3]));
                item.setTotal(r[4] != null ? (Long) r[4] : 0L);
                lista.add(item);
            }
        } catch (Exception e) {
            log.error("{} Error contando petitorios: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    private String obtenerNombrePetitorio(String cuo, String tipoId) {
        if (isBlank(tipoId)) return "";
        String nombre = cachePetitorios.get(tipoId);
        if (nombre != null) return nombre;
        try {
            List<CatalogoPetitorio> petitorios = gestionPetitorioUseCasePort.buscarPetitorio(cuo);
            for (CatalogoPetitorio p : petitorios) {
                if (p.getId() != null && p.getNombre() != null) {
                    cachePetitorios.putIfAbsent(String.valueOf(p.getId()), p.getNombre());
                }
            }
            nombre = cachePetitorios.get(tipoId);
            if (nombre != null) return nombre;
        } catch (Exception e) {
            log.warn("No se pudo resolver nombre de Petitorio {}: {}", tipoId, e.getMessage());
        }
        return tipoId;
    }

    private String obtenerNombrePretensionPrincipal(String cuo, String pretensionPrincipalId) {
        if (isBlank(pretensionPrincipalId)) return "";
        String nombre = cachePretensionesPrincipales.get(pretensionPrincipalId);
        if (nombre != null) return nombre;
        try {
            List<CatalogoPretensionPrincipal> principales = gestionPretensionPrincipalUseCasePort
                    .buscarPretensionPrincipal(cuo, java.util.Collections.emptyMap());
            for (CatalogoPretensionPrincipal pp : principales) {
                if (pp.getId() != null && pp.getNombre() != null) {
                    cachePretensionesPrincipales.putIfAbsent(String.valueOf(pp.getId()), pp.getNombre());
                }
            }
            nombre = cachePretensionesPrincipales.get(pretensionPrincipalId);
            if (nombre != null) return nombre;
        } catch (Exception e) {
            log.warn("No se pudo resolver nombre de Pretensión Principal {}: {}", pretensionPrincipalId, e.getMessage());
        }
        return pretensionPrincipalId;
    }

    private String obtenerNombreConcepto(String cuo, String conceptoId) {
        if (isBlank(conceptoId)) return "";
        String nombre = cacheConceptos.get(conceptoId);
        if (nombre != null) return nombre;
        try {
            List<CatalogoConcepto> conceptos = gestionConceptoUseCasePort
                    .buscarConcepto(cuo, java.util.Collections.emptyMap());
            for (CatalogoConcepto c : conceptos) {
                if (c.getId() != null && c.getNombre() != null) {
                    cacheConceptos.putIfAbsent(String.valueOf(c.getId()), c.getNombre());
                }
            }
            nombre = cacheConceptos.get(conceptoId);
            if (nombre != null) return nombre;
        } catch (Exception e) {
            log.warn("No se pudo resolver nombre de Concepto {}: {}", conceptoId, e.getMessage());
        }
        return conceptoId;
    }

    private String obtenerNombrePretensionAccesoria(String cuo, String pretensionAccesoriaId) {
        if (isBlank(pretensionAccesoriaId)) return "";
        String nombre = cachePretensionesAccesorias.get(pretensionAccesoriaId);
        if (nombre != null) return nombre;
        try {
            List<CatalogoPretensionAccesoria> accesorias = gestionPretensionAccesoriaUseCasePort
                    .buscarPretensionAccesoria(cuo, java.util.Collections.emptyMap());
            for (CatalogoPretensionAccesoria pa : accesorias) {
                if (pa.getId() != null && pa.getNombre() != null) {
                    cachePretensionesAccesorias.putIfAbsent(String.valueOf(pa.getId()), pa.getNombre());
                }
            }
            nombre = cachePretensionesAccesorias.get(pretensionAccesoriaId);
            if (nombre != null) return nombre;
        } catch (Exception e) {
            log.warn("No se pudo resolver nombre de Pretensión Accesoria {}: {}", pretensionAccesoriaId, e.getMessage());
        }
        return pretensionAccesoriaId;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
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

            // Por edad rango con desglose por sexo (cálculo en memoria)
            List<Object[]> generoFechas = sf.getCurrentSession()
                    .createQuery("SELECT d.genero, d.fechaNacimiento FROM MovDemandante d", Object[].class)
                    .getResultList();

            Map<String, Long> totalPorRango = new HashMap<>();
            Map<String, Long> varonPorRango = new HashMap<>();
            Map<String, Long> mujerPorRango = new HashMap<>();
            totalPorRango.put("18-65", 0L);
            totalPorRango.put("65+", 0L);
            varonPorRango.put("18-65", 0L);
            varonPorRango.put("65+", 0L);
            mujerPorRango.put("18-65", 0L);
            mujerPorRango.put("65+", 0L);

            Date now = new Date();
            for (Object[] row : generoFechas) {
                String genero = (String) row[0];
                Date fn = (Date) row[1];
                if (fn == null) continue;
                int edad = calcularEdad(fn, now);
                String rango = obtenerRangoEdad(edad);
                totalPorRango.put(rango, totalPorRango.get(rango) + 1);
                if ("M".equalsIgnoreCase(genero)) {
                    varonPorRango.put(rango, varonPorRango.get(rango) + 1);
                } else if ("F".equalsIgnoreCase(genero)) {
                    mujerPorRango.put(rango, mujerPorRango.get(rango) + 1);
                }
            }

            List<ConteoEdadSexoItem> rangos = new ArrayList<>();
            for (String rango : totalPorRango.keySet()) {
                ConteoEdadSexoItem item = new ConteoEdadSexoItem();
                item.setEdad(rango);
                Long v = varonPorRango.get(rango);
                Long m = mujerPorRango.get(rango);
                Long t = totalPorRango.get(rango);
                item.setVaron(v != null ? v : 0L);
                item.setMujer(m != null ? m : 0L);
                item.setTotal(t != null ? t : 0L);
                rangos.add(item);
            }
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
                    .createQuery("SELECT md.tipoRecepcion, COUNT(md) FROM MovDemanda md WHERE md.estadoDemanda.bEstadoDemanda = :estado GROUP BY md.tipoRecepcion", Object[].class)
                    .setParameter("estado", "P")
                    .getResultList();
            lista = mapConteos(rows);
        } catch (Exception e) {
            log.error("{} Error contando demanda por tipoRecepcion: {}", cuo, e.getMessage());
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