package ch.uzh.marugoto.backend.security;

import ch.uzh.marugoto.backend.security.shibboleth.ShibbolethAuthenticationEntryPoint;
import ch.uzh.marugoto.backend.security.shibboleth.ShibbolethAuthenticationProvider;
import ch.uzh.marugoto.core.CoreConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.session.SessionManagementFilter;


@Configuration
@Order(1)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigShibbolethUser extends WebSecurityConfigurerAdapter {

    @Autowired
    private CoreConfiguration coreConfig;

    @Autowired
    private ShibbolethAuthenticationProvider authProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider);
    }

    @Bean
    public ShibbolethAuthenticationEntryPoint unauthorizedHandler() {
        return new ShibbolethAuthenticationEntryPoint();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        	.csrf().disable()
        	.authorizeRequests()
            // Allow pre flight requests
            .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
            // Following paths require no authentication
            .antMatchers("/api/", "/api/dev/**", "/api/user/**").permitAll()
            // Following paths require token authentication
            .antMatchers("/api/**").authenticated()
	        .and()
	        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler())
            .and()
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authProvider);

        // Enable CORS for /api/** routes
        http.addFilterBefore(corsFilter(), SessionManagementFilter.class);
    }

    @Bean
    CorsFilter corsFilter() {
        return new CorsFilter();
    }
}
