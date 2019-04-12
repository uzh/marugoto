package ch.uzh.marugoto.shell;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.shell.jline.PromptProvider;

import ch.uzh.marugoto.core.CoreConfiguration;

@SpringBootApplication
@Import(CoreConfiguration.class)
public class ShellApplication {

	public static void main(String[] args) {
        new SpringApplicationBuilder(ShellApplication.class)
	        .web(WebApplicationType.NONE)
            .logStartupInfo(false)
            .addCommandLineProperties(true)
	        .run(args);
	}

	@Bean
	public PromptProvider myPromptProvider() {
		return () -> new AttributedString("marugoto:>", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
	}
}