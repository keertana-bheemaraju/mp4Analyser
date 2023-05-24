package com.castlabs.mp4Analyzer.services;

import com.castlabs.mp4Analyzer.helpers.Mp4Helper;
import com.castlabs.mp4Analyzer.model.Box;
import com.castlabs.mp4Analyzer.model.ErrorResponse;
import com.castlabs.mp4Analyzer.model.StackEntry;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
public class Mp4Service {

    private ObjectMapper om = new ObjectMapper();

    public String analyzeMp4(String mp4Url)  {


        try {
            if (mp4Url == null || mp4Url.equals("")) {
                return om.writeValueAsString(new ErrorResponse("empty url"));
            }

            Mp4Helper mp4Helper = new Mp4Helper(mp4Url);

            Box firstBox = mp4Helper.getNextBox();

            if (firstBox.getType().equals(Mp4Helper.TERMINAL_BOX.getType())) {
                return om.writeValueAsString(new ErrorResponse("file is empty"));
            }

            Box container = new Box(Mp4Helper.CONTAINER, 0, new ArrayList<>());

            mp4Helper.getStack().push(new StackEntry(container, 0));

            processBox(firstBox, mp4Helper);

            return getMachineReadable(container);
        } catch (IOException e) {
            return e.toString();
        }
    }

    private void processBox(Box currentBox, Mp4Helper mp4Helper) throws IOException {

        if (currentBox.getType().equals(Mp4Helper.TERMINAL_BOX.getType())) {
            return;
        }

        // place this box where it belongs in the container
        mp4Helper.placeBox(currentBox);

        if (Mp4Helper.NESTABLE_BOX_TYPES.contains(currentBox.getType())) {

            mp4Helper.pushBoxToStack(currentBox);

            processBox(mp4Helper.getNextBox(), mp4Helper);

        } else {
            Box nextBox = mp4Helper.skipPayload(currentBox);

            processBox(nextBox, mp4Helper);
        }
    }

    private String getMachineReadable(Box container) throws JsonProcessingException {
        return om.writeValueAsString(container.getSubBoxes());
    }
}
