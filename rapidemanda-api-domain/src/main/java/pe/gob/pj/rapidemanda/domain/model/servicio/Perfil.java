package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.Data;

@Data
public class Perfil implements Serializable {

	private static final long serialVersionUID = 1L;

	Integer id;
	String nombre;
	String rol;
	String activo;

}
