package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.UsuarioRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UsuarioMapper {

	@Mapping(target = "persona.idTipoDocumento", source = "idTipoDocumento")
	@Mapping(target = "persona.nombres", source = "nombres")
	@Mapping(target = "persona.primerApellido", source = "primerApellido")
	@Mapping(target = "persona.segundoApellido", source = "segundoApellido")
	@Mapping(target = "persona.fechaNacimiento", source = "fechaNacimiento")
	@Mapping(target = "persona.numeroDocumento", source = "numeroDocumento")
	@Mapping(target = "persona.sexo", source = "sexo")
	@Mapping(target = "persona.telefono", source = "telefono")
	@Mapping(target = "persona.correo", source = "correo")
	@Mapping(target = "usuario", source = "usuario")
	@Mapping(target = "clave", source = "password")
	Usuario toUsuario(UsuarioRequest usuarioRequest);
}
