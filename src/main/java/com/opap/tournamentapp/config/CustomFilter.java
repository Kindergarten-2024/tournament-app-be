package com.opap.tournamentapp.config;
import com.google.api.Http;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomFilter implements Filter {
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    private RequestCache requestCache = new HttpSessionRequestCache();

    public CustomFilter() {
        this.authorizationRequestRepository = new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        // Try to extract the OAuth2AuthorizationRequest from the cache
        OAuth2AuthorizationRequest authorizationRequest = this.authorizationRequestRepository.loadAuthorizationRequest(httpServletRequest);

        if (authorizationRequest != null && "linkedin".equals(authorizationRequest.getAttribute("registration_id"))) {

            // Create a new OAuth2AuthorizationRequest without the nonce
            OAuth2AuthorizationRequest updatedRequest =
                    OAuth2AuthorizationRequest.from(authorizationRequest)
                            .attributes(attrs -> attrs.remove("nonce"))
                            .build();

            // Save the modified request back to the repository
            this.authorizationRequestRepository.saveAuthorizationRequest(updatedRequest, httpServletRequest, httpServletResponse);

            // If you're using a request cache, you might want to save the current request for later
            SavedRequest savedRequest = requestCache.getRequest(httpServletRequest, httpServletResponse);
            if (savedRequest != null) {
                requestCache.saveRequest(httpServletRequest, httpServletResponse);
            }
        }

        // Proceed with the filter chain
        chain.doFilter(request, response);
    }
}