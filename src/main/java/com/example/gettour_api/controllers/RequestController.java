package com.example.gettour_api.controllers;

import com.example.gettour_api.dtos.OfferDataDTO;
import com.example.gettour_api.dtos.RequestDTO;
import com.example.gettour_api.exceptions.CompanyExistsException;
import com.example.gettour_api.exceptions.OfferNotFoundException;
import com.example.gettour_api.exceptions.UnableMoveToArchiveException;
import com.example.gettour_api.exceptions.UnableSendOfferException;
import com.example.gettour_api.services.interfaces.RequestService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/request")
public class RequestController {

    private final RequestService requestService;

    @ExceptionHandler(OfferNotFoundException.class)
    public ResponseEntity<String> handlerNotFoundException(OfferNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnableSendOfferException.class)
    public ResponseEntity<String> handlerNotFoundException(UnableSendOfferException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnableMoveToArchiveException.class)
    public ResponseEntity<String> handlerNotFoundException(UnableMoveToArchiveException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RequestDTO>> getAllRequests(HttpServletRequest httpServletRequest){
        return new ResponseEntity<>(requestService.getAllRequests(httpServletRequest), HttpStatus.OK);
    }

    @GetMapping("/archived")
    public ResponseEntity<List<RequestDTO>> getArchivedRequests(HttpServletRequest httpServletRequest){
        return new ResponseEntity<>(requestService.getArchivedRequests(httpServletRequest), HttpStatus.OK);
    }

    @GetMapping("/offer-made")
    public ResponseEntity<List<RequestDTO>> getOfferMadeRequests(HttpServletRequest httpServletRequest){
        return new ResponseEntity<>(requestService.getOfferMadeRequests(httpServletRequest), HttpStatus.OK);
    }

    @PutMapping("{id}/send-offer")
    public ResponseEntity<String> sendOffer(HttpServletRequest request, @PathVariable Long id, @RequestBody OfferDataDTO offerDataDTO) throws IOException {
        return new ResponseEntity<>(requestService.sendOffer(id, request, offerDataDTO), HttpStatus.OK);
    }

    @PutMapping("{id}/move-archive")
    public ResponseEntity<String> moveToArchive(HttpServletRequest request, @PathVariable Long id) {
        return new ResponseEntity<>(requestService.moveToArchive(id, request), HttpStatus.OK);
    }

    @PutMapping("{id}/unarchive")
    public ResponseEntity<String> removeFromArchive(HttpServletRequest request, @PathVariable Long id) {
        return new ResponseEntity<>(requestService.removeFromArchive(id, request), HttpStatus.OK);
    }
}