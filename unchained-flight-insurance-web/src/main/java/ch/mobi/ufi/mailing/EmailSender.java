package ch.mobi.ufi.mailing;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender implements Notifier {

    @Override
    public void notify(String from, String to, String subject, String content) {
        Session session = initSession();
        try {
            Transport.send(buildMessage(session, from, to, subject, content));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private Message buildMessage(Session session,
                                 String from,
                                 String to,
                                 String subject,
                                 String htmlContent) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");
        return message;
    }

    private Session initSession() {
        final String username = "olivier.vondach@obya.ch";
        final String secret = "..0googlebya..";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, secret);
                    }
                });
    }
}