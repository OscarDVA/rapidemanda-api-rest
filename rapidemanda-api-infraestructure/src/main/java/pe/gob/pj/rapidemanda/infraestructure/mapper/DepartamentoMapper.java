package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.servicio.Departamento;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DepartamentoRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DepartamentoMapper {
	Departamento toModel(DepartamentoRequest request);
}
