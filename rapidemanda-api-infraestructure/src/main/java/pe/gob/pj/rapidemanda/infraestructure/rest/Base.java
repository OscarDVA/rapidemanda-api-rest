package pe.gob.pj.rapidemanda.infraestructure.rest;

import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;
import pe.gob.pj.rapidemanda.infraestructure.utils.UtilInfraestructure;

public interface Base {

	public default void handleException(String cuo, ErrorException e, GlobalResponse res) {
		res.setCodigo(e.getCodigo());
		res.setDescripcion(e.getDescripcion());
		UtilInfraestructure.handleException(cuo, e, e.getCodigo(), e.getDescripcion());
	}
	
}
