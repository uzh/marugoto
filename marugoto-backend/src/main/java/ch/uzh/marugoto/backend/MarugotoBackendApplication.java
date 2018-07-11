package ch.uzh.marugoto.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MarugotoBackendApplication implements ApplicationRunner {
    private static final Logger logger = LogManager.getLogger(MarugotoBackendApplication.class);
    

	public static void main(String[] args) {
		SpringApplication.run(MarugotoBackendApplication.class, args);
	}
	
	@Override
    public void run(ApplicationArguments applicationArguments) {
        logger.info("Marugoto backend started!");
    }
}
