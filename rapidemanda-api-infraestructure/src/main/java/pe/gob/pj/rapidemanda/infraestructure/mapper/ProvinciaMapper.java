package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.servicio.Provincia;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.ProvinciaRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProvinciaMapper {
	Provincia toModel(ProvinciaRequest request);
}
