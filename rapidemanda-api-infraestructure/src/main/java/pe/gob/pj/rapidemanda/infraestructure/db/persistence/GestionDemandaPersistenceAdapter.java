package pe.gob.pj.rapidemanda.infraestructure.db.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
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
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemanda;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandado;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandante;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovFirma;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovFundamentacion;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovPetitorio;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovRelacionLaboral;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovUsuario;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;

@Component("gestionDemandaPersistencePort")
public class GestionDemandaPersistenceAdapter implements GestionDemandaPersistencePort {

	@Autowired
	@Qualifier("sessionNegocio")
	private SessionFactory sf;

	@Override
	public List<Demanda> buscarDemandas(String cuo, Map<String, Object> filters) throws Exception {

		var idFilter = filters.get(Demanda.P_ID);
		if (!ProjectUtils.isNullOrEmpty(idFilter)) {
			sf.getCurrentSession().enableFilter(MovDemanda.F_ID).setParameter(MovDemanda.P_ID, idFilter);
		}

		TypedQuery<MovDemanda> query = this.sf.getCurrentSession().createNamedQuery(MovDemanda.Q_ALL, MovDemanda.class);

		return query.getResultStream().map(this::mapearDemanda).toList();

	}

	private Demanda mapearDemanda(MovDemanda mov) {
		var demanda = new Demanda();
		demanda.setId(mov.getId());
		demanda.setSumilla(mov.getSumilla());

		demanda.setIdEstadoDemanda(mov.getEstadoDemanda().getBEstadoDemanda());
		demanda.setEstadoDemanda(mov.getEstadoDemanda().getXEstado());

		demanda.setIdTipoPresentacion(mov.getTipoPresentacion().getBTipoPresentacion());
		demanda.setTipoPresentacion(mov.getTipoPresentacion().getXTipo());

		demanda.setIdUsuario(mov.getUsuarioDemanda().getId());
		demanda.setUsuarioDemanda(mov.getUsuarioDemanda().getUsuario());

		demanda.setPdfUrl(mov.getPdfUrl());
		demanda.setActivo(mov.getActivo());
		demanda.setDemandantes(mapearDemandantes(mov.getDemandantes()));
		demanda.setDemandados(mapearDemandados(mov.getDemandados()));
		demanda.setPetitorios(mapearPetitorios(mov.getPetitorios()));

		if (mov.getRelacionLaboral() != null) {
			demanda.setRelacionLaboral(mapearRelacionLaboral(mov.getRelacionLaboral()));
		}
		demanda.setFundamentaciones(mapearFundamentaciones(mov.getFundamentaciones()));
		demanda.setFirmas(mapearFirmas(mov.getFirmas()));

		return demanda;
	}

	private List<Demandante> mapearDemandantes(List<MovDemandante> demandantes) {
		return demandantes.stream().map(this::mapearDemandante).toList();
	}

