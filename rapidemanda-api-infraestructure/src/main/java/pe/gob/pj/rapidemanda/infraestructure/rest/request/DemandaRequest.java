package pe.gob.pj.rapidemanda.infraestructure.rest.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import pe.gob.pj.rapidemanda.domain.model.servicio.RelacionLaboral;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemandaRequest {
	@JsonProperty(value = "formatoRespuesta")
	String formatoRespuesta;

	@Length(min = 1, max = 5000, message = "El parámetro sumilla tiene un tamaño no válido (min=1,max=5000).")
	@NotBlank(message = "El parámetro sumilla no puede ser vacío.")
	@NotNull(message = "El parámetro sumilla no puede ser nulo.")
	@JsonProperty("sumilla")
	String sumilla;

	@Length(min = 1, max = 1, message = "El parámetro estadoDemanda tiene un tamaño no válido (min=1,max=1).")
	@NotBlank(message = "El parámetro estadoDemanda no puede ser vacío.")
	@NotNull(message = "El parámetro estadoDemanda no puede ser nulo.")
	@Pattern(regexp = "[BCP]", message = "El parámetro estadoDemanda solo permite valores B, C o P.")
	@JsonProperty("idEstadoDemanda")
	String idEstadoDemanda;

	@Length(min = 1, max = 1, message = "El parámetro tipoPresentacion tiene un tamaño no válido (min=1,max=1).")
	@NotBlank(message = "El parámetro tipoPresentacion no puede ser vacío.")
	@NotNull(message = "El parámetro tipoPresentacion no puede ser nulo.")
	@Pattern(regexp = "[MF]", message = "El parámetro tipoPresentacion solo permite valores M o F.")
	@JsonProperty("idTipoPresentacion")
	String idTipoPresentacion;

	@NotNull(message = "El ID de usuario no puede ser nulo")
	@JsonProperty("idUsuario")
	private Integer idUsuario;

	@JsonProperty("demandantes")
	List<DemandanteRequest> demandantes;

	@JsonProperty("demandados")
	List<DemandadoRequest> demandados;

	@JsonProperty("petitorios")
	List<PetitorioRequest> petitorios;

	@Valid
	// @NotNull(message = "La relación laboral es requerida")
	@JsonProperty("relacionLaboral")
	private RelacionLaboral relacionLaboral;

	@JsonProperty("fundamentaciones")
	List<FundamentacionRequest> fundamentaciones;

	@JsonProperty("firmas")
	List<FirmaRequest> firmas;

	@JsonProperty("pdfUrl")
	String pdfUrl;

	@JsonProperty("activo")
	String activo;

	@Valid
	@NotNull(message = "El auditoria es requerido no puede ser null")
	@JsonProperty("auditoria")
	AuditoriaRequest auditoria;
}
