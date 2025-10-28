package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoVia;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoVia;

@Mapper(componentModel = "spring")
public interface TipoViaEntityMapper {

	TipoVia toModel(MaeTipoVia entity);
}