package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import pe.gob.pj.rapidemanda.domain.model.servicio.Fundamentacion;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovFundamentacion;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;

@Mapper(componentModel = "spring")
public interface FundamentacionEntityMapper {

    @Mapping(target = "NDemanda", source = "demanda.id")
    @Mapping(target = "contenido", source = "XContenido")
    @Mapping(target = "activo", source = "activo")
    Fundamentacion toModel(MovFundamentacion entity);

    @Mapping(target = "demanda", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "XContenido", source = "contenido")
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    MovFundamentacion toEntity(Fundamentacion model);

    @Mapping(target = "demanda", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "XContenido", source = "contenido")
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    void updateEntity(@MappingTarget MovFundamentacion entity, Fundamentacion model);

    @Named("setDefaultActivo")
    default String setDefaultActivo(String activo) {
        return activo != null ? activo : Estado.ACTIVO_NUMERICO.getNombre();
    }
}