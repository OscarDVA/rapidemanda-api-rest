package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import pe.gob.pj.rapidemanda.domain.model.servicio.TipoPresentacionDemanda;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MaeTipoPresentacion;

@Mapper(componentModel = "spring")
public interface TipoPresentacionDemandaEntityMapper {
    @Mapping(target = "id", source = "BTipoPresentacion")
    @Mapping(target = "nombre", source = "XTipo")
    TipoPresentacionDemanda toModel(MaeTipoPresentacion entity);
}