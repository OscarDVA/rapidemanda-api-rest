package pe.gob.pj.rapidemanda.infraestructure.enums;

import lombok.Getter;

@Getter
public enum FormatoRespuesta {
	
	XML("XML","Formato de respuesta xml"), JSON("JSON","Formato de respuesta json");
	
	String nombre;
	String descripcion;

	FormatoRespuesta(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
	}
}
