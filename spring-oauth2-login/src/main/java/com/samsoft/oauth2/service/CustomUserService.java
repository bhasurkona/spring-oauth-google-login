/**
 * 
 */
package com.samsoft.oauth2.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.samsoft.oauth2.model.CustomUserDetails;

/**
 * 
 * Application specific custom {@link UserDetailsService} implementation that
 * gets user details from backend.
 * 
 * This can be RDMS, Mongo...
 * 
 * 
 * @author Kumar Sambhav Jain
 * 
 */
@Service
public class CustomUserService implements UserDetailsService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#
	 * loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		return new CustomUserDetails("sambhav", "sambhav");
	}

}
