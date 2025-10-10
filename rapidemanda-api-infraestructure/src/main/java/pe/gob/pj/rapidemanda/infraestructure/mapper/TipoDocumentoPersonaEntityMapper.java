package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoDocumentoPersona;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoDocumentoPersona;

@Mapper(componentModel = "spring")
public interface TipoDocumentoPersonaEntityMapper {

    TipoDocumentoPersona toModel(MaeTipoDocumentoPersona entity);
}