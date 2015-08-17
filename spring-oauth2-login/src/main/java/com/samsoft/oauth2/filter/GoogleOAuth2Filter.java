/**
 * 
 */
package com.samsoft.oauth2.filter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.StringUtils;

import com.samsoft.oauth2.model.CustomUserDetails;

/**
 * @author Diva
 * 
 */
public class GoogleOAuth2Filter extends AbstractAuthenticationProcessingFilter {

	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory
			.getLogger(GoogleOAuth2Filter.class);

	private static final Authentication dummyAuthentication;

	static {
		dummyAuthentication = new UsernamePasswordAuthenticationToken(
				"dummyUserName23452346789", "dummyPassword54245",
				CustomUserDetails.DEFAULT_ROLES);
	}

	@Value(value = "${google.authorization.url}")
	private String googleAuhorizationUrl;

	public GoogleOAuth2Filter(String defaultFilterProcessesUrl) {
		super(defaultFilterProcessesUrl);
	}

	private static final String NAME = "name";
	private static final String EMAIL = "email";
	private static final Logger logger = LoggerFactory
			.getLogger(GoogleOAuth2Filter.class);

	@Autowired
	private OAuth2RestTemplate oauth2RestTemplate;

	/**
	 * 
	 * @param email
	 *            - the username
	 * @param name
	 *            - Full name of the user fetched from oauth2 response.
	 * @return CustomOAuth2AuthenticationToken with RegisteredUser serving as
	 *         the principal.
	 */
	private CustomOAuth2AuthenticationToken getOAuth2Token(String email,
			String name) {
		CustomUserDetails registeredUser = new CustomUserDetails(email, "test");

		CustomOAuth2AuthenticationToken authenticationToken = new CustomOAuth2AuthenticationToken(
				registeredUser);
		return authenticationToken;
	}

	@Autowired
	@Override
	public void setAuthenticationManager(
			AuthenticationManager authenticationManager) {
		super.setAuthenticationManager(authenticationManager);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.web.authentication.
	 * AbstractAuthenticationProcessingFilter
	 * #attemptAuthentication(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request,
			HttpServletResponse response) throws AuthenticationException,
			IOException, ServletException {
		logger.info("Google Oauth Filter Triggered!!");
		URI authURI;
		try {
			authURI = new URI(googleAuhorizationUrl);
		} catch (URISyntaxException e) {
			log.error("\n\n\n\nERROR WHILE CREATING GOOGLE AUTH URL", e);
			return null;
		}
		SecurityContext context = SecurityContextHolder.getContext();
		// auth null or not authenticated.
		String code = request.getParameter("code");
		Map<String, String[]> parameterMap = request.getParameterMap();
		logger.debug(parameterMap.toString());
		if (StringUtils.isEmpty(code)) {
			// Google authentication in progress. will return null.
			logger.debug("Will set dummy user in context ");
			context.setAuthentication(dummyAuthentication);
			// trigger google oauth2.
			oauth2RestTemplate.postForEntity(authURI, null, Object.class);
			return null;
		} else {
			// response from google received !!.
			// remove dummy authentication from context.
		//	SecurityContextHolder.clearContext();
			logger.info("Response from Google Recieved !!");
			// get user profile and prepare the authentication token object.
			OAuth2ClientContext oAuth2ClientContext = oauth2RestTemplate.getOAuth2ClientContext();
			//OAuth2AccessToken accessToken = oauth2RestTemplate.getAccessToken();
			//log.debug("Access Toke ",accessToken.getValue());
			ResponseEntity<Object> forEntity = oauth2RestTemplate.getForEntity(
					"https://www.googleapis.com/plus/v1/people/me/openIdConnect",
					Object.class);

			@SuppressWarnings("unchecked")
			Map<String, String> profile = (Map<String, String>) forEntity
					.getBody();
			
			System.out.println("Email :: " + profile.get(EMAIL) + "," + "Name:: " + profile.get(NAME));
			
			CustomOAuth2AuthenticationToken authenticationToken = getOAuth2Token(
					profile.get(EMAIL), profile.get(NAME));
			
			System.out.println("Auth token :: " + authenticationToken);
			authenticationToken.setAuthenticated(false);
			
			return getAuthenticationManager().authenticate(authenticationToken);
		}
	}
}
