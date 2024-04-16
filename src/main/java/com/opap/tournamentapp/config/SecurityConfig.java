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
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LogManager.getLogger(SecurityConfig.class);
    private final UserService userService;

    public SecurityConfig(UserService userService){
        this.userService=userService;
    }

    @Bean
    public CustomFilter customFilter(){
        return new CustomFilter();
    }
    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http, @Value("${frontendUrl:http://localhost:3000}") String frontendUrl,CustomFilter customFilter) throws Exception {
        http.addFilterBefore(customFilter, OAuth2LoginAuthenticationFilter.class);
        http
                .csrf().disable()
                .cors(cors -> cors.configurationSource(corsConfigurationSource(frontendUrl)))
                .authorizeHttpRequests()
                .requestMatchers("/oauth/login/linkedin","/oauth/login/github","/loggedin/**","/redirect", "/ws-message/public/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .loginPage("/redirect")
                .defaultSuccessUrl("/oauth/login/success");
        return http.build();
    }

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
        logger.info("IM IN SEC CONFIG OAUTH2SUSERSEREIVECCECEFERWERFDSDF");
        return request -> {

            OAuth2User user = delegate.loadUser(request);
            logger.info("FIND ME FOR CRYING OUT LOUD!!!" + user);
            String registrationId = request.getClientRegistration().getRegistrationId();
            logger.info("what is happening holmes: " + registrationId);
            OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(request.getClientRegistration(), user.getName(), request.getAccessToken());
            String fullname, username, avatarUrl;

            if ("github".equals(registrationId)) {
                fullname = user.getAttribute("name");
                username = user.getAttribute("login");
                avatarUrl = user.getAttribute("avatar_url");
            } else {
                fullname = user.getAttribute("name");
                username = user.getAttribute("email");
                avatarUrl = user.getAttribute("picture");
                logger.info("1234123412 !!! IN SEC CONFIG22341@#$!" + user);
            }
            logger.info("Fullname: " + fullname + " Username: " + username);
            try {
                userService.loginUser(fullname, username, avatarUrl);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
            return user;
        };
    }


    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        OidcUserService delegate = new OidcUserService();
        return request -> {
            OidcUser user = delegate.loadUser(request);
            logger.info("OIDC User: " + user);
            String registrationId = request.getClientRegistration().getRegistrationId();
            logger.info("Registration ID: " + registrationId);

            String fullname, email, avatarUrl;
            if ("linkedin".equals(registrationId) && user instanceof OidcUser) {
                OidcUser oidcUser = user;
                fullname = oidcUser.getAttribute("name");
                email = oidcUser.getAttribute("email");
                avatarUrl = oidcUser.getAttribute("picture");
                logger.info("LinkedIn user details: Fullname: " + fullname + ", Email: " + email);
                try {
                    userService.loginUser(fullname, email, avatarUrl);
                } catch (JsonProcessingException e) {
                    logger.error("Error processing user details: " + e.getMessage(), e);
                    throw new RuntimeException(e);
                }
            }


            return (OidcUser) user;
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