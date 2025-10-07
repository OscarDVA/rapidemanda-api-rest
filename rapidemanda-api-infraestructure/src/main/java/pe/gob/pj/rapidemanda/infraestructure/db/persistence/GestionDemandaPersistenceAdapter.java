package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.Anexo;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demandado;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demandante;
import pe.gob.pj.rapidemanda.domain.model.servicio.Firma;
import pe.gob.pj.rapidemanda.domain.model.servicio.Fundamentacion;
import pe.gob.pj.rapidemanda.domain.model.servicio.Petitorio;
import pe.gob.pj.rapidemanda.domain.model.servicio.RelacionLaboral;
import pe.gob.pj.rapidemanda.domain.port.persistence.GestionDemandaPersistencePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeEstadoDemanda;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoPresentacion;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovAnexo;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemanda;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandado;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandante;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovFirma;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovFundamentacion;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovPetitorio;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovRelacionLaboral;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovUsuario;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;
import pe.gob.pj.rapidemanda.infraestructure.mapper.AnexoEntityMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.DemandadoEntityMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.DemandanteEntityMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.FirmaEntityMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.FundamentacionEntityMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.PetitorioEntityMapper;
import pe.gob.pj.rapidemanda.infraestructure.mapper.RelacionLaboralEntityMapper;

@Component("gestionDemandaPersistencePort")
public class GestionDemandaPersistenceAdapter implements GestionDemandaPersistencePort {

	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Autowired
	private DemandanteEntityMapper demandanteEntityMapper;

	@Autowired
	private DemandadoEntityMapper demandadoEntityMapper;

	@Autowired
	private FirmaEntityMapper firmaEntityMapper;

	@Autowired
	private PetitorioEntityMapper petitorioEntityMapper;

	@Autowired
	private RelacionLaboralEntityMapper relacionLaboralEntityMapper;

	@Autowired
	private FundamentacionEntityMapper fundamentacionEntityMapper;

	@Autowired
	private AnexoEntityMapper anexoEntityMapper;

	@Override
	public List<Demanda> buscarDemandas(String cuo, Map<String, Object> filters) throws Exception {

		if (!ProjectUtils.isNullOrEmpty(filters.get(Demanda.P_ID))) {
			sf.getCurrentSession().enableFilter(MovDemanda.F_ID).setParameter(MovDemanda.P_ID,
					filters.get(Demanda.P_ID));
		}

		if (!ProjectUtils.isNullOrEmpty(filters.get(Demanda.P_ESTADO_ID))) {
			sf.getCurrentSession().enableFilter(MovDemanda.F_ESTADO_DEMANDA).setParameter(MovDemanda.P_ESTADO_DEMANDA,
					filters.get(Demanda.P_ESTADO_ID));
		}

		if (!ProjectUtils.isNullOrEmpty(filters.get(Demanda.P_USUARIO))) {
			sf.getCurrentSession().enableFilter(MovDemanda.F_USUARIO).setParameter(MovDemanda.P_USUARIO,
					filters.get(Demanda.P_USUARIO));
		}

		if (!ProjectUtils.isNullOrEmpty(filters.get(Demanda.P_TIPO_PRESENTACION))) {
			sf.getCurrentSession().enableFilter(MovDemanda.F_TIPO_PRESENTACION)
					.setParameter(MovDemanda.P_TIPO_PRESENTACION, filters.get(Demanda.P_TIPO_PRESENTACION));
		}

		if (!ProjectUtils.isNullOrEmpty(filters.get(Demanda.P_TIPO_RECEPCION))) {
			sf.getCurrentSession().enableFilter(MovDemanda.F_TIPO_RECEPCION).setParameter(MovDemanda.P_TIPO_RECEPCION,
					filters.get(Demanda.P_TIPO_RECEPCION));
		}

		if (!ProjectUtils.isNullOrEmpty(filters.get(Demanda.P_USUARIO_RECEPCION))) {
			sf.getCurrentSession().enableFilter(MovDemanda.F_USUARIO_RECEPCION)
					.setParameter(MovDemanda.P_USUARIO_RECEPCION, filters.get(Demanda.P_USUARIO_RECEPCION));
		}

		TypedQuery<MovDemanda> query = this.sf.getCurrentSession().createNamedQuery(MovDemanda.Q_ALL, MovDemanda.class);
		return query.getResultStream().map(this::mapToModel).toList();
	}

