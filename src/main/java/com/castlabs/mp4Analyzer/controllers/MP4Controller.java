package com.castlabs.mp4Analyzer.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(path="/mp4")
public class MP4Controller {

    @GetMapping(path = "/analyzeMp4")
    public @ResponseBody
    String analyzeMp4(@RequestParam String mp4Url) {
        System.out.println(mp4Url);
        return null;
    }

}


