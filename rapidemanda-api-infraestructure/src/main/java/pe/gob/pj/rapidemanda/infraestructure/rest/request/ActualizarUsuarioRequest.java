package pe.gob.pj.rapidemanda.infraestructure.rest.request;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActualizarUsuarioRequest {

	@JsonProperty(value = "formatoRespuesta")
    String formatoRespuesta;
    
    @Length(min = 8, max = 15, message = "El parámetro usuario tiene un tamaño no válido (min=4,max=15).")
    @NotBlank(message = "El parámetro usuario no puede ser vacío.")
    @NotNull(message = "El parámetro usuario no puede ser nulo.")
    @JsonProperty("usuario")
    String usuario;
    
    // Campo clave removido para actualización - no se debe enviar por seguridad
      
    @NotNull(message = "El parámetro idPersona no puede ser nulo.")
    @JsonProperty("idPersona")
    Integer idPersona;
    
    @JsonProperty("activo")
    String activo;
    
    @JsonProperty("perfiles")
    List<Integer> perfiles;

    @Valid
    @NotNull(message = "El auditoria es requerido no puede ser null")
    @JsonProperty("auditoria")
    AuditoriaRequest auditoria;
}