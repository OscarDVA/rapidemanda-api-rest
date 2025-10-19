package pe.gob.pj.rapidemanda.infraestructure.mapper;

import java.util.List;

import org.mapstruct.*;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demandado;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demandante;
import pe.gob.pj.rapidemanda.domain.model.servicio.Firma;
import pe.gob.pj.rapidemanda.domain.model.servicio.Fundamentacion;
import pe.gob.pj.rapidemanda.domain.model.servicio.Petitorio;
import pe.gob.pj.rapidemanda.domain.model.servicio.RelacionLaboral;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandaRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandadoRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandanteRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.FirmaRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.FundamentacionRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.PetitorioRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.RelacionLaboralRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DemandaMapper {

	List<Demandante> toDemandantes(List<DemandanteRequest> demandantes);

	List<Demandado> toDemandados(List<DemandadoRequest> demandados);
	
	List<Petitorio> toPetitorios(List<PetitorioRequest> petitorios);
	
	List<Fundamentacion> toFundamentaciones(List<FundamentacionRequest> fundamentaciones);
	
	List<Firma> toFirmas(List<FirmaRequest> firmas);
	
	@Mapping(target = "NDemanda", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activo", ignore = true)
	Demandante toDemandante(DemandanteRequest demandanteRequest);

	@Mapping(target = "NDemanda", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activo", ignore = true)
	Demandado toDemandado(DemandadoRequest demandadoRequest);
	
	@Mapping(target = "NDemanda", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activo", ignore = true)
	Petitorio toPetitorio(PetitorioRequest petitorioRequest);

	@Mapping(target = "NDemanda", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activo", ignore = true)
    RelacionLaboral toRelacionLaboral(RelacionLaboralRequest relacionLaboralRequest);
	
	@Mapping(target = "NDemanda", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activo", ignore = true)
	Fundamentacion toFundamentacion(FundamentacionRequest fundamentacionRequest);
	
	@Mapping(target = "NDemanda", ignore = true)
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "activo", ignore = true)
	Firma toFirma(FirmaRequest firmaRequest);
	
	// Método principal para convertir DemandaRequest a Demanda
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "idEstadoDemanda", ignore = true)
	@Mapping(target = "estadoDemanda", ignore = true)
	@Mapping(target = "tipoPresentacion", ignore = true)
	@Mapping(target = "idUsuarioRecepcion", ignore = true)
	@Mapping(target = "usuarioDemanda", ignore = true)
	@Mapping(target = "usuarioRecepcion", ignore = true)
	@Mapping(target = "fechaRecepcion", ignore = true)
	@Mapping(target = "fechaCompletado", ignore = true)
	@Mapping(target = "demandantes", source = "demandantes")
	@Mapping(target = "demandados", source = "demandados")
	@Mapping(target = "petitorios", source = "petitorios")
	@Mapping(target = "relacionLaboral", source = "relacionLaboral")
	@Mapping(target = "fundamentaciones", source = "fundamentaciones")
	@Mapping(target = "firmas", source = "firmas")
	Demanda toDemanda(DemandaRequest request);
}
