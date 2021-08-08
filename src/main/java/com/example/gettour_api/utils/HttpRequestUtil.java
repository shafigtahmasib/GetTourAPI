package com.example.gettour_api.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

public class HttpRequestUtil {

    /**
     This method is used for decode token and take the mail of app user
     */

    public static String getUserMailFromHeader(HttpServletRequest request, String header){
        String requestTokenHeader = request.getHeader(header);

        //for testing purpose
        if(requestTokenHeader==null)
            requestTokenHeader="Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJzaGFmaWd0YWhtYXNpYkBnbWFpbC5jb20iLCJleHAiOjE2MjgxODczMDMsImlhdCI6MTYyODE2OTMwM30.E9h9fjbP3uI0TngXe3JffcRfR4V7xk-0eoob873tVNl727Ou_f1F6VZTiuPudMEp4hqdaVPxePDKvY18jTyGdw";

        String[] chunks = requestTokenHeader.substring(7).split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        String[] chunksPayload = payload.split("\"");
        return chunksPayload[3];
    }

}