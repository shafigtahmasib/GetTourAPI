package com.example.gettour_api.services;


import com.example.gettour_api.exceptions.UserNotFoundException;
import com.example.gettour_api.models.AppUser;
import com.example.gettour_api.repositories.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return appUserRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found with email: " +email));
    }
}