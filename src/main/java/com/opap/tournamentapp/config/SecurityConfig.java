package com.opap.tournamentapp.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);
    private final UserService userService;

    public SecurityConfig(UserService userService){
        this.userService=userService;
    }

    // H2 config
//    @Bean
//    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, @Value("${frontendUrl:http://localhost:3000}") String frontendUrl)
//            throws Exception {
//        http
//                .csrf().disable()
//                .cors(cors -> cors.configurationSource(corsConfigurationSource(frontendUrl))) // Using frontendUrl
//                .authorizeHttpRequests(authz -> authz
//                        .requestMatchers(new AntPathRequestMatcher("/oauth/login/google")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/oauth/login/github")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/loggedin/**")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/ws-message/**")).permitAll()
//                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth -> oauth
//                        .defaultSuccessUrl("/oauth/login/success", true)
//                )
//                .headers(headers -> headers
//                        .frameOptions().sameOrigin() // Allow frames for the H2 console
//                );
//
//        return http.build();
//    }

    // PostgreSQL config
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, @Value("${frontendUrl:http://localhost:3000}") String frontendUrl) throws Exception {
        http
                .csrf().disable()
                .cors(cors -> cors.configurationSource(corsConfigurationSource(frontendUrl)))
                .authorizeHttpRequests()
                .requestMatchers("/oauth/login/google","/oauth/login/github","/loggedin/**", "/admin/**","/ws-message/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/oauth/login/success", true);
        return http.build();
    }

    // @Bean
    CorsConfigurationSource corsConfigurationSource(String frontendUrl) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of( frontendUrl));
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", configuration);
        return urlBasedCorsConfigurationSource;
    }
    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService(WebClient rest) {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User user = delegate.loadUser(request);
            String registrationId = request.getClientRegistration().getRegistrationId();
            OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(request.getClientRegistration(), user.getName(), request.getAccessToken());
            String fullname, username, avatarUrl;

            if ("github".equals(registrationId)) {
                fullname = user.getAttribute("name");
                username = user.getAttribute("login");
                avatarUrl = user.getAttribute("avatar_url");
            } else if ("google".equals(registrationId)) {
                fullname = user.getAttribute("name");
                username = user.getAttribute("email"); // Assuming you use email as username for Google
                avatarUrl = user.getAttribute("picture");
            } else {
                throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
            }

            logger.info("Fullname: " + fullname + " Username: " + username);

            try {
                userService.loginUser(fullname, username, avatarUrl, 0);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            return user;
        };
    }

    @Bean
    public WebClient rest(ClientRegistrationRepository clients, OAuth2AuthorizedClientRepository authz) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(clients, authz);
        return WebClient.builder()
                .filter(oauth2).build();
    }
}