package com.example.yinyoga.utils;

import android.util.Log;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI implements Runnable {
    private final String senderEmail = "trannqgcc210042@fpt.edu.vn";
    private final String senderPassword = "bpxo tdzn qfar layn";

    private final String recipientEmail;
    private final String subject;
    private final String message;
    private OnEmailSentListener onEmailSentListener;

    public JavaMailAPI(String recipientEmail, String subject, String message) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
    }

    @Override
    public void run() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(senderEmail));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
            if (onEmailSentListener != null) {
                onEmailSentListener.onEmailSent(true);
            }
        } catch (MessagingException e) {
            Log.d("run send email", e.getMessage());
            if (onEmailSentListener != null) {
                onEmailSentListener.onEmailSent(false);
            }
        }
    }

    public void setOnEmailSentListener(OnEmailSentListener listener) {
        this.onEmailSentListener = listener;
    }

    public interface OnEmailSentListener {
        void onEmailSent(boolean success);
    }
}
