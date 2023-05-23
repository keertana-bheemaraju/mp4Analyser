package com.castlabs.mp4Analyzer.services;

import com.castlabs.mp4Analyzer.helpers.BoxHelper;
import com.castlabs.mp4Analyzer.model.Box;
import com.castlabs.mp4Analyzer.model.ErrorResponse;
import com.castlabs.mp4Analyzer.model.StackEntry;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

@Service
public class Mp4Service {

    private ObjectMapper om = new ObjectMapper();

    @Autowired
    private BoxHelper boxHelper;

    public String analyzeMp4(String mp4Url) throws IOException {

        // Sub boxes are pushed to box at top od the stack
        Stack<StackEntry> stack = new Stack<>();

        BufferedInputStream in;

        if (mp4Url == null || mp4Url.equals("")) {
            return om.writeValueAsString(new ErrorResponse("empty url"));
        }

        in = new BufferedInputStream(new URL(mp4Url).openStream());

        Box firstBox = boxHelper.getNextBox(in);

        if(firstBox.getType().equals(BoxHelper.TERMINAL_BOX.getType())) {
            return om.writeValueAsString(new ErrorResponse("file is empty"));
        }

        Box container = new Box(BoxHelper.MP4_FILE_BOX, 0, new ArrayList<>());

        stack.push(new StackEntry(container, 0));

        processBox(firstBox, stack, in);

        return om.writeValueAsString(container.getSubBoxes());
    }

    private void processBox(Box currentBox, Stack<StackEntry> stack, BufferedInputStream in) throws IOException {

        //System.out.println(om.writeValueAsString(MP4_FILE.getSubBoxes()));

        if (currentBox.getType().equals(BoxHelper.TERMINAL_BOX.getType())) {
            return;
        }

        adjustTopOfStack(stack, in);

        stack.peek()
                .getBox()
                .getSubBoxes()
                .add(currentBox);

        if (BoxHelper.NESTABLE_BOX_TYPES.contains(currentBox.getType())) {

            stack.push(new StackEntry(currentBox, boxHelper.getTotalBytesProcessed(in) + currentBox.getSize() - 8));

            processBox(boxHelper.getNextBox(in), stack, in);

        } else {
            Box nextBox = boxHelper.skipPayload(in, currentBox);

            processBox(nextBox, stack, in);
        }
    }

    /**
     * Ensure in progress nestable box is at the top of stack
     */
    private void adjustTopOfStack(Stack<StackEntry> stack, BufferedInputStream in) {
        while (!stack.peek().getBox().getType().equals(BoxHelper.MP4_FILE_BOX)) {
            if (stack.peek().getEndByte() <= boxHelper.getTotalBytesProcessed(in)) {
                stack.pop();
            } else {
                break;
            }
        }
    }
}
