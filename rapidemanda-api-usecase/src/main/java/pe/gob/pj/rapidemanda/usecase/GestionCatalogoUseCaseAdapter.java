package pe.gob.pj.rapidemanda.usecase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.CatalogoPetitorio;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionCatalogoPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.GestionCatalogoUseCasePort;

import java.sql.SQLException;
import java.util.List;

@Service("gestionCatalogoUseCasePort")

public class GestionCatalogoUseCaseAdapter implements GestionCatalogoUseCasePort{
	 private final GestionCatalogoPersistencePort gestionCatalogoPersistencePort;

	    public GestionCatalogoUseCaseAdapter(
	            @Qualifier("gestionCatalogoPersistencePort") GestionCatalogoPersistencePort gestionCatalogoPersistencePort) {
	        this.gestionCatalogoPersistencePort = gestionCatalogoPersistencePort;
	    }

	    @Override
	    @Transactional(transactionManager = "txManagerNegocio", 
	                   propagation = Propagation.REQUIRES_NEW, 
	                   readOnly = true,
	                   rollbackFor = { Exception.class, SQLException.class })
	    public List<CatalogoPetitorio> buscarCatalogo(String cuo) throws Exception {
	        List<CatalogoPetitorio> catalogos = gestionCatalogoPersistencePort.buscarCatalogo(cuo);
	        
	        if (catalogos == null || catalogos.isEmpty()) {
	            throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
	                    String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.CATALOGO_CONSULTAR.getNombre()));
	        }
	        
	        return catalogos;
	    }
}
