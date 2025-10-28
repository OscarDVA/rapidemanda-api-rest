package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoRegimen;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoRegimen;

@Mapper(componentModel = "spring")
public interface TipoRegimenEntityMapper {

	TipoRegimen toModel(MaeTipoRegimen entity);
}