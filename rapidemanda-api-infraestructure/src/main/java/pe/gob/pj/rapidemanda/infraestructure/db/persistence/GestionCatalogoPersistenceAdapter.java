package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionCatalogoPersistencePort;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeCatalogoAccesorio;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeCatalogoConcepto;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeCatalogoPetitorio;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeCatalogoPretension;

import java.util.ArrayList;
import java.util.List;

@Component("gestionCatalogoPersistencePort")
public class GestionCatalogoPersistenceAdapter implements GestionCatalogoPersistencePort {
    @Autowired
    @Qualifier("sessionNegocio")
    private SessionFactory sf;

    @Override
  public List<CatalogoPetitorio> buscarCatalogo(String cuo) throws Exception {
    List<CatalogoPetitorio> resultado = new ArrayList<>();
    
    // Consulta optimizada para petitorios con pretensiones
    TypedQuery<MaeCatalogoPetitorio> queryPetitorios = sf.getCurrentSession()
            .createQuery("SELECT DISTINCT p FROM MaeCatalogoPetitorio p " +
                         "LEFT JOIN FETCH p.pretensiones pr " +
                         "WHERE p.activo = '1'", MaeCatalogoPetitorio.class);
    
    List<MaeCatalogoPetitorio> petitorios = queryPetitorios.getResultList();
    
    for (MaeCatalogoPetitorio petitorio : petitorios) {
        CatalogoPetitorio pj = new CatalogoPetitorio();
        pj.setId(petitorio.getId());
        pj.setNombre(petitorio.getNombre());
        pj.setActivo(petitorio.getActivo());
        pj.setPretensionPrincipal(new ArrayList<>());
        
        // Procesar pretensiones de cada petitorio
        for (MaeCatalogoPretension pretension : petitorio.getPretensiones()) {
            if (!"1".equals(pretension.getActivo())) continue;
            
            CatalogoPetitorio.PretensionPrincipal prj = new CatalogoPetitorio.PretensionPrincipal();
            prj.setId(pretension.getId());
            prj.setNombre(pretension.getNombre());
            prj.setActivo(pretension.getActivo());
            prj.setTieneConceptos(pretension.getTieneConceptos());
            prj.setConceptos(new ArrayList<>());
            prj.setPretensionAccesoria(new ArrayList<>());
            
            // Cargar conceptos si aplica
            if ("1".equals(pretension.getTieneConceptos())) {
                TypedQuery<MaeCatalogoConcepto> queryConceptos = sf.getCurrentSession()
                        .createQuery("FROM MaeCatalogoConcepto c WHERE c.pretension.id = :pretensionId AND c.activo = '1'", MaeCatalogoConcepto.class)
                        .setParameter("pretensionId", pretension.getId());
                
                for (MaeCatalogoConcepto concepto : queryConceptos.getResultList()) {
                    CatalogoPetitorio.Concepto cj = new CatalogoPetitorio.Concepto();
                    cj.setId(concepto.getId());
                    cj.setNombre(concepto.getNombre());
                    cj.setActivo(concepto.getActivo());
                    cj.setPretensionAccesoria(new ArrayList<>());
                    
                    // Cargar accesorios de cada concepto
                    TypedQuery<MaeCatalogoAccesorio> queryAccesoriosConcepto = sf.getCurrentSession()
                            .createQuery("FROM MaeCatalogoAccesorio a WHERE a.concepto.id = :conceptoId AND a.activo = '1'", MaeCatalogoAccesorio.class)
                            .setParameter("conceptoId", concepto.getId());
                    
                    for (MaeCatalogoAccesorio accesorio : queryAccesoriosConcepto.getResultList()) {
                        CatalogoPetitorio.PretensionAccesoria aj = new CatalogoPetitorio.PretensionAccesoria();
                        aj.setId(accesorio.getId());
                        aj.setNombre(accesorio.getNombre());
                        aj.setActivo(accesorio.getActivo());

                        aj.setIdRelacion(concepto.getId());
                        cj.getPretensionAccesoria().add(aj);
                    }
                    
                    prj.getConceptos().add(cj);
                }
            }
            
            // Cargar accesorios directos de la pretensión
            TypedQuery<MaeCatalogoAccesorio> queryAccesoriosPretension = sf.getCurrentSession()
                    .createQuery("FROM MaeCatalogoAccesorio a WHERE " +
                               "a.pretension.id = :pretensionId AND " +
                               "(a.concepto IS NULL) AND " +
                               "a.activo = '1'", MaeCatalogoAccesorio.class)
                    .setParameter("pretensionId", pretension.getId());
            
            for (MaeCatalogoAccesorio accesorio : queryAccesoriosPretension.getResultList()) {
                CatalogoPetitorio.PretensionAccesoria aj = new CatalogoPetitorio.PretensionAccesoria();
                aj.setId(accesorio.getId());
                aj.setNombre(accesorio.getNombre());
                aj.setActivo(accesorio.getActivo());

                aj.setIdRelacion(pretension.getId());
                prj.getPretensionAccesoria().add(aj);
            }
            
            pj.getPretensionPrincipal().add(prj);
        }
        
        resultado.add(pj);
    }
    
    return resultado;
}

}
