package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoSexo;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoSexo;

@Mapper(componentModel = "spring")
public interface TipoSexoEntityMapper {
	TipoSexo toModel(MaeTipoSexo entity);
}