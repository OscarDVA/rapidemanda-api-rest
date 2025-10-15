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
public class PersonaRequest {

	@JsonProperty(value = "formatoRespuesta")
	String formatoRespuesta;
	
	@Length(min = 1, max = 1, message = "El parámetro idTipoDocumento tiene un tamaño no válido (min=1,max=1).")
	@NotNull(message = "El parámetro idTipoDocumento no puede ser nulo.")
	@JsonProperty("idTipoDocumento")
	String idTipoDocumento;
	
	@Length(min = 2, max = 60, message = "El parámetro nombres tiene un tamaño no válido (min=2,max=60).")
	@NotBlank(message = "El parámetro nombres no puede ser vacío.")
	@NotNull(message = "El parámetro nombres no puede ser nulo.")
	@JsonProperty("nombres")
	String nombres;
	
	@Length(min = 2, max = 60, message = "El parámetro primerApellido tiene un tamaño no válido (min=2,max=60).")
	@NotBlank(message = "El parámetro primerApellido no puede ser vacío.")
	@NotNull(message = "El parámetro primerApellido no puede ser nulo.")
	@JsonProperty("primerApellido")
	String primerApellido;
	
	@Length(min = 2, max = 60, message = "El parámetro segundoApellido tiene un tamaño no válido (min=2,max=60).")
	@NotBlank(message = "El parámetro segundoApellido no puede ser vacío.")
	@NotNull(message = "El parámetro segundoApellido no puede ser nulo.")
	@JsonProperty("segundoApellido")
	String segundoApellido;
	
	@Pattern(regexp = ProjectConstants.Pattern.FECHA, message = "El parámetro fechaNacimiento tiene un formato no válido(dd/MM/yyyy)." )
	@NotBlank(message = "El parámetro fechaNacimiento no puede ser vacío.")
	@NotNull(message = "El parámetro fechaNacimiento no puede ser nulo.")
	@JsonProperty("fechaNacimiento")
	String fechaNacimiento;
	
	
	@Length(min = 8, max = 20, message = "El parámetro numeroDocumento tiene un tamaño no válido (min=8,max=20).")
	@NotBlank(message = "El parámetro numeroDocumento no puede ser vacío.")
	@NotNull(message = "El parámetro numeroDocumento no puede ser nulo.")
	@JsonProperty("numeroDocumento")
	String numeroDocumento;
	
	@Length(min = 1, max = 1, message = "El parámetro sexo tiene un tamaño no válido (min=1,max=1).")
	@NotBlank(message = "El parámetro sexo no puede ser vacío.")
	@NotNull(message = "El parámetro sexo no puede ser nulo.")
	@Pattern(regexp = ProjectConstants.Pattern.SEXO, message = "El parámetro sexo no es válido.")
	@JsonProperty("sexo")
	String sexo;
	
	@Pattern(regexp = ProjectConstants.Pattern.CELULAR, message = "El parámetro telefono tiene un formato no válido.")
	@Length(min = 6, max = 15, message = "El parámetro telefono tiene un tamaño no válido (min=6,max=15).")
	@NotBlank(message = "El parámetro telefono no puede ser vacío.")
	@NotNull(message = "El parámetro telefono no puede ser nulo.")
	@JsonProperty("telefono")
	String telefono;
	
	@Pattern(regexp = ProjectConstants.Pattern.EMAIL, message = "El parámetro email tiene un formato no válido.")
	@Length(min = 3, max = 50, message = "El parámetro correo tiene un tamaño no válido (min=3,max=50).")
	@NotBlank(message = "El parámetro correo no puede ser vacío.")
	@NotNull(message = "El parámetro correo no puede ser nulo.")
	@JsonProperty("correo")
	String correo;
	
	@JsonProperty("activo")
	String activo;

	@Valid
	@NotNull(message = "El auditoria es requerido no puede ser null")
	@JsonProperty("auditoria")
	AuditoriaRequest auditoria;
	
}
