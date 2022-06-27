package com.example.webemailmanagement.controller;

import com.example.webemailmanagement.model.EmailMessage;
import com.example.webemailmanagement.service.DowloadMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class MailRestController {
    @Autowired
    private DowloadMailService mailService;

    @GetMapping("/mails")
    public List<EmailMessage> getAllMail(@RequestParam String folder)  {
        List<EmailMessage> mails = mailService.downloadEmails("imap", " imap.gmail.com", "993",
                "baohuynh9b@gmail.com", "baobao26092001", folder);
        return mails;
    }

    @GetMapping("/mails/{id}")
    public EmailMessage getMail(@PathVariable int id, @RequestParam String folder)  {
        return mailService.readEmailById("imap", " imap.gmail.com", "993",
                "baohuynh9b@gmail.com", "baobao26092001", folder, id);
    }

    @DeleteMapping("/mails/{id}")
    public void deleteMail(@PathVariable int id){
        mailService.deleteEmail("imap", " imap.gmail.com", "993",
                "baohuynh9b@gmail.com", "baobao26092001", id);
    }
}
