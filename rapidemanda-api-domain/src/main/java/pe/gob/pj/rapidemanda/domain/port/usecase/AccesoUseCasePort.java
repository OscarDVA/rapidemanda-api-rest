package pe.gob.pj.rapidemanda.domain.port.usecase;

import pe.gob.pj.rapidemanda.domain.model.servicio.PerfilOpcions;
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;

public interface AccesoUseCasePort {
	
	/**
	 * 
	 * Método que permite enviar las credenciales para ser validadas y 
	 * devolver un objeto con los datos del usuario o lanza una ErrorException
	 * indicando que las credenciales no son válidas o el usuario esta inactivo.
	 * 
	 * @param cuo Código unico de operación
	 * @param usuario Usuario con el que se inicia sesión
	 * @param clave Clave con la que se inicia sesión
	 * @return Usuario
	 * @throws Exception
	 */
	public Usuario iniciarSesion(String cuo, String usuario, String clave) throws Exception;
	
	/**
	 * 
	 * Método que permite registrar un nuevo usuario en el sistema.
	 * Valida que no exista un usuario con el mismo nombre de usuario o correo electrónico.
	 * 
	 * @param cuo Código unico de operación
	 * @param usuario Objeto Usuario con los datos del nuevo usuario a registrar
	 * @return Usuario registrado con su ID generado
	 * @throws Exception Si el usuario ya existe o hay errores de validación
	 */
	public Usuario registrarUsuario(String cuo, Usuario usuario) throws Exception;

	/**
	 *
	 * Método que permite cambiar la clave del usuario autenticado
	 * validando la clave actual y aplicando reglas de complejidad.
	 *
	 * @param cuo Código unico de operación
	 * @param usuario Nombre de usuario autenticado
	 * @param claveActual Clave actual del usuario
	 * @param nuevaClave Nueva clave a establecer
	 * @throws Exception
	 */
	public void cambiarClave(String cuo, String usuario, String claveActual, String nuevaClave) throws Exception;

	/**
	 * Inicia el proceso de recuperación de contraseña enviando un correo con enlace.
	 *
	 * @param cuo Código unico de operación
	 * @param usuario Nombre de usuario
	 * @throws Exception
	 */
    public void solicitarReset(String cuo, String usuario, String ip) throws Exception;

	/**
	 * Completa el proceso de recuperación estableciendo una nueva clave.
	 *
	 * @param cuo Código unico de operación
	 * @param token Token recibido por correo
	 * @param nuevaClave Nueva clave a establecer
	 * @throws Exception
	 */
	public void restablecerClave(String cuo, String token, String nuevaClave) throws Exception;
	
	/**
	 * 
	 * Método que permite enviar el identificador de perfil para ser validado si existe y
	 * devolver las opciones asignadas a este perfil.
	 * 
	 * @param cuo Código unico de operación
	 * @param idPerfil Identificador del perfil
	 * @return Retorna las opciones asignadas al perfil
	 * @throws Exception
	 */
	public PerfilOpcions obtenerOpciones(String cuo, Integer idPerfil) throws Exception;
}
