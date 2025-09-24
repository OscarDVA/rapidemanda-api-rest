package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import pe.gob.pj.rapidemanda.domain.model.servicio.Demandado;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandado;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;

@Mapper(componentModel = "spring")
public interface DemandadoEntityMapper {

    @Mapping(target = "NDemanda", source = "demanda.id")
    @Mapping(target = "activo", source = "activo")
    Demandado toModel(MovDemandado entity);

    @Mapping(target = "demanda", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    MovDemandado toEntity(Demandado model);

    @Mapping(target = "demanda", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    void updateEntity(@MappingTarget MovDemandado entity, Demandado model);

    @Named("setDefaultActivo")
    default String setDefaultActivo(String activo) {
        return activo != null ? activo : Estado.ACTIVO_NUMERICO.getNombre();
    }
}