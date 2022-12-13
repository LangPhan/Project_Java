package com.controllers;

import com.utils.SendEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmailController {
    @Autowired
    private SendEmail sendEmail;

    @GetMapping("/sendMail")
    public String sendMail(){
        System.out.println(sendEmail.sendSimpleMail("langphankk@gmail.com","This is email","Hello"));
        return "home/home";
    }
}
