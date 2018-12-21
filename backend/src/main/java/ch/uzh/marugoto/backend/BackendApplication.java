package ch.uzh.marugoto.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import com.arangodb.springframework.core.ArangoOperations;

import ch.uzh.marugoto.core.CoreConfiguration;

/**
 * Main entry point of the Spring Boot web application.
 * The application ensures that the configured database exists,
 * if not, it will be created automatically.
 */
@SpringBootApplication
@Import(CoreConfiguration.class)
public class BackendApplication implements ApplicationRunner {
	private static final Logger logger = LogManager.getLogger(BackendApplication.class);

	@Value("${marugoto.database}")
	private String DB_NAME;

	@Value("${spring.profiles.active}")
	private String SPRING_PROFILE;

	@Autowired
	private ArangoOperations operations;

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments applicationArguments) {
		logger.info("------------------------------------------------");
		logger.info("Marugoto backend started:");
		logger.info(" > Spring profile: " + SPRING_PROFILE);
		logger.info(" > Java version: " + System.getProperty("java.version"));
		logger.info(" > Database: " + DB_NAME);

		// Make sure database exists, create if not
		if (!operations.driver().getDatabases().contains(DB_NAME)) {
			operations.driver().createDatabase(DB_NAME);
			logger.info(String.format("Database `%s` created.", DB_NAME));
		}
		//check if every collection is added
		operations.collection("chapter");
		operations.collection("component");
		operations.collection("exerciseState");
		operations.collection("notebookEntry");
		operations.collection("page");
		operations.collection("pageState");
		operations.collection("pageTransition");
		operations.collection("personalNote");
		operations.collection("storyline");
		operations.collection("storylineState");
		operations.collection("topic");
		operations.collection("user");
		logger.info("------------------------------------------------");
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
