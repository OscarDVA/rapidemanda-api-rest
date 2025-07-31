package pe.gob.pj.rapidemanda.infraestructure.enums;

import lombok.Getter;

@Getter
public enum Sexo {
	
	MASCULIO("M","Masculino"), FEMENINO("F","Femenino");
	
	String nombre;
	String descripcion;

	Sexo(String nombre, String descripcion) {
		this.nombre = nombre;
		this.descripcion = descripcion;
	}
}
