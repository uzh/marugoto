package ch.uzh.marugoto.core;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.arangodb.springframework.annotation.EnableArangoRepositories;

@Configuration
@ComponentScan(basePackages = { "ch.uzh.marugoto" })
@EntityScan(basePackages = { "ch.uzh.marugoto" })
@EnableArangoRepositories(basePackages = { "ch.uzh.marugoto" })
public class CoreConfiguration {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}