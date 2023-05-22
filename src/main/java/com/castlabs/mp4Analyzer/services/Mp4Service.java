package com.castlabs.mp4Analyzer.services;

import com.castlabs.mp4Analyzer.model.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class Mp4Service {

    private ObjectMapper om = new ObjectMapper();

    public String analyzeMp4(String mp4Url) throws JsonProcessingException {

        //todo url decode the url param?
        if(mp4Url == null || mp4Url.equals("")) {
            return om.writeValueAsString(new ErrorResponse("empty url"));
        }

        System.out.println(mp4Url);

        return null;
    }


}
