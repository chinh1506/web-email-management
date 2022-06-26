package com.example.webemailmanagement.controller;

import com.example.webemailmanagement.model.EmailMessage;
import com.example.webemailmanagement.service.DowloadMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mail.MailReceiver;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("api")
public class MailRestController {
    @Autowired
    private DowloadMailService mailService;

    @GetMapping("/mails")
    public List<EmailMessage>  getAllMail() throws MessagingException {
        List<EmailMessage> mails = mailService.downloadEmails("imap", "outlook.office365.com", "993",
                "19525091.chinh@student.iuh.edu.vn", "chinh123@");
        System.out.println(mails.get(0));
        return mails;
    }
    @GetMapping("/multi")
    public String getMultiparts(HttpServletResponse response) throws MessagingException, IOException {

        String s="";

        return s;
    }
}
