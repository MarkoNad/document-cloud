package hr.documentcloud.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@Configuration
@Slf4j
//@Order(100)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private final PasswordEncoder passwordEncoder;

	@Autowired
	public WebSecurityConfig(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.antMatchers( "/login", "/css/**")
				.permitAll()
				.anyRequest().authenticated()
				.and()
				.formLogin()
//				.successHandler(appAuthenticationSuccessHandler())
		//		.loginPage("/login")
				.and()
				.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))

				.and()
				.requiresChannel()
				.anyRequest().requiresSecure();
		log.info("Web security enabled");
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication()
				.withUser("user1").password(passwordEncoder.encode("pwd1")).roles("USER")
				.and()
				.withUser("admin").password(passwordEncoder.encode("pwd")).roles("ADMIN");
	}

//	@Bean
//	public AuthenticationSuccessHandler appAuthenticationSuccessHandler(){
//		return new RefererAuthenticationSuccessHandler();
//	}

}
