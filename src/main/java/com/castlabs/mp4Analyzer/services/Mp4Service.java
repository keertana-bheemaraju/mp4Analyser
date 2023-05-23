package com.castlabs.mp4Analyzer.services;

import com.castlabs.mp4Analyzer.model.Box;
import com.castlabs.mp4Analyzer.model.ErrorResponse;
import com.castlabs.mp4Analyzer.model.StackEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

@Service
public class Mp4Service {

    private ObjectMapper om = new ObjectMapper();

    // Sub boxes are pushed to box at top od the stack
    private Stack<StackEntry> stack = new Stack<>();

    private static final String FILETYPE_BOX = "fileTypeBox";

    private static final String TERMIMAL_BOX = "terminalBox";

    // Create the top most container that holds all other boxes
    private Box MP4_FILE = new Box(FILETYPE_BOX, 0, new ArrayList<>());

    private int totalBytesProcessed;

    private BufferedInputStream in;

    private List<String> nestableBoxTypes = Arrays.asList("moof", "traf");

    public String analyzeMp4(String mp4Url) throws IOException {

        if (mp4Url == null || mp4Url.equals("")) {
            return om.writeValueAsString(new ErrorResponse("empty url"));
        }

        in = new BufferedInputStream(new URL(mp4Url).openStream());

        Box firstBox = getNextBox();

        if(firstBox.getType().equals(TERMIMAL_BOX)) {
            return om.writeValueAsString(new ErrorResponse("file is empty"));
        }

        stack.push(new StackEntry(MP4_FILE, 0));

        processBox(firstBox);

        return om.writeValueAsString(MP4_FILE.getSubBoxes());
    }

    private void processBox(Box currentBox) throws IOException {

        //System.out.println(om.writeValueAsString(MP4_FILE.getSubBoxes()));

        if (currentBox.getType().equals(TERMIMAL_BOX)) {
            return;
        }

        adjustTopOfStack();

        stack.peek()
                .getBox()
                .getSubBoxes()
                .add(currentBox);

        if (nestableBoxTypes.contains(currentBox.getType())) {

            stack.push(new StackEntry(currentBox, totalBytesProcessed + currentBox.getSize() - 8));

            processBox(getNextBox());

        } else {
            Box nextBox = skipPayload(currentBox);

            processBox(nextBox);
        }
    }

    /**
     * Ensure in progress nestable box is at the top of stack
     */
    private void adjustTopOfStack() {
        while (!stack.peek().getBox().getType().equals(FILETYPE_BOX)) {
            if (stack.peek().getEndByte() <= totalBytesProcessed) {
                stack.pop();
            } else {
                break;
            }
        }
    }

    /**
     * Get next box if one exists
     * @return
     * @throws IOException
     */
    private Box getNextBox() throws IOException {

        byte[] buffer = new byte[8];

        if (in.read(buffer, 0, 8) != -1) {

            totalBytesProcessed += 8;

            return buildBox(buffer);
        }

        return new Box(TERMIMAL_BOX, 0);
    }

    /**
     * Skip payload and return next box if one exists
     *
     * @param currentBox
     * @throws IOException
     */
    private Box skipPayload(Box currentBox) throws IOException {

        if (in.read(new byte[currentBox.getSize()], 0, currentBox.getSize() - 8) == -1) {
            totalBytesProcessed += currentBox.getSize() - 8;
            return new Box(TERMIMAL_BOX, 0);
        }

        totalBytesProcessed += currentBox.getSize() - 8;

        return getNextBox();
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

        if (nestableBoxTypes.contains(boxType)) {
            return new Box(boxType, boxSize, new ArrayList<>());
        }

        return new Box(boxType, boxSize);
    }
}
