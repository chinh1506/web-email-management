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
import java.util.*;

@Service
public class DownloadMailServiceImpl implements DowloadMailService {

    private Properties getServerProperties(String protocol, String host, String port) {
        Properties properties = new Properties();
        // server setting
        properties.put(String.format("mail.%s.host", protocol), host);
        properties.put(String.format("mail.%s.port", protocol), port);

        // SSL setting
        properties.setProperty(String.format("mail.%s.socketFactory.class", protocol),
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty(String.format("mail.%s.socketFactory.fallback", protocol), "false");
        properties.setProperty(String.format("mail.%s.socketFactory.port", protocol), String.valueOf(port));

        return properties;
    }
    public List<EmailMessage> downloadEmails(String protocol, String host, String port, String userName, String password) {
        Properties properties = getServerProperties(protocol, host, port);
        Session session = Session.getDefaultInstance(properties);
        List<EmailMessage> emailMessages= new ArrayList<>();
        try {
            // connects to the message store
            Store store = session.getStore(protocol);
            store.connect(userName, password);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
            folderInbox.open(Folder.READ_ONLY);

            // fetches new messages from server
            Message[] messages = folderInbox.getMessages();
            System.out.println("Total Message" + messages.length);
            for (int i = messages.length - 1; i > 0; i--) {
                Message msg = messages[i];
                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();
                String subject = msg.getSubject();
                String toList = parseAddresses(msg.getRecipients(Message.RecipientType.TO));
                String ccList = parseAddresses(msg.getRecipients(Message.RecipientType.CC));
                Date sentDate = msg.getSentDate();

                String contentType = msg.getContentType();
                String messageContent = contentType;
                try {
                    if (msg.isMimeType("text/plain")) {
                        messageContent = msg.getContent().toString();
                    } else if (msg.isMimeType("multipart/*")) {
                        MimeMultipart mimeMultipart = (MimeMultipart) msg.getContent();
                        messageContent = getTextFromMimeMultipart(mimeMultipart);
                    }
                    else if(msg.isMimeType("text/html")) {
                        messageContent = msg.getContent().toString();
                    }

                } catch (Exception e) {
                    messageContent = "[Error downloading content]";
                    e.printStackTrace();
                }
                EmailMessage message= new EmailMessage();
                message.setContent(messageContent);
                message.setTo(toList);
                message.setSubject(subject);
                message.setSendDate(sentDate);
                message.setCc(ccList);

                emailMessages.add(message);
            }

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
        return emailMessages;
    }

//    public JavaMailSender getMailSender(String host, int port, String userName, String password) {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setJavaMailProperties(getServerProperties());
//        mailSender.setHost(host);
//        mailSender.setPort(port);
//        mailSender.setUsername(userName);
//        mailSender.setPassword(password);
//        return mailSender;
//    }


//    public void sendMail(EmailMessage emailMessage, String userNameFrom) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setBcc(();
//        message.setCc();
//        message.setFrom(userNameFrom);
//        message.setTo((String[]) emailMessage.getTo().toArray());
//        message.setSentDate(new Date());
//    }


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
