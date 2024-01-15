package com.opap.tournamentapp.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opap.tournamentapp.service.UserService;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService){
        this.userService=userService;
    }
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests()
                .requestMatchers("/oauth/login/google","/oauth/login/github","/loggedin/**", "/admin/**","/ws-message/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .defaultSuccessUrl("/oauth/login/success", true);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
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
            if (!"github".equals(request.getClientRegistration().getRegistrationId())) {
                return user;
            }


            OAuth2AuthorizedClient client = new OAuth2AuthorizedClient
                    (request.getClientRegistration(), user.getName(), request.getAccessToken());

            String fullname = user.getAttribute("name");
            String username = user.getAttribute("login");
            String avatarUrl = user.getAttribute("avatar_url");

            System.out.println("Fullname: " + fullname + " Username: " + username);

            try {
                userService.loginUser(fullname, username, avatarUrl);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

//            if (RequestContextHolder.currentRequestAttributes().getSessionId() != null) {
//                System.out.println("Session Id: " + RequestContextHolder.currentRequestAttributes().getSessionId());
//            }

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