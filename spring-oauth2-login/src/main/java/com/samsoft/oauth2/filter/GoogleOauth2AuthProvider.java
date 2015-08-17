/**
 * 
 */
package com.samsoft.oauth2.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.samsoft.oauth2.model.CustomUserDetails;
import com.samsoft.oauth2.service.CustomUserService;

/**
 * @author Diva
 * 
 */
public class GoogleOauth2AuthProvider implements AuthenticationProvider {

	private static final Logger logger = LoggerFactory
			.getLogger(GoogleOauth2AuthProvider.class);

	@Autowired(required = true)
	private CustomUserService userService;

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		logger.info("Provider Manager Executed ?!!");
		CustomOAuth2AuthenticationToken token = (CustomOAuth2AuthenticationToken) authentication;
		CustomUserDetails registeredUser = (CustomUserDetails) token
				.getPrincipal();
		try {
			registeredUser = (CustomUserDetails) userService
					.loadUserByUsername(registeredUser.getUsername());
		} catch (UsernameNotFoundException usernameNotFoundException) {
			logger.info("User trying google/login not already a registered user. Register Him !!");
			token = new CustomOAuth2AuthenticationToken(registeredUser);
			token.setAuthenticated(true);
		}
		return token;
	}

	@Override
	public boolean supports(Class<? extends Object> authentication) {
		return (CustomOAuth2AuthenticationToken.class
				.isAssignableFrom(authentication));
	}
}
