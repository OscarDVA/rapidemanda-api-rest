package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoAnexo;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoAnexo;

@Mapper(componentModel = "spring")
public interface TipoAnexoEntityMapper {

    TipoAnexo toModel(MaeTipoAnexo entity);
}