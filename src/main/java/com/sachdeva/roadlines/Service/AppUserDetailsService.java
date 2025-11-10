package com.sachdeva.roadlines.Service;

import java.util.List;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.sachdeva.roadlines.Entity.UserEntity;
import com.sachdeva.roadlines.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserEntity existingUser = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Email not found: " + email));

		// Convert String role -> GrantedAuthority
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(existingUser.getRole());

		/* If you want to store role in DB as "ADMIN" (without prefix) */
		// SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" +
		// existingUser.getRole());

		/* we don't have any Roles so pass empty ArrayList */
		// return new User(existingUser.getEmail(), existingUser.getPassword(), new
		// ArrayList<>());

		return new User(existingUser.getEmail(), existingUser.getPassword(), List.of(authority));
	}
}
