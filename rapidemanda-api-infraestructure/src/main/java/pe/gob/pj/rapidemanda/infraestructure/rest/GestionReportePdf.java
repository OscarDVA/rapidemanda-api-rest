package pe.gob.pj.rapidemanda.infraestructure.rest;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
@RestController
@RequestMapping(value = "reportes", produces = { MediaType.APPLICATION_PDF_VALUE })
public interface GestionReportePdf extends Base {

	/**
	 * GET /reportes/demanda/pdf : Generar reporte PDF de una demanda
	 *
	 * @param cuo          Código único de operación para auditoría
	 * @param ips          IP del servidor
	 * @param usuauth      Usuario autenticado
	 * @param uri          URI de la petición
	 * @param params       Parámetros de la petición
	 * @param herramienta  Herramienta utilizada
	 * @param ip           IP del cliente
	 * @param idDemanda    ID de la demanda a reportar
	 * @return ResponseEntity con el PDF generado
	 */
	@GetMapping(value = "/demanda/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generarReporteDemandaPdf(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IPS) String ips,
			@RequestAttribute(name = ProjectConstants.AUD_USUARIO) String usuauth,
			@RequestAttribute(name = ProjectConstants.AUD_URI) String uri,
			@RequestAttribute(name = ProjectConstants.AUD_PARAMS) String params,
			@RequestAttribute(name = ProjectConstants.AUD_HERRAMIENTA) String herramienta,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestParam("id") Integer idDemanda);
}