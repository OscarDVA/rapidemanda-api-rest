package pe.gob.pj.rapidemanda.infraestructure.rest.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RelacionLaboralRequest {
	
	   @Length(min = 2, max = 50, message = "El parámetro regimen tiene un tamaño no válido (min=2,max=50).")
	    @NotBlank(message = "El parámetro regimen no puede ser vacío.")
	    @NotNull(message = "El parámetro regimen no puede ser nulo.")
	    @JsonProperty("regimen")
	    String regimen;
	   
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
	    
	    @PositiveOrZero(message = "Los años deben ser un número positivo o cero")
	    Integer anios;

	    @PositiveOrZero(message = "Los meses deben ser un número positivo o cero")
	    Integer meses;

	    @PositiveOrZero(message = "Los días deben ser un número positivo o cero")
	    Integer dias;
	    	    
	    @Pattern(regexp = ProjectConstants.Pattern.DECIMAL, message = "El parámetro remuneración tiene un formato no válido. Debe ser un número con hasta 2 decimales.")
	    @Length(min = 1, max = 10, message = "El parámetro remuneracion tiene un tamaño no válido (min=1,max=10).")
	    @NotBlank(message = "El parámetro remuneracion no puede ser vacío.")
	    @NotNull(message = "El parámetro remuneracion no puede ser nulo.")
	    @JsonProperty("remuneracion")
	    String remuneracion;
}
