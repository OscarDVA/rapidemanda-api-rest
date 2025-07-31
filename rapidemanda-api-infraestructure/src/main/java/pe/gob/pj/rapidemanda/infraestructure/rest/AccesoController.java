package pe.gob.pj.rapidemanda.infraestructure.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.model.servicio.PerfilOpcions;
import pe.gob.pj.rapidemanda.domain.model.servicio.Usuario;
import pe.gob.pj.rapidemanda.domain.port.usecase.AccesoUseCasePort;
import pe.gob.pj.rapidemanda.domain.utils.CaptchaUtils;
import pe.gob.pj.rapidemanda.domain.utils.ProjectProperties;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.domain.utils.SecurityConstants;
import pe.gob.pj.rapidemanda.infraestructure.enums.Claim;
import pe.gob.pj.rapidemanda.infraestructure.enums.Estado;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.LoginRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.request.ObtenerOpcionesRequest;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccesoController implements Acceso {

	@Qualifier("accesoUseCasePort")
	final AccesoUseCasePort accesoUC;

	@Override
	public ResponseEntity<GlobalResponse> iniciarSesion(String cuo, String ip, String jwt, @Valid LoginRequest request) {

		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			if (!request.getAplicaCaptcha().equalsIgnoreCase(Estado.ACTIVO_LETRA.getNombre())
					|| (request.getAplicaCaptcha().equalsIgnoreCase(Estado.ACTIVO_LETRA.getNombre())
							&& !ProjectUtils.isNullOrEmpty(request.getTokenCaptcha()))) {
				if (!request.getAplicaCaptcha().equalsIgnoreCase(Estado.ACTIVO_LETRA.getNombre())
						|| CaptchaUtils.validCaptcha(request.getTokenCaptcha(), ip, cuo)) {
					Usuario usuario = accesoUC.iniciarSesion(cuo, request.getUsuario(), request.getClave());
					List<String> rolesUsuario = usuario.getPerfiles().stream().map(perfilDTO -> perfilDTO.getRol())
							.collect(Collectors.toList());
					res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
					res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());

					String token = getNewToken(jwt, request.getUsuario(), rolesUsuario, ip, cuo, true);
					if (!ProjectUtils.isNullOrEmpty(token)) {
						usuario.setToken(token);
						res.setData(usuario);
					} else {
						res.setCodigo(Errors.ERROR_TOKEN_NO_VALIDO.getCodigo());
						res.setDescripcion(Errors.ERROR_TOKEN_NO_VALIDO.getNombre());
					}
				} else {
					log.error(
							"{} Datos de validación captcha -> indicador de validación: {}, token captcha: {} y la ip de la petición",
							cuo, request.getAplicaCaptcha(), request.getTokenCaptcha(), ip);
					throw new ErrorException(Errors.ERROR_TOKEN_CAPTCHA.getCodigo(), 
							String.format(Errors.ERROR_TOKEN_CAPTCHA.getNombre(), Proceso.INICIAR_SESION.getNombre()));
				}
			} else {
				log.error(
						"{} Datos de validación captcha -> indicador de validación: {}, token captcha: {} y la ip de la petición",
						cuo, request.getAplicaCaptcha(), request.getTokenCaptcha(), ip);
				throw new ErrorException(Errors.ERROR_TOKEN_CAPTCHA.getCodigo(), 
						String.format(Errors.ERROR_TOKEN_CAPTCHA.getNombre(), Proceso.INICIAR_SESION.getNombre()));
			}
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(
							Errors.ERROR_INESPERADO.getCodigo(), 
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.INICIAR_SESION.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType(FormatoRespuesta.XML.getNombre().equalsIgnoreCase(request.getFormatoRespuesta()) ? MediaType.APPLICATION_XML_VALUE : MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<GlobalResponse> obtenerOpciones(String cuo, String ip, String jwt,
			@Valid ObtenerOpcionesRequest request) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {

			PerfilOpcions perfilOpciones = accesoUC.obtenerOpciones(cuo, request.getIdPerfil());
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());

			Map<String, Object> map = new HashMap<String, Object>();

			List<String> rolesPerfil = Arrays.asList(perfilOpciones.getRol());

			String token = getNewToken(jwt, request.getUsuario(), rolesPerfil, ip, cuo, true);
			if (!ProjectUtils.isNullOrEmpty(token)) {
				map.put("opciones", perfilOpciones);
				map.put("token", token);
				res.setData(map);
			} else {
				res.setCodigo(Errors.ERROR_TOKEN_NO_VALIDO.getCodigo());
				res.setDescripcion(Errors.ERROR_TOKEN_NO_VALIDO.getNombre());
			}
		} catch (ErrorException e) {
			handleException(cuo, e, res);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(
							Errors.ERROR_INESPERADO.getCodigo(), 
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.ELEGIR_PERFIL.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.parseMediaType(FormatoRespuesta.XML.getNombre().equalsIgnoreCase(request.getFormatoRespuesta()) ? MediaType.APPLICATION_XML_VALUE : MediaType.APPLICATION_JSON_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);
	}

	private String getNewToken(String token, String usuario, List<String> rolesUsuario, String ipRemota, String cuo,
			boolean seleccionaRol) {
		String newToken = "";
		StringBuilder rolSeleccionado = new StringBuilder();
		try {
			byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
			try {
				Jws<Claims> parsedToken = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token);
				List<String> roles = new ArrayList<>();

				@SuppressWarnings("unchecked")
				List<String> rolesToken = (List<String>) parsedToken.getBody().get(Claim.ROLES.getNombre());
				if (seleccionaRol) {
					rolSeleccionado.append(rolesUsuario.get(0));
					if (rolesToken.stream().filter(x -> x.equals(rolSeleccionado.toString())).count() < 1)
						return newToken;
					else
						rolesToken = rolesUsuario;
				} else {
					rolSeleccionado.append(parsedToken.getBody().get(Claim.ROL_SELECCIONADO.getNombre()));
				}
				roles = rolesToken;

				String ipRemotaToken = parsedToken.getBody().get(Claim.IP_REALIZA_PETICION.getNombre()).toString();
				String subject = parsedToken.getBody().getSubject();

				Date ahora = new Date();

				int tiempoSegundosExpira = ProjectProperties.getSeguridadTiempoExpiraSegundos();
				int tiempoSegundosRefresh = ProjectProperties.getSeguridadTiempoRefreshSegundos();

				if (ipRemota.equals(ipRemotaToken) && (!seleccionaRol || (seleccionaRol && !roles.isEmpty()))) {
					newToken = Jwts.builder().signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
							.setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
							.setIssuer(SecurityConstants.TOKEN_ISSUER).setAudience(SecurityConstants.TOKEN_AUDIENCE)
							.setSubject(subject)
							.setExpiration(ProjectUtils.sumarRestarSegundos(ahora, tiempoSegundosExpira))
							.claim(Claim.ROLES.getNombre(), roles)
							.claim(Claim.ROL_SELECCIONADO.getNombre(), rolSeleccionado.toString())
							.claim(Claim.USUARIO_REALIZA_PETICION.getNombre(), usuario)
							.claim(Claim.IP_REALIZA_PETICION.getNombre(), ipRemota)
							.claim(Claim.LIMITE_TOKEN.getNombre(), ProjectUtils.sumarRestarSegundos(ahora,
									tiempoSegundosExpira + tiempoSegundosRefresh))
							.compact();
				}
			} catch (ExpiredJwtException e) {
				List<String> roles = rolesUsuario;
				String ipRemotaToken = e.getClaims().get(Claim.IP_REALIZA_PETICION.getNombre()).toString();
				String subject = e.getClaims().getSubject();
				Integer tiempoToken = ProjectProperties.getSeguridadTiempoExpiraSegundos();
				if (ipRemota.equals(ipRemotaToken)) {
					newToken = Jwts.builder().signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
							.setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
							.setIssuer(SecurityConstants.TOKEN_ISSUER).setAudience(SecurityConstants.TOKEN_AUDIENCE)
							.setSubject(subject).setExpiration(new Date(System.currentTimeMillis() + tiempoToken))
							.claim(Claim.ROLES.getNombre(), roles)
							.claim(Claim.ROL_SELECCIONADO.getNombre(), rolSeleccionado.toString())
							.claim(Claim.USUARIO_REALIZA_PETICION.getNombre(), usuario)
							.claim(Claim.IP_REALIZA_PETICION.getNombre(), ipRemota)
							.compact();
				}
			}
		} catch (Exception e) {
			log.error("{} error al intentar generar nuevo Token: {}", cuo,
					ProjectUtils.isNull(e.getCause()).concat(e.getMessage()));
		}
		return newToken;
	}

}
