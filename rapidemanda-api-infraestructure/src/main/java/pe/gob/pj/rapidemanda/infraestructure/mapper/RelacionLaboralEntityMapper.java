package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import pe.gob.pj.rapidemanda.domain.model.servicio.RelacionLaboral;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovRelacionLaboral;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface RelacionLaboralEntityMapper {

    @Mapping(target = "NDemanda", source = "demanda.id")
    @Mapping(target = "fechaInicio", source = "fechaInicio", qualifiedByName = "dateToString")
    @Mapping(target = "fechaFin", source = "fechaFin", qualifiedByName = "dateToString")
    @Mapping(target = "activo", source = "activo")
    RelacionLaboral toModel(MovRelacionLaboral entity);

    @Mapping(target = "demanda", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaInicio", source = "fechaInicio", qualifiedByName = "stringToDate")
    @Mapping(target = "fechaFin", source = "fechaFin", qualifiedByName = "stringToDate")
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    MovRelacionLaboral toEntity(RelacionLaboral model);

    @Mapping(target = "demanda", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaInicio", source = "fechaInicio", qualifiedByName = "stringToDate")
    @Mapping(target = "fechaFin", source = "fechaFin", qualifiedByName = "stringToDate")
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    void updateEntity(@MappingTarget MovRelacionLaboral entity, RelacionLaboral model);

    @Named("dateToString")
    default String dateToString(Date date) {
        return ProjectUtils.convertDateToString(date, ProjectConstants.Formato.FECHA_DD_MM_YYYY);
    }

    @Named("stringToDate")
    default Date stringToDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return ProjectUtils.parseStringToDate(dateString, ProjectConstants.Formato.FECHA_DD_MM_YYYY);
        } catch (Exception e) {
            return null;
        }
    }

    @Named("setDefaultActivo")
    default String setDefaultActivo(String activo) {
        return activo != null ? activo : Estado.ACTIVO_NUMERICO.getNombre();
    }
}