	@Override
	public void registrarDemanda(String cuo, Demanda demanda) throws Exception {
		Session session = sf.getCurrentSession();
		session.beginTransaction();
		try {
			// Crear entidades de referencia
			MaeEstadoDemanda estadoDemanda = new MaeEstadoDemanda();
			// Estado inicial controlado por backend: siempre BORRADOR ('B')
			estadoDemanda.setBEstadoDemanda("B");

			MaeTipoPresentacion tipoPresentacion = new MaeTipoPresentacion();
			tipoPresentacion.setBTipoPresentacion(demanda.getIdTipoPresentacion());

			MovUsuario usuario = new MovUsuario();
			usuario.setId(demanda.getIdUsuario());

			// Crear y configurar la entidad principal de demanda
			MovDemanda movDemanda = new MovDemanda();
			movDemanda.setSumilla(demanda.getSumilla());
			movDemanda.setEstadoDemanda(estadoDemanda);
			movDemanda.setTipoPresentacion(tipoPresentacion);
			movDemanda.setUsuarioDemanda(usuario);
			movDemanda.setPdfUrl(demanda.getPdfUrl());
			movDemanda.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(demanda.getActivo())
					? Estado.ACTIVO_NUMERICO.getNombre()
					: Estado.INACTIVO_NUMERICO.getNombre());

			// Persistir la demanda principal para obtener el ID
			session.persist(movDemanda);
			session.flush(); // Forzar generación del ID

			// Registrar entidades relacionadas
			registrarDemandantes(movDemanda, demanda, session);
			registrarDemandados(movDemanda, demanda, session);
			registrarPetitorios(movDemanda, demanda, session);
			registrarRelacionLaboral(movDemanda, demanda, session);
			registrarFundamentaciones(movDemanda, demanda, session);
			registrarFirmas(movDemanda, demanda, session);
			registrarAnexos(movDemanda, demanda, session);

			// Confirmar transacción
			session.getTransaction().commit();
			demanda.setId(movDemanda.getId());

		} catch (Exception e) {
			session.getTransaction().rollback();
			throw new Exception("Error al registrar demanda: " + e.getMessage(), e);
		}
	}

	@Override
	public void actualizarDemanda(String cuo, Demanda demanda) throws Exception {
		var session = sf.getCurrentSession();

		// Buscar la demanda existente
		session.enableFilter(MovDemanda.F_ID).setParameter(MovDemanda.P_ID, demanda.getId());
		TypedQuery<MovDemanda> query = sf.getCurrentSession().createNamedQuery(MovDemanda.Q_ALL, MovDemanda.class);
		MovDemanda movDemanda = query.getSingleResult();

		// Actualizar datos básicos de la demanda
		actualizarDatosBasicos(movDemanda, demanda);

		// Actualizar entidades relacionadas
		actualizarDemandantes(movDemanda, demanda, session);
		actualizarDemandados(movDemanda, demanda, session);
		actualizarPetitorios(movDemanda, demanda, session);
		actualizarRelacionLaboral(movDemanda, demanda, session);
		actualizarFundamentaciones(movDemanda, demanda, session);
		actualizarFirmas(movDemanda, demanda, session);
		actualizarAnexos(movDemanda, demanda, session);

		// Persistir cambios
		session.merge(movDemanda);
	}

	@Override
	public void actualizarCamposDemanda(String cuo, Integer idDemanda, String nuevoEstadoDemanda, String TipoRecepcion,
			Date fechaRecepcion, Integer idUsuarioRecepcion) throws Exception {
		var session = sf.getCurrentSession();

		// Cargar demanda por ID usando filtro de Hibernate
		session.enableFilter(MovDemanda.F_ID).setParameter(MovDemanda.P_ID, idDemanda);
		TypedQuery<MovDemanda> query = sf.getCurrentSession().createNamedQuery(MovDemanda.Q_ALL, MovDemanda.class);
		MovDemanda movDemanda = query.getSingleResult();

		// Actualizar estado de demanda a nuevo valor
		MaeEstadoDemanda estadoDemanda = new MaeEstadoDemanda();
		estadoDemanda.setBEstadoDemanda(nuevoEstadoDemanda);
		movDemanda.setEstadoDemanda(estadoDemanda);

		// Actualizar campos de recepción
		movDemanda.setTipoRecepcion(TipoRecepcion);
		movDemanda.setFechaRecepcion(fechaRecepcion);
		
		MovUsuario usuarioRecepcion = new MovUsuario();
		usuarioRecepcion.setId(idUsuarioRecepcion);
		movDemanda.setUsuarioRecepcion(usuarioRecepcion);

		// Persistir cambios
		session.merge(movDemanda);
	}

	// ========================================================================
	// MÉTODOS PRIVADOS - MAPEO PRINCIPAL DE ENTIDAD A MODELO
	// ========================================================================

	private Demanda mapToModel(MovDemanda entity) {
		if (entity == null)
			return null;

		Demanda demanda = new Demanda();

		// Mapear campos básicos
		demanda.setId(entity.getId());
		demanda.setSumilla(entity.getSumilla());
		demanda.setPdfUrl(entity.getPdfUrl());
		demanda.setTipoRecepcion(entity.getTipoRecepcion());
		demanda.setFechaRecepcion(ProjectUtils.convertDateToString(entity.getFechaRecepcion(),
				ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		//demanda.setIdUsuarioRecepcion(entity.getIdUsuarioRecepcion());
		demanda.setActivo(entity.getActivo());

		// Mapear referencias con validación de nulos
		mapearReferenciasBasicas(entity, demanda);

		// Mapear entidades relacionadas
		mapearEntidadesRelacionadas(entity, demanda);

		return demanda;
	}

	private void mapearReferenciasBasicas(MovDemanda entity, Demanda demanda) {
		if (entity.getEstadoDemanda() != null) {
			demanda.setIdEstadoDemanda(entity.getEstadoDemanda().getBEstadoDemanda());
			demanda.setEstadoDemanda(entity.getEstadoDemanda().getXEstado());
		}

		if (entity.getTipoPresentacion() != null) {
			demanda.setIdTipoPresentacion(entity.getTipoPresentacion().getBTipoPresentacion());
			demanda.setTipoPresentacion(entity.getTipoPresentacion().getXTipo());
		}

		if (entity.getUsuarioDemanda() != null) {
			demanda.setIdUsuario(entity.getUsuarioDemanda().getId());
			demanda.setUsuarioDemanda(entity.getUsuarioDemanda().getUsuario());
		}
		
		if (entity.getUsuarioRecepcion() != null) {
			demanda.setIdUsuarioRecepcion(entity.getUsuarioRecepcion().getId());
			demanda.setUsuarioRecepcion(entity.getUsuarioRecepcion().getUsuario());
		}
	}

	private void mapearEntidadesRelacionadas(MovDemanda entity, Demanda demanda) {
		// Mapear demandantes
		if (entity.getDemandantes() != null && !entity.getDemandantes().isEmpty()) {
			demanda.setDemandantes(entity.getDemandantes().stream().map(this::mapDemandanteToModel).toList());
		}

		// Mapear demandados
		if (entity.getDemandados() != null && !entity.getDemandados().isEmpty()) {
			demanda.setDemandados(entity.getDemandados().stream().map(this::mapDemandadoToModel).toList());
		}

		// Mapear petitorios
		if (entity.getPetitorios() != null && !entity.getPetitorios().isEmpty()) {
			demanda.setPetitorios(entity.getPetitorios().stream().map(this::mapPetitorioToModel).toList());
		}

		// Mapear relación laboral
		if (entity.getRelacionLaboral() != null) {
			demanda.setRelacionLaboral(mapRelacionLaboralToModel(entity.getRelacionLaboral()));
		}

		// Mapear fundamentaciones
		if (entity.getFundamentaciones() != null && !entity.getFundamentaciones().isEmpty()) {
			demanda.setFundamentaciones(
					entity.getFundamentaciones().stream().map(this::mapFundamentacionToModel).toList());
		}

		// Mapear firmas
		if (entity.getFirmas() != null && !entity.getFirmas().isEmpty()) {
			demanda.setFirmas(entity.getFirmas().stream().map(this::mapFirmaToModel).toList());
		}

		// Mapear anexos
		if (entity.getAnexos() != null && !entity.getAnexos().isEmpty()) {
			demanda.setAnexos(entity.getAnexos().stream().map(this::mapAnexoToModel).toList());
		}
	}

	// ========================================================================
	// MÉTODOS PRIVADOS - MAPEO DE ENTIDADES A MODELOS (ENTITY TO MODEL)
	// ========================================================================

	private Demandante mapDemandanteToModel(MovDemandante entity) {
		return demandanteEntityMapper.toModel(entity);
	}

	private Demandado mapDemandadoToModel(MovDemandado entity) {
		return demandadoEntityMapper.toModel(entity);
	}

	private Petitorio mapPetitorioToModel(MovPetitorio entity) {
		return petitorioEntityMapper.toModel(entity);
	}

	private RelacionLaboral mapRelacionLaboralToModel(MovRelacionLaboral entity) {
		return relacionLaboralEntityMapper.toModel(entity);
	}

	private Fundamentacion mapFundamentacionToModel(MovFundamentacion entity) {
		return fundamentacionEntityMapper.toModel(entity);
	}

	private Firma mapFirmaToModel(MovFirma entity) {
		return firmaEntityMapper.toModel(entity);
	}

	private Anexo mapAnexoToModel(MovAnexo entity) {
		return anexoEntityMapper.toModel(entity);
	}

	// ========================================================================
	// MÉTODOS PRIVADOS - MAPEO DE MODELOS A ENTIDADES (MODEL TO ENTITY)
	// ========================================================================

	private MovDemandante mapDemandanteToEntity(Demandante model) throws Exception {
		return demandanteEntityMapper.toEntity(model);
	}

	private MovDemandado mapDemandadoToEntity(Demandado model) {
		return demandadoEntityMapper.toEntity(model);
	}

	private MovPetitorio mapPetitorioToEntity(Petitorio model) throws Exception {
		return petitorioEntityMapper.toEntity(model);
	}

	private MovRelacionLaboral mapRelacionLaboralToEntity(RelacionLaboral model) throws Exception {
		return relacionLaboralEntityMapper.toEntity(model);
	}

	private MovFundamentacion mapFundamentacionToEntity(Fundamentacion model) {
		return fundamentacionEntityMapper.toEntity(model);
	}

	private MovFirma mapFirmaToEntity(Firma model) {
		return firmaEntityMapper.toEntity(model);
	}

	private MovAnexo mapAnexoToEntity(Anexo model) {
		return anexoEntityMapper.toEntity(model);
	}

	// ========================================================================
	// MÉTODOS PRIVADOS - REGISTRO DE ENTIDADES RELACIONADAS
	// ========================================================================

	private void registrarDemandantes(MovDemanda movDemanda, Demanda demanda, Session session) throws Exception {
		if (demanda.getDemandantes() != null && !demanda.getDemandantes().isEmpty()) {
			List<MovDemandante> movDemandantes = new ArrayList<>();
			for (int i = 0; i < demanda.getDemandantes().size(); i++) {
				Demandante d = demanda.getDemandantes().get(i);
				MovDemandante demandante = mapDemandanteToEntity(d);
				demandante.setDemanda(movDemanda);
				session.persist(demandante);
				session.flush(); // Forzar generación del ID
				movDemandantes.add(demandante);

				// Actualizar el demandante en el objeto de dominio con los datos generados
				d.setId(demandante.getId());
				d.setNDemanda(movDemanda.getId());
			}
			movDemanda.setDemandantes(movDemandantes);
		}
	}

	private void registrarDemandados(MovDemanda movDemanda, Demanda demanda, Session session) {
		if (demanda.getDemandados() != null && !demanda.getDemandados().isEmpty()) {
			List<MovDemandado> movDemandados = new ArrayList<>();
			for (Demandado d : demanda.getDemandados()) {
				MovDemandado demandado = mapDemandadoToEntity(d);
				demandado.setDemanda(movDemanda);
				session.persist(demandado);
				movDemandados.add(demandado);
			}
			movDemanda.setDemandados(movDemandados);
		}
	}

	private void registrarPetitorios(MovDemanda movDemanda, Demanda demanda, Session session) throws Exception {
		if (demanda.getPetitorios() != null && !demanda.getPetitorios().isEmpty()) {
			List<MovPetitorio> movPetitorios = new ArrayList<>();
			for (Petitorio p : demanda.getPetitorios()) {
				MovPetitorio petitorio = mapPetitorioToEntity(p);
				petitorio.setDemanda(movDemanda);
				session.persist(petitorio);
				movPetitorios.add(petitorio);
			}
			movDemanda.setPetitorios(movPetitorios);
		}
	}

	private void registrarRelacionLaboral(MovDemanda movDemanda, Demanda demanda, Session session) throws Exception {
		if (demanda.getRelacionLaboral() != null) {
			MovRelacionLaboral relacion = mapRelacionLaboralToEntity(demanda.getRelacionLaboral());
			relacion.setDemanda(movDemanda);
			session.persist(relacion);
			movDemanda.setRelacionLaboral(relacion);
		}
	}

	private void registrarFundamentaciones(MovDemanda movDemanda, Demanda demanda, Session session) {
		if (demanda.getFundamentaciones() != null && !demanda.getFundamentaciones().isEmpty()) {
			List<MovFundamentacion> movFundamentaciones = new ArrayList<>();
			for (Fundamentacion f : demanda.getFundamentaciones()) {
				MovFundamentacion fundamentacion = mapFundamentacionToEntity(f);
				fundamentacion.setDemanda(movDemanda);
				session.persist(fundamentacion);
				movFundamentaciones.add(fundamentacion);
			}
			movDemanda.setFundamentaciones(movFundamentaciones);
		}
	}

	private void registrarFirmas(MovDemanda movDemanda, Demanda demanda, Session session) {
		if (demanda.getFirmas() != null && !demanda.getFirmas().isEmpty()) {
			List<MovFirma> movFirmas = new ArrayList<>();
			for (Firma f : demanda.getFirmas()) {
				MovFirma firma = mapFirmaToEntity(f);
				firma.setDemanda(movDemanda);
				session.persist(firma);
				movFirmas.add(firma);
			}
			movDemanda.setFirmas(movFirmas);
		}
	}

	private void registrarAnexos(MovDemanda movDemanda, Demanda demanda, Session session) {
		if (demanda.getAnexos() != null && !demanda.getAnexos().isEmpty()) {
			List<MovAnexo> movAnexos = new ArrayList<>();
			for (Anexo a : demanda.getAnexos()) {
				MovAnexo anexo = mapAnexoToEntity(a);
				anexo.setDemanda(movDemanda);
				session.persist(anexo);
				movAnexos.add(anexo);
			}
			movDemanda.setAnexos(movAnexos);
		}
	}

	// ========================================================================
	// MÉTODOS PRIVADOS - ACTUALIZACIÓN DE DATOS BÁSICOS
	// ========================================================================

	private void actualizarDatosBasicos(MovDemanda movDemanda, Demanda demanda) {
		// Actualizar campos básicos
		movDemanda.setSumilla(demanda.getSumilla());
		movDemanda.setPdfUrl(demanda.getPdfUrl());
		movDemanda.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(demanda.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());

		// Actualizar estado si es diferente
		if (!movDemanda.getEstadoDemanda().getBEstadoDemanda().equals(demanda.getIdEstadoDemanda())) {
			MaeEstadoDemanda estadoDemanda = new MaeEstadoDemanda();
			estadoDemanda.setBEstadoDemanda(demanda.getIdEstadoDemanda());
			movDemanda.setEstadoDemanda(estadoDemanda);
		}

		// Actualizar tipo presentación solo si es diferente
		if (!movDemanda.getTipoPresentacion().getBTipoPresentacion().equals(demanda.getIdTipoPresentacion())) {
			MaeTipoPresentacion tipoPresentacion = new MaeTipoPresentacion();
			tipoPresentacion.setBTipoPresentacion(demanda.getIdTipoPresentacion());
			movDemanda.setTipoPresentacion(tipoPresentacion);
		}
	}

	// ========================================================================
	// MÉTODOS PRIVADOS - ACTUALIZACIÓN DE ENTIDADES RELACIONADAS
	// ========================================================================

	private void actualizarDemandantes(MovDemanda movDemanda, Demanda demanda, Session session) throws Exception {
		// Inicializar lista si es null
		if (movDemanda.getDemandantes() == null) {
			movDemanda.setDemandantes(new ArrayList<>());
		}

		// Si no hay demandantes en la demanda, limpiar la lista
		if (demanda.getDemandantes() == null || demanda.getDemandantes().isEmpty()) {
			movDemanda.getDemandantes().clear();
			return;
		}

		// Eliminar demandantes que ya no están en la demanda actualizada
		movDemanda.getDemandantes().removeIf(md -> demanda.getDemandantes().stream()
				.noneMatch(d -> d.getId() != null && d.getId().equals(md.getId())));

		// Procesar cada demandante
		for (Demandante demandante : demanda.getDemandantes()) {
			if (demandante.getId() == null) {
				// Nuevo demandante
				MovDemandante nuevo = mapDemandanteToEntity(demandante);
				nuevo.setDemanda(movDemanda);
				movDemanda.getDemandantes().add(nuevo);
			} else {
				// Demandante existente - actualizar
				movDemanda.getDemandantes().stream().filter(md -> md.getId().equals(demandante.getId())).findFirst()
						.ifPresent(md -> {
							try {
								updateDemandanteEntity(md, demandante);
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
			}
		}
	}

	private void actualizarDemandados(MovDemanda movDemanda, Demanda demanda, Session session) {
		// Inicializar lista si es null
		if (movDemanda.getDemandados() == null) {
			movDemanda.setDemandados(new ArrayList<>());
		}

		// Si no hay demandados en la demanda, limpiar la lista
		if (demanda.getDemandados() == null || demanda.getDemandados().isEmpty()) {
			movDemanda.getDemandados().clear();
			return;
		}

		// Eliminar demandados que ya no están en la demanda actualizada
		movDemanda.getDemandados().removeIf(md -> demanda.getDemandados().stream()
				.noneMatch(d -> d.getId() != null && d.getId().equals(md.getId())));

		// Procesar cada demandado
		for (Demandado demandado : demanda.getDemandados()) {
			if (demandado.getId() == null) {
				// Nuevo demandado
				MovDemandado nuevo = mapDemandadoToEntity(demandado);
				nuevo.setDemanda(movDemanda);
				movDemanda.getDemandados().add(nuevo);
			} else {
				// Demandado existente - actualizar
				movDemanda.getDemandados().stream().filter(md -> md.getId().equals(demandado.getId())).findFirst()
						.ifPresent(md -> updateDemandadoEntity(md, demandado));
			}
		}
	}

	private void actualizarPetitorios(MovDemanda movDemanda, Demanda demanda, Session session) throws Exception {
		// Inicializar lista si es null
		if (movDemanda.getPetitorios() == null) {
			movDemanda.setPetitorios(new ArrayList<>());
		}

		// Si no hay petitorios en la demanda, limpiar la lista
		if (demanda.getPetitorios() == null || demanda.getPetitorios().isEmpty()) {
			movDemanda.getPetitorios().clear();
			return;
		}

		// Eliminar petitorios que ya no están en la demanda actualizada
		movDemanda.getPetitorios().removeIf(mp -> demanda.getPetitorios().stream()
				.noneMatch(p -> p.getId() != null && p.getId().equals(mp.getId())));

		// Procesar cada petitorio
		for (Petitorio petitorio : demanda.getPetitorios()) {
			if (petitorio.getId() == null) {
				// Nuevo petitorio
				MovPetitorio nuevo = mapPetitorioToEntity(petitorio);
				nuevo.setDemanda(movDemanda);
				movDemanda.getPetitorios().add(nuevo);
			} else {
				// Petitorio existente - actualizar
				movDemanda.getPetitorios().stream().filter(mp -> mp.getId().equals(petitorio.getId())).findFirst()
						.ifPresent(mp -> {
							try {
								updatePetitorioEntity(mp, petitorio);
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
			}
		}
	}

	private void actualizarRelacionLaboral(MovDemanda movDemanda, Demanda demanda, Session session) throws Exception {
		if (demanda.getRelacionLaboral() == null) {
			if (movDemanda.getRelacionLaboral() != null) {
				// Eliminar relación existente
				session.delete(movDemanda.getRelacionLaboral());
				movDemanda.setRelacionLaboral(null);
			}
			return;
		}

		if (movDemanda.getRelacionLaboral() == null) {
			// Nueva relación laboral
			MovRelacionLaboral nueva = mapRelacionLaboralToEntity(demanda.getRelacionLaboral());
			nueva.setDemanda(movDemanda);
			movDemanda.setRelacionLaboral(nueva);
		} else {
			// Actualizar relación existente
			updateRelacionLaboralEntity(movDemanda.getRelacionLaboral(), demanda.getRelacionLaboral());
		}
	}

	private void actualizarFundamentaciones(MovDemanda movDemanda, Demanda demanda, Session session) {
		// Inicializar lista si es null
		if (movDemanda.getFundamentaciones() == null) {
			movDemanda.setFundamentaciones(new ArrayList<>());
		}

		// Si no hay fundamentaciones en la demanda, limpiar la lista
		if (demanda.getFundamentaciones() == null || demanda.getFundamentaciones().isEmpty()) {
			movDemanda.getFundamentaciones().clear();
			return;
		}

		// Eliminar fundamentaciones que ya no están en la demanda actualizada
		movDemanda.getFundamentaciones().removeIf(mf -> demanda.getFundamentaciones().stream()
				.noneMatch(f -> f.getId() != null && f.getId().equals(mf.getId())));

		// Procesar cada fundamentación
		for (Fundamentacion fundamentacion : demanda.getFundamentaciones()) {
			if (fundamentacion.getId() == null) {
				// Nueva fundamentación
				MovFundamentacion nueva = mapFundamentacionToEntity(fundamentacion);
				nueva.setDemanda(movDemanda);
				movDemanda.getFundamentaciones().add(nueva);
			} else {
				// Fundamentación existente - actualizar
				movDemanda.getFundamentaciones().stream().filter(mf -> mf.getId().equals(fundamentacion.getId()))
						.findFirst().ifPresent(mf -> updateFundamentacionEntity(mf, fundamentacion));
			}
		}
	}

	private void actualizarFirmas(MovDemanda movDemanda, Demanda demanda, Session session) {
		// Inicializar lista si es null
		if (movDemanda.getFirmas() == null) {
			movDemanda.setFirmas(new ArrayList<>());
		}

		// Si no hay firmas en la demanda, limpiar la lista
		if (demanda.getFirmas() == null || demanda.getFirmas().isEmpty()) {
			movDemanda.getFirmas().clear();
			return;
		}

		// Eliminar firmas que ya no están en la demanda actualizada
		movDemanda.getFirmas().removeIf(
				mf -> demanda.getFirmas().stream().noneMatch(f -> f.getId() != null && f.getId().equals(mf.getId())));

		// Procesar cada firma
		for (Firma firma : demanda.getFirmas()) {
			if (firma.getId() == null) {
				// Nueva firma
				MovFirma nueva = mapFirmaToEntity(firma);
				nueva.setDemanda(movDemanda);
				movDemanda.getFirmas().add(nueva);
			} else {
				// Firma existente - actualizar
				movDemanda.getFirmas().stream().filter(mf -> mf.getId().equals(firma.getId())).findFirst()
						.ifPresent(mf -> updateFirmaEntity(mf, firma));
			}
		}
	}

	private void actualizarAnexos(MovDemanda movDemanda, Demanda demanda, Session session) {
		// Inicializar lista si es null
		if (movDemanda.getAnexos() == null) {
			movDemanda.setAnexos(new ArrayList<>());
		}

		// Si no hay anexos en la demanda, limpiar la lista
		if (demanda.getAnexos() == null || demanda.getAnexos().isEmpty()) {
			movDemanda.getAnexos().clear();
			return;
		}

		// Eliminar anexos que ya no están en la demanda actualizada
		movDemanda.getAnexos().removeIf(
				ma -> demanda.getAnexos().stream().noneMatch(a -> a.getId() != null && a.getId().equals(ma.getId())));

		// Procesar cada anexo
		for (Anexo anexo : demanda.getAnexos()) {
			if (anexo.getId() == null) {
				// Nuevo anexo
				MovAnexo nuevo = mapAnexoToEntity(anexo);
				nuevo.setDemanda(movDemanda);
				movDemanda.getAnexos().add(nuevo);
			} else {
				// Anexo existente - actualizar
				movDemanda.getAnexos().stream().filter(ma -> ma.getId().equals(anexo.getId())).findFirst()
						.ifPresent(ma -> updateAnexoEntity(ma, anexo));
			}
		}
	}

	// ========================================================================
	// MÉTODOS PRIVADOS - ACTUALIZACIÓN DE ENTIDADES INDIVIDUALES
	// ========================================================================

	private void updateDemandanteEntity(MovDemandante entity, Demandante model) throws Exception {
		demandanteEntityMapper.updateEntity(entity, model);
	}

	private void updateDemandadoEntity(MovDemandado entity, Demandado model) {
		demandadoEntityMapper.updateEntity(entity, model);
	}

	private void updatePetitorioEntity(MovPetitorio entity, Petitorio model) throws Exception {
		petitorioEntityMapper.updateEntity(entity, model);
	}

	private void updateRelacionLaboralEntity(MovRelacionLaboral entity, RelacionLaboral model) throws Exception {
		relacionLaboralEntityMapper.updateEntity(entity, model);
	}

	private void updateFundamentacionEntity(MovFundamentacion entity, Fundamentacion model) {
		fundamentacionEntityMapper.updateEntity(entity, model);
	}

	private void updateFirmaEntity(MovFirma entity, Firma model) {
		firmaEntityMapper.updateEntity(entity, model);
	}

	private void updateAnexoEntity(MovAnexo entity, Anexo model) {
		anexoEntityMapper.updateEntity(entity, model);
	}

	@Override
	public void eliminar(String cuo, Integer id) throws Exception {
		MovDemanda ent = sf.getCurrentSession().get(MovDemanda.class, id);

		// Validar que la demanda existe
		if (ent == null) {
			throw new ErrorException(Errors.DATOS_NO_ENCONTRADOS.getCodigo(),
					String.format(Errors.DATOS_NO_ENCONTRADOS.getNombre(), Proceso.DEMANDA_ELIMINAR.getNombre()));
		}

		// Validar que solo se puedan eliminar demandas en estado BORRADOR
		if (!"B".equals(ent.getEstadoDemanda().getBEstadoDemanda())) {
			throw new ErrorException(Errors.NEGOCIO_DEMANDA_NO_ELIMINABLE.getCodigo(),
					String.format(Errors.NEGOCIO_DEMANDA_NO_ELIMINABLE.getNombre(),
							Proceso.DEMANDA_ELIMINAR.getNombre(), ent.getEstadoDemanda().getXEstado()));
		}

		sf.getCurrentSession().remove(ent);
	}
}
