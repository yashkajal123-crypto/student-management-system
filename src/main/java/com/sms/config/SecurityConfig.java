package com.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Spring Security setup.
 *
 * Every screen requires a login. The only public pages are the
 * login page itself, the static assets (css/js) and the error pages.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/css/**", "/js/**", "/images/**", "/error/**").permitAll()
				.anyRequest().authenticated())
			.formLogin(form -> form
				.loginPage("/login")
				.defaultSuccessUrl("/dashboard", true)
				.permitAll())
			.logout(logout -> logout
				// GET /logout keeps the plain "Logout" links from the design working
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
				.logoutSuccessUrl("/login?logout")
				.permitAll());

		return http.build();
	}

	/**
	 * Single admin user for the application.
	 * Password is stored BCrypt-hashed, never in plain text.
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		UserDetails admin = User.builder()
				.username("admin@sms.com")
				.password(passwordEncoder().encode("admin123"))
				.roles("ADMIN")
				.build();

		return new InMemoryUserDetailsManager(admin);
	}
}
