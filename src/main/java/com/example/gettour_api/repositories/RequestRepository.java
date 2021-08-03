package com.example.gettour_api.repositories;

import com.example.gettour_api.enums.RequestStatus;
import com.example.gettour_api.models.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Request getRequestById(Long id);
    Optional<Request> getRequestByIdAndAgent_Id(Long id, Long agentId);
    Request getRequestByAgent_IdAndDataContains(Long id, String tgId);
    List<Request> getRequestByAgent_IdAndIsArchivedIsTrue(Long id);
    List<Request> getRequestByAgent_IdAndIsArchivedIsFalse(Long id);
    List<Request> getRequestByAgent_IdAndStatus(Long id, RequestStatus requestStatus);
    List<Request> getAllByAgent_Id(Long id);
    List<Request> getAllByDataContains(String tgId);
}
