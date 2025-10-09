package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import jakarta.persistence.TypedQuery;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import pe.gob.pj.rapidemanda.domain.model.servicio.Opcion;
import pe.gob.pj.rapidemanda.domain.model.servicio.PerfilOpcions;
import pe.gob.pj.rapidemanda.domain.model.servicio.PerfilUsuario;
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.domain.port.persistence.AccesoPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeOpcion;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaePerfil;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoDocumentoPersona;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovPersona;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovUsuario;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovUsuarioPerfil;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.EncryptUtils;
import javax.crypto.Cipher;

@Component("accesoPersistencePort")
public class AccesoPersistenceAdapter implements AccesoPersistencePort {

	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Override
	public Usuario iniciarSesion(String cuo, String usuario) throws Exception {
		Usuario usuarioDTO = new Usuario();
		MovUsuario movUsuario;

		this.sf.getCurrentSession().enableFilter(MovUsuario.F_ACCESO)
				.setParameter(MovUsuario.P_ACTIVO, Estado.ACTIVO_NUMERICO.getNombre())
				.setParameter(MovUsuario.P_USUARIO, usuario);

		TypedQuery<MovUsuario> query = this.sf.getCurrentSession().createNamedQuery(MovUsuario.Q_ALL, MovUsuario.class);
		movUsuario = query.getResultStream().findFirst().orElse(null);

		if (movUsuario != null) {
			usuarioDTO.setIdUsuario(movUsuario.getId());
			usuarioDTO.setUsuario(movUsuario.getUsuario());
			usuarioDTO.setClave(movUsuario.getClave());

			usuarioDTO.getPersona().setId(movUsuario.getPersona().getId());
			usuarioDTO.getPersona().setPrimerApellido(movUsuario.getPersona().getPrimerApellido());
			usuarioDTO.getPersona().setSegundoApellido(movUsuario.getPersona().getSegundoApellido());
			usuarioDTO.getPersona().setNombres(movUsuario.getPersona().getNombres());
			usuarioDTO.getPersona().setNumeroDocumento(movUsuario.getPersona().getNumeroDocumento());
			usuarioDTO.getPersona().setTelefono(movUsuario.getPersona().getTelefono());
			usuarioDTO.getPersona().setCorreo(movUsuario.getPersona().getCorreo());
			usuarioDTO.getPersona().setIdTipoDocumento(movUsuario.getPersona().getTipoDocumento().getCodigo());
			usuarioDTO.getPersona().setTipoDocumento(movUsuario.getPersona().getTipoDocumento().getAbreviatura());
			usuarioDTO.getPersona().setFechaNacimiento(ProjectUtils.convertDateToString(
					movUsuario.getPersona().getFechaNacimiento(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
			usuarioDTO.getPersona().setSexo(movUsuario.getPersona().getSexo());
			usuarioDTO.getPersona().setActivo(movUsuario.getPersona().getActivo());

			movUsuario.getPerfils().forEach(perfilUsuario -> {
				if (perfilUsuario.getActivo().equalsIgnoreCase(Estado.ACTIVO_NUMERICO.getNombre())) {
					usuarioDTO.getPerfiles()
							.add(new PerfilUsuario(perfilUsuario.getId(), perfilUsuario.getPerfil().getId(),
									perfilUsuario.getPerfil().getNombre(), perfilUsuario.getPerfil().getRol()));
				}
			});
		}
		return usuarioDTO;
	}

	@Override
	public PerfilOpcions obtenerOpciones(String cuo, Integer idPerfil) throws Exception {
		PerfilOpcions perfilOpciones = new PerfilOpcions();
		MaePerfil maePerfil;

		this.sf.getCurrentSession().enableFilter(MaePerfil.F_ACTIVO).setParameter(MaePerfil.P_ACTIVO,
				Estado.ACTIVO_NUMERICO.getNombre());

		this.sf.getCurrentSession().enableFilter(MaePerfil.F_ID).setParameter(MaePerfil.P_ID, idPerfil);

		TypedQuery<MaePerfil> query = this.sf.getCurrentSession().createNamedQuery(MaePerfil.Q_ALL, MaePerfil.class);
		maePerfil = query.getResultStream().findFirst().orElse(null);

		if (maePerfil != null) {
			perfilOpciones.setRol(maePerfil.getRol());
			maePerfil.getPerfilsOpcion().forEach(x -> {
				if (x.getActivo().equalsIgnoreCase(Estado.ACTIVO_NUMERICO.getNombre())) {
					MaeOpcion maeOpcion = x.getOpcion();
					Opcion opcion = new Opcion();
					opcion.setId(maeOpcion.getId());
					opcion.setCodigo(maeOpcion.getCodigo());
					opcion.setUrl(maeOpcion.getUrl());
					opcion.setIcono(maeOpcion.getIcono());
					opcion.setNombre(maeOpcion.getNombre());
					opcion.setOrden(maeOpcion.getOrden());
					opcion.setActivo(maeOpcion.getActivo());
					opcion.setIdOpcionSuperior(
							maeOpcion.getOpcionSuperior() != null ? maeOpcion.getOpcionSuperior().getId() : null);
					opcion.setNombreOpcionSuperior(
							maeOpcion.getOpcionSuperior() != null ? maeOpcion.getOpcionSuperior().getNombre() : null);
					perfilOpciones.getOpciones().add(opcion);
				}
			});
		}
		return perfilOpciones;
	}

	@Override
	public Usuario registrarUsuario(String cuo, Usuario usuario) throws Exception {

		// Encriptar la clave
		String claveEncriptada = EncryptUtils.cryptBase64u(usuario.getClave(), Cipher.ENCRYPT_MODE);
		usuario.setClave(claveEncriptada);

		// Crear tipo de documento
		MaeTipoDocumentoPersona maeTipoDocumento = new MaeTipoDocumentoPersona();
		maeTipoDocumento.setCodigo(usuario.getPersona().getIdTipoDocumento());

		// 1. Crear y persistir persona
		MovPersona movPersona = new MovPersona();
		movPersona.setTipoDocumento(maeTipoDocumento);
		movPersona.setNumeroDocumento(usuario.getPersona().getNumeroDocumento());
		movPersona.setPrimerApellido(usuario.getPersona().getPrimerApellido());
		movPersona.setSegundoApellido(usuario.getPersona().getSegundoApellido());
		movPersona.setNombres(usuario.getPersona().getNombres());
		movPersona.setSexo(usuario.getPersona().getSexo());
		movPersona.setCorreo(usuario.getPersona().getCorreo());
		movPersona.setTelefono(usuario.getPersona().getTelefono());
		movPersona.setFechaNacimiento(ProjectUtils.parseStringToDate(usuario.getPersona().getFechaNacimiento(),
				ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		movPersona.setActivo(Estado.ACTIVO_NUMERICO.getNombre());

		sf.getCurrentSession().persist(movPersona);
		usuario.getPersona().setId(movPersona.getId());

		// 2. Crear y persistir usuario
		MovUsuario movUsuario = new MovUsuario();
		movUsuario.setUsuario(usuario.getUsuario());
		movUsuario.setClave(usuario.getClave());
		movUsuario.setPersona(movPersona);
		// Registrar usuario en estado INACTIVO por defecto para activación por correo
		movUsuario.setActivo(Estado.INACTIVO_NUMERICO.getNombre());

		sf.getCurrentSession().persist(movUsuario);
		usuario.setIdUsuario(movUsuario.getId());

		// 3. Obtener el perfil por defecto (3) de la base de datos
		MaePerfil perfilDefault = sf.getCurrentSession().get(MaePerfil.class, 3);
		if (perfilDefault == null) {
			throw new Exception("Perfil por defecto no encontrado");
		}

		// 4. Asignar perfil por defecto
		MovUsuarioPerfil usuarioPerfil = new MovUsuarioPerfil();
		usuarioPerfil.setUsuario(movUsuario);
		usuarioPerfil.setPerfil(perfilDefault);
		usuarioPerfil.setActivo(Estado.ACTIVO_NUMERICO.getNombre());

		sf.getCurrentSession().persist(usuarioPerfil);

		// 5. Limpiar la contraseña en el objeto de retorno
		usuario.setClave("******");

		// 6. Inicializar perfiles en el objeto de retorno
		usuario.getPerfiles().add(new PerfilUsuario(usuarioPerfil.getId(), perfilDefault.getId(),
				perfilDefault.getNombre(), perfilDefault.getRol()));

		return usuario;
	}

	@Override
	public void actualizarClaveUsuario(String cuo, String usuario, String nuevaClave) throws Exception {
		// Buscar usuario activo por nombre de usuario
		this.sf.getCurrentSession().enableFilter(MovUsuario.F_ACCESO)
				.setParameter(MovUsuario.P_ACTIVO, Estado.ACTIVO_NUMERICO.getNombre())
				.setParameter(MovUsuario.P_USUARIO, usuario);

		TypedQuery<MovUsuario> query = this.sf.getCurrentSession().createNamedQuery(MovUsuario.Q_ALL, MovUsuario.class);
		MovUsuario movUser = query.getResultStream().findFirst().orElse(null);

		if (movUser != null) {
			String claveEncriptada = EncryptUtils.cryptBase64u(nuevaClave, Cipher.ENCRYPT_MODE);
			movUser.setClave(claveEncriptada);
			this.sf.getCurrentSession().merge(movUser);
		}
		// Si no se encontró, no se hace nada aquí; el caso de uso ya validó existencia
	}

}
