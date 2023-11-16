package com.shopme.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}	
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     		
		 http.authorizeHttpRequests(configure -> configure
				 	.requestMatchers("/account_details", "/update_account_details", "/cart", "/orders/**",
				 			"/checkout", "/place_order").authenticated()
				 	.anyRequest().permitAll()
				 	
	            )
	            .formLogin(form -> form
	                .loginPage("/login")
	                .usernameParameter("email")
	                .permitAll()
	            )
	            .logout(logout -> logout.permitAll())
	            .rememberMe(remember -> remember
	            				.key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
	            				.tokenValiditySeconds(14 * 24 * 60 * 60) //7 days
	            		)
	            .authenticationProvider(authenticationProvider());
	           
		 return http.build();
    }
	

	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
	}

}
