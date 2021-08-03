package com.example.gettour_api.services.interfaces;

import com.example.gettour_api.dtos.AgentOfferDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface AgentOfferService {
    List<AgentOfferDTO> getAgentOffers(HttpServletRequest httpServletRequest);
}
