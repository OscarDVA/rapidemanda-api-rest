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
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaePerfil;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoDocumentoPersona;
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

//	@Override
//	public List<Usuario> buscarUsuario(String cuo, Map<String, Object> filters) throws Exception {
//	    TypedQuery<MovUsuario> query = this.sf.getCurrentSession()
//	        .createNamedQuery(MovUsuario.Q_ALL, MovUsuario.class);
//
//	    if (!ProjectUtils.isNullOrEmpty(filters.get(Usuario.P_NOMBRE_USUARIO))) {
//	        this.sf.getCurrentSession()
//	            .enableFilter(MovUsuario.F_USUARIO)
//	            .setParameter(MovUsuario.P_USUARIO, filters.get(Usuario.P_NOMBRE_USUARIO));
//	    }
//
//	    List<Usuario> lista = new ArrayList<>();
//
//	    for (MovUsuario mu : query.getResultList()) {
//	        // Inicializar relaciones
//	        if (mu.getPersona() != null) {
//	            Hibernate.initialize(mu.getPersona());
//	            if (mu.getPersona().getTipoDocumento() != null) {
//	                Hibernate.initialize(mu.getPersona().getTipoDocumento());
//	            }
//	        }
//
//	        // Mapear a DTO en un solo bloque
//	        Usuario usuario = new Usuario();
//	        usuario.setIdUsuario(mu.getId());
//	        usuario.setUsuario(mu.getUsuario());
//	        usuario.setClave("******");
//	        usuario.setActivo(mu.getActivo());
//
//	        if (mu.getPersona() != null) {
//	            MovPersona mp = mu.getPersona();
//	            Persona persona = new Persona();
//	            persona.setId(mp.getId());
//	            persona.setNumeroDocumento(mp.getNumeroDocumento());
//	            persona.setFechaNacimiento(ProjectUtils.convertDateToString(mp.getFechaNacimiento(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
//	            persona.setPrimerApellido(mp.getPrimerApellido());
//	            persona.setSegundoApellido(mp.getSegundoApellido());
//	            persona.setNombres(mp.getNombres());
//	            persona.setSexo(mp.getSexo());
//	            persona.setCorreo(mp.getCorreo());
//	            persona.setTelefono(mp.getTelefono());
//	            persona.setActivo(mp.getActivo());
//
//	            if (mp.getTipoDocumento() != null) {
//	                persona.setIdTipoDocumento(mp.getTipoDocumento().getCodigo());
//	                persona.setTipoDocumento(mp.getTipoDocumento().getAbreviatura());
//	            }
//
//	            usuario.setPersona(persona);
//	        }
//
//	        lista.add(usuario);
//	    }
//
//	    return lista;
//	}
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
	public void registrarUsuario(String cuo, Usuario usuario) throws Exception {

		// 0. Encriptar la contraseña
		String claveEncriptada = EncryptUtils.cryptBase64u(usuario.getClave(), Cipher.ENCRYPT_MODE);
		usuario.setClave(claveEncriptada);

		MaeTipoDocumentoPersona maeTipoDocumento = new MaeTipoDocumentoPersona();
		maeTipoDocumento.setCodigo(usuario.getPersona().getIdTipoDocumento());

		// 1. Crear y persistir persona
		MovPersona movPersona = new MovPersona();
		movPersona.setTipoDocumento(maeTipoDocumento);
		// Mapear todos los campos de usuario.getPersona() a movPersona
		movPersona.setNumeroDocumento(usuario.getPersona().getNumeroDocumento());
		movPersona.setPrimerApellido(usuario.getPersona().getPrimerApellido());
		movPersona.setSegundoApellido(usuario.getPersona().getSegundoApellido());
		movPersona.setNombres(usuario.getPersona().getNombres());
		movPersona.setSexo(usuario.getPersona().getSexo());
		movPersona.setCorreo(usuario.getPersona().getCorreo());
		movPersona.setTelefono(usuario.getPersona().getTelefono());
		movPersona.setFechaNacimiento(ProjectUtils.parseStringToDate(usuario.getPersona().getFechaNacimiento(),
				ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		movPersona.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(usuario.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());

		sf.getCurrentSession().persist(movPersona);
		usuario.getPersona().setId(movPersona.getId());

		// 2. Crear y persistir usuario
		MovUsuario movUsuario = new MovUsuario();
		movUsuario.setUsuario(usuario.getUsuario());
		movUsuario.setClave(usuario.getClave());
		movUsuario.setPersona(movPersona); // Entidad gestionada
		movUsuario.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(usuario.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());

		sf.getCurrentSession().persist(movUsuario);
		usuario.setIdUsuario(movUsuario.getId());

		// 3. Obtener el perfil por defecto (3) de la base de datos
		MaePerfil perfilDefault = sf.getCurrentSession().get(MaePerfil.class, 3);
		if (perfilDefault == null) {
			throw new Exception("Perfil por defecto no encontrado");
		}

		// 4. Asignar perfil por defecto (3)
		MovUsuarioPerfil usuarioPerfil = new MovUsuarioPerfil();
		usuarioPerfil.setUsuario(movUsuario);
		usuarioPerfil.setPerfil(perfilDefault); // Perfil por defecto para usuarios externos
		usuarioPerfil.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(usuario.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());

		sf.getCurrentSession().persist(usuarioPerfil);
		// 7. Limpiar la contraseña en el objeto de retorno
		usuario.setClave("******");
		
		// 8. Inicializar perfiles en el objeto de retorno
		usuario.getPerfiles().add(new PerfilUsuario(usuarioPerfil.getId(), perfilDefault.getId(), perfilDefault.getNombre(), perfilDefault.getRol()));
		
	}
}
