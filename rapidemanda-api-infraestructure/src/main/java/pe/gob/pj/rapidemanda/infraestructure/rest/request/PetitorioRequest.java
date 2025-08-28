package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PetitorioRequest {
	
	@NotBlank(message = "El parámetro tipo no puede ser vacío.")
	@NotNull(message = "El parámetro tipo no puede ser nulo.")
	@JsonProperty("tipo")
	String tipo;
	
	@NotBlank(message = "El parámetro pretensionPrincipal no puede ser vacío.")
	@NotNull(message = "El parámetro pretensionPrincipal no puede ser nulo.")
	@JsonProperty("pretensionPrincipal")
	String pretensionPrincipal;
	
	
	@NotBlank(message = "El parámetro concepto no puede ser vacío.")
	@NotNull(message = "El parámetro concepto no puede ser nulo.")
	@JsonProperty("concepto")
	String concepto;
	
	
	@NotBlank(message = "El parámetro pretensionAccesoria no puede ser vacío.")
	@NotNull(message = "El parámetro pretensionAccesoria no puede ser nulo.")
	@JsonProperty("pretensionAccesoria")
	String pretensionAccesoria;
	
	@NotNull(message = "El parámetro monto no puede ser nulo.")
	@JsonProperty("monto")
	Double monto;
	
	@NotBlank(message = "El parámetro justificacion no puede ser vacío.")
	@NotNull(message = "El parámetro justificacion no puede ser nulo.")
	@JsonProperty("justificacion")
	String justificacion;
	
	@Pattern(regexp = ProjectConstants.Pattern.FECHA, message = "El parámetro fechaInicio tiene un formato no válido(dd/MM/yyyy).")
	@NotBlank(message = "El parámetro fechaInicio no puede ser vacío.")
	@NotNull(message = "El parámetro fechaInicio no puede ser nulo.")
	@JsonProperty("fechaInicio")
	String fechaInicio;
	
	@Pattern(regexp = ProjectConstants.Pattern.FECHA, message = "El parámetro fechaFin tiene un formato no válido(dd/MM/yyyy).")
	@NotBlank(message = "El parámetro fechaFin no puede ser vacío.")
	@NotNull(message = "El parámetro fechaFin no puede ser nulo.")
	@JsonProperty("fechaFin")
	String fechaFin;
	
	@Valid
	@NotNull(message = "El auditoria es requerido no puede ser null")
	@JsonProperty("auditoria")
	AuditoriaRequest auditoria;

}
