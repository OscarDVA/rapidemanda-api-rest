package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPretensionPrincipal;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoPetitorioTipoPretensionItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoEdadSexoItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.ConteoItem;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandanteConteos;
import pe.gob.pj.rapidemanda.domain.model.servicio.DemandantePetitorioItem;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionReportesPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPetitorioUseCasePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionPretensionPrincipalUseCasePort;

@Slf4j
@Component("gestionReportesPersistencePort")
public class GestionReportesPersistenceAdapter implements GestionReportesPersistencePort {

    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sf;

    @Autowired
    @Qualifier("gestionPetitorioUseCasePort")
    private GestionPetitorioUseCasePort gestionPetitorioUseCasePort;

    @Autowired
    @Qualifier("gestionPretensionPrincipalUseCasePort")
    private GestionPretensionPrincipalUseCasePort gestionPretensionPrincipalUseCasePort;

    private final Map<String, String> cachePetitorios = new ConcurrentHashMap<>();
    private final Map<String, String> cachePretensionesPrincipales = new ConcurrentHashMap<>();

    @Override
    public List<ConteoPetitorioTipoItem> contarPetitoriosPorTipo(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception {
        List<ConteoPetitorioTipoItem> lista = new ArrayList<>();
        try {
            String jpql = "SELECT p.tipo, COUNT(p) FROM MovPetitorio p JOIN p.demanda md " +
                    "WHERE md.fechaCompletado BETWEEN :inicio AND :fin AND md.estadoDemanda.bEstadoDemanda IN (:estados) " +
                    "GROUP BY p.tipo ORDER BY COUNT(p) DESC";
            TypedQuery<Object[]> query = sf.getCurrentSession().createQuery(jpql, Object[].class);
            query.setParameter("inicio", fechaInicio);
            query.setParameter("fin", fechaFin);
            query.setParameter("estados", estados);

            for (Object[] r : query.getResultList()) {
                ConteoPetitorioTipoItem item = new ConteoPetitorioTipoItem();
                item.setTipo(obtenerNombrePetitorio(cuo, (String) r[0]));
                item.setTotal(r[1] != null ? (Long) r[1] : 0L);
                lista.add(item);
            }
        } catch (Exception e) {
            log.error("{} Error contando petitorios por tipo: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public DemandanteConteos contarDemandantesSexoEdadPorFechasEstados(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception {
        DemandanteConteos res = new DemandanteConteos();
        try {
            // Por sexo con filtros de demanda
            String qSexo = "SELECT d.genero, COUNT(d) FROM MovDemanda md JOIN md.demandantes d " +
                    "WHERE md.fechaCompletado BETWEEN :inicio AND :fin AND md.estadoDemanda.bEstadoDemanda IN (:estados) " +
                    "GROUP BY d.genero";
            TypedQuery<Object[]> querySexo = sf.getCurrentSession().createQuery(qSexo, Object[].class);
            querySexo.setParameter("inicio", fechaInicio);
            querySexo.setParameter("fin", fechaFin);
            querySexo.setParameter("estados", estados);
            List<Object[]> sexo = querySexo.getResultList();
            res.setPorSexo(mapConteos(sexo));

            // Por edad rango con desglose por sexo (cálculo en memoria) con filtros
            String qFechas = "SELECT d.genero, d.fechaNacimiento FROM MovDemanda md JOIN md.demandantes d " +
                    "WHERE md.fechaCompletado BETWEEN :inicio AND :fin AND md.estadoDemanda.bEstadoDemanda IN (:estados)";
            TypedQuery<Object[]> queryFechas = sf.getCurrentSession().createQuery(qFechas, Object[].class);
            queryFechas.setParameter("inicio", fechaInicio);
            queryFechas.setParameter("fin", fechaFin);
            queryFechas.setParameter("estados", estados);
            List<Object[]> generoFechas = queryFechas.getResultList();

            java.util.Map<String, Long> totalPorRango = new java.util.HashMap<>();
            java.util.Map<String, Long> varonPorRango = new java.util.HashMap<>();
            java.util.Map<String, Long> mujerPorRango = new java.util.HashMap<>();
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
            log.error("{} Error contando demandantes (sexo/edad) con filtros: {}", cuo, e.getMessage());
            throw e;
        }
        return res;
    }

    private List<ConteoItem> mapConteos(List<Object[]> rows) {
        List<ConteoItem> lista = new ArrayList<>();
        for (Object[] r : rows) {
            ConteoItem item = new ConteoItem();
            item.setValor((String) r[0]);
            item.setTotal(r[1] != null ? (Long) r[1] : 0L);
            lista.add(item);
        }
        return lista;
    }

    private int calcularEdad(Date fechaNacimiento, Date referencia) {
        java.util.Calendar birth = java.util.Calendar.getInstance();
        birth.setTime(fechaNacimiento);
        java.util.Calendar ref = java.util.Calendar.getInstance();
        ref.setTime(referencia);
        int edad = ref.get(java.util.Calendar.YEAR) - birth.get(java.util.Calendar.YEAR);
        if (ref.get(java.util.Calendar.DAY_OF_YEAR) < birth.get(java.util.Calendar.DAY_OF_YEAR)) {
            edad--;
        }
        return Math.max(0, edad);
    }

    private String obtenerRangoEdad(int edad) {
        return edad >= 65 ? "65+" : "18-65";
    }

    @Override
    public List<ConteoPetitorioTipoPretensionItem> contarPetitoriosPorTipoYPrincipal(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception {
        List<ConteoPetitorioTipoPretensionItem> lista = new ArrayList<>();
        try {
            String jpql = "SELECT p.tipo, p.pretensionPrincipal, COUNT(p) FROM MovPetitorio p JOIN p.demanda md " +
                    "WHERE md.fechaCompletado BETWEEN :inicio AND :fin AND md.estadoDemanda.bEstadoDemanda IN (:estados) " +
                    "GROUP BY p.tipo, p.pretensionPrincipal ORDER BY COUNT(p) DESC";
            TypedQuery<Object[]> query = sf.getCurrentSession().createQuery(jpql, Object[].class);
            query.setParameter("inicio", fechaInicio);
            query.setParameter("fin", fechaFin);
            query.setParameter("estados", estados);

            for (Object[] r : query.getResultList()) {
                ConteoPetitorioTipoPretensionItem item = new ConteoPetitorioTipoPretensionItem();
                item.setTipo(obtenerNombrePetitorio(cuo, (String) r[0]));
                item.setPretensionPrincipal(obtenerNombrePretensionPrincipal(cuo, (String) r[1]));
                item.setTotal(r[2] != null ? (Long) r[2] : 0L);
                lista.add(item);
            }
        } catch (Exception e) {
            log.error("{} Error contando petitorios por tipo y pretensión principal: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    @Override
    public List<DemandantePetitorioItem> listarDemandantesPetitorioPorFechasEstados(String cuo, Date fechaInicio, Date fechaFin, List<String> estados) throws Exception {
        List<DemandantePetitorioItem> lista = new ArrayList<>();
        try {
            String jpql = "SELECT d.razonSocial, p.tipo, p.pretensionPrincipal " +
                    "FROM MovPetitorio p JOIN p.demanda md JOIN md.demandantes d " +
                    "WHERE md.fechaCompletado BETWEEN :inicio AND :fin " +
                    "AND md.estadoDemanda.bEstadoDemanda IN (:estados) " +
                    "ORDER BY d.razonSocial ASC";
            TypedQuery<Object[]> query = sf.getCurrentSession().createQuery(jpql, Object[].class);
            query.setParameter("inicio", fechaInicio);
            query.setParameter("fin", fechaFin);
            query.setParameter("estados", estados);

            for (Object[] r : query.getResultList()) {
                DemandantePetitorioItem item = new DemandantePetitorioItem();
                item.setDemandante((String) r[0]);
                item.setTipo(obtenerNombrePetitorio(cuo, (String) r[1]));
                item.setPretensionPrincipal(obtenerNombrePretensionPrincipal(cuo, (String) r[2]));
                lista.add(item);
            }
        } catch (Exception e) {
            log.error("{} Error listando demandantes vs petitorio y pretensión: {}", cuo, e.getMessage());
            throw e;
        }
        return lista;
    }

    private String obtenerNombrePetitorio(String cuo, String tipoId) {
        if (tipoId == null || tipoId.isBlank()) return "";
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
        if (pretensionPrincipalId == null || pretensionPrincipalId.isBlank()) return "";
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
}