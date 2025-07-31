package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PerfilUsuario implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer idPerfil;
	private String nombre;
	private String rol;
	
	

}
