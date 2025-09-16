package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.UsuarioRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMapper {
	
    @Mapping(target = "persona.id", source = "idPersona")
    
    @Mapping(target = "perfiles", ignore = true)
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "idUsuario", ignore = true)
    
    Usuario toUsuario(UsuarioRequest usuarioRequest);
	
}
