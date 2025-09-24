package com.app.service.impl;

import com.app.entity.User;
import com.app.entity.UserRole;
import com.app.repository.UserRepository;
import com.app.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
    private UserRoleRepository userRoleRepository;

	 @Override
	    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
	        User user = userRepository.findByUserName(userName)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userName));
		 
//		 User user = userRepository.findByUserNameOrContactNumber(loginId, loginId)
//		            .orElseThrow(() -> new UsernameNotFoundException("User not found with username or contact: " + loginId));


	        List<UserRole> roles = userRoleRepository.findByUser(user);
	        List<SimpleGrantedAuthority> authorities = roles.stream()
	                .map(r -> new SimpleGrantedAuthority("ROLE_" + r.getRole().getRoleName()))
	                .collect(Collectors.toList());

	        return org.springframework.security.core.userdetails.User
	                .withUsername(user.getUserName())  
	                .password(user.getPassword())
	                .authorities(authorities)
	                .accountExpired(false)
	                .accountLocked(false)
	                .credentialsExpired(false)
	                .disabled(!user.getIsActive())
	                .build();
	    }
}
