package utils;

import domain.Student;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO
//TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO  //TODO

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


    public static void sendmail(String email, String content)
    {

        // Sender's email ID needs to be mentioned
        String from = Data.adminEmail;

        // Setup mail server
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");


        // Get the default Session object.
        Session session = Session.getInstance(properties,
                new javax.mail.Authenticator()
                {
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(Data.adminEmail,
                                Data.adminPass);
                    }
                });
        try
        {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            // Set Subject: header field
            message.setSubject("Actualizare situatie note!");

            // Now set the actual message
            message.setText(content);

            // Send message
            Transport.send(message);//TODO: send fails no matter what (declaration not found / bad credentials)
            System.out.println("Sent message successfully....");
        }
        catch (MessagingException mex)
        {
            System.out.println(mex.getMessage());
        }

    }
}


