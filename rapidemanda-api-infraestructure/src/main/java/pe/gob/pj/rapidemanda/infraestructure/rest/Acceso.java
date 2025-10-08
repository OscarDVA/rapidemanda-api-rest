package pe.gob.pj.rapidemanda.infraestructure.rest;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.LoginRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.ObtenerOpcionesRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.RegistroRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.CambiarClaveRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.ForgotPasswordRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.ResetPasswordRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@RequestMapping(value = "authenticate", produces = MediaType.APPLICATION_JSON_VALUE)
public interface Acceso extends Base{

	/***
	 * 
	 * POST /authenticate/login : Iniciar sesión
	 * 
	 * @param cuo
	 * @param ip
	 * @param jwt
	 * @param login
	 * @return
	 */
	@PostMapping(value = "login")
	public ResponseEntity<GlobalResponse> iniciarSesion(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestAttribute(name = ProjectConstants.AUD_JWT) String jwt, 
			@Valid @RequestBody LoginRequest login);
	
	/***
	 * 
	 * POST /authenticate/opciones : Obtener las opciones del perfil enviado
	 * 
	 * @param cuo
	 * @param ip
	 * @param jwt
	 * @param perfil
	 * @return
	 */
	@PostMapping(value = "opciones")
	public ResponseEntity<GlobalResponse> obtenerOpciones(
			@RequestAttribute(name=ProjectConstants.AUD_CUO) String cuo, 
			@RequestAttribute(name=ProjectConstants.AUD_IP) String ip, 
			@RequestAttribute(name=ProjectConstants.AUD_JWT) String jwt,
			@Valid @RequestBody ObtenerOpcionesRequest perfil);
	
	/***
	 * 
	 * POST /authenticate/registrar : Registrar nuevo usuario
	 * 
	 * @param cuo
	 * @param ip
	 * @param jwt
	 * @param registro
	 * @return
	 */
	@PostMapping(value = "registrar")
	public ResponseEntity<GlobalResponse> registrarUsuario(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestAttribute(name = ProjectConstants.AUD_JWT) String jwt, 
			@Valid @RequestBody RegistroRequest registro);

	/***
	 * 
	 * POST /authenticate/cambiar-clave : Cambiar la clave del usuario autenticado
	 * 
	 * @param cuo
	 * @param ip
	 * @param jwt
	 * @param request
	 * @return
	 */
	@PostMapping(value = "cambiar-clave")
	public ResponseEntity<GlobalResponse> cambiarClave(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestAttribute(name = ProjectConstants.AUD_JWT) String jwt,
			@Valid @RequestBody CambiarClaveRequest request);

	/***
	 * 
	 * POST /authenticate/solicitar-reset : Solicita envío de enlace de restablecimiento de clave
	 * 
	 * @param cuo
	 * @param ip
	 * @param jwt
	 * @param request
	 * @return
	 */
	@PostMapping(value = "solicitar-reset")
	public ResponseEntity<GlobalResponse> solicitarReset(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestAttribute(name = ProjectConstants.AUD_JWT) String jwt,
			@Valid @RequestBody ForgotPasswordRequest request);

	/***
	 * 
	 * POST /authenticate/restablecer-clave : Restablece la clave usando el token recibido
	 * 
	 * @param cuo
	 * @param ip
	 * @param jwt
	 * @param request
	 * @return
	 */
	@PostMapping(value = "restablecer-clave")
	public ResponseEntity<GlobalResponse> restablecerClave(
			@RequestAttribute(name = ProjectConstants.AUD_CUO) String cuo,
			@RequestAttribute(name = ProjectConstants.AUD_IP) String ip,
			@RequestAttribute(name = ProjectConstants.AUD_JWT) String jwt,
			@Valid @RequestBody ResetPasswordRequest request);

}
