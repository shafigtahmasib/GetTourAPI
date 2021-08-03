package com.example.gettour_api.models;

import com.example.gettour_api.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="requests")
@Entity
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String data;
    @ManyToOne
    private AppUser agent;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    private String userContact;
    private LocalDateTime requestedTime;
    private LocalDateTime requestedDeadLine;
    private Boolean isArchived;
}