package pe.gob.pj.rapidemanda.infraestructure.mapper;

import org.mapstruct.*;
import pe.gob.pj.rapidemanda.domain.model.servicio.Demandante;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio.MovDemandante;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;

import java.util.Date;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    imports = {ProjectUtils.class, ProjectConstants.class, Estado.class}
)
public interface DemandanteEntityMapper {

    @Mapping(target = "NDemanda", source = "demanda.id")
    @Mapping(target = "fechaNacimiento", source = "fechaNacimiento", qualifiedByName = "dateToString")
    @Mapping(target = "apoderadoComun", source = "apoderadoComun")
    @Mapping(target = "activo", source = "activo")
    Demandante toModel(MovDemandante entity);


    @Mapping(target = "demanda", ignore = true) // Se establece manualmente en el adaptador
    @Mapping(target = "fechaNacimiento", source = "fechaNacimiento", qualifiedByName = "stringToDate")
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    MovDemandante toEntity(Demandante model);


    @Mapping(target = "id", ignore = true) // No actualizar el ID
    @Mapping(target = "demanda", ignore = true) // No actualizar la relación
    @Mapping(target = "fechaNacimiento", source = "fechaNacimiento", qualifiedByName = "stringToDate")
    @Mapping(target = "activo", source = "activo", qualifiedByName = "setDefaultActivo")
    void updateEntity(@MappingTarget MovDemandante entity, Demandante model);


    @Named("dateToString")
    default String dateToString(Date fecha) {
        return ProjectUtils.convertDateToString(fecha, ProjectConstants.Formato.FECHA_DD_MM_YYYY);
    }

    @Named("stringToDate")
    default Date stringToDate(String fechaString) {
        if (fechaString == null || fechaString.trim().isEmpty()) {
            return null;
        }
        try {
            return ProjectUtils.parseStringToDate(fechaString, ProjectConstants.Formato.FECHA_DD_MM_YYYY);
        } catch (Exception e) {
            return null;
        }
    }

    @Named("setDefaultActivo")
    default String setDefaultActivo(String activo) {
        return activo != null ? activo : Estado.ACTIVO_NUMERICO.getNombre();
    }
}