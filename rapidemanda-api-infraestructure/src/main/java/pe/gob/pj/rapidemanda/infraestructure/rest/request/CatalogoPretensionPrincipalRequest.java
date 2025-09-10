package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CatalogoPretensionPrincipalRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	Integer id;

	@JsonProperty("formatoRespuesta")
	String formatoRespuesta;

	@NotBlank
	@NotNull
	@JsonProperty("nombre")
	String nombre;

	@NotBlank
	@NotNull
	@JsonProperty("petitorioId")
	Integer petitorioId;

	@NotNull
	@NotBlank
	@JsonProperty("tieneConceptos")
	String tieneConceptos;

	@JsonProperty("activo")
	String activo;

	@Valid
	@NotNull
	@JsonProperty("auditoria")
	AuditoriaRequest auditoria;
}
