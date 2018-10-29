package ch.uzh.marugoto.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.arangodb.springframework.annotation.EnableArangoRepositories;

@Configuration
@ComponentScan(basePackages = { "ch.uzh.marugoto" })
@EntityScan(basePackages = { "ch.uzh.marugoto" })
@PropertySource("application-production.properties")
@EnableArangoRepositories(basePackages = { "ch.uzh.marugoto" })
public class CoreConfiguration {

	@Value("${smtp.host}")
	private String smtpHost;
	@Value("${smtp.port}")
	private int smtpPort;
	@Value("${smtp.username}")
	private String smtpUsername;
	@Value("${smtp.password}")
	private String smtpPassword;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public JavaMailSenderImpl mailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        
        javaMailSender.setHost(smtpHost);
        javaMailSender.setPort(smtpPort);
        javaMailSender.setUsername(smtpUsername);
        javaMailSender.setPassword(smtpPassword);
        return javaMailSender;
    }
}