package com.example.webemailmanagement.controller;

import com.example.webemailmanagement.model.EmailMessage;
import com.example.webemailmanagement.model.User;
import com.example.webemailmanagement.service.DowloadMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class MailRestController {
    private String user="";
    private String password="";
    @Autowired
    private DowloadMailService mailService;

    @GetMapping("/mails")
    public List<EmailMessage> getAllMail(@RequestParam String folder)  {
        List<EmailMessage> mails = mailService.downloadEmails("imap", "outlook.office365.com", "993",
                user, password, folder);
        return mails;
    }

    @GetMapping("/mails/{id}")
    public EmailMessage getMail(@PathVariable int id, @RequestParam String folder)  {
        return mailService.readEmailById("imap", "outlook.office365.com", "993",
                user, password, folder, id);
    }
    @PostMapping ("/mails")
    public void sendEmail(@RequestBody MailRequest mailRequest ){
        System.out.println(mailRequest);
        mailService.sendMail(user, password,mailRequest);
    }
    @DeleteMapping("/mails/{id}")
    public void deleteMail(@PathVariable int id){
        mailService.deleteEmail("imap", "outlook.office365.com", "993",
                user, password, id);
    }
    @PutMapping ("/mails")
    public void checkLogin(@RequestBody User user ){
        System.out.println(user);
        this.user= user.getUser();
        this.password=user.getPassword();
        mailService.getSession("imap", "outlook.office365.com", "993",
                user.getUser(), user.getPassword());
    }
}
