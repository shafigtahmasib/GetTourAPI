package com.example.gettour_api.services.implementations;

import com.example.gettour_api.components.ModelMapperComponent;
import com.example.gettour_api.dtos.AgentOfferDTO;
import com.example.gettour_api.models.AgentOffer;
import com.example.gettour_api.models.AppUser;
import com.example.gettour_api.models.Request;
import com.example.gettour_api.repositories.AgentOfferRepository;
import com.example.gettour_api.repositories.RequestRepository;
import com.example.gettour_api.services.interfaces.AgentOfferService;
import com.example.gettour_api.utils.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AgentOfferServiceImpl implements AgentOfferService {

    private final AppUserService appUserService;
    private final AgentOfferRepository agentOfferRepository;
    private final RequestRepository requestRepository;
    private final ModelMapperComponent modelMapperComponent;

    @Override
    public List<AgentOfferDTO> getAgentOffers(HttpServletRequest httpServletRequest) {
        AppUser appUser = appUserService.findAppUserByEmail(HttpRequestUtil.getUserMailFromHeader(httpServletRequest, "Authorization"));
        List<Request> requestList = requestRepository.getAllByAgent_Id(appUser.getId());
        List<AgentOffer> allOffers = agentOfferRepository.findAll();
        List<AgentOffer> agentOffers = new ArrayList<>();
        for(AgentOffer x: allOffers){
            if(requestList.contains(x.getRequest()))
                agentOffers.add(x);
        }

        return agentOffers
                .stream()
                .map(modelMapperComponent::convertAgentOfferToAgentOfferDTO)
                .collect(Collectors.toList());
    }
}
