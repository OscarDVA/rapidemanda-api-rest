package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import jakarta.persistence.TypedQuery;
import pe.gob.pj.rapidemanda.domain.model.servicio.PerfilUsuario;
import pe.gob.pj.rapidemanda.domain.model.servicio.Persona;
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionPersonaPersistencePort;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionUsuarioPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.EncryptUtils;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovPersona;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovUsuario;
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

	}

	@Override
	public void actualizarUsuario(String cuo, Usuario usuario) throws Exception {

		String claveEncriptada = EncryptUtils.cryptBase64u(usuario.getClave(), Cipher.ENCRYPT_MODE);
		usuario.setClave(claveEncriptada);
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

		// Actualizar datos
		movUsuario.setUsuario(usuario.getUsuario());
		movUsuario.setClave(usuario.getClave());
		movUsuario.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(usuario.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		movUsuario.setPersona(movPersona);
		
		usuario.setClave("******");

		this.sf.getCurrentSession().merge(movUsuario);
	}
}
