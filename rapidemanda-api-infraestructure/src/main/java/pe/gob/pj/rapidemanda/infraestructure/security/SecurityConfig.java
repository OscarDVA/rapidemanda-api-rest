package pe.gob.pj.rapidemanda.infraestructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;
import pe.gob.pj.rapidemanda.domain.port.usecase.SeguridadUseCasePort;
import pe.gob.pj.rapidemanda.infraestructure.security.adapter.UserDetailsServiceAdapter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  private final SeguridadUseCasePort seguridadService;

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.addAllowedHeader("*");
    configuration.addAllowedOrigin("*");
    configuration.addAllowedMethod("*");
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            authz -> authz
                .requestMatchers("/", "/healthcheck", "/css/**", "/v3/api-docs/**",
                    "/swagger-ui/**", "/swagger-ui.html", "/configuration/ui",
                    "/swagger-resources/**", "/configuration/security", "/webjars/**")
                .permitAll().anyRequest().authenticated())
        .addFilter(new JwtAuthenticationFilter(authenticationManager(), seguridadService))
        .addFilter(new JwtAuthorizationFilter(authenticationManager(), seguridadService))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  DaoAuthenticationProvider authenticationProvider() {
    UserDetailsServiceAdapter userDetailsService =
        new UserDetailsServiceAdapter(seguridadService, passwordEncoder());
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  AuthenticationManager authenticationManager() throws Exception {
    return new ProviderManager(authenticationProvider());
  }
}
