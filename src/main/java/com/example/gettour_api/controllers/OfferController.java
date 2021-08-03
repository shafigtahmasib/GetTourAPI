package com.example.gettour_api.controllers;

import com.example.gettour_api.dtos.AgentOfferDTO;
import com.example.gettour_api.services.interfaces.AgentOfferService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/offer")
public class OfferController {

    private final AgentOfferService agentOfferService;

    @GetMapping
    public ResponseEntity<List<AgentOfferDTO>> getAgentOffers(HttpServletRequest httpServletRequest){
        return new ResponseEntity<>(agentOfferService.getAgentOffers(httpServletRequest), OK);
    }

}