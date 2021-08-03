package com.example.gettour_api.services.interfaces;

import com.example.gettour_api.dtos.AgentMessageDTO;

public interface ConsumerService {
    void consumeRequestMessageFromQueue(Object message);
    void consumeAcceptedMessageFromQueue(AgentMessageDTO agentMessageDTO);
    void consumeAcceptedMessageFromQueue(String message);
}
