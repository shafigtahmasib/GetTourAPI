package com.example.gettour_api.services.interfaces;

import com.example.gettour_api.dtos.OfferDataDTO;
import com.example.gettour_api.dtos.RequestDTO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface RequestService {
    String sendOffer(Long id, HttpServletRequest request, OfferDataDTO offerDataDTO) throws IOException;
    String moveToArchive(Long id, HttpServletRequest httpServletRequest);
    String removeFromArchive(Long id, HttpServletRequest httpServletRequest);
    void checkDeadline();
    List<RequestDTO> getAllRequests(HttpServletRequest httpServletRequest);
    List<RequestDTO> getArchivedRequests(HttpServletRequest httpServletRequest);
    List<RequestDTO> getOfferMadeRequests(HttpServletRequest httpServletRequest);
}
