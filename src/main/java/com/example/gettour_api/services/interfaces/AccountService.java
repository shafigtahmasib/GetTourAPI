package com.example.gettour_api.services.interfaces;

import com.example.gettour_api.dtos.UserDTO;

import javax.servlet.http.HttpServletRequest;

public interface AccountService {
    String resetPassword(String email, String newPassword, String confirmedNewPassword);
    String changePassword(HttpServletRequest request, String newPassword, String confirmedNewPassword);
    String authenticate(String username, String password) throws Exception;
    String register(UserDTO request);
    String confirmPasswordToken(String token, String newPassword);
    String confirmToken(String token);
    String buildEmail(String name, String link);
}
