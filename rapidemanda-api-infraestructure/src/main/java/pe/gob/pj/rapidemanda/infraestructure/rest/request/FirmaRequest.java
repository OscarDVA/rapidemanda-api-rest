package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FirmaRequest {

	@NotBlank(message = "El parámetro tipo no puede ser vacío.")
	@NotNull(message = "El parámetro tipo no puede ser nulo.")
	@JsonProperty("tipo")
	String tipo;

	@NotBlank(message = "El parámetro archivoUrl no puede ser vacío.")
	@NotNull(message = "El parámetro archivoUrl no puede ser nulo.")
	@JsonProperty("archivoUrl")
	String archivoUrl;

	@Valid
	@NotNull(message = "El auditoria es requerido no puede ser null")
	@JsonProperty("auditoria")
	AuditoriaRequest auditoria;
}
