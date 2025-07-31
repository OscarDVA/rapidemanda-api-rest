package pe.gob.pj.rapidemanda.infraestructure.doc;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

  @Bean
  OpenAPI apiInfo() {
    return new OpenAPI().info(new Info().title("Consulta Servicios de Pide").description(
        "Servicio que permite mostrar base de proyecto rest")
        .version("1.0.0"));
  }

  @Bean
  GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder().group("public").pathsToMatch("/api/**")
        .packagesToScan("pe.gob.pj.rapidemanda.infraestructure.rest").build();
  }

}
