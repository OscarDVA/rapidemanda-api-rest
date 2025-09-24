package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.*;
import pe.gob.pj.rapidemanda.domain.model.servicio.Anexo;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovAnexo;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {Estado.class}
)
public interface AnexoEntityMapper {

    @Mapping(target = "NDemanda", source = "demanda.id")
    @Mapping(target = "activo", source = "activo")
    Anexo toModel(MovAnexo entity);

    @Mapping(target = "demanda", ignore = true) // Se establece manualmente en el adaptador
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    MovAnexo toEntity(Anexo model);

    @Mapping(target = "id", ignore = true) // No actualizar el ID
    @Mapping(target = "demanda", ignore = true) // No actualizar la relación
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    void updateEntity(@MappingTarget MovAnexo entity, Anexo model);

    @Named("setDefaultActivo")
    default String setDefaultActivo(String activo) {
        return activo != null ? activo : Estado.ACTIVO_NUMERICO.getNombre();
    }
}