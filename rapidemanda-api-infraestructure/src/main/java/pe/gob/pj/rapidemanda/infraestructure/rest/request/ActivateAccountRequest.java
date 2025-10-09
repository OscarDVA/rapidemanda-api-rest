package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateAccountRequest {
	
	@JsonProperty(value = "formatoRespuesta")
	String formatoRespuesta;
	
    @NotNull
    private Integer idUsuario;

    @NotNull
    private Long exp;

    @NotBlank
    private String sig;

}