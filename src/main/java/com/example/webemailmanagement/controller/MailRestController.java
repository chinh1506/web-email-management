package com.example.webemailmanagement.controller;

import com.example.webemailmanagement.model.Message;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class MailRestController {

    @GetMapping("/messages")
    public Message getAllMail(){
        return null;
    }
}
