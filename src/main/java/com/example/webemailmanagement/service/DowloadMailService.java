package com.example.webemailmanagement.service;

import com.example.webemailmanagement.controller.MailRequest;
import com.example.webemailmanagement.model.EmailMessage;

import javax.mail.Message;
import javax.mail.internet.MimeMultipart;
import java.util.List;
import java.util.Properties;

public interface DowloadMailService {
    public EmailMessage readEmailById(String protocol, String host, String port, String userName, String password, String folder, int id);
    public List<EmailMessage> downloadEmails(String protocol, String host, String port, String userName, String password,String folder);
    public String getTextFromMimeMultipart(MimeMultipart mimeMultipart);
    public void deleteEmail(String protocol, String host, String port, String userName, String password,int i);
    public void sendMail(String username, String password, MailRequest mailRequest);
}
