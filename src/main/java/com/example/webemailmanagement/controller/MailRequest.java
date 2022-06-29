package com.example.webemailmanagement.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailRequest {
    private String subject;
    private String to;
    private String content;
    private MultipartFile file;
}
