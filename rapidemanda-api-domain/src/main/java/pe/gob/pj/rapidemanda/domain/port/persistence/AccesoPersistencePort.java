package pe.gob.pj.rapidemanda.domain.port.persistence;

import pe.gob.pj.rapidemanda.domain.model.servicio.PerfilOpcions;
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;

public interface AccesoPersistencePort {
	/**
	 * 
	 * Método que permite obtener datos de la entidad MovUsuario y encapsularlos en el modelo
	 * Usuario si hay coincidencia con el usuario enviado.
	 * 
	 * @param cuo Código unico de operación
	 * @param usuario Usuario con el que se quiere encontrar coincidencia
	 * @return Modelo Usuario o null
	 * @throws Exception
	 */
	public Usuario iniciarSesion(String cuo, String usuario) throws Exception;
	
	/**
	 * 
	 * Método que permite registrar un nuevo usuario en el sistema.
	 * 
	 * @param cuo Código unico de operación
	 * @param usuario Modelo Usuario con los datos del usuario a registrar
	 * @return Usuario registrado con su ID generado
	 * @throws Exception
	 */
	public Usuario registrarUsuario(String cuo, Usuario usuario) throws Exception;
	
	/**
	 * 
	 * Método que permite obtener una lista de la entidad MaeOpcion asignadas a la entidad MaePerfil y 
	 * encapsularlo en el modelo PerfilOpcions
	 * 
	 * @param cuo Código unico de operación
	 * @param idPerfil Identificador de perfil del cual se quiere obtener las opciones
	 * @return Modelo PerfilOpcions o null
	 * @throws Exception
	 */
	public PerfilOpcions obtenerOpciones(String cuo, Integer idPerfil) throws Exception;
}
