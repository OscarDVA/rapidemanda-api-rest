package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemandanteRequest {
	@Length(min = 1, max = 20, message = "El parámetro tipoDocumento tiene un tamaño no válido (min=1,max=20).")
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

	@Length(min = 1, max = 1, message = "El parámetro genero tiene un tamaño no válido (min=1,max=1).")
	@NotBlank(message = "El parámetro genero no puede ser vacío.")
	@NotNull(message = "El parámetro genero no puede ser nulo.")
	@Pattern(regexp = "[MF]", message = "El parámetro genero solo permite valores M o F.")
	@JsonProperty("genero")
	String genero;

	@Pattern(regexp = ProjectConstants.Pattern.FECHA, message = "El parámetro fechaNacimiento tiene un formato no válido(dd/MM/yyyy).")
	@NotBlank(message = "El parámetro fechaNacimiento no puede ser vacío.")
	@NotNull(message = "El parámetro fechaNacimiento no puede ser nulo.")
	@JsonProperty("fechaNacimiento")
	String fechaNacimiento;

	@Size(max = 50, message = "El departamento no puede exceder los 50 caracteres")
	String departamento;

	@Size(max = 50, message = "La provincia no puede exceder los 50 caracteres")
	String provincia;

	@Size(max = 50, message = "El distrito no puede exceder los 50 caracteres")
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

	@Pattern(regexp = ProjectConstants.Pattern.EMAIL, message = "El parámetro correo tiene un formato no válido.")
	@Length(min = 1, max = 100, message = "El parámetro correo tiene un tamaño no válido (min=1,max=100).")
	@NotBlank(message = "El parámetro correo no puede ser vacío.")
	@NotNull(message = "El parámetro correo no puede ser nulo.")
	@JsonProperty("correo")
	String correo;

	@Pattern(regexp = ProjectConstants.Pattern.CELULAR, message = "El parámetro celular tiene un formato no válido.")
	@Length(min = 6, max = 15, message = "El parámetro celular tiene un tamaño no válido (min=6,max=15).")
	@NotBlank(message = "El parámetro celular no puede ser vacío.")
	@NotNull(message = "El parámetro celular no puede ser nulo.")
	@JsonProperty("celular")
	String celular;

	@Length(min = 1, max = 100, message = "El parámetro casillaElectronica tiene un tamaño no válido (min=1,max=100).")
	@NotBlank(message = "El parámetro casillaElectronica no puede ser vacío.")
	@NotNull(message = "El parámetro casillaElectronica no puede ser nulo.")
	@JsonProperty("casillaElectronica")
	String casillaElectronica;

	@Length(min = 1, max = 1, message = "El parámetro apoderadoComun tiene un tamaño no válido (min=1,max=1).")
	@NotBlank(message = "El parámetro apoderadoComun no puede ser vacío.")
	@NotNull(message = "El parámetro apoderadoComun no puede ser nulo.")
	@Pattern(regexp = "[SN]", message = "El parámetro apoderadoComun solo permite valores S o N.")
	@JsonProperty("apoderadoComun")
	String apoderadoComun;

	@Length(min = 1, max = 100, message = "El parámetro archivoUrl tiene un tamaño no válido (min=1,max=100).")
	@NotBlank(message = "El parámetro archivoUrl no puede ser vacío.")
	@NotNull(message = "El parámetro archivoUrl no puede ser nulo.")
	@JsonProperty("archivoUrl")
	String archivoUrl;

	@Valid
	@NotNull(message = "El auditoria es requerido no puede ser null")
	@JsonProperty("auditoria")
	AuditoriaRequest auditoria;

}
