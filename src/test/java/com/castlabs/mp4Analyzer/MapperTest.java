package com.castlabs.mp4Analyzer;

import com.castlabs.mp4Analyzer.model.Box;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.ArrayList;
import java.util.List;


public class MapperTest {

    @Test
    public void testMapper() throws JsonProcessingException {
        List<Box> file = new ArrayList<>();

        //Moof Box
        Box moofBox = new Box("moof", 181, new ArrayList<>());
        Box trafBox = new Box("traf", 50, new ArrayList<>());
        Box uuidBox = new Box("uuid", 10);
        trafBox.getSubBoxes().add(uuidBox);
        moofBox.getSubBoxes().add(trafBox);
        file.add(moofBox);

        //MDAT Box
        Box mdatNox = new Box("mdat", 34);
        file.add(mdatNox);

        ObjectMapper om = new ObjectMapper();
        String actual = om.writeValueAsString(file);
        String expected = "[{\"type\":\"moof\",\"size\":181,\"subBoxes\":[{\"type\":\"traf\",\"size\":50,\"subBoxes\":[{\"type\":\"uuid\",\"size\":10,\"subBoxes\":null}]}]},{\"type\":\"mdat\",\"size\":34,\"subBoxes\":null}]";

        Assertions.assertEquals(expected, actual);

    }
}
