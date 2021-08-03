package com.example.gettour_api.services.implementations;

import com.example.gettour_api.dtos.AgentMessageDTO;
import com.example.gettour_api.enums.RequestStatus;
import com.example.gettour_api.models.AppUser;
import com.example.gettour_api.models.Request;
import com.example.gettour_api.repositories.AppUserRepository;
import com.example.gettour_api.repositories.RequestRepository;
import com.example.gettour_api.services.interfaces.ConsumerService;
import com.example.gettour_api.utils.RequestUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Value("${work-hours.start-time}")
    private String startTime;
    @Value("${work-hours.end-time}")
    private String endTime;
    @Value("${request.duration}")
    private int duration;


    private final RequestRepository requestRepository;
    private final AppUserRepository appUserRepository;

    @Override
    @RabbitListener(queues = "usermsg_queue")
    public void consumeRequestMessageFromQueue(Object message){
        String data = Arrays.asList(message.toString().split("'")).get(1);
        for(AppUser agent: appUserRepository.findAll()) {
            requestRepository.save(Request.builder()
                    .agent(agent)
                    .data(data)
                    .status(RequestStatus.NEW_REQUEST)
                    .requestedTime(LocalDateTime.now())
                    .requestedDeadLine(RequestUtil.getDeadLine(LocalTime.now(), startTime, endTime, duration))
                    .isArchived(false)
                    .build());
        }
    }

    @Override
    @RabbitListener(queues = "acceptedmsg_queue")
    public void consumeAcceptedMessageFromQueue(AgentMessageDTO agentMessageDTO){
        Request request = requestRepository.getRequestByAgent_IdAndDataContains(agentMessageDTO.getAgentId(), agentMessageDTO.getClientId());
        request.setStatus(RequestStatus.ACCEPTED);
        request.setUserContact(agentMessageDTO.getUserContact());
        requestRepository.save(request);
    }

    @Override
    @RabbitListener(queues = "stoppedmsg_queue")
    public void consumeAcceptedMessageFromQueue(String message) {
        List<Request> requestList = requestRepository.getAllByDataContains(message);
        for(Request request: requestList){
            request.setStatus(RequestStatus.EXPIRED);
            requestRepository.save(request);
        }
    }
}