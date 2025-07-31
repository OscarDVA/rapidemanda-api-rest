package pe.gob.pj.rapidemanda.domain.model.servicio;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Data
public class Opcion implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String P_IDIOMA = "idiomaOpcion";
	public static final String P_ID = "idOpcion";
	public static final String P_ID_SUPERIOR = "idSuperiorOpcion";
	
	private Integer id;
	private String codigo;
	private String url;
	private String nombre;
	private String descripcion;
	private Integer orden;
	private String icono;
	private Integer idOpcionSuperior;
	private String nombreOpcionSuperior;
	
	private String activo;
	private String perfiles;

}
