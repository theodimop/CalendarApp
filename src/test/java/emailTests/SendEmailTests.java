package emailTests;

import email.SendEmail;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by irs6 on 13/12/16.
 */
public class SendEmailTests {

    String from =  "irs6@st-andrews.ac.uk";
    String singleInvalidRecipient = "irs76@st-andrews.ac.uk";
    String singleRecipient = "irs6@st-andrews.ac.uk";
    String multipleRecipient = "irs6@st-andrews.ac.uk,ek89@st-andrews.ac.uk";
    String eventDescription = "Test";
    String eventDate = "10:00 12/14/2016";
    String eventLocation = "test location";

    @Test
    public void testSendSingleRecipient(){
        assertEquals(SendEmail.send(from, singleRecipient, eventDescription, eventDate, eventLocation), 0);
    }

    @Test
    public void testSendMultipleRecipient(){
        assertEquals(SendEmail.send(from, multipleRecipient, eventDescription, eventDate, eventLocation), 0);
    }

    @Test
    public void testSendSingleInvalidRecipient(){
        assertEquals(SendEmail.send(from, singleInvalidRecipient, eventDescription, eventDate, eventLocation), 0);
    }
}
