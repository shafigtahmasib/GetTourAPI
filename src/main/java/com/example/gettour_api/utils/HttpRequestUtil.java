package com.example.gettour_api.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

public class HttpRequestUtil {

    public static String getUserMailFromHeader(HttpServletRequest request, String header){
        final String requestTokenHeader = request.getHeader(header);
        String[] chunks = requestTokenHeader.substring(7).split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        String[] chunksPayload = payload.split("\"");
        return chunksPayload[3];
    }

}