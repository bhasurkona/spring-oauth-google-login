/**
 * 
 */
package com.samsoft.oauth2.filter;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.samsoft.oauth2.model.CustomUserDetails;

/**
 * @author Diva
 * 
 */
public class CustomOAuth2AuthenticationToken extends
		AbstractAuthenticationToken {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8254831403638075928L;

	private CustomUserDetails registeredUser;

	public CustomOAuth2AuthenticationToken(
			Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
	}

	/**
	 * @param authorities
	 * @param registeredUser
	 */
	public CustomOAuth2AuthenticationToken(CustomUserDetails registeredUser) {
		super(CustomUserDetails.DEFAULT_ROLES);
		this.registeredUser = registeredUser;
	}

	@Override
	public Object getCredentials() {
		return "NOT_REQUIRED";
	}

	@Override
	public Object getPrincipal() {
		return registeredUser;
	}

	/**
	 * @return the registeredUser
	 */
	public CustomUserDetails getUserDetail() {
		return registeredUser;
	}

	/**
	 * @param registeredUser
	 *            the registeredUser to set
	 */
	public void setUserDetail(CustomUserDetails registeredUser) {
		this.registeredUser = registeredUser;
		setDetails(registeredUser);
	}
}
