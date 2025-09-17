package pe.gob.pj.rapidemanda.infraestructure.mapper;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import pe.gob.pj.rapidemanda.domain.model.servicio.PerfilUsuario;
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.ActualizarUsuarioRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.UsuarioRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMapper {
	
    @Mapping(target = "persona.id", source = "idPersona")
    @Mapping(target = "perfiles", source = "perfiles", qualifiedByName = "mapPerfiles")
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "idUsuario", ignore = true)
    
    Usuario toUsuario(UsuarioRequest usuarioRequest);
    
    @Mapping(target = "persona.id", source = "idPersona")
    @Mapping(target = "perfiles", source = "perfiles", qualifiedByName = "mapPerfiles")
    @Mapping(target = "token", ignore = true)
    @Mapping(target = "idUsuario", ignore = true)
    @Mapping(target = "clave", ignore = true)
    Usuario toUsuarioFromActualizar(ActualizarUsuarioRequest actualizarUsuarioRequest);
    
    @Named("mapPerfiles")
    default List<PerfilUsuario> mapPerfiles(List<Integer> perfilesIds) {
        List<PerfilUsuario> perfiles = new ArrayList<>();
        if (perfilesIds != null) {
            for (Integer perfilId : perfilesIds) {
                // Crear un PerfilUsuario temporal solo con el ID del perfil
                // Los demás datos se completarán en la capa de persistencia
                PerfilUsuario perfilUsuario = new PerfilUsuario(null, perfilId, null, null);
                perfiles.add(perfilUsuario);
            }
        }
        return perfiles;
    }
	
}
