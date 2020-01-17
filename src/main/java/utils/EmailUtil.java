package utils;

import domain.Student;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class EmailUtil {

    /**
     * Utility method to send simple HTML email
     * @param session
     * @param toEmail
     * @param subject
     * @param body
     */
    public static void sendEmail(Session session, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress("maik@gmail.com", "George Ciubotariu"));

            msg.setReplyTo(InternetAddress.parse("mail@gmail.com", false));

            msg.setSubject(subject, "UTF-8");

            msg.setContent(body, "text/html");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            System.out.println("Message is ready");
            Transport.send(msg);

            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void send(Student student){
        //final String fromEmail = ApplicationContext.getPROPERTIES().getProperty("data.email.address"); // requires valid gmail id
        //final String fromEmail ="sender@gmail.com";
        final String fromEmail ="mail@scs.ubbcluj.ro";
        //final String password = ApplicationContext.getPROPERTIES().getProperty("data.email.password"); // correct password for gmail
        //final String password = "another password";
        final String password = "password";
        //final String toEmail = student.getEmail(); // can be any email id
        final String toEmail = "receiver@yahoo.com";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP host
        props.put("mail.smtp.port", "587"); //TLS port
        props.put("mail.smtp.auth", "true"); //enable authentification
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authentication object to pass in Session,getInstance argument
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };

        Session session = Session.getInstance(props, auth);
        session.setDebug(true);
        //String nume = ApplicationContext.getPROPERTIES().getProperty("data.path.feedback")+student.getNume()+student.getPrenume()+".txt";
        String nume = "ASDF";
        //Path path = Paths.get(nume);
        EmailUtil.sendEmail(session, toEmail, "<head> Feedback updated </head>","<body> makeMailBody(nume) </body>");
    }
}