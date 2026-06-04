package com.krce.amazon.utilities;

import java.io.File;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.krce.amazon.config.ConfigReader;

public class EmailUtil {
    private static final Logger log = LogManager.getLogger(EmailUtil.class);

    public static void sendEmailWithAttachments(String[] attachmentPaths) {
        String to = ConfigReader.getEmailTo();
        String from = ConfigReader.getEmailFrom();
        String host = ConfigReader.getEmailHost();
        String port = ConfigReader.getEmailPort();
        String username = ConfigReader.getEmailUsername();
        String password = ConfigReader.getEmailPassword();
        String subject = ConfigReader.getEmailSubject();

        if (to == null || to.isEmpty() || from == null || from.isEmpty()) {
            log.warn("Email configuration incomplete - skipping email");
            return;
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Please find attached the Amazon Automation Test Report.");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            for (String path : attachmentPaths) {
                File file = new File(path);
                if (file.exists()) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(file);
                    multipart.addBodyPart(attachmentPart);
                }
            }

            message.setContent(multipart);
            Transport.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email", e);
        } catch (Exception e) {
            log.error("Email attachment error", e);
        }
    }
}
