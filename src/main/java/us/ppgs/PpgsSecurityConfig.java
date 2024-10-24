package us.ppgs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import us.ppgs.security.RESTAuthenticationEntryPoint;
import us.ppgs.security.RESTAuthenticationFailureHandler;
import us.ppgs.security.RESTAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class PpgsSecurityConfig {

	@Autowired
	DataSource dataSource;

    @Autowired
    private RESTAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private RESTAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private RESTAuthenticationSuccessHandler authenticationSuccessHandler;
    
    @Bean
    UserDetailsManager users(DataSource dataSource) {
    	return new JdbcUserDetailsManager(dataSource);
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
    	
        http.requiresChannel(c -> c
        		.requestMatchers(r -> r.getHeader("x-forwarded-proto") != null)
		        .requiresSecure())
        	.headers(h -> h.frameOptions(c -> c.sameOrigin()))
    		.authorizeHttpRequests(c -> c.requestMatchers(new AntPathRequestMatcher("/**"),
    													  new AntPathRequestMatcher("/login"),
    													  new AntPathRequestMatcher("/pssdb/**"),
    													  new AntPathRequestMatcher("/zxing/**")).permitAll())
    		
        	.exceptionHandling(h -> h.authenticationEntryPoint(authenticationEntryPoint))

            .formLogin(l -> l.successHandler(authenticationSuccessHandler))
        	.formLogin(l -> l.failureHandler(authenticationFailureHandler))
            .logout(l -> l.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))))
			.rememberMe(r -> r.tokenRepository(persistentTokenRepository()).userDetailsService(null)
							  .tokenValiditySeconds(30 * 24 * 60 * 60)
							  .rememberMeCookieName("RMSESSION"));

        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);
        http.csrf(csrf -> csrf
        		.ignoringRequestMatchers(new AntPathRequestMatcher("/pssdb/**"))
        		.csrfTokenRequestHandler(requestHandler)
        		.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
        
        return http.build();
    }

	@Bean
	PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}
}