package com.example.webemailmanagement.service.serviceImpl;

import com.example.webemailmanagement.model.EmailMessage;
import com.example.webemailmanagement.service.DowloadMailService;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private Session getSession(String protocol, String host, String port, String userName, String password){
        Properties properties = getServerProperties(protocol, host, port);
        Session session = Session.getDefaultInstance(properties);
        return session;
    }
    @Override
    public void deleteEmail(String protocol, String host, String port, String userName, String password,int i){
        Properties properties = getServerProperties(protocol, host, port);
        Session session = Session.getDefaultInstance(properties);
        try {
            Store store = session.getStore(protocol);
            store.connect(host, userName, password);
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            messages[i].setFlag(Flags.Flag.DELETED, true);
            folder.close(true);
            store.close();

            System.out.println("Email deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in deleting email.");
        }
    }
    public List<EmailMessage> downloadEmails(String protocol, String host, String port, String userName, String password) {
        Properties properties = getServerProperties(protocol, host, port);
        Session session = Session.getDefaultInstance(properties);
        List<EmailMessage> emailMessages = new ArrayList<>();
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
            for (int i = messages.length - 1; i > messages.length - 10; i--) {
                Message msg = messages[i];
                Address[] fromAddress = msg.getFrom();
                String from = fromAddress[0].toString();
                String subject = msg.getSubject();
                String toList = parseAddresses(msg.getRecipients(Message.RecipientType.TO));
                String ccList = parseAddresses(msg.getRecipients(Message.RecipientType.CC));
                Date sentDate = msg.getSentDate();
                String flags = msg.getFlags().toString();
                String contentType = msg.getContentType();
                System.out.println(contentType);

                String messageContent = contentType;
                try {
                    if (msg.isMimeType("text/plain")) {
                        messageContent = msg.getContent().toString();
                    } else if (msg.isMimeType("multipart/*")) {
                        MimeMultipart mimeMultipart = (MimeMultipart) msg.getContent();
                        messageContent = getTextFromMimeMultipart(mimeMultipart);
//                        messageContent= readAttackment(mimeMultipart);
                    } else if (msg.isMimeType("text/html")) {
                        messageContent = msg.getContent().toString();
                    }
//                    messageContent= msg.getContent().toString();

                } catch (Exception e) {
                    messageContent = "[Error downloading content]";
                    e.printStackTrace();
                }
                EmailMessage message = new EmailMessage();
                message.setContent(messageContent);
                message.setTo(toList);
                message.setSubject(subject);
                message.setSendDate(sentDate);
                message.setCc(ccList);
                message.setFrom(from);
                message.setFlags(flags);
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

    private String readAttackment(MimeMultipart multipart){
        //Iterate multiparts
        String result="";
        try {
            for(int k = 0; k < multipart.getCount(); k++){
                BodyPart bodyPart = multipart.getBodyPart(k);
                InputStream stream =
                        (InputStream) bodyPart.getInputStream();
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(stream));

                while (bufferedReader.ready()) {
                    result+= bufferedReader.readLine();
                    System.out.println(result);
                }
                System.out.println();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    public String getTextFromMimeMultipart(MimeMultipart mimeMultipart) {
        String result = "";

        try {
            int count = mimeMultipart.getCount();
            for (int i = 0; i < count; i++) {
//                BodyPart bodyPart = mimeMultipart.getBodyPart(i);
                MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(i);
                if (bodyPart.isMimeType("text/plain")) {
                    result = result + "\n" + bodyPart.getContent();
                    break; // without break same text appears twice in my tests
                } else if (bodyPart.isMimeType("text/html")) {
                    String html = (String) bodyPart.getContent();
//                    result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
                    result = result + html;
                } else if (bodyPart.getContent() instanceof MimeMultipart) {
                    result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


}
