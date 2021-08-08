package com.example.gettour_api.controllers;

import com.example.gettour_api.exceptions.CompanyExistsException;
import com.example.gettour_api.exceptions.EmailExistsException;
import com.example.gettour_api.exceptions.EmailIsNotValidException;
import com.example.gettour_api.exceptions.PasswordsNotMatchingException;
import com.example.gettour_api.services.AccountServiceImpl;
import com.example.gettour_api.dtos.UserDTO;
import com.example.gettour_api.utils.jwt.JwtRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/account")
public class AccountController {

    private final AccountServiceImpl accountServiceImpl;

    @ExceptionHandler(CompanyExistsException.class)
    public ResponseEntity<String> handlerNotFoundException(CompanyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<String> handlerNotFoundException(EmailExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(PasswordsNotMatchingException.class)
    public ResponseEntity<String> handlerNotFoundException(PasswordsNotMatchingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<String> handlerNotFoundException(DisabledException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handlerNotFoundException(BadCredentialsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailIsNotValidException.class)
    public ResponseEntity<String> handlerNotFoundException(EmailIsNotValidException ex) {
        return ResponseEntity.ok(ex.getMessage());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO request) {
        return ResponseEntity.ok(accountServiceImpl.register(request));
    }

    @GetMapping(path = "/confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) {
        return ResponseEntity.ok(accountServiceImpl.confirmToken(token));
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<String> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        return ResponseEntity.ok(accountServiceImpl.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword()));
    }

    @PostMapping(value = "/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String confirmedNewPassword) throws IOException {
        return ResponseEntity.ok(accountServiceImpl.resetPassword(email, newPassword, confirmedNewPassword));
    }

    @GetMapping(path = "/confirm-password")
    public String confirmPassword(@RequestParam("token") String token, @RequestParam String newPassword, String email) {
        return accountServiceImpl.confirmPasswordToken(token, newPassword);
    }

    @PostMapping(value = "/logged/change-password")
    public ResponseEntity<?> changePassword(HttpServletRequest request, @RequestParam String newPassword, @RequestParam String confirmedNewPassword){
        return ResponseEntity.ok(accountServiceImpl.changePassword(request, newPassword, confirmedNewPassword));
    }
}