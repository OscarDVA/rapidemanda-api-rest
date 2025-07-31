package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.servicio.Persona;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.PersonaRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PersonaMapper {

	Persona toPersona(PersonaRequest personaRequest);
	
}
