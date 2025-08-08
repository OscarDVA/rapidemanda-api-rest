package pe.gob.pj.rapidemanda.infraestructure.mapper;

import java.util.List;

import org.mapstruct.*;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.servicio.Demanda;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demandado;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demandante;
import pe.gob.pj.rapidemanda.domain.model.servicio.RelacionLaboral;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandaRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandadoRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DemandanteRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.RelacionLaboralRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DemandaMapper {

	Demanda toDemanda(DemandaRequest demandaRequest);

	List<Demandante> toDemandantes(List<DemandanteRequest> demandantes);

	List<Demandado> toDemandados(List<DemandadoRequest> demandados);
	
	@Mapping(target = "NDemanda", ignore = true)
	Demandante toDemandante(DemandanteRequest demandanteRequest);

	@Mapping(target = "NDemanda", ignore = true)
	Demandado toDemandado(DemandadoRequest demandadoRequest);

	@Mapping(target = "NDemanda", ignore = true)
    RelacionLaboral toRelacionLaboral(RelacionLaboralRequest relacionLaboralRequest);
}
