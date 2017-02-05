package email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Class to send email notifications.
 */
public class SendEmail {

    private static final String USERNAME = "cal.cs5031@gmail.com";
    private static final String PASSWORD = "12345678@";
    private static final int SUCCESS = 0;
    private static final int FAILURE = -1;
    private static final String HOST = "smtp.gmail.com";

    /**
     * Send an email.
     * @param from             the sender mail address.
     * @param addresses        the recipients mail addresses.
     * @param eventDescription the event title.
     * @param date             the event date.
     * @param location         the event's location.
     * @return 0 for success 1 for failure.
     */
    public static int send(String from, String addresses, String eventDescription, String date, String location) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addresses));

            message.setSubject(eventDescription + " on " + date);

            String messagetext = "You are invited to " + eventDescription + ", by " + from.split("@")[0] + ", at " + location + ", on " + date + ".";

            message.setText(messagetext);

            Transport.send(message);

            return SUCCESS;
        }
        catch (MessagingException e) {
            return FAILURE;
        }

    }

}
