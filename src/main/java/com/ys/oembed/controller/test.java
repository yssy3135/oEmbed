package com.ys.oembed.controller;


import com.ys.oembed.service.oembed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;


@RestController
public class test {


    @GetMapping("/api")
    public Map oEmebed(@RequestParam("oembedUrl") String url) throws IOException {

        oembed test = new oembed();

        return test.getOembedInfo(url);
    }
}
