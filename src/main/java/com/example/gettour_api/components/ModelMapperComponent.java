package com.example.gettour_api.components;

import com.example.gettour_api.dtos.AgentOfferDTO;
import com.example.gettour_api.dtos.RequestDTO;
import com.example.gettour_api.models.AgentOffer;
import com.example.gettour_api.models.Request;
import org.springframework.stereotype.Component;

@Component
public class ModelMapperComponent {

    public AgentOfferDTO convertAgentOfferToAgentOfferDTO(AgentOffer agentOffer){
        return AgentOfferDTO.builder()
                .request(agentOffer.getRequest())
                .price(agentOffer.getPrice())
                .date(agentOffer.getDate())
                .description(agentOffer.getDescription())
                .build();
    }

    public RequestDTO convertRequestToRequestDTO(Request request){
        return RequestDTO.builder()
                .id(request.getId())
                .data(request.getData())
                .status(request.getStatus())
                .userContact(request.getUserContact())
                .requestedTime(request.getRequestedTime())
                .requestedDeadLine(request.getRequestedDeadLine())
                .build();
    }

}
