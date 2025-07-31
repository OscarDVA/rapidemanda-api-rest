package pe.gob.pj.rapidemanda.infraestructure.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorDefault {
	
	TRAZA("TRAZA_LOG"),
	CAUSA_NO_IDENTIFICADA("Causa: No se puede identificar."),
	CLASE_METODO_LINEA_NO_IDENTIFICADO("Clase-Metodo-Linea: No se puede identificar."),
	FORMATO_RESPUESTA_NO_IDENTIFICADO("Formato Respuesta: Error al convertir en formato xml.");
	
	String nombre;
	
	ErrorDefault(String nombre){
		this.nombre = nombre;
	}
	
}
