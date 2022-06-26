package com.example.webemailmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.mail.Flags;
import java.util.ArrayList;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private int id;
    private String to;
    private String from;
    private String cc;
    private String bcc;
    private String subject;
    private Object content;
    private Object attachment;
    private String replyTo;
    private Date sendDate;
    private String flags;
}
