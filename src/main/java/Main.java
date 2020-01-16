public class Main {
    public static void main(String[] args) {
        MainApp.main(args);

        //SendEmail.send();
        //SendEmailSMTP.send();
        //SendMessageRe.send();
        //String email = "whatever@yahoo.com";
        //String email = "anyhow@gmail.com";
        //String content = "Am trimis un mesaj.";
        //SendEmailUtility.sendmail(email,content);
        //Student student = new Student("0", "Ciubotariu","George",212,"somewhat@yahoo.com","DA");
        //List<Student> sts = new ArrayList<>();
        //sts.add(student);
        //EmailUtil.send(student);
        //SendEmailUtility.sendmail("EMAIL","<head>MESAJJJJJ</head>");
        //SendingEmails sendingEmails = new SendingEmails();
        //sendingEmails.sendEmailToStudent("georgeciubotariu@yahoo.com");
    }
}

//TODO: rapoarte: change from directory chooser to file saver:- auto save in static path the let the user move the doc.     (V)
//TODO: raporate: add file type to the file saver (.pdf)                                                                    (V)
//TODO: raporate: consider only the students of the logged in teacher                                                       (V)
//TODO: tabel note profesori: recheck filter. consider only the students of the logged in teacher                           (V)
//TODO: admin note: doar drepturi de vizualizare asupra notelor (eventual stergere)                                         (V)
//TODO: mail :(                                                                                                             c
//TODO: date validation for semester (using static start/end + any deadline during holidays must be set to holiday end)     c
//TODO: be careful (add validation) not to have two entities with the same email (login will go boom)                       (V)
//TODO: treat ValidationExceptions with user messages in the GUI (add try-catch blocks maybe) (and romanian msg)

//TODO: maybe do not set automatically task change on date change ???