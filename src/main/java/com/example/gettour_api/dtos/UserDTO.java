package com.example.gettour_api.dtos;

import lombok.*;

@Data
public class UserDTO {
    private final String TIN;
    private final String agentName;
    private final String companyName;
    private final String email;
    private final String password;
}