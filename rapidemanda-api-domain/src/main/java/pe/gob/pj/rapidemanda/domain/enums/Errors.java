package pe.gob.pj.rapidemanda.domain.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum Errors {

	OPERACION_EXITOSA("0000","La operación se realizo de manera exitosa."),
	DATOS_NO_ENCONTRADOS("0001","Datos no encontrados al %s."),
	
	ERROR_INESPERADO("E000"," Error al %s - Error inesperado."),
	ERROR_SESSION_EXPIERADO("E001","La sesión ha expirado."),
	ERROR_TOKEN_NO_VALIDO("E002","No se pudo validar el token."),
	ERROR_REFRESCAR_TOKEN_TIEMPO_EXPIRADO("E003","El tiempo límite para refrescar el token enviado a expirado."),
	ERROR_EJECUCION_SP("E004"," Error al %s - Problemas en la ejecución del SP: %s"),
	ERROR_CONEXION_SEGURIDAD_NO_ESTABLECIDA("E005"," - No se estableció conexión con la BD PJ Seguridad."),
	ERROR_CREDENCIALES_SIJ_NO_EXISTE("E006"," Error al %s - No se puede obtener datos de configuración de la BD: %s"),
	ERROR_CREDENCIALES_SIJ_NO_ACEPTADAS("E007"," Error al %s - Problemas con las credenciales de conexión a la BD: %s"),
	ERROR_FTP_SUBIR_ARCHIVO_ERROR("E009"," Error al %s - No se pudo subir documento al ftp indicado."),
	ERROR_FTP_DESCARGAR_ARCHIVO("E010"," Error al %s - No se pudo descargar documento del ftp indicado."),
	ERROR_ALFRESCO_SUBIR_ARCHIVO("E011"," Error al %s - No se pudo subir documento al alfresco indicado."),
	ERROR_ALFRESCO_DESCARGAR_ARCHIVO("E012"," Error al %s - No se pudo descargar documento del alfresco indicado."),
	ERROR_CONSUMIR_SERVICIO_EXTERNO("E013"," Error al %s - Problemas con el consumo del endpoint: %"),
	ERROR_TOKEN_CAPTCHA("E014"," Error al %s - Se indica que se valide el captcha y el token captcha es nulo o no se pudo validar correctamente."),
	ERROR_AUDITORIA_GENERAL_REGISTRAR("E015", " Error al %s - No se pudo guardar trazabilidad en la auditoría general."),
	
	NEGOCIO_CREDENCIALES_INCORRECTAS("N001"," Error al %s - Las credenciales son incorrectas o el usuario esta inactivo."),
	NEGOCIO_PERFIL_NO_ENCONTRADO("N002"," Error al %s - No se encontro el perfil indicado y/o no tiene opciones asignadas."),
	NEGOCIO_PERSONA_YA_REGISTRADA("N003"," Error al %s - La persona ingresada ya esta registrada."),
	NEGOCIO_PERSONA_DNI_EXISTE("N003"," Error en %s - El dni ingresado ya esta registrado."),
	NEGOCIO_CORREO_YA_REGISTRADO("N004"," Error al %s - El correo ingresado ya esta registrado."),
	NEGOCIO_DEMANDA_YA_REGISTRADA("N003"," Error al %s - La demanda ingresada ya esta registrada."),
	NEGOCIO_DEMANDA_NO_EDITABLE("N004"," Error al %s - La demanda no se puede editar porque no esta en estado BORRADOR, estado actual: %s."),
	NEGOCIO_DEMANDA_NO_ELIMINABLE("N005"," Error al %s - La demanda no se puede eliminar, estado actual: %s."),
	NEGOCIO_DEMANDA_NO_AUTORIZADA("N005"," Error al %s - No se puede modificar la demanda porque no es el usuario creador de la misma."),
	NEGOCIO_USUARIO_YA_REGISTRADO("N006"," Error al %s - El usuario ingresado ya esta registrado."),
	NEGOCIO_ESTADO_INVALIDO("N010"," Error al %s - El estado es no es válido."),
	NEGOCIO_PARAMETRO_REQUERIDO("N011"," Error al %s - El parámetro %s es requerido."),
	DATOS_ENTRADA_INCORRECTOS("D001"," Error al %s - Los datos de entrada son incorrectos o incompletos.");
	
	String codigo;
	String nombre;
	
	Errors(String codigo, String nombre){
		this.codigo = codigo;
		this.nombre = nombre;
	}
	
}
