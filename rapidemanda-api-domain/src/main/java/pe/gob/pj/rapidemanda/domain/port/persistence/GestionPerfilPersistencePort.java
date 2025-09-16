package pe.gob.pj.rapidemanda.domain.port.persistence;

import java.util.List;

import pe.gob.pj.rapidemanda.domain.model.servicio.Perfil;

public interface GestionPerfilPersistencePort {

	/**
	 * Método que permite obtener una lista de perfiles activos del sistema
	 * 
	 * @param cuo Código único de operación
	 * @param filters Lista de clave valor, donde la clave son los parámetros de filtrado
	 * @return Lista de perfiles que coinciden con los filtros enviados
	 * @throws Exception
	 */
	public List<Perfil> buscarPerfiles(String cuo) throws Exception;

}