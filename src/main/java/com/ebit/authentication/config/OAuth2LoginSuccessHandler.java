package com.ebit.authentication.config;

import com.ebit.authentication.entity.User;
import com.ebit.authentication.payloads.OAuth2UserDto;
import com.ebit.authentication.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private AuthService authService;

    private static OAuth2UserDto getoAuth2UserDto(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
        OidcUser oidcUser = (OidcUser) oAuth2AuthenticationToken.getPrincipal();
        OAuth2UserDto auth2User = new OAuth2UserDto();
        auth2User.setFirstName(oidcUser.getGivenName());
        auth2User.setLastName(oidcUser.getFamilyName());
        auth2User.setEmail(oidcUser.getEmail());
        auth2User.setPhone(oidcUser.getPhoneNumber());
        auth2User.setImgUrl(oidcUser.getPicture());
        auth2User.setOauth2Id(oidcUser.getSubject());
        auth2User.setOauth2Provider(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
        return auth2User;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OAuth2UserDto auth2User = getoAuth2UserDto(oAuth2AuthenticationToken);
        User createdUser = authService.saveOrUpdateUser(auth2User);
        log.info("Authenticated success with provider: {} and email: {}", createdUser.getOauth2Provider(), createdUser.getEmail());
        response.sendRedirect("/");
    }
}


