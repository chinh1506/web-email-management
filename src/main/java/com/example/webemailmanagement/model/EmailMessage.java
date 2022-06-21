package com.example.webemailmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String content;
    private Object attachment;
    private String replyTo;
    private Date sendDate;
}
