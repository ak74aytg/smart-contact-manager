package com.smart.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.entities.User;
import com.smart.repository.UserRepository;

public class CustomUserdetailsService implements UserDetailsService {
	@Autowired
	UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getUserByUsername(username);
		if(user==null) {
			throw new UsernameNotFoundException("no user available with this username");
		}
		CustomUser customUser = new CustomUser(user);
		
		return customUser;
	}

}
