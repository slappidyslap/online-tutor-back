package kg.musabaev.onlinetutorback.config;

import kg.musabaev.onlinetutorback.filter.TokenFilter;
import kg.musabaev.onlinetutorback.repository.UserRepo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfig {

	UserRepo userRepo;
	TokenFilter tokenFilter;

	public SecurityConfig(UserRepo userRepo, @Lazy TokenFilter tokenFilter) {
		this.userRepo = userRepo;
		this.tokenFilter = tokenFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.csrf().disable()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.exceptionHandling(customizer -> customizer
						.accessDeniedHandler((req, res, e) -> res.setStatus(FORBIDDEN.value()))
						.authenticationEntryPoint(new HttpStatusEntryPoint(UNAUTHORIZED)))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(customizer -> customizer
						.requestMatchers(HttpMethod.POST, "/users/specialists").permitAll()
						.requestMatchers(HttpMethod.POST, "/users/students").permitAll()
						.requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/classes/**").permitAll()
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/api-docs/**", "/swagger-ui/**", "/actuator/**").permitAll()
						.anyRequest().authenticated())
				.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		var daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService());
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		return daoAuthenticationProvider;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> userRepo.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found by username"));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}