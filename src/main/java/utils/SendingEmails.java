package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
//George... a mers de pe al meu. Verifica-ti mail-ul sa vezi daca ai sprimit.sss stiu ; am primit notificare acum..  Pare rau man.. nu stiu ce poate avea. Trebuie sa fie ceva configurare de la antivirus.
//sUNT Curios. Ai intrat pe mail settings sa lasi alte aplicatii in afara de g apps sa trimita mail? da. doar tie iti merge
//A .. you're right. Hmmmm. Ceva iti blocheaza port-ul. Pare rau. Eu cel putin nu-mi pot da seama de aici care-i problema. Cand o identifici macar partial, scrie-mi si fac tot posibilul sa-ti mearga.
//1 SEC
public class SendingEmails {
    public void sendEmailToStudent(String studentEmail) {
        //final String username = "mailul ce trimite, ex georgeBOT@gmail.com";  // like yourname@outlook.com
        final String username = "mymail@gmail.com";  // like yourname@outlook.com
        //final String password = "parolaDeLaMailulCeTrimite";   // password here
        final String password = "parola mea";   // password here
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        session.setDebug(true);
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(studentEmail));   // like inzi769@gmail.com
            message.setSubject("You've just got a new GRADE");
            message.setText("Your teacher just gave you a grade. Check it out in the GradeBook app.");
            Transport.send(message);
            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
