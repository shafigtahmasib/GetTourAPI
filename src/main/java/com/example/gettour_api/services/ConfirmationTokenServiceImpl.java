package com.example.gettour_api.services;

import com.example.gettour_api.models.ConfirmationToken;
import com.example.gettour_api.repositories.ConfirmationTokenRepository;
import com.example.gettour_api.services.interfaces.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public ConfirmationToken findByToken(String token) {
        return confirmationTokenRepository.findByToken(token).orElseThrow(() ->
                new IllegalStateException("token not found"));
    }

    @Override
    public void setConfirmedAt(String token) {
        confirmationTokenRepository.updateConfirmedAt(
                token, LocalDateTime.now());
    }

    @Override
    public void saveConfirmationToken(ConfirmationToken token){
        confirmationTokenRepository.save(token);
    }
}