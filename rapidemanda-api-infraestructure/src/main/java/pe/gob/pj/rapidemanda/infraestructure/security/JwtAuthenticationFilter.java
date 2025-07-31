package pe.gob.pj.rapidemanda.infraestructure.security;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import pe.gob.pj.rapidemanda.domain.port.usecase.SeguridadUseCasePort;
import pe.gob.pj.rapidemanda.domain.utils.EncryptUtils;
import pe.gob.pj.rapidemanda.domain.utils.ProjectProperties;
import pe.gob.pj.rapidemanda.domain.utils.ProjectUtils;
import pe.gob.pj.rapidemanda.domain.utils.SecurityConstants;
import pe.gob.pj.rapidemanda.infraestructure.enums.Claim;

@Slf4j
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


	@Getter @Setter
	private SeguridadUseCasePort seguridadService;

	private final AuthenticationManager authenticationManager;

	public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SeguridadUseCasePort service) {
		this.authenticationManager = authenticationManager;
		this.setSeguridadService(service);
		setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
	}

	/**
	* Descripción : evalua la autenticacion del usuario
	* @param HttpServletRequest request - peticion HTTP
	* @param HttpServletResponse response - respuesta HTTP    
	* @return Authentication - respuesta de la evaluacion de usuario
	* @exception Captura excepcion generica
	*/
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {		
		String cuo= ProjectUtils.obtenerCodigoUnico();
		String username = request.getHeader(SecurityConstants.HEAD_USERNAME);
		String password = request.getHeader(SecurityConstants.HEAD_PASSWORD);
		String codigoCliente = request.getHeader(SecurityConstants.HEAD_COD_CLIENTE);
		String codigoRol= request.getHeader(SecurityConstants.HEAD_COD_ROL);
		String idUsuario = null;
		try {
			username= EncryptUtils.decryptPastFrass(username);
			password= EncryptUtils.decryptPastFrass(password);
			idUsuario = seguridadService.autenticarUsuario(cuo, codigoCliente, codigoRol, username, password);
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			log.error("{} ERROR AUTENTIFICANDO USUARIO CON BASE DE DATOS DE SEGURIDAD : {}",cuo,ProjectUtils.convertExceptionToString(e));
			return null;
		}
		if (idUsuario != null && !idUsuario.isEmpty()) {
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(idUsuario, EncryptUtils.encrypt(username, password)));
		}
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		return null;
	}

	/**
	* Descripción : Procesa la evaluacion positiva y genera el token
	* @param HttpServletRequest request - peticion HTTP
	* @param HttpServletResponse response - respuesta HTTP    
	* @param FilterChain filterChain - cadenas filtro
	* @param Authentication authentication - resultado de la evaluacion
	* @return void
	* @exception Captura excepcion generica
	*/
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain, Authentication authentication) throws IOException {
		User user = ((User) authentication.getPrincipal());
		List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
		byte[] signingKey = SecurityConstants.JWT_SECRET.getBytes();
		
		Date ahora = new Date();
		int tiempoSegundosExpira = ProjectProperties.getSeguridadTiempoExpiraSegundos();
		int tiempoSegundosRefresh = ProjectProperties.getSeguridadTiempoRefreshSegundos();
		String codigoRolSeleccionado= request.getHeader(SecurityConstants.HEAD_COD_ROL);
		
		String token = Jwts.builder()
				.signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
				.setHeaderParam("typ", SecurityConstants.TOKEN_TYPE)
				.setIssuer(SecurityConstants.TOKEN_ISSUER)
				.setAudience(SecurityConstants.TOKEN_AUDIENCE)
				.setSubject(user.getUsername())
				.setExpiration(ProjectUtils.sumarRestarSegundos(ahora, tiempoSegundosExpira))
				.claim(Claim.ROLES.getNombre(), roles)
				.claim(Claim.ROL_SELECCIONADO.getNombre(), codigoRolSeleccionado)
				.claim(Claim.USUARIO_REALIZA_PETICION.getNombre(), user.getUsername())
				.claim(Claim.IP_REALIZA_PETICION.getNombre(), request.getRemoteAddr())
				.claim(Claim.LIMITE_TOKEN.getNombre(), ProjectUtils.sumarRestarSegundos(ahora, tiempoSegundosExpira + tiempoSegundosRefresh))
				.compact();
		response.addHeader(SecurityConstants.TOKEN_HEADER, SecurityConstants.TOKEN_PREFIX + token);
		response.setContentType("application/json");
		response.getWriter().write("{\"token\":\""+token+"\",\"exps\":\""+tiempoSegundosExpira+"\",\"refs\":\""+tiempoSegundosRefresh+"\"}");
	}
	
	/**
	* Descripción : Procesa la evaluacion negativa 
	* @param HttpServletRequest request - peticion HTTP
	* @param HttpServletResponse response - respuesta HTTP    
	* @param AuthenticationException failed - excepcion por el fallo
	* @return void
	* @exception Captura excepcion generica
	*/
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
		log.error("ERROR CON LA UTORIZACION DE SPRING SECURITY: "+failed.getMessage());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}
}