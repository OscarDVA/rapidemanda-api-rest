package pe.gob.pj.rapidemanda.infraestructure.enums;

import lombok.Getter;

@Getter
public enum Estado {

	ACTIVO_NUMERICO("1", "Flag númerico activo."), INACTIVO_NUMERICO("0", "Flag númerico inactivo"),
	ACTIVO_LETRA("S", "Flag letra activo."), INACTIVO_LETRA("N", "Flag letra inactivo");

	String nombre;
	String descripcion;

	Estado(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
	}

}
