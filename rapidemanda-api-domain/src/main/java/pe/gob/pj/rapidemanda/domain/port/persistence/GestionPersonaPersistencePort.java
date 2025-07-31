package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;
import java.util.Map;

import pe.gob.pj.rapidemanda.domain.model.servicio.Persona;

public interface GestionPersonaPersistencePort {

	/**
	 * 
	 * Método que permite obtener una lista de entidad MovPersona y encapsularlo en una lista del modelo
	 * Persona, las cuales coinciden con los filtros enviados.
	 * 
	 * @param cuo Código unico de operación
	 * @param filters Lista de clave valor, donde la clave son los parámetros declarados en el modelo Persona
	 * @return Lista del modelo Persona que coinciden con los filtros enviados
	 * @throws Exception
	 */
	public List<Persona> buscarPersona(String cuo, Map<String, Object> filters) throws Exception;

	/**
	 * 
	 * Método que permite registrar entidad MovPersona con los datos encapsulados en el modelo Persona
	 * 
	 * @param cuo Código unico de operación
	 * @param persona Modelo que contiene los atributos de una persona que se quieren guardar
	 * @throws Exception
	 */
	public void registrarPersona(String cuo, Persona persona) throws Exception;

	/**
	 * 
	 * Método que permite actualizar entidad MovPersona con los datos encapsulados en el modelo Persona
	 * 
	 * @param cuo Código unico de operación
	 * @param persona Modelo que contiene los atributos de una persona que se quieren guardar incluyendo el identificador
	 * @throws Exception
	 */
	public void actualizarPersona(String cuo, Persona persona) throws Exception;

}
