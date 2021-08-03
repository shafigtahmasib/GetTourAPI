package com.example.gettour_api.services.interfaces;

import com.example.gettour_api.models.ConfirmationToken;

public interface ConfirmationTokenService {
    ConfirmationToken findByToken(String token);
    void setConfirmedAt(String token);
    void saveConfirmationToken(ConfirmationToken token);
}
