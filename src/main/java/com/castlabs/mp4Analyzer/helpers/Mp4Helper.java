package com.castlabs.mp4Analyzer.helpers;

import com.castlabs.mp4Analyzer.model.Box;
import com.castlabs.mp4Analyzer.model.StackEntry;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Mp4Helper {

    public static final String CONTAINER = "container";

    public static final List<String> NESTABLE_BOX_TYPES = Arrays.asList("moof", "traf");

    public static final Box TERMINAL_BOX = new Box("TERMINAL_BOX", 0);

    private int totalBytesProcessed;

    private Stack<StackEntry> stack = new Stack<>();

    private BufferedInputStream inputStream;

    public Mp4Helper(String url) throws IOException{
        inputStream = new BufferedInputStream(new URL(url).openStream());
    }

    /**
     * Get next box if one exists
     * @return
     * @throws IOException
     */
    public Box getNextBox() throws IOException {

        byte[] buffer = new byte[8];

        if (inputStream.read(buffer, 0, 8) != -1) {

            increaseBytesProcessed(8);

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
    public Box skipPayload(Box currentBox) throws IOException {

        if (inputStream.read(new byte[currentBox.getSize()], 0, currentBox.getSize() - 8) == -1) {
            increaseBytesProcessed(currentBox.getSize() - 8);
            return TERMINAL_BOX;
        }

        increaseBytesProcessed(currentBox.getSize() - 8);

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

        if (NESTABLE_BOX_TYPES.contains(boxType)) {
            return new Box(boxType, boxSize, new ArrayList<>());
        }

        return new Box(boxType, boxSize);
    }

    public void placeBox(Box box) {

        // Ensure nestable box that is currently being built is at the top of stack
        while (!stack.peek().getBox().getType().equals(Mp4Helper.CONTAINER)) {
            if (stack.peek().getEndByte() <= getTotalBytesProcessed()) {
                stack.pop();
            } else {
                break;
            }
        }

        stack.peek()
                .getBox()
                .getSubBoxes()
                .add(box);
    }

    public void pushBoxToStack(Box box) {
        getStack().push(new StackEntry(box, getTotalBytesProcessed() + box.getSize() - 8));
    }


    public Integer getTotalBytesProcessed() {
        return totalBytesProcessed;
    }

    private void increaseBytesProcessed(int bytesProcessed) {
        totalBytesProcessed += bytesProcessed;
    }

    public Stack<StackEntry> getStack() {
        return stack;
    }

}
