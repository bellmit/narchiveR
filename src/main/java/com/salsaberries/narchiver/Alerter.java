package com.salsaberries.narchiver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;

/**
 * Emailing class.
 * 
 * @author njanetos
 */
public class Alerter {
        
    /**
     * Emails the log to me.
     */
    public void alert() {
        // Common variables
        String host = "smtp.comcast.net";
        String from = "njanetos@comcast.net";
        String to = "njanetos@comcast.net";

        // Set properties
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.debug", "true");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "true");

        // Load log
        String log;
        try {
            FileInputStream is = new FileInputStream("log.out");
            log = IOUtils.toString(is);
        }
        catch (FileNotFoundException e) {
            log = "Nick, \n \n Something went wrong with the archiver, so seriously wrong that the log file cannot be found. \n \n Best, \n Archiver";
        }
        catch (IOException e) {
            log = "Nick, \n \n Something went wrong with the archiver, so seriously wrong that I can't even tell you what went wrong. \n \n Best, \n Archiver";
        }

        // Get session
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("njanetos", "2point7182");
                }
            });

        try {
            // Instantiate a message
            Message msg = new MimeMessage(session);

            // Set the FROM message
            msg.setFrom(new InternetAddress(from));

            // The recipients can be more than one so we use an array but you can
            // use 'new InternetAddress(to)' for only one address.
            InternetAddress[] address = {new InternetAddress(to)};
            msg.setRecipients(Message.RecipientType.TO, address);

            // Set the message subject and date we sent it.
            msg.setSubject("A message from the archiver.");
            msg.setSentDate(new Date());

            // Set message content
            msg.setText(log);

            // Send the message
            Transport.send(msg);
        }
        catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
    
}
