package pe.gob.pj.rapidemanda.infraestructure.rest.exceptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;

@Slf4j
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * Error handle for @Validate For Validating Path Variables and Request Parameters
	 */
	@ExceptionHandler(value = { ConstraintViolationException.class })
    protected ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
		String cuo = String.valueOf(request.getAttribute(ProjectConstants.AUD_CUO, WebRequest.SCOPE_REQUEST));
		Map<String, Object> body = new LinkedHashMap<>();
		
		Map<String, String> errors = new HashMap<>();
		StringBuilder errorsString = new StringBuilder();
        ex.getConstraintViolations().forEach(violation -> {
        	String fieldName = violation.getPropertyPath().toString();
			String errorMessage = violation.getMessage();
			String valor = (violation.getInvalidValue() == null) ? "null" : violation.getInvalidValue().toString();
			errorsString.append((errors.size()+1) + " : " + errorMessage +" ");
			errorsString.append("\n");
			errors.put(fieldName + " (" + valor + ")", errorMessage);
        });
        body.put("codigo", HttpStatus.BAD_REQUEST);
		body.put("descripcion", errorsString);
		body.put("codigoOperacion", cuo);
		body.put("data", null);
		log.error("{} {} {}",cuo,errorsString,errors.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", ")));
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.OK);//400 - HttpStatus.BAD_REQUEST
    }
	
	@Override
	protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
			HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String cuo = String.valueOf(request.getAttribute(ProjectConstants.AUD_CUO, WebRequest.SCOPE_REQUEST));
		Map<String, Object> body = new LinkedHashMap<>();
		
		StringBuilder builder = new StringBuilder();
		if(!ProjectUtils.isNullOrEmpty(ex.getContentType())) {
			builder.append("El formato de solicitud ");
			builder.append(ex.getContentType());
			builder.append(" no es compatible.");
		}else {
			builder.append("No se mandaron los datos requeridos relacionados al cuerpo de la solicitud. ");
		}
		
		List<MediaType> mediaTypes = ex.getSupportedMediaTypes();

		body.put("codigo", status.value());
		body.put("descripcion", builder.toString());
		body.put("codigoOperacion", cuo);
		body.put("data", null);
		log.error("{} {} La URI: {} solo acepta formato {}",cuo, builder.toString(),((ServletWebRequest)request).getRequest().getRequestURI().toString(),mediaTypes);
		return new ResponseEntity<>(body, headers, HttpStatus.OK);//415 - HttpStatus.UNSUPPORTED_MEDIA_TYPE
	}

	/**
	 * Error handle for @Valid
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String cuo = String.valueOf(request.getAttribute(ProjectConstants.AUD_CUO, WebRequest.SCOPE_REQUEST));
		Map<String, Object> body = new LinkedHashMap<>();

		Map<String, String> errors = new HashMap<>();
		StringBuilder errorsString = new StringBuilder();
		errorsString.append("Error en la validación de los parámetros. \n");
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = ((FieldError) error).getDefaultMessage();
			String valor = (((FieldError) error).getRejectedValue() == null) ? "null" : ((FieldError) error).getRejectedValue().toString();
			errorsString.append((errors.size()+1) + " : " + errorMessage +" ");
			errorsString.append("\n");
			errors.put(fieldName + " (" + valor + ")", errorMessage);
		});
		
		body.put("codigo", status.value());
		body.put("descripcion", errorsString);
		body.put("codigoOperacion", cuo);
		body.put("data", null);
		log.error("{} {} {} ",cuo,errorsString,errors.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", ")));
		return new ResponseEntity<>(body, headers, HttpStatus.OK);//400 - HttpStatus.BAD_REQUEST
	}

	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		String cuo = String.valueOf(request.getAttribute(ProjectConstants.AUD_CUO, WebRequest.SCOPE_REQUEST));
		Map<String, Object> body = new LinkedHashMap<>();
		StringBuilder builder = new StringBuilder();
		builder.append("El método de solicitud ");
		builder.append(ex.getMethod());
		builder.append(" no es compatible. ");

		body.put("codigo", status.value());
		body.put("descripcion", builder.toString());
		body.put("codigoOperacion", cuo);
		body.put("data", null);
		log.error("{} {} La URI: {} solo acepta {}",cuo, builder.toString(),((ServletWebRequest)request).getRequest().getRequestURI().toString(),Arrays.toString(ex.getSupportedMethods()));
		return new ResponseEntity<>(body, headers, HttpStatus.OK);//405 - HttpStatus.METHOD_NOT_ALLOWED
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,HttpStatusCode status, WebRequest request) {
		String cuo = String.valueOf(request.getAttribute(ProjectConstants.AUD_CUO, WebRequest.SCOPE_REQUEST));
		String error = "No se ha encontrado ningun controlador para  " + ex.getHttpMethod() + " " + ex.getRequestURL();
		Map<String, Object> body = new LinkedHashMap<>();

		Map<String, String> errors = new HashMap<>();
		errors.put("motivo", error);
		errors.put("mensaje", ex.getLocalizedMessage());
//		body.put("error", errors);
		
		body.put("codigo",HttpStatus.NOT_FOUND);
		body.put("descripcion", errors);
		body.put("codigoOperacion", cuo);
		body.put("data", null);
		log.error("{} {}",cuo , errors.entrySet().stream().map(entry -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", ")));
		return new ResponseEntity<Object>(body, new HttpHeaders(), HttpStatus.OK); //404 - HttpStatus.NOT_FOUND
	}

	@ExceptionHandler({Exception.class})
	public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
		String cuo = String.valueOf(request.getAttribute(ProjectConstants.AUD_CUO, WebRequest.SCOPE_REQUEST));
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("codigo", HttpStatus.INTERNAL_SERVER_ERROR.toString().substring(0, 4));
		StringBuilder builder = new StringBuilder();
		builder.append("Ocurrió un error no controlado. Error : ");
		builder.append(ex);
		body.put("descripcion", "Ocurrió un error no controlado.");
		body.put("codigoOperacion", cuo);
		body.put("data", null);
		log.error("{} {} {}",cuo,builder.toString(), ProjectUtils.convertExceptionToString(ex));
		return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.OK); //500 - HttpStatus.INTERNAL_SERVER_ERROR
	}
}