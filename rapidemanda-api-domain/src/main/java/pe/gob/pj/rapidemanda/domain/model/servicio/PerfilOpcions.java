package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PerfilOpcions implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String rol;
	private List<Opcion> opciones = new ArrayList<Opcion>();
	
}
