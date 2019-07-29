package ch.uzh.marugoto.backend.security;

import ch.uzh.marugoto.backend.security.shibboleth.ShibbolethAuthenticationProvider;
import ch.uzh.marugoto.core.CoreConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.SessionManagementFilter;

import javax.annotation.Resource;

@Configuration
@Order(1)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigShibbolethUser extends WebSecurityConfigurerAdapter {

    @Resource(name = "userService")
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private CoreConfiguration coreConfig;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(encoder());
    }

    @Autowired
    private ShibbolethAuthenticationProvider authProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

    @Bean
    public JwtAuthenticationFilter authenticationTokenFilterBean() {
        return new JwtAuthenticationFilter();
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
	        .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
            .and()
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authenticationProvider(authProvider);

        // Enable CORS for /api/** routes
        http.addFilterBefore(corsFilter(), SessionManagementFilter.class);
    }
    
    @Bean
    public PasswordEncoder encoder(){
        return coreConfig.passwordEncoder();
    }

    @Bean
    CorsFilter corsFilter() {
        return new CorsFilter();
    }
}
