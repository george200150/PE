package utils;

import domain.Student;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO

//IMAP/SMTH www.scs.ubbcluj.ro
//IMAP - 993
//SMTP - 465
//ssl/tls / normal password (auth)

public class SendEmailUtility
{

    public static void sendEmail(Iterable<Student> students, String message) {

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        students.forEach(student ->
        {
            if (!student.getEmail().equals(""))
                executorService.execute(() -> {
                    SendEmailUtility.sendmail(student.getEmail(), message);
                    executorService.shutdown();
                });
        });
        executorService.shutdown();
    }


    /*private static final String SMTP_HOST_NAME = "www.scs.ubbcluj.ro";
    private static final String SMTP_AUTH_USER = "cgir2476@scs.ubbcluj.ro";
    private static final String SMTP_AUTH_PWD = "3e6#a#e826";*/


    /*public static void sendmail(String email, String content)
    {

        // Sender's email ID needs to be mentioned
        String subject = "SUBIECT";
        String toAddress = "georgeciubotariu@yahoo.com";

        //String userName = Data.adminEmail;
        String userName = "cgir2476";
        String password = Data.adminPass;

        String from = Data.adminEmail;

        // Setup mail server
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smpt");
        //properties.setProperty("mail.smtp.ehlo", "false");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", true);
        properties.put("mail.smtp.host", "www.scs.ubbcluj.ro");
        //properties.put("mail.smtp.port", "465");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);



        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);

        session.setDebug(true);
        try
        {
            // creates a new e-mail message
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(from));
            InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject);
            msg.setText("MAILUL MEU\r\n.\r\n");
            msg.setSentDate(new Date());

            Transport.send(msg);
            System.out.println("Sent message successfully....");
        }
        catch (MessagingException mex)
        {
            System.out.println(mex.getMessage());
        }

    }*/

    private static final String SMTP_HOST_NAME = "smtp.gmail.com";
    private static final String SMTP_AUTH_USER = "proiect.extins@gmail.com";
    private static final String SMTP_AUTH_PWD = "Admin!23";
    public static void sendmail(String email, String content)
    {

        // Sender's email ID needs to be mentioned
        String subject = "SUBIECT";
        String toAddress = "georgeciubotariu@yahoo.com";

        //String userName = Data.adminEmail;
        String userName = SMTP_AUTH_USER;
        String password = SMTP_AUTH_PWD;

        // Setup mail server
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smpt");
        properties.put("mail.smtp.starttls.enable", "true");
        //properties.setProperty("mail.smtp.ehlo", "false");
        properties.put("mail.smtp.auth", "true");
        //properties.put("mail.smtp.ssl.enable", true);
        properties.put("mail.smtp.host", SMTP_HOST_NAME);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);



        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);

        session.setDebug(true);
        try
        {
            // creates a new e-mail message
            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(SMTP_AUTH_USER));
            InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject);
            msg.setText("MAILUL MEU\r\n.\r\n");
            msg.setSentDate(new Date());

            Transport.send(msg);
            System.out.println("Sent message successfully....");
        }
        catch (MessagingException mex)
        {
            System.out.println(mex.getMessage());
        }

    }
}


