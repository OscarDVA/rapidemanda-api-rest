package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import pe.gob.pj.rapidemanda.domain.model.servicio.Firma;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovFirma;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;

@Mapper(componentModel = "spring")
public interface FirmaEntityMapper {

    @Mapping(target = "NDemanda", source = "demanda.id")
    @Mapping(target = "tipo", source = "CTipo")
    @Mapping(target = "archivoUrl", source = "XArchivoUrl")
    @Mapping(target = "activo", source = "activo")
    Firma toModel(MovFirma entity);

    @Mapping(target = "demanda", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "CTipo", source = "tipo")
    @Mapping(target = "XArchivoUrl", source = "archivoUrl")
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    MovFirma toEntity(Firma model);

    @Mapping(target = "demanda", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "CTipo", source = "tipo")
    @Mapping(target = "XArchivoUrl", source = "archivoUrl")
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    void updateEntity(@MappingTarget MovFirma entity, Firma model);

    @Named("setDefaultActivo")
    default String setDefaultActivo(String activo) {
        return activo != null ? activo : Estado.ACTIVO_NUMERICO.getNombre();
    }
}