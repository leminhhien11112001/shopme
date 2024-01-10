package com.shopme.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.shopme.security.oauth.CustomerOAuth2UserService;
import com.shopme.security.oauth.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig{

	@Autowired private CustomerOAuth2UserService oAuth2UserService;
	@Autowired private OAuth2LoginSuccessHandler oauth2LoginHandler;
	@Autowired private DatabaseLoginSuccessHandler databaseLoginHandler;
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	UserDetailsService userDetailsService() {
		return new CustomerUserDetailsService();
	}
	
	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}	
	
	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests(configure -> configure
			 	.requestMatchers("/customer").authenticated()
			 	.requestMatchers("/account_details", "/update_account_details", "/orders/**",
						"/cart", "/address_book/**", "/checkout", "/place_order", 
						"/process_paypal_order").authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("email")
                .successHandler(databaseLoginHandler)
                .permitAll()
            ).oauth2Login(oauth2  -> oauth2
            	.loginPage("/login")
            	.userInfoEndpoint(u -> u.userService(oAuth2UserService))
				.successHandler(oauth2LoginHandler)
            )
            .logout(logout -> logout.permitAll())
            .rememberMe(remember -> remember
            				.key("AbcDefgHijKlmnOpqrs_1234567890")
            				.tokenValiditySeconds(14 * 24 * 60 * 60) //7 days
            		)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
            .authenticationProvider(authenticationProvider());
           
		return http.build();
    }
	
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		
		return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/webjars/**");
		
	}

}
