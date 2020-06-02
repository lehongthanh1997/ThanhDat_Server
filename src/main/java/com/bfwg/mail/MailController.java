package com.bfwg.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

public class MailController {

    @Autowired
    private JavaMailSender javaMailSender;


    public void SendMail(){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("1@gmail.com");

        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World \n Spring Boot Email");

        javaMailSender.send(msg);

    }
    public void sendEmailWithAttachment(String email) throws MessagingException,IOException{
        MimeMessage msg = javaMailSender.createMimeMessage();

        // true = multipart message
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setTo("1@gmail.com");

        helper.setSubject("Confirm your account");
        helper.setText("Thanks for signup! Please before you begin, you must confirm your account.");


        // default = text/plain
        //helper.setText("Check attachment for image!");

        // true = text/html
//        helper.setText("<h1>Check attachment for image!</h1></br>"+"<h2>Goodbye</h2>", true);

        //FileSystemResource file = new FileSystemResource(new File("classpath:android.png"));

        //Resource resource = new ClassPathResource("android.png");
        //InputStream input = resource.getInputStream();

        //ResourceUtils.getFile("classpath:android.png");

//        helper.addAttachment("my_photo.png", new ClassPathResource("android.png"));

        javaMailSender.send(msg);

    }
}
