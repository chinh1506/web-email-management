package com.example.webemailmanagement.service;

import javax.mail.Message;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

public interface DowloadMailService {
    public Message[] downloadEmails(String folder);
    public Properties getServerProperties();
    public String getTextFromMimeMultipart(MimeMultipart mimeMultipart);
}
