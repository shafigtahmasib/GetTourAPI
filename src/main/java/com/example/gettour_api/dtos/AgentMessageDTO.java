package com.example.gettour_api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AgentMessageDTO {
    private String clientId;
    private Long agentId;
    private Integer messageId;
    private String userContact;
}
