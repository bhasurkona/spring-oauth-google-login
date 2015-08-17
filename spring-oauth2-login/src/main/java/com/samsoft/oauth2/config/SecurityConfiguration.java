/**
 * 
 */
package com.samsoft.oauth2.config;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import com.samsoft.oauth2.filter.GoogleOAuth2Filter;
import com.samsoft.oauth2.filter.GoogleOauth2AuthProvider;
import com.samsoft.oauth2.service.CustomUserService;

/**
 * Configuration class for security
 * 
 * @author Diva
 * 
 */
@Configuration
@EnableWebMvcSecurity
@EnableGlobalAuthentication
@EnableOAuth2Client
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@PropertySource(value = { "classpath:googleoauth.properties" })
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * Scope list for Google OAuth
	 */
	private static final List<String> scopes;

	static {
		scopes = new ArrayList<>(3);
		scopes.add("https://www.googleapis.com/auth/plus.profile.emails.read");
		scopes.add("https://www.googleapis.com/auth/plus.login");
		scopes.add("https://www.googleapis.com/auth/userinfo.email");
	}

	@Value(value = "${google.client.id}")
	private String googleClientId;

	@Value(value = "${google.client.secret}")
	private String googleClientSecret;

	@Value(value = "${google.access.token.uri}")
	private String googleAcessTokenUri;

	@Value(value = "${google.authorization.url}")
	private String googleAuhorizationUrl;

	@Value(value = "${google.authorizatoin.code}")
	private String googleAuthorizationCode;

	@Value(value = "${google.preestablished.redirect.url}")
	private String googlePreEstabledUrl;

	@Autowired
	private CustomUserService customUserService;
	
	@Resource
	@Qualifier("accessTokenRequest")
	private AccessTokenRequest accessTokenRequest;
	
	@Autowired
	private OAuth2ClientContextFilter oAuth2ClientContextFilter;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.config.annotation.web.configuration.
	 * WebSecurityConfigurerAdapter
	 * #configure(org.springframework.security.config
	 * .annotation.web.builders.HttpSecurity)
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		// @formatter:off
		http.
		authorizeRequests()
			.antMatchers(HttpMethod.GET, "/login","/public/**", "/resources/**","/resources/public/**").permitAll()
			.antMatchers("/google_oauth2_login").anonymous()
			.anyRequest().authenticated()
			.and()
			.formLogin()
				.loginPage("/login")
				.loginProcessingUrl("/login")
				.defaultSuccessUrl("/")
				.and()
				.csrf().disable()
				.logout()
					.logoutSuccessUrl("/")
					.logoutUrl("/logout")
			.and()
				.requiresChannel().anyRequest().requiresSecure()
			.and()
				.addFilterAfter(oAuth2ClientContextFilter,ExceptionTranslationFilter.class)
				.addFilterAfter(googleOAuth2Filter(),OAuth2ClientContextFilter.class)
			.userDetailsService(customUserService);
		// @formatter:on
	}

	@Bean
	public OAuth2ProtectedResourceDetails auth2ProtectedResourceDetails() {
		AuthorizationCodeResourceDetails auth2ProtectedResourceDetails = new AuthorizationCodeResourceDetails();
		auth2ProtectedResourceDetails
				.setClientAuthenticationScheme(AuthenticationScheme.form);
		auth2ProtectedResourceDetails
				.setAuthenticationScheme(AuthenticationScheme.form);
		auth2ProtectedResourceDetails.setGrantType(googleAuthorizationCode);
		auth2ProtectedResourceDetails.setClientId(googleClientId);
		auth2ProtectedResourceDetails.setClientSecret(googleClientSecret);
		auth2ProtectedResourceDetails.setAccessTokenUri(googleAcessTokenUri);
		auth2ProtectedResourceDetails.setScope(scopes);
		auth2ProtectedResourceDetails
				.setUserAuthorizationUri(googleAuhorizationUrl);
		auth2ProtectedResourceDetails.setUseCurrentUri(false);
		auth2ProtectedResourceDetails
				.setPreEstablishedRedirectUri(googlePreEstabledUrl);
		return auth2ProtectedResourceDetails;
	}

	@Bean
	public OAuth2RestTemplate oauth2RestTemplate() {
	    return new OAuth2RestTemplate(auth2ProtectedResourceDetails(), new DefaultOAuth2ClientContext(accessTokenRequest));
	}


	@Bean
	public GoogleOAuth2Filter googleOAuth2Filter() {
		GoogleOAuth2Filter googleOAuth2Filter = new GoogleOAuth2Filter(
				"/google_oauth2_login");
		return googleOAuth2Filter;
	}

	@Bean
	public GoogleOauth2AuthProvider googleOauth2AuthProvider() {
		GoogleOauth2AuthProvider googleOauth2AuthProvider = new GoogleOauth2AuthProvider();
		return googleOauth2AuthProvider;
	}

}
