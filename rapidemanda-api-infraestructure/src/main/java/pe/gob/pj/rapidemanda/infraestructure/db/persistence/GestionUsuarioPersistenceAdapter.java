package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.crypto.Cipher;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.PerfilUsuario;
import pe.gob.pj.rapidemanda.domain.model.servicio.Persona;
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPersonaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionUsuarioPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.EncryptUtils;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaePerfil;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovPersona;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovUsuario;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovUsuarioPerfil;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;

@Component("gestionUsuarioPersistencePort")
public class GestionUsuarioPersistenceAdapter implements GestionUsuarioPersistencePort {

	@Autowired
	@Qualifier("gestionPersonaPersistencePort")
	private GestionPersonaPersistencePort gestionPersonaPersistencePort;

	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Override
	public List<Usuario> buscarUsuario(String cuo, Map<String, Object> filters) throws Exception {
		List<Usuario> lista = new ArrayList<>();

		TypedQuery<MovUsuario> query = this.sf.getCurrentSession().createNamedQuery(MovUsuario.Q_ALL, MovUsuario.class);

		if (!ProjectUtils.isNullOrEmpty(filters.get(Usuario.P_NOMBRE_USUARIO))) {
			this.sf.getCurrentSession().enableFilter(MovUsuario.F_USUARIO).setParameter(MovUsuario.P_USUARIO,
					filters.get(Usuario.P_NOMBRE_USUARIO));
		}
		if (!ProjectUtils.isNullOrEmpty(filters.get(Usuario.P_USUARIO_ID))) {
			this.sf.getCurrentSession().enableFilter(MovUsuario.F_ID).setParameter(MovUsuario.P_ID,
					filters.get(Usuario.P_USUARIO_ID));
		}
		query.getResultStream().forEach(movUsuario -> {
			Usuario usuarioDto = new Usuario();
			usuarioDto.setIdUsuario(movUsuario.getId());
			usuarioDto.setUsuario(movUsuario.getUsuario());
			usuarioDto.setClave("******"); // No se retorna la clave
			usuarioDto.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(movUsuario.getActivo())
					? Estado.ACTIVO_NUMERICO.getNombre()
					: Estado.INACTIVO_NUMERICO.getNombre());
			// Inicializar persona

			if (movUsuario.getPersona() != null) {
				Hibernate.initialize(movUsuario.getPersona());
				Persona personaDto = new Persona();
				personaDto.setId(movUsuario.getPersona().getId());
				personaDto.setNumeroDocumento(movUsuario.getPersona().getNumeroDocumento());
				personaDto.setFechaNacimiento(ProjectUtils.convertDateToString(
						movUsuario.getPersona().getFechaNacimiento(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
				personaDto.setPrimerApellido(movUsuario.getPersona().getPrimerApellido());
				personaDto.setSegundoApellido(movUsuario.getPersona().getSegundoApellido());
				personaDto.setNombres(movUsuario.getPersona().getNombres());
				personaDto.setSexo(movUsuario.getPersona().getSexo());
				personaDto.setCorreo(movUsuario.getPersona().getCorreo());
				personaDto.setTelefono(movUsuario.getPersona().getTelefono());
				personaDto.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(movUsuario.getPersona().getActivo())
						? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());

				if (movUsuario.getPersona().getTipoDocumento() != null) {
					personaDto.setIdTipoDocumento(movUsuario.getPersona().getTipoDocumento().getCodigo());
					personaDto.setTipoDocumento(movUsuario.getPersona().getTipoDocumento().getAbreviatura());
				}
				usuarioDto.setPersona(personaDto);
			}

			// Inicializar perfiles
			movUsuario.getPerfils().forEach(perfilUsuario -> {
				if (perfilUsuario.getActivo().equalsIgnoreCase(Estado.ACTIVO_NUMERICO.getNombre())) {
					usuarioDto.getPerfiles()
							.add(new PerfilUsuario(perfilUsuario.getId(), perfilUsuario.getPerfil().getId(),
									perfilUsuario.getPerfil().getNombre(), perfilUsuario.getPerfil().getRol()));
				}
			});

			lista.add(usuarioDto);

		});

		return lista;
	}

	@Override
	public void crearUsuario(String cuo, Usuario usuario) throws Exception {

		String claveEncriptada = EncryptUtils.cryptBase64u(usuario.getClave(), Cipher.ENCRYPT_MODE);
		usuario.setClave(claveEncriptada);

		MovUsuario movUsuario = new MovUsuario();

		this.sf.getCurrentSession().enableFilter(MovPersona.F_ID).setParameter(MovPersona.P_ID,
				usuario.getPersona().getId());

		TypedQuery<MovPersona> personaQuery = this.sf.getCurrentSession().createNamedQuery(MovPersona.Q_ALL,
				MovPersona.class);

		MovPersona movPersona = personaQuery.getSingleResult();

		movUsuario.setUsuario(usuario.getUsuario());
		movUsuario.setClave(usuario.getClave());
		movUsuario.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(usuario.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		movUsuario.setPersona(movPersona);

		usuario.setClave("******");

		this.sf.getCurrentSession().persist(movUsuario);
		usuario.setIdUsuario(movUsuario.getId());

		// Asignar perfiles al usuario
		if (usuario.getPerfiles() != null && !usuario.getPerfiles().isEmpty()) {
			// Crear una lista temporal para almacenar los perfiles completos
			List<PerfilUsuario> perfilesCompletos = new ArrayList<>();

			for (PerfilUsuario perfilUsuario : usuario.getPerfiles()) {
				if (perfilUsuario.getIdPerfil() != null) {
					// Obtener el perfil de la base de datos
					MaePerfil maePerfil = this.sf.getCurrentSession().get(MaePerfil.class, perfilUsuario.getIdPerfil());
					if (maePerfil == null) {
						throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
								String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(),
										"Perfil con ID: " + perfilUsuario.getIdPerfil()));
					}

					// Crear la relación usuario-perfil
					MovUsuarioPerfil movUsuarioPerfil = new MovUsuarioPerfil();
					movUsuarioPerfil.setUsuario(movUsuario);
					movUsuarioPerfil.setPerfil(maePerfil);
					movUsuarioPerfil.setActivo(Estado.ACTIVO_NUMERICO.getNombre());

					this.sf.getCurrentSession().persist(movUsuarioPerfil);

					// Agregar el perfil completo a la lista temporal
					perfilesCompletos.add(new PerfilUsuario(movUsuarioPerfil.getId(), maePerfil.getId(),
							maePerfil.getNombre(), maePerfil.getRol()));
				}
			}

			// Reemplazar la lista de perfiles con los datos completos
			usuario.getPerfiles().clear();
			usuario.getPerfiles().addAll(perfilesCompletos);
		} else {
			// Si no se especifican perfiles, asignar perfil por defecto (ID=3) como en
			// AccesoPersistenceAdapter
			MaePerfil perfilDefault = this.sf.getCurrentSession().get(MaePerfil.class, 3);
			if (perfilDefault == null) {
				throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
						String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), "Perfil por defecto con ID: 3"));
			}

			// Crear la relación usuario-perfil por defecto
			MovUsuarioPerfil usuarioPerfil = new MovUsuarioPerfil();
			usuarioPerfil.setUsuario(movUsuario);
			usuarioPerfil.setPerfil(perfilDefault);
			usuarioPerfil.setActivo(Estado.ACTIVO_NUMERICO.getNombre());

			this.sf.getCurrentSession().persist(usuarioPerfil);

			// Inicializar la lista de perfiles si es null
			if (usuario.getPerfiles() == null) {
				usuario.setPerfiles(new ArrayList<>());
			}

			// Agregar el perfil por defecto al objeto de retorno
			usuario.getPerfiles().add(new PerfilUsuario(usuarioPerfil.getId(), perfilDefault.getId(),
					perfilDefault.getNombre(), perfilDefault.getRol()));
		}

	}

	@Override
	public void actualizarUsuario(String cuo, Usuario usuario) throws Exception {

		// Buscar el usuario existente
		this.sf.getCurrentSession().enableFilter(MovUsuario.F_ID).setParameter(MovUsuario.P_ID, usuario.getIdUsuario());

		TypedQuery<MovUsuario> query = this.sf.getCurrentSession().createNamedQuery(MovUsuario.Q_ALL, MovUsuario.class);

		MovUsuario movUsuario = query.getSingleResult();

		// Buscar la persona existente
		this.sf.getCurrentSession().enableFilter(MovPersona.F_ID).setParameter(MovPersona.P_ID,
				usuario.getPersona().getId());

		TypedQuery<MovPersona> personaQuery = this.sf.getCurrentSession().createNamedQuery(MovPersona.Q_ALL,
				MovPersona.class);

		MovPersona movPersona = personaQuery.getSingleResult();

		movUsuario.setUsuario(usuario.getUsuario());
		movUsuario.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(usuario.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		movUsuario.setPersona(movPersona);

		actualizarPerfilesUsuario(movUsuario, usuario);

		usuario.setClave("******");

		this.sf.getCurrentSession().merge(movUsuario);
	}

	/**
	 * Método privado para actualizar los perfiles de un usuario
	 * 
	 * @param movUsuario Entidad del usuario en base de datos
	 * @param usuario    Objeto de dominio con los nuevos perfiles
	 * @throws Exception
	 */
	private void actualizarPerfilesUsuario(MovUsuario movUsuario, Usuario usuario) throws Exception {
		// 1. Eliminar todos los perfiles existentes del usuario (marcar como inactivos)
		eliminarPerfilesExistentes(movUsuario);

		// 2. Asignar los nuevos perfiles
		if (usuario.getPerfiles() != null && !usuario.getPerfiles().isEmpty()) {
			// Crear una lista temporal para almacenar los perfiles completos
			List<PerfilUsuario> perfilesCompletos = new ArrayList<>();

			for (PerfilUsuario perfilUsuario : usuario.getPerfiles()) {
				if (perfilUsuario.getIdPerfil() != null) {
					// Obtener el perfil de la base de datos
					MaePerfil maePerfil = this.sf.getCurrentSession().get(MaePerfil.class, perfilUsuario.getIdPerfil());
					if (maePerfil == null) {
						throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
								String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(),
										"Perfil con ID: " + perfilUsuario.getIdPerfil()));
					}

					// Crear la nueva relación usuario-perfil
					MovUsuarioPerfil movUsuarioPerfil = new MovUsuarioPerfil();
					movUsuarioPerfil.setUsuario(movUsuario);
					movUsuarioPerfil.setPerfil(maePerfil);
					movUsuarioPerfil.setActivo(Estado.ACTIVO_NUMERICO.getNombre());

					this.sf.getCurrentSession().persist(movUsuarioPerfil);

					// Agregar el perfil completo a la lista temporal
					perfilesCompletos.add(new PerfilUsuario(movUsuarioPerfil.getId(), maePerfil.getId(),
							maePerfil.getNombre(), maePerfil.getRol()));
				}
			}

			// Reemplazar la lista de perfiles con los datos completos
			usuario.getPerfiles().clear();
			usuario.getPerfiles().addAll(perfilesCompletos);
		} else {
			// Si no se especifican perfiles, asignar perfil por defecto (ID=3)
			asignarPerfilPorDefecto(movUsuario, usuario);
		}
	}

	/**
	 * Método privado para eliminar (marcar como inactivos) los perfiles existentes
	 * de un usuario
	 * 
	 * @param movUsuario Entidad del usuario
	 */
	private void eliminarPerfilesExistentes(MovUsuario movUsuario) {
		// Buscar todos los perfiles activos del usuario
		String hql = "FROM MovUsuarioPerfil up WHERE up.usuario.id = :usuarioId AND up.activo = :activo";
		TypedQuery<MovUsuarioPerfil> queryPerfiles = this.sf.getCurrentSession().createQuery(hql,
				MovUsuarioPerfil.class);
		queryPerfiles.setParameter("usuarioId", movUsuario.getId());
		queryPerfiles.setParameter("activo", Estado.ACTIVO_NUMERICO.getNombre());

		List<MovUsuarioPerfil> perfilesExistentes = queryPerfiles.getResultList();

		// Marcar todos los perfiles como inactivos
		for (MovUsuarioPerfil perfilExistente : perfilesExistentes) {
			perfilExistente.setActivo(Estado.INACTIVO_NUMERICO.getNombre());
			this.sf.getCurrentSession().merge(perfilExistente);
		}
	}

	/**
	 * Método privado para asignar el perfil por defecto cuando no se especifican
	 * perfiles
	 * 
	 * @param movUsuario Entidad del usuario
	 * @param usuario    Objeto de dominio del usuario
	 * @throws Exception
	 */
	private void asignarPerfilPorDefecto(MovUsuario movUsuario, Usuario usuario) throws Exception {
		// Asignar perfil por defecto (ID=3) como en AccesoPersistenceAdapter
		MaePerfil perfilDefault = this.sf.getCurrentSession().get(MaePerfil.class, 3);
		if (perfilDefault == null) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), "Perfil por defecto con ID: 3"));
		}

		// Crear la relación usuario-perfil por defecto
		MovUsuarioPerfil usuarioPerfil = new MovUsuarioPerfil();
		usuarioPerfil.setUsuario(movUsuario);
		usuarioPerfil.setPerfil(perfilDefault);
		usuarioPerfil.setActivo(Estado.ACTIVO_NUMERICO.getNombre());

		this.sf.getCurrentSession().persist(usuarioPerfil);

		// Inicializar la lista de perfiles si es null
		if (usuario.getPerfiles() == null) {
			usuario.setPerfiles(new ArrayList<>());
		}

		// Agregar el perfil por defecto al objeto de retorno
		usuario.getPerfiles().add(new PerfilUsuario(usuarioPerfil.getId(), perfilDefault.getId(),
				perfilDefault.getNombre(), perfilDefault.getRol()));
	}

	@Override
	public void actualizarEstadoUsuario(String cuo, Integer id, String nuevoEstado) throws Exception {
		this.sf.getCurrentSession().enableFilter(MovUsuario.F_ID).setParameter(MovUsuario.P_ID, id);
		TypedQuery<MovUsuario> query = this.sf.getCurrentSession().createNamedQuery(MovUsuario.Q_ALL, MovUsuario.class);
		MovUsuario movUser = query.getSingleResult();

		movUser.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(nuevoEstado) ? Estado.ACTIVO_NUMERICO.getNombre()
				: Estado.INACTIVO_NUMERICO.getNombre());

		this.sf.getCurrentSession().merge(movUser);
	}
}
