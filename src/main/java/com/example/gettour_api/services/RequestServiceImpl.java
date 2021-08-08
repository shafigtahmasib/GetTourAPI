package com.example.gettour_api.services;

import com.example.gettour_api.components.ModelMapperComponent;
import com.example.gettour_api.configs.RabbitMQConfig;
import com.example.gettour_api.dtos.OfferDTO;
import com.example.gettour_api.dtos.RequestDTO;
import com.example.gettour_api.enums.RequestStatus;
import com.example.gettour_api.exceptions.OfferNotFoundException;
import com.example.gettour_api.exceptions.UnableSendOfferException;
import com.example.gettour_api.models.AgentOffer;
import com.example.gettour_api.models.AppUser;
import com.example.gettour_api.dtos.OfferDataDTO;
import com.example.gettour_api.models.Request;
import com.example.gettour_api.repositories.AgentOfferRepository;
import com.example.gettour_api.repositories.RequestRepository;
import com.example.gettour_api.services.interfaces.RequestService;
import com.example.gettour_api.utils.ImageUtil;
import com.example.gettour_api.utils.HttpRequestUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {

    @Value("${work-hours.start-time}")
    private String startTime;
    @Value("${work-hours.end-time}")
    private String endTime;

    private final RequestRepository requestRepository;
    private final AgentOfferRepository agentOfferRepository;
    private final AppUserService appUserService;
    private final RabbitTemplate rabbitTemplate;
    private final ModelMapperComponent modelMapperComponent;

    /**
     This method works in every minute and checks that if any of requests are reached deadline or not.
     If it gets one changes the status to expired
     */

    @Override
    @Scheduled(cron = "* * * * * *")
    public void checkDeadline(){
        List<Request> requestList = requestRepository.findAll();
        for(Request request : requestList){
            if(!request.getStatus().equals(RequestStatus.ACCEPTED) && LocalDateTime.now().compareTo(request.getRequestedDeadLine()) > 0 && request.getStatus().equals(RequestStatus.NEW_REQUEST)){
                request.setStatus(RequestStatus.EXPIRED);
                request.setIsArchived(true);
                requestRepository.save(request);
            }
        }
    }

    @Override
    public List<RequestDTO> getAllRequests(HttpServletRequest httpServletRequest) {
        AppUser appUser = appUserService.findAppUserByEmail(HttpRequestUtil.getUserMailFromHeader(httpServletRequest, "Authorization"));
        return requestRepository
                .getRequestByAgent_IdAndIsArchivedIsFalse(appUser.getId())
                .stream()
                .map(modelMapperComponent::convertRequestToRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDTO> getOfferMadeRequests(HttpServletRequest httpServletRequest) {
        AppUser appUser = appUserService.findAppUserByEmail(HttpRequestUtil.getUserMailFromHeader(httpServletRequest, "Authorization"));
        return requestRepository.getRequestByAgent_IdAndStatus(appUser.getId(), RequestStatus.OFFER_MADE)
                .stream()
                .map(modelMapperComponent::convertRequestToRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RequestDTO> getArchivedRequests(HttpServletRequest httpServletRequest) {
        AppUser appUser = appUserService.findAppUserByEmail(HttpRequestUtil.getUserMailFromHeader(httpServletRequest, "Authorization"));
        return requestRepository.getRequestByAgent_IdAndIsArchivedIsTrue(appUser.getId())
                .stream()
                .map(modelMapperComponent::convertRequestToRequestDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String moveToArchive(Long id, HttpServletRequest httpServletRequest) {
        AppUser appUser = appUserService.findAppUserByEmail(HttpRequestUtil.getUserMailFromHeader(httpServletRequest, "Authorization"));
        Request request = requestRepository.getRequestByIdAndAgent_Id(id, appUser.getId()).orElseThrow(() ->
                new IllegalStateException("Offer not found"));
        request.setIsArchived(true);
        requestRepository.save(request);
        return "Success";
    }

    /**
     With this method agent send offer to the user. Agent can send offers only with the new request status.
     Also, agent can send offers only in the given interval in the properties file.
     */

    @Override
    public String sendOffer(Long id, HttpServletRequest httpServletRequest, OfferDataDTO offerDataDTO) throws IOException {
        AppUser appUser = appUserService.findAppUserByEmail(HttpRequestUtil.getUserMailFromHeader(httpServletRequest, "Authorization"));
        Request request = requestRepository.getRequestByIdAndAgent_Id(id, appUser.getId())
                .orElseThrow(() -> new OfferNotFoundException("Offer not found"));

        if(!request.getStatus().equals(RequestStatus.NEW_REQUEST))
            throw new UnableSendOfferException("Can not send offer for selected request with the status: "+request.getStatus());

        if(LocalTime.now().compareTo(LocalTime.parse(startTime)) < 0 || LocalTime.now().compareTo(LocalTime.parse(endTime)) > 0) return "Non working hour";

        else {
            agentOfferRepository.save(AgentOffer.builder().price(offerDataDTO.getPrice()).date(offerDataDTO.getDate()).description(offerDataDTO.getDescription()).request(request).build());
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> map = mapper.readValue(request.getData(), new TypeReference<>() {});
            request.setStatus(RequestStatus.OFFER_MADE);
            requestRepository.save(request);
            BufferedImage image = ImageUtil.writeTextOnImage(Arrays.asList(offerDataDTO.getPrice().toString(), offerDataDTO.getDate(), offerDataDTO.getDescription()));
            byte[] imageArray = ImageUtil.imageToByteArray(image);
            rabbitTemplate.convertAndSend(RabbitMQConfig.exchange,
                    RabbitMQConfig.AGENT_ROUTING_KEY,
                    OfferDTO.builder().agentId(appUser.getId()).clientId(map.get("telegramIdentifier")).imgArray(imageArray).build());
            return "Success";
        }
    }
}