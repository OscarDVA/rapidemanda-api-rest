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
	CATALOGO_PETITORIO_CONSULTAR("Consultar Catalogo de Petitorios"),
	CATALOGO_PRETENSION_PRINCIPAL_CONSULTAR("Consultar Catalogo de Petitorios"),
	CATALOGO_PRETENSION_ACCESORIA_CONSULTAR("Consultar Catalogo de Pretensiones Accesorias"),
	CATALOGO_CONCEPTO_CONSULTAR("Consultar Catalogo de Conceptos"),
	DEMANDA_CONSULTAR("Consultar Demanda"),
	DEMANDA_REGISTRAR("Registrar Demanda"),
	DEMANDA_ACTUALIZAR("Actualizar Demanda"),
	DEMANDA_ELIMINAR("Eliminar Demanda"),
	DEMANDA_REPORTE_PDF("Generar Reporte PDF de Demanda"),
	USUARIO_CONSULTAR("Consultar Usuario"),
	USUARIO_REGISTRAR("Registrar Usuario"),
	USUARIO_ACTUALIZAR("Actualizar Usuario"),
	PERFIL_CONSULTAR("Consultar Perfil"),
	DEPARTAMENTO_CONSULTAR("Consultar Departamento"),
	PROVINCIA_CONSULTAR("Consultar Provincia"),
	DISTRITO_CONSULTAR("Consultar Distrito"),
	ESTADO_ACTUALIZAR("Actualizar Estado");
	String nombre;
	
	Proceso(String nombre){
		this.nombre = nombre;
	}
	
}
