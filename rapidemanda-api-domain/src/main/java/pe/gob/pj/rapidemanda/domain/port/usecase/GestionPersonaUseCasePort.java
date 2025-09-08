package pe.gob.pj.rapidemanda.domain.port.usecase;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.Persona;

public interface GestionPersonaUseCasePort {

	/**
	 * 
	 * Método que permite enviar filtros para obtener una lista del modelo persona
	 * 
	 * @param cuo Código unico de operación
	 * @param filters Lista de clave valor, donde la clave son los parámetros declarados en el modelo Persona
	 * @return Lista del modelo Persona que coinciden con los filtros enviados
	 * @throws Exception
	 */
	public List<Persona> buscarPersona(String cuo, Map<String, Object> filters) throws Exception;

	/**
	 * 
	 * Método que permite enviar los datos encapsulados en el modelo Persona para ser guardados
	 * aplicando reglas propias del negocio
	 * 
	 * @param cuo Código unico de operación
	 * @param persona Modelo que contiene los atributos de una persona que se quieren guardar
	 * @throws Exception
	 */
	public void registrarPersona(String cuo, Persona persona) throws Exception;

	/**
	 * 
	 * Método que permite enviar los datos encapsulados en el modelo Persona para ser actualizados
	 * aplicando reglas propias del negocio
	 * 
	 * @param cuo Código unico de operación
	 * @param persona Modelo que contiene los atributos de una persona que se quieren guardar incluyendo el identificador
	 * @throws Exception
	 */
	public void actualizarPersona(String cuo, Persona persona) throws Exception;

	/**
	 * 
	 * Método que permite cambiar el estado activo de una persona específica
	 * aplicando reglas propias del negocio
	 * 
	 * @param cuo Código unico de operación
	 * @param idPersona Identificador de la persona
	 * @param nuevoEstado Nuevo estado activo ("0" o "1")
	 * @throws Exception
	 */
	public void cambiarEstadoPersona(String cuo, Integer idPersona, String nuevoEstado) throws Exception;

}
