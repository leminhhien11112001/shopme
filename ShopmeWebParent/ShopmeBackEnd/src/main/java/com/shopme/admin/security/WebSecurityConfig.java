package com.shopme.admin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
	public UserDetailsService userDetailsService() {
		return new ShopmeUserDetailsService();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
	    return authConfig.getAuthenticationManager();
	}
	
	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
     		
		 http.authorizeHttpRequests(configure -> configure
				 	.requestMatchers("/users/**").hasAuthority("Admin")
				 	.requestMatchers("/categories/**", "/brands/**").hasAnyAuthority("Admin", "Editor")
				 	
				 	.requestMatchers("/products/new", "/products/delete/**").hasAnyAuthority("Admin", "Editor")
				 	
				 	.requestMatchers("/products/edit/**", "/products/save", "/products/check_unique")
				 								.hasAnyAuthority("Admin", "Editor", "Salesperson")
				 	.requestMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
				 								.hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
				 	
				 	.requestMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
				 	
				 	.requestMatchers("/orders", "/orders/", "/orders/page/**", "/orders/detail/**")
				 			.hasAnyAuthority("Admin", "Salesperson", "Shipper")
				 	
				 	.requestMatchers("/customers/**",  "/orders/**",  "/reports/**").hasAnyAuthority("Admin", "Salesperson")
				 	
				 	.requestMatchers("/orders_shipper/update/**").hasAnyAuthority("Shipper")
				 					 	
				 	.requestMatchers("/reviews/**").hasAnyAuthority("Admin", "Salesperson")
				 	
	                .anyRequest().authenticated()
	            )
	            .formLogin(form -> form
	                .loginPage("/login")
	                .usernameParameter("email")
	                .permitAll()
	            )
	            .logout(logout -> logout.permitAll())
	            .rememberMe(remember -> remember
	            				.key("AbcDefgHijKlmnOpqrs_1234567890")
	            				.tokenValiditySeconds(7 * 24 * 60 * 60) //7 days
	            		)
	            .authenticationProvider(authenticationProvider());
	           
        return http.build();
    }
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
		
	}

}
