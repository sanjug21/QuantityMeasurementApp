package com.example.quantity_measurement.security;

import com.example.quantity_measurement.entity.User;
import com.example.quantity_measurement.repository.UserManagementRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserManagementRepository userManagementRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");

        User user = userManagementRepository.findByEmail(email);

        if (user == null) {
            user = User.builder()
                    .email(email)
                    .username(name != null ? name : email)
                    .provider("google")
                    .providerId(providerId)
                    .password(UUID.randomUUID().toString()) // random password for oauth users
                    .build();
            userManagementRepository.save(user);
        }

        String token = jwtService.generateToken(user);
        
        // Redirect to frontend with token
        // In a real application, the redirect URI should be configured in application.properties
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();
                
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
