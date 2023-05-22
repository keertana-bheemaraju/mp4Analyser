package com.castlabs.mp4Analyzer.services;

import com.castlabs.mp4Analyzer.model.Box;
import com.castlabs.mp4Analyzer.model.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Service
public class Mp4Service {

    private ObjectMapper om = new ObjectMapper();

    public String analyzeMp4(String mp4Url) throws JsonProcessingException {

        //todo url decode the url param?
        if(mp4Url == null || mp4Url.equals("")) {
            return om.writeValueAsString(new ErrorResponse("empty url"));
        }
        System.out.println(mp4Url);

        parseMp4File(mp4Url);

        return null;
    }

    /**
     * Create top level boxes from an MP4 URL
     * @param mp4Url
     */
    private void parseMp4File(String mp4Url) {
        try (BufferedInputStream in = new BufferedInputStream(new URL(mp4Url).openStream())) {
            byte [] dataBuffer= new byte[8];
            int bytesRead;
            int totalBytesRead = 0;

            while ((bytesRead = in.read(dataBuffer, 0, 8)) != -1) {
                totalBytesRead = totalBytesRead + bytesRead;
                Box currentBox = buildBox(dataBuffer);
                skipToNextBox(in, currentBox);
                totalBytesRead = totalBytesRead + currentBox.getSize() - bytesRead;
                System.out.println("tot-bytes-read: " + totalBytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Build a box object from box header data
     * @param headerBuffer header data
     * @return
     */
    private Box buildBox(byte[] headerBuffer) {
        Charset charset = StandardCharsets.UTF_8;
        byte[] boxPropertyBuffer = new byte[4];

        System.arraycopy(headerBuffer, 0, boxPropertyBuffer, 0, boxPropertyBuffer.length);
        int boxSize = 0;
        for (byte b : boxPropertyBuffer) {
            boxSize = (boxSize << 8) + (b & 0xFF);
        }
        System.out.println(boxSize);

        System.arraycopy(headerBuffer, 4, boxPropertyBuffer, 0, boxPropertyBuffer.length);
        String boxType = new String(boxPropertyBuffer, charset);
        System.out.println(boxType);

        return new Box(boxType, boxSize);
    }

    /**
     * Skip to next box
     * @param in
     * @param currentBox
     * @throws IOException
     */
    private void skipToNextBox(BufferedInputStream in, Box currentBox) throws IOException{
        in.read(new byte[currentBox.getSize()], 0, currentBox.getSize() - 8);
    }


}
