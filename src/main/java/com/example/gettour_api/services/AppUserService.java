package com.example.gettour_api.services;

import com.example.gettour_api.exceptions.CompanyExistsException;
import com.example.gettour_api.exceptions.EmailExistsException;
import com.example.gettour_api.exceptions.UserNotFoundException;
import com.example.gettour_api.models.AppUser;
import com.example.gettour_api.models.ConfirmationToken;
import com.example.gettour_api.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final String USER_NOT_FOUND_MSG = "User with email %s not found";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenServiceImpl confirmationTokenServiceImpl;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public AppUser findAppUserByEmail(String email){
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    public void save(AppUser appUser){
        appUserRepository.save(appUser);
    }

    public AppUser findAppUserById(Long id){
        return appUserRepository.findAppUserById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();

        boolean companyExists = appUserRepository.getAppUserByCompanyName(appUser.getCompanyName()).isPresent();

        if(companyExists) throw new CompanyExistsException("Company is already registered");

        if (userExists) throw new EmailExistsException("Email is already taken");

        appUser.setPassword(bCryptPasswordEncoder.encode(appUser.getPassword()));
        appUserRepository.save(appUser);

        ConfirmationToken confirmationToken = new ConfirmationToken(
                UUID.randomUUID()+"email",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        confirmationTokenServiceImpl.saveConfirmationToken(confirmationToken);
        return confirmationToken.getToken();
    }

    public void enableAppUser(String email) {
        appUserRepository.enableAppUser(email);
    }
}