	private Demandante mapearDemandante(MovDemandante d) {
		var demandante = new Demandante();
		demandante.setId(d.getId());
		demandante.setTipoDocumento(d.getTipoDocumento());
		demandante.setNumeroDocumento(d.getNumeroDocumento());
		demandante.setRazonSocial(d.getRazonSocial());
		demandante.setGenero(d.getGenero());
		demandante.setFechaNacimiento(
				ProjectUtils.convertDateToString(d.getFechaNacimiento(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		demandante.setDepartamento(d.getDepartamento());
		demandante.setProvincia(d.getProvincia());
		demandante.setDistrito(d.getDistrito());
		demandante.setTipoDomicilio(d.getTipoDomicilio());
		demandante.setDomicilio(d.getDomicilio());
		demandante.setReferencia(d.getReferencia());
		demandante.setCorreo(d.getCorreo());
		demandante.setCelular(d.getCelular());
		demandante.setCasillaElectronica(d.getCasillaElectronica());
		demandante.setApoderadoComun("1".equals(d.getApoderadoComun()) ? "1" : "0");
		demandante.setArchivoUrl(d.getArchivoUrl());
		demandante.setNDemanda(d.getDemanda() != null ? d.getDemanda().getId() : null);
		demandante.setActivo(d.getActivo());
		return demandante;
	}

	private List<Demandado> mapearDemandados(List<MovDemandado> demandados) {
		return demandados.stream().map(this::mapearDemandado).toList();
	}

	private Demandado mapearDemandado(MovDemandado d) {
		var demandado = new Demandado();
		demandado.setId(d.getId());
		demandado.setTipoDocumento(d.getTipoDocumento());
		demandado.setNumeroDocumento(d.getNumeroDocumento());
		demandado.setRazonSocial(d.getRazonSocial());
		demandado.setDepartamento(d.getDepartamento());
		demandado.setProvincia(d.getProvincia());
		demandado.setDistrito(d.getDistrito());
		demandado.setTipoDomicilio(d.getTipoDomicilio());
		demandado.setDomicilio(d.getDomicilio());
		demandado.setReferencia(d.getReferencia());
		demandado.setNDemanda(d.getDemanda() != null ? d.getDemanda().getId() : null);
		demandado.setActivo(d.getActivo());
		return demandado;
	}

	private List<Petitorio> mapearPetitorios(List<MovPetitorio> petitorios) {
		return petitorios.stream().map(this::mapearPetitorio).toList();
	}

	private Petitorio mapearPetitorio(MovPetitorio p) {
		var petitorio = new Petitorio();
		petitorio.setId(p.getId());
		petitorio.setTipo(p.getTipo());
		petitorio.setPretensionPrincipal(p.getPretensionPrincipal());
		petitorio.setConcepto(p.getConcepto());
		petitorio.setPretensionAccesoria(p.getPretensionAccesoria());
		petitorio.setMonto(p.getMonto());
		petitorio.setJustificacion(p.getJustificacion());
		petitorio.setFechaInicio(
				ProjectUtils.convertDateToString(p.getFechaInicio(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		petitorio.setFechaFin(ProjectUtils.convertDateToString(p.getFechaFin(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		petitorio.setNDemanda(p.getDemanda() != null ? p.getDemanda().getId() : null);
		petitorio.setActivo(p.getActivo());
		return petitorio;
	}

	private RelacionLaboral mapearRelacionLaboral(MovRelacionLaboral r) {
		var relacion = new RelacionLaboral();
		relacion.setId(r.getId());
		relacion.setRegimen(r.getRegimen());
		relacion.setFechaInicio(
				ProjectUtils.convertDateToString(r.getFechaInicio(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		relacion.setFechaFin(
				ProjectUtils.convertDateToString(r.getFechaFin(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		relacion.setAnios(r.getAnios());
		relacion.setMeses(r.getMeses());
		relacion.setDias(r.getDias());
		relacion.setRemuneracion(r.getRemuneracion());
		relacion.setNDemanda(r.getDemanda() != null ? r.getDemanda().getId() : null);
		relacion.setActivo(r.getActivo());
		return relacion;
	}

	private List<Fundamentacion> mapearFundamentaciones(List<MovFundamentacion> fundamentaciones) {
		return fundamentaciones.stream().map(this::mapearFundamentaciones).toList();
	}

	private Fundamentacion mapearFundamentaciones(MovFundamentacion f) {
		var fundamentacion = new Fundamentacion();
		fundamentacion.setId(f.getId());
		fundamentacion.setContenido(f.getXContenido());
		fundamentacion.setNDemanda(f.getDemanda() != null ? f.getDemanda().getId() : null);
		fundamentacion.setActivo(f.getActivo());
		return fundamentacion;
	}

	private List<Firma> mapearFirmas(List<MovFirma> firmas) {
		return firmas.stream().map(this::mapearFirmas).toList();
	}

	private Firma mapearFirmas(MovFirma f) {
		var firma = new Firma();
		firma.setId(f.getId());
		firma.setTipo(f.getCTipo());
		firma.setArchivoUrl(f.getXArchivoUrl());
		firma.setNDemanda(f.getDemanda() != null ? f.getDemanda().getId() : null);
		firma.setActivo(f.getActivo());
		return firma;
	}

	@Override
	public void registrarDemanda(String cuo, Demanda demanda) throws Exception {
		Session session = sf.getCurrentSession();
		session.beginTransaction();
		try {

			MaeEstadoDemanda estadoDemanda = new MaeEstadoDemanda();
			estadoDemanda.setBEstadoDemanda(demanda.getIdEstadoDemanda());

			MaeTipoPresentacion tipoPresentacion = new MaeTipoPresentacion();
			tipoPresentacion.setBTipoPresentacion(demanda.getIdTipoPresentacion());

			MovUsuario usuario = new MovUsuario();
			usuario.setId(demanda.getIdUsuario());

			MovDemanda movDemanda = new MovDemanda();
			movDemanda.setSumilla(demanda.getSumilla());
			movDemanda.setEstadoDemanda(estadoDemanda);
			movDemanda.setTipoPresentacion(tipoPresentacion);
			movDemanda.setUsuarioDemanda(usuario);
			movDemanda.setPdfUrl(demanda.getPdfUrl());
			movDemanda.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(demanda.getActivo())
					? Estado.ACTIVO_NUMERICO.getNombre()
					: Estado.INACTIVO_NUMERICO.getNombre());

			// Persistir primero la demanda para obtener el ID
			session.persist(movDemanda);
			session.flush(); // Forzar generación del ID

			// 2. Registrar demandantes
			if (demanda.getDemandantes() != null && !demanda.getDemandantes().isEmpty()) {
				List<MovDemandante> movDemandantes = new ArrayList<>();
				System.out.println("Demandantes registrados: " + movDemandantes.size());
				for (Demandante d : demanda.getDemandantes()) {
					MovDemandante demandante = mapDemandanteToEntity(d, movDemanda);
					session.persist(demandante);
					movDemandantes.add(demandante);
				}
				movDemanda.setDemandantes(movDemandantes);
			}

			// 3. Registrar demandados
			if (demanda.getDemandados() != null && !demanda.getDemandados().isEmpty()) {
				List<MovDemandado> movDemandados = new ArrayList<>();
				System.out.println("Demandantes registrados: " + movDemandados.size());
				for (Demandado d : demanda.getDemandados()) {
					MovDemandado demandado = mapDemandadoToEntity(d, movDemanda);
					session.persist(demandado);
					movDemandados.add(demandado);
				}

				movDemanda.setDemandados(movDemandados);
			}
			// 4. Registrar petitorios
			if (demanda.getPetitorios() != null && !demanda.getPetitorios().isEmpty()) {
				List<MovPetitorio> movPetitorios = new ArrayList<>();
				System.out.println("Petitorios registrados: " + movPetitorios.size());
				for (Petitorio p : demanda.getPetitorios()) {
					MovPetitorio petitorio = mapPetitorioToEntity(p, movDemanda);
					session.persist(petitorio);
					movPetitorios.add(petitorio);
				}

				movDemanda.setPetitorios(movPetitorios);
			}
			// 5. Registrar relación laboral
			if (demanda.getRelacionLaboral() != null) {
				MovRelacionLaboral relacion = mapRelacionLaboralToEntity(demanda.getRelacionLaboral(), movDemanda);
				session.persist(relacion);
				movDemanda.setRelacionLaboral(relacion);
			}
			
			// 6. Registrar fundamentaciones
			if (demanda.getFundamentaciones() != null && !demanda.getFundamentaciones().isEmpty()) {
				List<MovFundamentacion> movFundamentaciones = new ArrayList<>();
				System.out.println("Fundamentaciones registradas: " + movFundamentaciones.size());
				for (Fundamentacion f : demanda.getFundamentaciones()) {
					MovFundamentacion fundamentacion = mapFundamentacionToEntity(f, movDemanda);
					session.persist(fundamentacion);
					movFundamentaciones.add(fundamentacion);
				}

				movDemanda.setFundamentaciones(movFundamentaciones);
			}
			// 7. Registrar firmas
			if (demanda.getFirmas() != null && !demanda.getFirmas().isEmpty()) {
				List<MovFirma> movFirmas = new ArrayList<>();
				System.out.println("Firmas registradas: " + movFirmas.size());
				for (Firma f : demanda.getFirmas()) {
					MovFirma firma = mapFirmaToEntity(f, movDemanda);
					session.persist(firma);
					movFirmas.add(firma);
				}

				movDemanda.setFirmas(movFirmas);
			}

			// Forzar operaciones pendientes session.flush();
			session.getTransaction().commit();

			demanda.setId(movDemanda.getId());
		} catch (Exception e) {
			session.getTransaction().rollback();
			throw new Exception("Error al registrar demanda: " + e.getMessage(), e);
		}
	}

	private MovDemandante mapDemandanteToEntity(Demandante d, MovDemanda demanda) throws Exception {
		MovDemandante demandante = new MovDemandante();
		demandante.setTipoDocumento(d.getTipoDocumento());
		demandante.setNumeroDocumento(d.getNumeroDocumento());
		demandante.setRazonSocial(d.getRazonSocial());
		demandante.setGenero(d.getGenero());
		demandante.setFechaNacimiento(
				ProjectUtils.parseStringToDate(d.getFechaNacimiento(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		demandante.setDepartamento(d.getDepartamento());
		demandante.setProvincia(d.getProvincia());
		demandante.setDistrito(d.getDistrito());
		demandante.setTipoDomicilio(d.getTipoDomicilio());
		demandante.setDomicilio(d.getDomicilio());
		demandante.setReferencia(d.getReferencia());
		demandante.setCorreo(d.getCorreo());
		demandante.setCelular(d.getCelular());
		demandante.setCasillaElectronica(d.getCasillaElectronica());
		demandante.setApoderadoComun("1".equals(d.getApoderadoComun()) ? "1" : "0");
		demandante.setArchivoUrl(d.getArchivoUrl());
		demandante.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(d.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		demandante.setDemanda(demanda);
		return demandante;
	}

	private MovDemandado mapDemandadoToEntity(Demandado d, MovDemanda demanda) {
		MovDemandado demandado = new MovDemandado();
		demandado.setTipoDocumento(d.getTipoDocumento());
		demandado.setNumeroDocumento(d.getNumeroDocumento());
		demandado.setRazonSocial(d.getRazonSocial());
		demandado.setDepartamento(d.getDepartamento());
		demandado.setProvincia(d.getProvincia());
		demandado.setDistrito(d.getDistrito());
		demandado.setTipoDomicilio(d.getTipoDomicilio());
		demandado.setDomicilio(d.getDomicilio());
		demandado.setReferencia(d.getReferencia());
		demandado.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(d.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		demandado.setDemanda(demanda);
		return demandado;
	}

	private MovPetitorio mapPetitorioToEntity(Petitorio p, MovDemanda demanda) throws Exception {
		MovPetitorio petitorio = new MovPetitorio();
		petitorio.setTipo(p.getTipo());
		petitorio.setPretensionPrincipal(p.getPretensionPrincipal());
		petitorio.setConcepto(p.getConcepto());
		petitorio.setPretensionAccesoria(p.getPretensionAccesoria());
		petitorio.setMonto(p.getMonto());
		petitorio.setJustificacion(p.getJustificacion());
		petitorio.setFechaInicio(
				ProjectUtils.parseStringToDate(p.getFechaInicio(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		petitorio.setFechaFin(
				ProjectUtils.parseStringToDate(p.getFechaFin(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		petitorio.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(p.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		petitorio.setDemanda(demanda);
		return petitorio;
	}
	
	private MovRelacionLaboral mapRelacionLaboralToEntity(RelacionLaboral r, MovDemanda demanda) throws Exception {
		MovRelacionLaboral relacion = new MovRelacionLaboral();
		relacion.setRegimen(r.getRegimen());
		relacion.setFechaInicio(
				ProjectUtils.parseStringToDate(r.getFechaInicio(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		relacion.setFechaFin(
				ProjectUtils.parseStringToDate(r.getFechaFin(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		relacion.setAnios(r.getAnios());
		relacion.setMeses(r.getMeses());
		relacion.setDias(r.getDias());
		relacion.setRemuneracion(r.getRemuneracion());
		relacion.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(r.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		relacion.setDemanda(demanda);
		return relacion;
	}
	
	private MovFundamentacion mapFundamentacionToEntity(Fundamentacion f, MovDemanda demanda) {
		MovFundamentacion fundamentacion = new MovFundamentacion();
		fundamentacion.setXContenido(f.getContenido());
		fundamentacion.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(f.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		fundamentacion.setDemanda(demanda);
		return fundamentacion;
	}
	
	private MovFirma mapFirmaToEntity(Firma f, MovDemanda demanda) {
		MovFirma firma = new MovFirma();
		firma.setCTipo(f.getTipo());
		firma.setXArchivoUrl(f.getArchivoUrl());
		firma.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(f.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		firma.setDemanda(demanda);
		return firma;
	}

	@Override
	public void actualizarDemanda(String cuo, Demanda demanda) throws Exception {

		var session = sf.getCurrentSession();

		// Habilitar filtro por ID
		session.enableFilter(MovDemanda.F_ID).setParameter(MovDemanda.P_ID, demanda.getId());

		TypedQuery<MovDemanda> query = sf.getCurrentSession().createNamedQuery(MovDemanda.Q_ALL, MovDemanda.class);

		MovDemanda movDemanda = query.getSingleResult();

		// Actualizar datos básicos
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

		// Actualizar relaciones
		actualizarDemandantes(movDemanda, demanda, session);
		actualizarDemandados(movDemanda, demanda, session);
		actualizarPetitorios(movDemanda, demanda, session);
		actualizarRelacionLaboral(movDemanda, demanda, session);
		actualizarFundamentaciones(movDemanda, demanda, session);
		actualizarFirmas(movDemanda, demanda, session);
		
		// Actualizar la demanda
		session.merge(movDemanda);

		// sf.getCurrentSession().merge(movDemanda);
	}

	private void actualizarDemandantes(MovDemanda movDemanda, Demanda demanda, Session session) throws Exception {
		// Eliminar demandantes que ya no están en la demanda actualizada
		movDemanda.getDemandantes().removeIf(md -> demanda.getDemandantes().stream()
				.noneMatch(d -> d.getId() != null && d.getId().equals(md.getId())));

		// Procesar cada demandante
		for (Demandante demandante : demanda.getDemandantes()) {
			if (demandante.getId() == null) {
				// Nuevo demandante
				MovDemandante nuevo = mapDemandanteToMovDemandante(demandante);
				nuevo.setDemanda(movDemanda);
				movDemanda.getDemandantes().add(nuevo);
			} else {
				// Demandante existente - actualizar
				movDemanda.getDemandantes().stream().filter(md -> md.getId().equals(demandante.getId())).findFirst()
						.ifPresent(md -> {
							try {
								actualizarMovDemandante(md, demandante);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});
			}
		}
	}

	private void actualizarDemandados(MovDemanda movDemanda, Demanda demanda, Session session) {
		// Eliminar demandados que ya no están en la demanda actualizada
		movDemanda.getDemandados().removeIf(md -> demanda.getDemandados().stream()
				.noneMatch(d -> d.getId() != null && d.getId().equals(md.getId())));

		// Procesar cada demandado
		for (Demandado demandado : demanda.getDemandados()) {
			if (demandado.getId() == null) {
				// Nuevo demandado
				MovDemandado nuevo = mapDemandadoToMovDemandado(demandado);
				nuevo.setDemanda(movDemanda);
				movDemanda.getDemandados().add(nuevo);
			} else {
				// Demandado existente - actualizar
				movDemanda.getDemandados().stream().filter(md -> md.getId().equals(demandado.getId())).findFirst()
						.ifPresent(md -> actualizarMovDemandado(md, demandado));
			}
		}
	}
	
	private void actualizarPetitorios(MovDemanda movDemanda, Demanda demanda, Session session) throws Exception {
		// Eliminar petitorios que ya no están en la demanda actualizada
		movDemanda.getPetitorios().removeIf(mp -> demanda.getPetitorios().stream()
				.noneMatch(p -> p.getId() != null && p.getId().equals(mp.getId())));

		// Procesar cada petitorio
		for (Petitorio petitorio : demanda.getPetitorios()) {
			if (petitorio.getId() == null) {
				// Nuevo petitorio
				MovPetitorio nuevo = mapPetitorioToMov(petitorio);
				nuevo.setDemanda(movDemanda);
				movDemanda.getPetitorios().add(nuevo);
			} else {
				// Petitorio existente - actualizar
				movDemanda.getPetitorios().stream().filter(mp -> mp.getId().equals(petitorio.getId())).findFirst()
						.ifPresent(mp -> {
							try {
								actualizarMovPetitorio(mp, petitorio);
							} catch (Exception e) {
								// TODO Auto-generated catch block
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
			MovRelacionLaboral nueva = mapRelacionLaboralToMov(demanda.getRelacionLaboral());
			nueva.setDemanda(movDemanda);
			movDemanda.setRelacionLaboral(nueva);
		} else {
			// Actualizar relación existente
			actualizarMovRelacionLaboral(movDemanda.getRelacionLaboral(), demanda.getRelacionLaboral());
		}
	}
	
	private void actualizarFundamentaciones(MovDemanda movDemanda, Demanda demanda, Session session) {
		// Eliminar fundamentaciones que ya no están en la demanda actualizada
		movDemanda.getFundamentaciones().removeIf(mf -> demanda.getFundamentaciones().stream()
				.noneMatch(f -> f.getId() != null && f.getId().equals(mf.getId())));

		// Procesar cada fundamentación
		for (Fundamentacion fundamentacion : demanda.getFundamentaciones()) {
			if (fundamentacion.getId() == null) {
				// Nueva fundamentación
				MovFundamentacion nueva = mapFundamentacionToMov(fundamentacion);
				nueva.setDemanda(movDemanda);
				movDemanda.getFundamentaciones().add(nueva);
			} else {
				// Fundamentación existente - actualizar
				movDemanda.getFundamentaciones().stream().filter(mf -> mf.getId().equals(fundamentacion.getId())).findFirst()
						.ifPresent(mf -> actualizarMovFundamentacion(mf, fundamentacion));
			}
		}
	}
	
	private void actualizarFirmas(MovDemanda movDemanda, Demanda demanda, Session session) {
		// Eliminar firmas que ya no están en la demanda actualizada
		movDemanda.getFirmas().removeIf(mf -> demanda.getFirmas().stream()
				.noneMatch(f -> f.getId() != null && f.getId().equals(mf.getId())));

		// Procesar cada firma
		for (Firma firma : demanda.getFirmas()) {
			if (firma.getId() == null) {
				// Nueva firma
				MovFirma nueva = mapFirmaToMov(firma);
				nueva.setDemanda(movDemanda);
				movDemanda.getFirmas().add(nueva);
			} else {
				// Firma existente - actualizar
				movDemanda.getFirmas().stream().filter(mf -> mf.getId().equals(firma.getId())).findFirst()
						.ifPresent(mf -> actualizarMovFirma(mf, firma));
			}
		}
	}	
	
	private MovDemandante mapDemandanteToMovDemandante(Demandante demandante) throws Exception {
		var mov = new MovDemandante();
		mov.setTipoDocumento(demandante.getTipoDocumento());
		mov.setNumeroDocumento(demandante.getNumeroDocumento());
		mov.setRazonSocial(demandante.getRazonSocial());
		mov.setGenero(demandante.getGenero());
		mov.setFechaNacimiento(ProjectUtils.parseStringToDate(demandante.getFechaNacimiento(),
				ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setDepartamento(demandante.getDepartamento());
		mov.setProvincia(demandante.getProvincia());
		mov.setDistrito(demandante.getDistrito());
		mov.setTipoDomicilio(demandante.getTipoDomicilio());
		mov.setDomicilio(demandante.getDomicilio());
		mov.setReferencia(demandante.getReferencia());
		mov.setCorreo(demandante.getCorreo());
		mov.setCelular(demandante.getCelular());
		mov.setCasillaElectronica(demandante.getCasillaElectronica());
		mov.setApoderadoComun("1".equals(demandante.getApoderadoComun()) ? "1" : "0");
		mov.setArchivoUrl(demandante.getArchivoUrl());
		mov.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(demandante.getActivo())
				? Estado.ACTIVO_NUMERICO.getNombre()
				: Estado.INACTIVO_NUMERICO.getNombre());
		return mov;
	}

	private void actualizarMovDemandante(MovDemandante mov, Demandante demandante) throws Exception {
		mov.setTipoDocumento(demandante.getTipoDocumento());
		mov.setNumeroDocumento(demandante.getNumeroDocumento());
		mov.setRazonSocial(demandante.getRazonSocial());
		mov.setGenero(demandante.getGenero());
		mov.setFechaNacimiento(ProjectUtils.parseStringToDate(demandante.getFechaNacimiento(),
				ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setDepartamento(demandante.getDepartamento());
		mov.setProvincia(demandante.getProvincia());
		mov.setDistrito(demandante.getDistrito());
		mov.setTipoDomicilio(demandante.getTipoDomicilio());
		mov.setDomicilio(demandante.getDomicilio());
		mov.setReferencia(demandante.getReferencia());
		mov.setCorreo(demandante.getCorreo());
		mov.setCelular(demandante.getCelular());
		mov.setCasillaElectronica(demandante.getCasillaElectronica());
		mov.setApoderadoComun("1".equals(demandante.getApoderadoComun()) ? "1" : "0");
		mov.setArchivoUrl(demandante.getArchivoUrl());
		mov.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(demandante.getActivo())
				? Estado.ACTIVO_NUMERICO.getNombre()
				: Estado.INACTIVO_NUMERICO.getNombre());
	}

	private MovDemandado mapDemandadoToMovDemandado(Demandado demandado) {
		var mov = new MovDemandado();
		mov.setTipoDocumento(demandado.getTipoDocumento());
		mov.setNumeroDocumento(demandado.getNumeroDocumento());
		mov.setRazonSocial(demandado.getRazonSocial());
		mov.setDepartamento(demandado.getDepartamento());
		mov.setProvincia(demandado.getProvincia());
		mov.setDistrito(demandado.getDistrito());
		mov.setTipoDomicilio(demandado.getTipoDomicilio());
		mov.setDomicilio(demandado.getDomicilio());
		mov.setReferencia(demandado.getReferencia());
		mov.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(demandado.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		return mov;
	}

	private void actualizarMovDemandado(MovDemandado mov, Demandado demandado) {
		mov.setTipoDocumento(demandado.getTipoDocumento());
		mov.setNumeroDocumento(demandado.getNumeroDocumento());
		mov.setRazonSocial(demandado.getRazonSocial());
		mov.setDepartamento(demandado.getDepartamento());
		mov.setProvincia(demandado.getProvincia());
		mov.setDistrito(demandado.getDistrito());
		mov.setTipoDomicilio(demandado.getTipoDomicilio());
		mov.setDomicilio(demandado.getDomicilio());
		mov.setReferencia(demandado.getReferencia());
		mov.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(demandado.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
	}

	private MovPetitorio mapPetitorioToMov(Petitorio petitorio) throws Exception {
		var mov = new MovPetitorio();
		mov.setTipo(petitorio.getTipo());
		mov.setPretensionPrincipal(petitorio.getPretensionPrincipal());
		mov.setConcepto(petitorio.getConcepto());
		mov.setPretensionAccesoria(petitorio.getPretensionAccesoria());
		mov.setMonto(petitorio.getMonto());
		mov.setJustificacion(petitorio.getJustificacion());
		mov.setFechaInicio(
				ProjectUtils.parseStringToDate(petitorio.getFechaInicio(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setFechaFin(
				ProjectUtils.parseStringToDate(petitorio.getFechaFin(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(petitorio.getActivo())
				? Estado.ACTIVO_NUMERICO.getNombre()
				: Estado.INACTIVO_NUMERICO.getNombre());
		return mov;
	}
	
	private void actualizarMovPetitorio(MovPetitorio mov, Petitorio petitorio) throws Exception {
		mov.setTipo(petitorio.getTipo());
		mov.setPretensionPrincipal(petitorio.getPretensionPrincipal());
		mov.setConcepto(petitorio.getConcepto());
		mov.setPretensionAccesoria(petitorio.getPretensionAccesoria());
		mov.setMonto(petitorio.getMonto());
		mov.setJustificacion(petitorio.getJustificacion());
		mov.setFechaInicio(
				ProjectUtils.parseStringToDate(petitorio.getFechaInicio(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setFechaFin(
				ProjectUtils.parseStringToDate(petitorio.getFechaFin(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(petitorio.getActivo())
				? Estado.ACTIVO_NUMERICO.getNombre()
				: Estado.INACTIVO_NUMERICO.getNombre());
	}	
	
	private MovRelacionLaboral mapRelacionLaboralToMov(RelacionLaboral relacion) throws Exception {
		var mov = new MovRelacionLaboral();
		mov.setRegimen(relacion.getRegimen());
		mov.setFechaInicio(
				ProjectUtils.parseStringToDate(relacion.getFechaInicio(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setFechaFin(
				ProjectUtils.parseStringToDate(relacion.getFechaFin(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setAnios(relacion.getAnios());
		mov.setMeses(relacion.getMeses());
		mov.setDias(relacion.getDias());
		mov.setRemuneracion(relacion.getRemuneracion());
		mov.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(relacion.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
		return mov;
	}

	private void actualizarMovRelacionLaboral(MovRelacionLaboral mov, RelacionLaboral relacion) throws Exception {
		mov.setRegimen(relacion.getRegimen());
		mov.setFechaInicio(
				ProjectUtils.parseStringToDate(relacion.getFechaInicio(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setFechaFin(
				ProjectUtils.parseStringToDate(relacion.getFechaFin(), ProjectConstants.Formato.FECHA_DD_MM_YYYY));
		mov.setAnios(relacion.getAnios());
		mov.setMeses(relacion.getMeses());
		mov.setDias(relacion.getDias());
		mov.setRemuneracion(relacion.getRemuneracion());
		mov.setActivo(
				!Estado.INACTIVO_NUMERICO.getNombre().equals(relacion.getActivo()) ? Estado.ACTIVO_NUMERICO.getNombre()
						: Estado.INACTIVO_NUMERICO.getNombre());
	}
	
	private MovFundamentacion mapFundamentacionToMov(Fundamentacion fundamentacion) {
		var mov = new MovFundamentacion();
		mov.setXContenido(fundamentacion.getContenido());
		mov.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(fundamentacion.getActivo())
				? Estado.ACTIVO_NUMERICO.getNombre()
				: Estado.INACTIVO_NUMERICO.getNombre());
		return mov;
	}
	
	private void actualizarMovFundamentacion(MovFundamentacion mov, Fundamentacion fundamentacion) {
		mov.setXContenido(fundamentacion.getContenido());
		mov.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(fundamentacion.getActivo())
				? Estado.ACTIVO_NUMERICO.getNombre()
				: Estado.INACTIVO_NUMERICO.getNombre());
	}
	
	private MovFirma mapFirmaToMov(Firma firma) {
		var mov = new MovFirma();
		mov.setCTipo(firma.getTipo());
		mov.setXArchivoUrl(firma.getArchivoUrl());
		mov.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(firma.getActivo())
				? Estado.ACTIVO_NUMERICO.getNombre()
				: Estado.INACTIVO_NUMERICO.getNombre());
		return mov;
	}
	
	private void actualizarMovFirma(MovFirma mov, Firma firma) {
		mov.setCTipo(firma.getTipo());
		mov.setXArchivoUrl(firma.getArchivoUrl());
		mov.setActivo(!Estado.INACTIVO_NUMERICO.getNombre().equals(firma.getActivo())
				? Estado.ACTIVO_NUMERICO.getNombre()
				: Estado.INACTIVO_NUMERICO.getNombre());
	}
}
