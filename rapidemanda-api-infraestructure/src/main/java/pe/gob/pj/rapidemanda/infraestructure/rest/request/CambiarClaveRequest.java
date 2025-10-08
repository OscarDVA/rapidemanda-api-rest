package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CambiarClaveRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("formatoRespuesta")
    String formatoRespuesta;

    @NotBlank(message = "El parámetro claveActual no puede tener un valor vacío.")
    @NotNull(message = "El parámetro claveActual no puede tener un valor nulo.")
    @JsonProperty("claveActual")
    String claveActual;

    @NotBlank(message = "El parámetro nuevaClave no puede tener un valor vacío.")
    @NotNull(message = "El parámetro nuevaClave no puede tener un valor nulo.")
    @JsonProperty("nuevaClave")
    String nuevaClave;
}