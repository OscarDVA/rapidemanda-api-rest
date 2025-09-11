package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.RegistroRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RegistroMapper {

	@Mapping(target = "persona.idTipoDocumento", source = "idTipoDocumento")
	@Mapping(target = "persona.nombres", source = "nombres")
	@Mapping(target = "persona.primerApellido", source = "primerApellido")
	@Mapping(target = "persona.segundoApellido", source = "segundoApellido")
	@Mapping(target = "persona.numeroDocumento", source = "numeroDocumento")
	@Mapping(target = "persona.telefono", source = "telefono")
	@Mapping(target = "persona.correo", source = "correo")
	@Mapping(target = "persona.sexo", source = "sexo")
	@Mapping(target = "persona.fechaNacimiento", source = "fechaNacimiento")
	@Mapping(target = "usuario", source = "usuario")
	@Mapping(target = "clave", source = "clave")
	@Mapping(target = "activo", source = "activo")
	@Mapping(target = "idUsuario", ignore = true)
	@Mapping(target = "persona.id", ignore = true)
	@Mapping(target = "persona.tipoDocumento", ignore = true)
	@Mapping(target = "perfiles", ignore = true)
	@Mapping(target = "token", ignore = true)
	Usuario toRegistro(RegistroRequest registroRequest);
	
}
