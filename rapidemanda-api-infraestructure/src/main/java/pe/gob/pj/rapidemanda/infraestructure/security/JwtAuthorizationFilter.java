package pe.gob.pj.rapidemanda.infraestructure.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.port.usecase.SeguridadUseCasePort;
import pe.gob.pj.rapidemanda.domain.utils.ProjectConstants;
import pe.gob.pj.rapidemanda.domain.utils.ProjectProperties;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.domain.utils.SecurityConstants;
import pe.gob.pj.rapidemanda.infraestructure.enums.Claim;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	@Getter @Setter
	private SeguridadUseCasePort seguridadService;

	public JwtAuthorizationFilter(AuthenticationManager authenticationManager, SeguridadUseCasePort servicio) {
		super(authenticationManager);
		this.setSeguridadService(servicio);
	}

	/**
	* Descripción : filtra las peticiones HTTP y evalua el token
	* @param HttpServletRequest request - peticion HTTP
	* @param HttpServletResponse response, - respuesta HTTP    
	* @param FilterChain filterChain - cadenas filtro 
	* @return void 
	* @exception Captura excepcion generica
	*/
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		request.setAttribute(ProjectConstants.AUD_CUO, ProjectUtils.obtenerCodigoUnico());
		request.setAttribute(ProjectConstants.AUD_IP, !ProjectUtils.isNullOrEmpty(request.getRemoteAddr()) ? request.getRemoteAddr() : request.getRemoteHost());
		request.setAttribute(ProjectConstants.AUD_USUARIO, ProjectConstants.Caracter.VACIO);
		request.setAttribute(ProjectConstants.AUD_URI, request.getRequestURI());
		request.setAttribute(ProjectConstants.AUD_PARAMS, Objects.nonNull(request.getQueryString())?request.getQueryString():ProjectConstants.Caracter.VACIO);
		request.setAttribute(ProjectConstants.AUD_HERRAMIENTA, request.getHeader("User-Agent"));
		StringBuilder ips = new StringBuilder();
		ips.append(request.getRemoteAddr());
		ips.append(Objects.nonNull(request.getRemoteHost()) && !ips.toString().contains(request.getRemoteHost())?"|"+request.getRemoteHost():ProjectConstants.Caracter.VACIO);
		ips.append(Objects.nonNull(request.getHeader("X-Forwarded-For")) && !ips.toString().contains(request.getHeader("X-Forwarded-For"))?"|"+request.getHeader("X-Forwarded-For"):ProjectConstants.Caracter.VACIO);
		ips.append(Objects.nonNull(request.getHeader("X-Real-IP")) && !ips.toString().contains(request.getHeader("X-Real-IP"))?"|"+request.getHeader("X-Real-IP"):ProjectConstants.Caracter.VACIO);
		ips.append(Objects.nonNull(request.getHeader("Proxy-Client-IP")) && !ips.toString().contains(request.getHeader("Proxy-Client-IP"))?"|"+request.getHeader("Proxy-Client-IP"):ProjectConstants.Caracter.VACIO);
		ips.append(Objects.nonNull(request.getHeader("WL-Proxy-Client-IP")) && !ips.toString().contains(request.getHeader("WL-Proxy-Client-IP"))?"|"+request.getHeader("WL-Proxy-Client-IP"):ProjectConstants.Caracter.VACIO);
		ips.append(Objects.nonNull(request.getHeader("HTTP_CLIENT_IP")) && !ips.toString().contains(request.getHeader("HTTP_CLIENT_IP"))?"|"+request.getHeader("HTTP_CLIENT_IP"):ProjectConstants.Caracter.VACIO);
		ips.append(Objects.nonNull(request.getHeader("HTTP_X_FORWARDED_FOR")) && !ips.toString().contains(request.getHeader("HTTP_X_FORWARDED_FOR"))?"|"+request.getHeader("HTTP_X_FORWARDED_FOR"):ProjectConstants.Caracter.VACIO);
		request.setAttribute(ProjectConstants.AUD_IPS, ips.toString());
		UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
		if (authentication == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			filterChain.doFilter(request, response);
			return;
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);
		filterChain.doFilter(request, response);
	}

	/**
	* Descripción : obtiene la autenticacion desde token
	* @param HttpServletRequest request - peticion HTTP
	* @return UsernamePasswordAuthenticationToken - Informacion de autenticacion proveniente token 
	* @exception Captura excepcion generica
	*/
	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

		String urlReq = request.getRequestURI();
		String metodo = request.getMethod();
		if(Arrays.stream(ProjectConstants.METHOD_CORTA_ULTIMA_BARRA_INVERTIDA).anyMatch(method -> method.equalsIgnoreCase(metodo))) {
			urlReq = urlReq.substring(0, urlReq.lastIndexOf("/"));//corta el id que se manda en la url
		}
		String token = request.getHeader(SecurityConstants.TOKEN_HEADER);
		String remoteIp = request.getAttribute(ProjectConstants.AUD_IP).toString();
		String cuo = request.getAttribute(ProjectConstants.AUD_CUO).toString();
		byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
		if (!ProjectUtils.isNullOrEmpty(token) && token.startsWith(SecurityConstants.TOKEN_PREFIX)) {
			try {
				String jwt = token.replace("Bearer ", "");
				request.setAttribute(ProjectConstants.AUD_JWT, jwt);
				Jws<Claims> parsedToken = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);
				
				String username = parsedToken.getBody().getSubject();
				request.setAttribute(ProjectConstants.AUD_USUARIO, username);
				
				String rolSeleccionado = (String) parsedToken.getBody().get(Claim.ROL_SELECCIONADO.getNombre());

				List<SimpleGrantedAuthority> authorities = ((List<?>) parsedToken.getBody().get(Claim.ROLES.getNombre())).stream()
						.map(authority -> new SimpleGrantedAuthority((String) authority)).collect(Collectors.toList());
				
				String ipRemotaDeToken = parsedToken.getBody().get(Claim.IP_REALIZA_PETICION.getNombre()).toString();
				
				int tiempoSegundosRefresh = ProjectProperties.getSeguridadTiempoRefreshSegundos();
				
				Date ahora = new Date();
				Date limiteExpira = parsedToken.getBody().getExpiration();
				Date limiteRefresh = ProjectUtils.sumarRestarSegundos(limiteExpira, tiempoSegundosRefresh);

				String tieneAcceso = "";
				if (!urlReq.endsWith("refresh")) {
					tieneAcceso = seguridadService.validarAccesoMetodo(cuo, username, rolSeleccionado, urlReq);
					if (ProjectUtils.isNullOrEmpty(tieneAcceso)) {
						log.warn("{} El usuario [{}] con rol [{}], no tiene acceso al método [{}] ", cuo, username, rolSeleccionado, urlReq);
						return null;
					}
				}

				if (!remoteIp.equals(ipRemotaDeToken)) {
					log.warn("{} La ip que generó el token {} no coincide con la ip desde donde se consume el método {}.",cuo,ipRemotaDeToken,remoteIp);
					return null;
				}

				if (ahora.after(limiteRefresh)) {
					log.warn("{} El token [{}] a superado tiempo de expiración [{}] y refresh [{}].",cuo,ahora,limiteExpira,limiteRefresh);
					return null;
				}

				if (!ProjectUtils.isNullOrEmpty(username)) {
					return new UsernamePasswordAuthenticationToken(username, null, authorities);
				}
				
			} catch (ExpiredJwtException exception) {
				String ipRemotaToken = exception.getClaims().get(Claim.IP_REALIZA_PETICION.getNombre()).toString();
				String subject = exception.getClaims().getSubject();	
				
				if (urlReq.endsWith("refresh") && remoteIp.equals(ipRemotaToken)) { // && !ahora.after(limiteRefresh)
					List<SimpleGrantedAuthority> authorities = ((List<?>) exception.getClaims().get(Claim.ROLES.getNombre())).stream()
							.map(authority -> new SimpleGrantedAuthority((String) authority))
							.collect(Collectors.toList());
					return new UsernamePasswordAuthenticationToken(subject, null, authorities);
				}
				log.warn(cuo + " Request to parse expired JWT : {} failed : {}", token, exception.getMessage());
			} catch (UnsupportedJwtException exception) {
				log.warn(cuo + " Request to parse unsupported JWT : {} failed : {}", token, exception.getMessage());
			} catch (MalformedJwtException exception) {
				log.warn(cuo + " Request to parse invalid JWT : {} failed : {}", token, exception.getMessage());
			} catch (SignatureException exception) {
				log.warn(cuo + " Request to parse JWT with invalid signature : {} failed : {}", token,
						exception.getMessage());
			} catch (IllegalArgumentException exception) {
				log.warn(cuo + " Request to parse empty or null JWT : {} failed : {}", token, exception.getMessage());
			} catch (Exception e) {
				log.error(cuo + " No se obtubo owner y código de BASE DE DATOS: " + ProjectUtils.convertExceptionToString(e));
				e.printStackTrace();
			}
		}
		if(!urlReq.endsWith("healthcheck")) {
			log.error(cuo + " Hubo un problema con el token : " + token);
		}
		return null;
	}

}