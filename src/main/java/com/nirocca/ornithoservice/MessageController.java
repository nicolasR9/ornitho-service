package com.nirocca.ornithoservice;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {
    @RequestMapping("/message")
    public Message message() {
        return new Message("Hello from Google Cloud", "High");
    }
}


