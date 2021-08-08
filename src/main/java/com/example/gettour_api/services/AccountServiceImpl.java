package com.example.gettour_api.services;

import com.example.gettour_api.exceptions.PasswordsNotMatchingException;
import com.example.gettour_api.models.AppUser;
import com.example.gettour_api.dtos.UserDTO;
import com.example.gettour_api.enums.AppUserRole;
import com.example.gettour_api.models.ConfirmationToken;
import com.example.gettour_api.services.interfaces.AccountService;
import com.example.gettour_api.services.interfaces.ConfirmationTokenService;
import com.example.gettour_api.services.interfaces.EmailService;
import com.example.gettour_api.utils.EmailValidator;
import com.example.gettour_api.utils.jwt.JwtTokenUtil;
import com.example.gettour_api.utils.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AppUserService appUserService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final JwtUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final EmailValidator emailValidator;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public String resetPassword(String email, String newPassword, String confirmedNewPassword){

            AppUser appUser = appUserService.findAppUserByEmail(email);
            if(newPassword.equals(confirmedNewPassword)){
                String token = UUID.randomUUID()+"password";
                ConfirmationToken confirmationToken = new ConfirmationToken(
                        token,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusMinutes(15),
                        appUser
                );
                confirmationTokenService.saveConfirmationToken(confirmationToken);
                String link = "http://localhost:8082/account/confirm-password?token="+token+"&newPassword="+newPassword;
                String status = "password";
                emailService.send(email, buildEmail(appUser.getAgentName(), link), status);
                return "Please check your email to reset the password";
            }
            else throw new PasswordsNotMatchingException("Passwords do not match");
    }

    @Override
    public String changePassword(HttpServletRequest request, String newPassword, String confirmedNewPassword){
        if(newPassword.equals(confirmedNewPassword)){
            AppUser appUser = appUserService.findAppUserByEmail(HttpRequestUtil.getUserMailFromHeader(request, "Authorization"));
            appUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
            appUserService.save(appUser);
            return "Password has been changed successfully";
        }
        else throw new PasswordsNotMatchingException("Passwords do not match");
    }

    @Override
    public String authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);



        return jwtTokenUtil.generateToken(userDetails);
    }

    @Override
    public String register(UserDTO request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());

        if(!isValidEmail) throw new IllegalStateException("Email is not valid");

        String token = appUserService.signUpUser(new AppUser(
                request.getTIN(),
                request.getAgentName(),
                request.getCompanyName(),
                request.getEmail(),
                request.getPassword(),
                AppUserRole.USER
        ));

        String link = "http://localhost:8082/account/confirm?token=" + token;
        emailService.send(request.getEmail(), buildEmail(request.getAgentName(), link), "email");
        return "Please, check your mail to verify your account";
    }

    @Override
    public String confirmPasswordToken(String token, String newPassword) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token);

        if (confirmationToken.getConfirmedAt() != null) throw new IllegalStateException("Password has already been reset");

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) throw new IllegalStateException("token expired");

        confirmationTokenService.setConfirmedAt(token);
        Long id = confirmationToken.getAppUser().getId();
        AppUser appUser = appUserService.findAppUserById(id);
        appUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
        appUserService.save(appUser);
        return "Password has been reset successfully";
    }

    @Override
    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByToken(token);
        if (confirmationToken.getConfirmedAt() != null) throw new IllegalStateException("email already confirmed");
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) throw new IllegalStateException("token expired");
        confirmationTokenService.setConfirmedAt(token);
        appUserService.enableAppUser(confirmationToken.getAppUser().getEmail());
        return "Email has been confirmed successfully";
    }

    @Override
    public String buildEmail(String name, String link) {
        if(link.contains("confirm-password")) return "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Please click on the below link to reset your password: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Reset Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>";
        else return "<p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>";
    }
}