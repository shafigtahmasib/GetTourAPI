package com.example.gettour_api.dtos;

import com.example.gettour_api.models.Request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AgentOfferDTO {
    private Request request;
    private Integer price;
    private String date;
    private String description;
}
