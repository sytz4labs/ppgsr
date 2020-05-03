package us.ppgs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import us.ppgs.security.RESTAuthenticationEntryPoint;
import us.ppgs.security.RESTAuthenticationFailureHandler;
import us.ppgs.security.RESTAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class PpgsSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;

    @Autowired
    private RESTAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private RESTAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private RESTAuthenticationSuccessHandler authenticationSuccessHandler;
	@Autowired
	public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {

		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery("select username,password, enabled from sec_users where username=?")
				.authoritiesByUsernameQuery("select username, role from sec_user_roles where username=?")
				.passwordEncoder(new BCryptPasswordEncoder());
	}

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	
        http.requiresChannel()
		        .requestMatchers(r -> r.getHeader("x-forwarded-proto") != null)
		        .requiresSecure()
        	.and()
        		.headers().frameOptions().sameOrigin()
        	.and()
	        	.authorizeRequests()
	        	.antMatchers("/**", "/login")
		        .permitAll()
            .and()
            	.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint)
            .and()
                .formLogin().successHandler(authenticationSuccessHandler)
            .and()
            	.formLogin().failureHandler(authenticationFailureHandler)
            .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessHandler((new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
			.and()
				.rememberMe().tokenRepository(persistentTokenRepository())
				.tokenValiditySeconds(30 * 24 * 60 * 60).rememberMeCookieName("RMSESSION")
        	.and()
        		.csrf().ignoringAntMatchers("/pssdb/**").csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}

	@Bean
	public SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler() {
		SavedRequestAwareAuthenticationSuccessHandler auth = new SavedRequestAwareAuthenticationSuccessHandler();
		auth.setTargetUrlParameter("targetUrl");
		return auth;
	}
}