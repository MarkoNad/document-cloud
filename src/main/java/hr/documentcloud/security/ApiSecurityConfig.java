package hr.documentcloud.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

//@EnableWebSecurity
//@Configuration
@Slf4j
//@Order(99)
public class ApiSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http
				.csrf()
				.disable()
				.requestMatchers()
				.antMatchers("/api/**")
				.and()
				.authorizeRequests()
				.anyRequest().authenticated()
				.and()
				.httpBasic();
		log.info("Api security enabled");

	}
}