package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import pe.gob.pj.rapidemanda.domain.model.auditoriageneral.AuditoriaAplicativos;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.auditoriageneral.MovAuditoriaAplicativosEntity;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.AuditoriaRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuditoriaGeneralMapper {

	@Mapping(target = "usuarioAplicativo", source = "auditoriaRequest.usuario")
	@Mapping(target = "usuario", source = "auditoriaRequest.usuario")
	@Mapping(target = "nombrePc", source = "auditoriaRequest.nombrePc")
	@Mapping(target = "numeroIp", source = "auditoriaRequest.numeroIp")
	@Mapping(target = "direccionMac", source = "auditoriaRequest.direccionMac")
	@Mapping(target = "fechaRegistro", expression = "java(new java.util.Date())")
	@Mapping(target = "codigoUnicoOperacion", source = "cuo")
	@Mapping(target = "ips", source = "ips")
	@Mapping(target = "usuarioAuth", source = "usuauth")
	@Mapping(target = "uri", source = "uri")
	@Mapping(target = "peticionUrl", source = "peticionUrl")
	@Mapping(target = "herramientaConsume", source = "herramientaConsume")
	@Mapping(target = "codigoRespuesta", source = "codigoRespuesta")
	@Mapping(target = "descripcionRespuesta", source = "descripcionRespuesta")
	@Mapping(target = "duracionRespuesta", source = "tiempo")
	AuditoriaAplicativos toAuditoriaAplicativos(AuditoriaRequest auditoriaRequest, String cuo, String ips, String usuauth,
			String uri, String peticionUrl, String herramientaConsume, String codigoRespuesta,
			String descripcionRespuesta, long tiempo);
	
	@Mapping(target = "CAudId", source = "usuarioAuth")
	MovAuditoriaAplicativosEntity toMovAuditoriaAplicativos(AuditoriaAplicativos auditoriaAplicativos);

}
