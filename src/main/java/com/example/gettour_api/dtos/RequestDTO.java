package com.example.gettour_api.dtos;

import com.example.gettour_api.enums.RequestStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder
@Data
public class RequestDTO {
    private Long id;
    private String data;
    private RequestStatus status;
    private String userContact;
    private LocalDateTime requestedTime;
    private LocalDateTime requestedDeadLine;
}
