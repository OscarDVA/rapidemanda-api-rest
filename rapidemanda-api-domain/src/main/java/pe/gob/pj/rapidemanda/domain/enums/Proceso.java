package pe.gob.pj.rapidemanda.domain.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum Proceso {
	
	HEALTHCHECK("Probar Disponibilidad"),
	REFRESH("Refrescar Token"),
	INICIAR_SESION("Iniciar Sesión"),
	ELEGIR_PERFIL("Elegir Perfil"),
	OBTENER_OPCIONES("Obtener Opciones"),
	PERSONA_CONSULTAR("Consultar Persona"),
	PERSONA_REGISTRAR("Registrar Persona"),
	PERSONA_ACTUALIZAR("Actualizar Persona"),
	CATALOGO_CONSULTAR("Consultar Catalogos"),
	DEMANDA_CONSULTAR("Consultar Demanda"),
	DEMANDA_REGISTRAR("Registrar Demanda"),
	DEMANDA_ACTUALIZAR("Actualizar Demanda"),
	USUARIO_CONSULTAR("Consultar Usuario"),
	USUARIO_REGISTRAR("Registrar Usuario"),
	DEPARTAMENTO_CONSULTAR("Consultar Departamento"),
	PROVINCIA_CONSULTAR("Consultar Provincia"),
	DISTRITO_CONSULTAR("Consultar Distrito");
	String nombre;
	
	Proceso(String nombre){
		this.nombre = nombre;
	}
	
}
