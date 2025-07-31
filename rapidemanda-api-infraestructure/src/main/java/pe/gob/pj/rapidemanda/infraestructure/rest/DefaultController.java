package pe.gob.pj.rapidemanda.infraestructure.rest;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.enums.Errors;
import pe.gob.pj.rapidemanda.domain.enums.Proceso;
import pe.gob.pj.rapidemanda.domain.exceptions.ErrorException;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectProperties;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.domain.utils.SecurityConstants;
import pe.gob.pj.rapidemanda.infraestructure.enums.Claim;
import pe.gob.pj.rapidemanda.infraestructure.enums.FormatoRespuesta;
import pe.gob.pj.rapidemanda.infraestructure.rest.response.GlobalResponse;

@Slf4j
@RestController
public class DefaultController implements Default, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@GetMapping(value = "/healthcheck")
	public ResponseEntity<Object> healthcheck(String cuo, String formatoRespuesta) {
		GlobalResponse res = new GlobalResponse();
		try {
			res.setCodigoOperacion(cuo);
			res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
			res.setDescripcion("Versión actual de aplicativo");
			Map<String, String> healthcheck = new HashMap<String, String>();
			healthcheck.put("Aplicativo", ProjectConstants.Aplicativo.NOMBRE);
			healthcheck.put("Estado", "Disponible");
			healthcheck.put("Versión", ProjectConstants.Aplicativo.VERSION);
			res.setData(healthcheck);
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(
							Errors.ERROR_INESPERADO.getCodigo(), 
							String.format(Errors.ERROR_INESPERADO.getNombre(), Proceso.HEALTHCHECK.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(
				FormatoRespuesta.JSON.getNombre().equalsIgnoreCase(formatoRespuesta) ? MediaType.APPLICATION_JSON_VALUE
						: MediaType.APPLICATION_XML_VALUE));
		return new ResponseEntity<>(res, headers, HttpStatus.OK);

	}

	@SuppressWarnings("unchecked")
	@GetMapping(value = "/seguridad/refresh")
	public ResponseEntity<GlobalResponse> refreshToken(String cuo, String ipRemota, String token) {
		GlobalResponse res = new GlobalResponse();
		res.setCodigoOperacion(cuo);
		try {
			byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();

			Map<String, String> dataToken = new HashMap<String, String>();
			try {
				String jwt = token.replace("Bearer ", "");
				Jws<Claims> parsedToken = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);
				List<String> roles = (List<String>) parsedToken.getBody().get(Claim.ROLES.getNombre());
				String ipRemotaToken = parsedToken.getBody().get(Claim.IP_REALIZA_PETICION.getNombre()).toString();
				String rolSeleccionado = parsedToken.getBody().get(Claim.ROL_SELECCIONADO.getNombre()).toString();
				String usuario = parsedToken.getBody().get(Claim.USUARIO_REALIZA_PETICION.getNombre()).toString();
				String subject = parsedToken.getBody().getSubject();

				Date ahora = new Date();

				int tiempoSegundosExpira = ProjectProperties.getSeguridadTiempoExpiraSegundos();
				int tiempoSegundosRefresh = ProjectProperties.getSeguridadTiempoRefreshSegundos();

				Date limiteExpira = parsedToken.getBody().getExpiration();
				Date limiteRefresh = ProjectUtils.sumarRestarSegundos(limiteExpira, tiempoSegundosRefresh);

				if (ipRemota.equals(ipRemotaToken)) {
					if (!ahora.after(limiteRefresh)) {
						String tokenResult = Jwts.builder()
								.signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
								.setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
								.setIssuer(SecurityConstants.TOKEN_ISSUER).setAudience(SecurityConstants.TOKEN_AUDIENCE)
								.setSubject(subject)
								.setExpiration(ProjectUtils.sumarRestarSegundos(ahora, tiempoSegundosExpira))
								.claim(Claim.ROLES.getNombre(), roles)
								.claim(Claim.ROL_SELECCIONADO.getNombre(), rolSeleccionado)
								.claim(Claim.USUARIO_REALIZA_PETICION.getNombre(), usuario)
								.claim(Claim.IP_REALIZA_PETICION.getNombre(), ipRemota)
								.claim(Claim.LIMITE_TOKEN.getNombre(), ProjectUtils.sumarRestarSegundos(ahora,
										tiempoSegundosExpira + tiempoSegundosRefresh))
								.compact();

						res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
						res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
						dataToken.put("token", tokenResult);
						res.setData(dataToken);
						return new ResponseEntity<>(res, HttpStatus.OK);
					} else {
						res.setCodigo(Errors.ERROR_REFRESCAR_TOKEN_TIEMPO_EXPIRADO.getCodigo());
						res.setDescripcion(Errors.ERROR_REFRESCAR_TOKEN_TIEMPO_EXPIRADO.getNombre());
						return new ResponseEntity<>(res, HttpStatus.OK);
					}
				} else {
					return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
				}
			} catch (ExpiredJwtException e) {
				List<String> roles = (List<String>) e.getClaims().get(Claim.ROLES.getNombre());
				String rolSeleccionado = e.getClaims().get(Claim.ROL_SELECCIONADO.getNombre()).toString();
				String usuario = e.getClaims().get(Claim.USUARIO_REALIZA_PETICION.getNombre()).toString();
				String ipRemotaToken = e.getClaims().get(Claim.IP_REALIZA_PETICION.getNombre()).toString();
				String subject = e.getClaims().getSubject();

				Date ahora = new Date();

				int tiempoSegundosExpira = ProjectProperties.getSeguridadTiempoExpiraSegundos();
				int tiempoSegundosRefresh = ProjectProperties.getSeguridadTiempoRefreshSegundos();

				Date limiteExpira = e.getClaims().getExpiration();
				Date limiteRefresh = ProjectUtils.sumarRestarSegundos(limiteExpira, tiempoSegundosRefresh);

				if (ipRemota.equals(ipRemotaToken)) {
					if (!ahora.after(limiteRefresh)) {
						String tokenResult = Jwts.builder()
								.signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
								.setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
								.setIssuer(SecurityConstants.TOKEN_ISSUER).setAudience(SecurityConstants.TOKEN_AUDIENCE)
								.setSubject(subject)
								.setExpiration(ProjectUtils.sumarRestarSegundos(ahora, tiempoSegundosExpira))
								.claim(Claim.ROLES.getNombre(), roles)
								.claim(Claim.ROL_SELECCIONADO.getNombre(), rolSeleccionado)
								.claim(Claim.USUARIO_REALIZA_PETICION.getNombre(), usuario)
								.claim(Claim.IP_REALIZA_PETICION.getNombre(), ipRemota)
								.claim(Claim.LIMITE_TOKEN.getNombre(), ProjectUtils.sumarRestarSegundos(ahora,
										tiempoSegundosExpira + tiempoSegundosRefresh))
								.compact();
						res.setCodigo(Errors.OPERACION_EXITOSA.getCodigo());
						res.setDescripcion(Errors.OPERACION_EXITOSA.getNombre());
						dataToken.put("token", tokenResult);
						res.setData(dataToken);
						return new ResponseEntity<>(res, HttpStatus.OK);
					} else {
						res.setCodigo(Errors.ERROR_REFRESCAR_TOKEN_TIEMPO_EXPIRADO.getCodigo());
						res.setDescripcion(Errors.ERROR_REFRESCAR_TOKEN_TIEMPO_EXPIRADO.getNombre());
						return new ResponseEntity<>(res, HttpStatus.OK);
					}
				} else {
					log.warn(
							"{} No se ha encontrado coincidencias válidas del token anterior o se ha excedido el tiempo limite para refrescar token.",
							cuo);
					return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
				}
			}
		} catch (Exception e) {
			handleException(cuo,
					new ErrorException(
							Errors.ERROR_TOKEN_NO_VALIDO.getCodigo(), 
							String.format(Errors.ERROR_TOKEN_NO_VALIDO.getNombre(), Proceso.REFRESH.getNombre()),
							e.getMessage(), e.getCause()),
					res);
		}
		return new ResponseEntity<>(res, HttpStatus.UNAUTHORIZED);
	}

}
