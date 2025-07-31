package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemandadoRequest {

	@NotBlank(message = "El parámetro tipoDocumento no puede ser vacío.")
	@NotNull(message = "El parámetro tipoDocumento no puede ser nulo.")
	@JsonProperty("tipoDocumento")
	String tipoDocumento;

	@Pattern(regexp = ProjectConstants.Pattern.NUMBER, message = "El parámetro numeroDocumento tiene un formato no válido.")
	@Length(min = 8, max = 20, message = "El parámetro numeroDocumento tiene un tamaño no válido (min=8,max=20).")
	@NotBlank(message = "El parámetro numeroDocumento no puede ser vacío.")
	@NotNull(message = "El parámetro numeroDocumento no puede ser nulo.")
	@JsonProperty("numeroDocumento")
	String numeroDocumento;

	@Length(min = 2, max = 200, message = "El parámetro razonSocial tiene un tamaño no válido (min=2,max=200).")
	@NotBlank(message = "El parámetro razonSocial no puede ser vacío.")
	@NotNull(message = "El parámetro razonSocial no puede ser nulo.")
	@JsonProperty("razonSocial")
	String razonSocial;

	@Length(max = 50, message = "El departamento no puede exceder los 50 caracteres")
	String departamento;

	@Length(max = 50, message = "La provincia no puede exceder los 50 caracteres")
	String provincia;

	@Length(max = 50, message = "El distrito no puede exceder los 50 caracteres")
	String distrito;

	@Length(min = 1, max = 50, message = "El parámetro tipoDomicilio tiene un tamaño no válido (min=1,max=50).")
	@NotBlank(message = "El parámetro tipoDomicilio no puede ser vacío.")
	@NotNull(message = "El parámetro tipoDomicilio no puede ser nulo.")
	@JsonProperty("tipoDomicilio")
	String tipoDomicilio;

	@Length(min = 1, max = 200, message = "El parámetro domicilio tiene un tamaño no válido (min=1,max=200).")
	@NotBlank(message = "El parámetro direccion no puede ser vacío.")
	@NotNull(message = "El parámetro direccion no puede ser nulo.")
	@JsonProperty("domicilio")
	String domicilio;

	@Length(min = 1, max = 200, message = "El parámetro referencia tiene un tamaño no válido (min=1,max=200).")
	@NotBlank(message = "El parámetro referencia no puede ser vacío.")
	@NotNull(message = "El parámetro referencia no puede ser nulo.")
	@JsonProperty("referencia")
	String referencia;
	
    @Valid
    @NotNull(message = "El auditoria es requerido no puede ser null")
    @JsonProperty("auditoria")
    AuditoriaRequest auditoria;
}
