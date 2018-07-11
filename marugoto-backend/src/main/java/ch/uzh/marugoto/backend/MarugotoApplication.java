package ch.uzh.marugoto.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.arangodb.springframework.core.ArangoOperations;

/**
 * Entry point of the Spring Boot application.
 * The application ensures that the configured database exists, if not, it will be created automatically.
 * 
 * @author Rino
 */
@SpringBootApplication
public class MarugotoApplication implements ApplicationRunner {
    private static final Logger logger = LogManager.getLogger(MarugotoApplication.class);
    
    @Value("${marugoto.database}")
    private String DB_NAME;
    
    @Value("${spring.profiles.active}")
    private String SPRING_PROFILE;

	@Autowired
	private ArangoOperations operations;
	

	public static void main(String[] args) {
		SpringApplication.run(MarugotoApplication.class, args);
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
		
		logger.info("------------------------------------------------");
    }
}
