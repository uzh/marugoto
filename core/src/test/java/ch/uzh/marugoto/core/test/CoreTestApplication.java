package ch.uzh.marugoto.core.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import ch.uzh.marugoto.core.CoreConfiguration;

@ActiveProfiles("testing")
@SpringBootApplication
@Import(CoreConfiguration.class)
public class CoreTestApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(CoreTestApplication.class)
	        .run(args);
    }
}