package ch.uzh.marugoto.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import ch.uzh.marugoto.core.CoreConfiguration;

/**
 * Main entry point of the Spring Boot web application.
 * The application ensures that the configured database exists,
 * if not, it will be created automatically.
 */
@SpringBootApplication
@Import(CoreConfiguration.class)
public class BackendApplication extends SpringBootServletInitializer {

	@Value("${marugoto.database}")
	private String DB_NAME;

	@Value("${spring.profiles.active}")
	private String SPRING_PROFILE;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(BackendApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}
}