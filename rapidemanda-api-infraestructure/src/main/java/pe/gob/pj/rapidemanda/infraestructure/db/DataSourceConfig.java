package pe.gob.pj.rapidemanda.infraestructure.db;

import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "pe.gob.pj.rapidemanda.infraestructure.db",
    entityManagerFactoryRef = "seguridadEntityManagerFactory",
    transactionManagerRef = "txManagerSeguridad"
)
public class DataSourceConfig {

    //CONEXION CON LA BASE DE DATOS SEGURIDAD
    @Primary
    @Bean(name = "cxSeguridadDS")
    DataSource seguridadDataSource() throws NamingException {
      log.info("::::::::> CREANDO DATASOURCE SEGURIDAD");
        return (DataSource) new InitialContext().lookup("java:jboss/datasources/servicioRapidemandaAPISeguridad");
    }

    @Primary
    @Bean(name = "seguridadEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean seguridadEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("cxSeguridadDS") DataSource dataSource) {
       log.info("::::::::> CREANDO ENTITY MANAGER PARA SEGURIDAD");
        return builder
            .dataSource(dataSource)
            .packages("pe.gob.pj.rapidemanda.infraestructure.db.entity.security")
            .persistenceUnit("seguridad")
            .properties(getHibernateProperties())
            .build();
    }

    @Primary
    @Bean(name = "txManagerSeguridad")
    PlatformTransactionManager seguridadTransactionManager(
            @Qualifier("seguridadEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
      log.info("::::::::> CREANDO TRANSACTION MANAGER PARA SEGURIDAD");
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Primary
    @Bean(name = "sessionSeguridad")
    SessionFactory sessionFactorySeguridad(
            @Qualifier("seguridadEntityManagerFactory") EntityManagerFactory emf) {
      log.info("::::::::> CREANDO SESSION FACTORY PARA SEGURIDAD");
        if (emf.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("seguridad factory is not a hibernate factory");
        }
        return emf.unwrap(SessionFactory.class);
    }

    //CONEXION CON LA BASE DE DATOS DE NEGOCIO
    @Bean(name = "cxNegocioDS")
    DataSource negocioDataSource() throws NamingException {
      log.info("::::::::> CREANDO DATASOURCE NEGOCIO");
        return (DataSource) new InitialContext().lookup("java:jboss/datasources/servicioRapidemandaAPINegocio");
    }

    @Bean(name = "negocioEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean negocioEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("cxNegocioDS") DataSource dataSource) {
      log.info("::::::::> CREANDO ENTITY MANAGER PARA NEGOCIO");
        return builder
            .dataSource(dataSource)
            .packages("pe.gob.pj.rapidemanda.infraestructure.db.entity.servicio")
            .persistenceUnit("negocio")
            .properties(getHibernateProperties())
            .build();
    }

    @Bean(name = "txManagerNegocio")
    PlatformTransactionManager negocioTransactionManager(
            @Qualifier("negocioEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
      log.info("::::::::> CREANDO TRANSACTION MANAGER PARA NEGOCIO");
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "sessionNegocio")
    SessionFactory sessionFactoryNegocio(
            @Qualifier("negocioEntityManagerFactory") EntityManagerFactory emf) {
      log.info("::::::::> CREANDO SESSION FACTORY PARA NEGOCIO");
        if (emf.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("negocio factory is not a hibernate factory");
        }
        return emf.unwrap(SessionFactory.class);
    }

    //CONEXION AUDITORIA GENERAL
    @Bean(name = "cxAuditoriaGeneralDS")
    DataSource auditoriaDataSource() throws NamingException {
      log.info("::::::::> CREANDO DATASOURCE AUDITORIA");
        return (DataSource) new InitialContext().lookup("java:jboss/datasources/servicioRapidemandaAPIAuditoriaGeneral");
    }

    @Bean(name = "auditoriaEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean auditoriaEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("cxAuditoriaGeneralDS") DataSource dataSource) {
      log.info("::::::::> CREANDO ENTITY MANAGER PARA AUDITORIA");
        return builder
            .dataSource(dataSource)
            .packages("pe.gob.pj.rapidemanda.infraestructure.db.entity.auditoriageneral")
            .persistenceUnit("auditoria")
            .properties(getHibernateProperties())
            .build();
    }

    @Bean(name = "txManagerAuditoriaGeneral")
    PlatformTransactionManager auditoriaTransactionManager(
            @Qualifier("auditoriaEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
      log.info("::::::::> CREANDO TRANSACTION MANAGER PARA AUDITORIA");
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(name = "sessionAuditoriaGeneral")
    SessionFactory sessionFactoryAuditoriaGeneral(
            @Qualifier("auditoriaEntityManagerFactory") EntityManagerFactory emf) {
      log.info("::::::::> CREANDO SESSION FACTORY PARA AUDITORIA");
        if (emf.unwrap(SessionFactory.class) == null) {
            throw new NullPointerException("auditoria factory is not a hibernate factory");
        }
        return emf.unwrap(SessionFactory.class);
    }

    private Map<String, Object> getHibernateProperties() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.format_sql", "true");
        properties.put("hibernate.connection.release_mode", "AFTER_TRANSACTION");
        return properties;
    }
}