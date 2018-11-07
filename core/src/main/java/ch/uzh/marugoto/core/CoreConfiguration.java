package ch.uzh.marugoto.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.arangodb.springframework.annotation.EnableArangoRepositories;

@Configuration
@ComponentScan(basePackages = { "ch.uzh.marugoto" })
@EntityScan(basePackages = { "ch.uzh.marugoto" })
//@PropertySource("classpath:application-production.properties")
@PropertySource({"classpath:application-production.properties",
	"classpath:application-testing.properties"})
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
        
//        javaMailSender.setHost("smtp.mailtrap.io");
//        javaMailSender.setPort(2525);
//        javaMailSender.setUsername("4b458884e3ec48");
//        javaMailSender.setPassword("dde2d81e4e3f56");
        return javaMailSender;
    }

	 @Bean
	 public MessageSource messageSource() {
	     ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	     messageSource.setBasename("classpath:locale/messages");
	     messageSource.setDefaultEncoding("UTF-8");
	     return messageSource;
	 }
	 
	  //TODO check why tests are not working with this method
//	 @Bean
//	 public LocalValidatorFactoryBean validator() {
//	     LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
//	     bean.setValidationMessageSource(messageSource());
//	     return bean;
//	 }
}