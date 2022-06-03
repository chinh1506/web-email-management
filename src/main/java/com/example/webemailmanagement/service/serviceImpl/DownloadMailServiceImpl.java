package com.example.webemailmanagement.service.serviceImpl;

import com.example.webemailmanagement.model.EmailMessage;
import com.example.webemailmanagement.service.DowloadMailService;
import com.sun.mail.smtp.SMTPSaslAuthenticator;
import org.jsoup.Jsoup;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

@Service
public class DownloadMailServiceImpl implements DowloadMailService {
    private String protocol;
    private String host;
    private String port;
    private String userName;
    private String password;


    public void setPropertiesBuilder(String protocol, String host, String port, String userName, String password) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.password = password;
    }

    public JavaMailSender getMailSender(){
        JavaMailSenderImpl mailSender= new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(getServerProperties());
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));
        mailSender.setUsername(userName);
        mailSender.setPassword(password);
        return mailSender;
    }
    /**
     * Downloads new messages and fetches details for each message.
     */
    public Message[] downloadEmails(String folder) {
        Properties properties = getServerProperties();
        Session session = Session.getDefaultInstance(properties);

        Message[] messages = new Message[0];
        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(userName, password);
            // opens the inbox folder
            Folder folderInbox = store.getFolder(folder);
            folderInbox.open(Folder.READ_ONLY);
            // fetches new messages from server
            messages = folderInbox.getMessages();
            // disconnect
            folderInbox.close(false);
            store.close();
        } catch (NoSuchProviderException ex) {
            System.out.println("No provider for protocol: " + protocol);
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            ex.printStackTrace();
        }
        return messages;
    }

    public void sendMail(EmailMessage emailMessage) {
        SimpleMailMessage message= new SimpleMailMessage();
        message.setBcc((String[])emailMessage.getBcc().toArray());
        message.setCc((String[])emailMessage.getCc().toArray());
        message.setFrom(userName);
        message.setTo((String[])emailMessage.getTo().toArray());
        message.setSentDate(new Date());
    }

    public Properties getServerProperties() {
        Properties properties = new Properties();

        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL setting
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol), "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));
        return properties;
    }

    /**
     * Returns a list of addresses in String format separated by comma
     *
     * @param address an array of Address objects
     * @return a string represents a list of addresses
     */
    private String parseAddresses(Address[] address) {
        String listAddress = "";

        if (address != null) {
            for (int i = 0; i < address.length; i++) {
                listAddress += address[i].toString() + ", ";
            }
        }
        if (listAddress.length() > 1) {
            listAddress = listAddress.substring(0, listAddress.length() - 2);
        }

        return listAddress;
    }

    public String getTextFromMimeMultipart(MimeMultipart mimeMultipart) {
        String result = "";
        int count = 0;
        try {
            count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result = result + "\n" + bodyPart.getContent();
                    break; // without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
                    result = result + "\n" + Jsoup.parse(html).text();
                } else if (bodyPart.getContent() instanceof MimeMultipart) {
                    result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
