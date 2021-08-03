package com.example.gettour_api.dtos;

import lombok.Builder;
import lombok.Data;
import java.io.Serializable;

@Builder
@Data
public class OfferDTO implements Serializable {
    private String clientId;
    private byte[] imgArray;
    private Long agentId;
}