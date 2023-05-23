package com.castlabs.mp4Analyzer.helpers;

import com.castlabs.mp4Analyzer.model.Box;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BoxHelper {

    public static final String MP4_FILE_BOX = "mp4File";

    public static final List<String> NESTABLE_BOX_TYPES = Arrays.asList("moof", "traf");

    public static final Box TERMINAL_BOX = new Box("TERMINAL_BOX", 0);

    private Map<BufferedInputStream, Integer> inputStreamMap = new HashMap<>();

    /**
     * Get next box if one exists
     * @return
     * @throws IOException
     */
    public Box getNextBox(BufferedInputStream in) throws IOException {

        byte[] buffer = new byte[8];

        if (in.read(buffer, 0, 8) != -1) {

            increaseBytesProcessed(in, 8);

            return buildBox(buffer);
        }

        return TERMINAL_BOX;
    }

    /**
     * Skip payload and return next box if one exists
     *
     * @param currentBox
     * @throws IOException
     */
    public Box skipPayload(BufferedInputStream in, Box currentBox) throws IOException {

        if (in.read(new byte[currentBox.getSize()], 0, currentBox.getSize() - 8) == -1) {
            increaseBytesProcessed(in, currentBox.getSize() - 8);
            return TERMINAL_BOX;
        }

        increaseBytesProcessed(in, currentBox.getSize() - 8);

        return getNextBox(in);
    }

    /**
     * Build a box object from box header data
     *
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
        // System.out.println(boxSize);

        System.arraycopy(headerBuffer, 4, boxPropertyBuffer, 0, boxPropertyBuffer.length);
        String boxType = new String(boxPropertyBuffer, charset);
        //System.out.println(boxType);

        if (NESTABLE_BOX_TYPES.contains(boxType)) {
            return new Box(boxType, boxSize, new ArrayList<>());
        }

        return new Box(boxType, boxSize);
    }


    public Integer getTotalBytesProcessed(BufferedInputStream in) {
        return inputStreamMap.get(in);
    }

    private void increaseBytesProcessed(BufferedInputStream in, int bytesProcessed) {
        inputStreamMap.put(in, inputStreamMap.getOrDefault(in,0) + bytesProcessed);
    }

}
