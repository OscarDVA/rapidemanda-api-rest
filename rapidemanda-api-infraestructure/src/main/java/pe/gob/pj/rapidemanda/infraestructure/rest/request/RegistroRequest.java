package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistroRequest {

	@JsonProperty(value = "formatoRespuesta")
	String formatoRespuesta;

	// Datos de persona
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

	@Length(min = 8, max = 15, message = "El parámetro numeroDocumento tiene un tamaño no válido (min=8,max=15).")
	@NotBlank(message = "El parámetro numeroDocumento no puede ser vacío.")
	@NotNull(message = "El parámetro numeroDocumento no puede ser nulo.")
	@Pattern(regexp = "^[0-9]+$", message = "El parámetro numeroDocumento debe contener solo números.")
	@JsonProperty("numeroDocumento")
	String numeroDocumento;

	@Length(min = 1, max = 1, message = "El parámetro sexo tiene un tamaño no válido (min=1,max=1).")
	@NotBlank(message = "El parámetro sexo no puede ser vacío.")
	@NotNull(message = "El parámetro sexo no puede ser nulo.")
	@Pattern(regexp = "^[MF]$", message = "El parámetro sexo debe ser M o F.")
	@JsonProperty("sexo")
	String sexo;

	@Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$", message = "El parámetro fechaNacimiento tiene un formato no válido (dd/MM/yyyy).")
	@NotBlank(message = "El parámetro fechaNacimiento no puede ser vacío.")
	@NotNull(message = "El parámetro fechaNacimiento no puede ser nulo.")
	@JsonProperty("fechaNacimiento")
	String fechaNacimiento;

	@Length(min = 9, max = 15, message = "El parámetro telefono tiene un tamaño no válido (min=9,max=15).")
	@NotBlank(message = "El parámetro telefono no puede ser vacío.")
	@NotNull(message = "El parámetro telefono no puede ser nulo.")
	@Pattern(regexp = "^[0-9]+$", message = "El parámetro telefono debe contener solo números.")
	@JsonProperty("telefono")
	String telefono;

	@Email(message = "El parámetro correo debe tener un formato válido.")
	@Length(min = 5, max = 100, message = "El parámetro correo tiene un tamaño no válido (min=5,max=100).")
	@NotBlank(message = "El parámetro correo no puede ser vacío.")
	@NotNull(message = "El parámetro correo no puede ser nulo.")
	@JsonProperty("correo")
	String correo;

	// Datos de usuario
	@Length(min = 4, max = 20, message = "El parámetro usuario tiene un tamaño no válido (min=4,max=20).")
	@NotBlank(message = "El parámetro usuario no puede ser vacío.")
	@NotNull(message = "El parámetro usuario no puede ser nulo.")
	@Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El parámetro usuario solo puede contener letras, números, puntos, guiones y guiones bajos.")
	@JsonProperty("usuario")
	String usuario;

	@Size(min = 8, max = 50, message = "El parámetro clave debe tener entre 8 y 50 caracteres.")
	@NotBlank(message = "El parámetro clave no puede ser vacío.")
	@NotNull(message = "El parámetro clave no puede ser nulo.")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "La clave debe contener al menos: una letra minúscula, una mayúscula, un número y un carácter especial.")
	@JsonProperty("clave")
	String clave;

	@Size(min = 8, max = 50, message = "El parámetro confirmarClave debe tener entre 8 y 50 caracteres.")
	@NotBlank(message = "El parámetro confirmarClave no puede ser vacío.")
	@NotNull(message = "El parámetro confirmarClave no puede ser nulo.")
	@JsonProperty("confirmarClave")
	String confirmarClave;

	// Validación de captcha
	@JsonProperty("aplicaCaptcha")
	String aplicaCaptcha;

	@JsonProperty("tokenCaptcha")
	String tokenCaptcha;

	// Estado por defecto
	@JsonProperty("activo")
	String activo = "1";

	@Valid
	@NotNull(message = "El auditoria es requerido no puede ser null")
	@JsonProperty("auditoria")
	AuditoriaRequest auditoria;

}