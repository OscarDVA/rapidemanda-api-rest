package pe.gob.pj.rapidemanda.usecase;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.PerfilOpcions;
import pe.gob.pj.rapidemanda.domain.model.servicio.Persona;
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.domain.port.persistence.AccesoPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPersonaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionUsuarioPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.usecase.AccesoUseCasePort;
import pe.gob.pj.rapidemanda.domain.utils.EncryptUtils;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;

@Service("accesoUseCasePort")
public class AccesoUseCaseAdapter implements AccesoUseCasePort {

	final AccesoPersistencePort accesoPersistencePort;
	private final GestionUsuarioPersistencePort gestionUsuarioPersistencePort;
	private final GestionPersonaPersistencePort gestionPersonaPersistencePort;

	public AccesoUseCaseAdapter(@Qualifier("accesoPersistencePort") AccesoPersistencePort accesoPersistencePort,
			@Qualifier("gestionUsuarioPersistencePort") GestionUsuarioPersistencePort gestionUsuarioPersistencePort,
			@Qualifier("gestionPersonaPersistencePort") GestionPersonaPersistencePort gestionPersonaPersistencePort) {
		this.accesoPersistencePort = accesoPersistencePort;
		this.gestionUsuarioPersistencePort = gestionUsuarioPersistencePort;
		this.gestionPersonaPersistencePort = gestionPersonaPersistencePort;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public Usuario iniciarSesion(String cuo, String usuario, String clave) throws Exception {

		Usuario user = accesoPersistencePort.iniciarSesion(cuo, usuario);
		String password = EncryptUtils.cryptBase64u(clave, Cipher.ENCRYPT_MODE);

		if (user == null || ProjectUtils.isNullOrEmpty(user.getClave()) || !user.getClave().equals(password))
			throw new ErrorException(Errors.NEGOCIO_CREDENCIALES_INCORRECTAS.getCodigo(), String
					.format(Errors.NEGOCIO_CREDENCIALES_INCORRECTAS.getNombre(), Proceso.INICIAR_SESION.getNombre()));

		user.setClave("******");

		return user;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = true, rollbackFor = {
			Exception.class, SQLException.class })
	public PerfilOpcions obtenerOpciones(String cuo, Integer idPerfil) throws Exception {
		PerfilOpcions perfilOpciones = accesoPersistencePort.obtenerOpciones(cuo, idPerfil);
		if (perfilOpciones.getOpciones().size() < 1)
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(), String
					.format(Errors.NEGOCIO_PERFIL_NO_ENCONTRADO.getNombre(), Proceso.OBTENER_OPCIONES.getNombre()));
		return perfilOpciones;
	}

	@Override
	@Transactional(transactionManager = "txManagerNegocio", propagation = Propagation.REQUIRES_NEW, readOnly = false, rollbackFor = {
			Exception.class, SQLException.class })
	public Usuario registrarUsuario(String cuo, Usuario usuario) throws Exception {

		try {
			// Validar que el usuario no exista por nombre de usuario
			String nombreUsuario = usuario.getUsuario();
			Map<String, Object> filtersUsuario = new HashMap<>();
			filtersUsuario.put(Usuario.P_NOMBRE_USUARIO, nombreUsuario);
			List<Usuario> usuarios = gestionUsuarioPersistencePort.buscarUsuario(cuo, filtersUsuario);
			if (!usuarios.isEmpty()) {
				throw new ErrorException(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getCodigo(),
						String.format(Errors.NEGOCIO_USUARIO_YA_REGISTRADO.getNombre(), nombreUsuario));
			}
			// Validar que la persona no exista
			String numeroDocumento = usuario.getPersona().getNumeroDocumento();
			Map<String, Object> filters = new HashMap<>();
			filters.put(Persona.P_NUMERO_DOCUMENTO, numeroDocumento);

			List<Persona> personas = gestionPersonaPersistencePort.buscarPersona(cuo, filters);
			if (!personas.isEmpty()) {
				throw new ErrorException(Errors.NEGOCIO_PERSONA_DNI_EXISTE.getCodigo(),
						String.format(Errors.NEGOCIO_PERSONA_DNI_EXISTE.getNombre(), numeroDocumento));
			}
			
			// Validar que el correo no exista
			String correo = usuario.getPersona().getCorreo();
			Map<String, Object> filtersCorreo = new HashMap<>();
			filtersCorreo.put(Persona.P_CORREO, correo);
			
			List<Persona> personasC = gestionPersonaPersistencePort.buscarPersona(cuo, filtersCorreo);
			if (!personasC.isEmpty()) {
				throw new ErrorException(Errors.NEGOCIO_CORREO_YA_REGISTRADO.getCodigo(),
						String.format(Errors.NEGOCIO_CORREO_YA_REGISTRADO.getNombre(), correo));
			}
			
			// Registrar el usuario
			Usuario usuarioRegistrado = accesoPersistencePort.registrarUsuario(cuo, usuario);
			return usuarioRegistrado;

		} catch (ErrorException ee) {
			// Re-lanzar directamente los errores de negocio
			throw ee;
		} catch (Exception e) {
			throw new ErrorException(Errors.ERROR_INESPERADO.getCodigo(),
					String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.USUARIO_REGISTRAR.getNombre()),
					e.getMessage(), e.getCause());

		}

	}

}
