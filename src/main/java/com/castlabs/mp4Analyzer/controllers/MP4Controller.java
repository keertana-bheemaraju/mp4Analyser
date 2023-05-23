package com.castlabs.mp4Analyzer.controllers;

import com.castlabs.mp4Analyzer.services.Mp4Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping(path="/mp4")
public class MP4Controller {

    @Autowired
    Mp4Service mp4Service;

    @GetMapping(path = "/analyzeMp4")
    public @ResponseBody String analyzeMp4(@RequestParam String mp4Url) throws IOException {

        return mp4Service.analyzeMp4(mp4Url);

    }

}


