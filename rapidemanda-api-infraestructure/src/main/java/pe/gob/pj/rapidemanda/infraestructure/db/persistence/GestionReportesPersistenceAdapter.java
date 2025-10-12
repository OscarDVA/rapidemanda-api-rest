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