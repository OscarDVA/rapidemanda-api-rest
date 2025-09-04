package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.servicio.Distrito;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.DistritoRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DistritoMapper {
	Distrito toModel(DistritoRequest request);
}
