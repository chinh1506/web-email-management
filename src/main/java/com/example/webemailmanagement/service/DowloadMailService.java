package com.example.webemailmanagement.service;

import com.example.webemailmanagement.model.EmailMessage;

import javax.mail.Message;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;

public interface DowloadMailService {
    public List<EmailMessage> downloadEmails(String protocol, String host, String port, String userName, String password);
    public String getTextFromMimeMultipart(MimeMultipart mimeMultipart);
}
