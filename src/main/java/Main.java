import utils.*;

public class Main {
    public static void main(String[] args) {
        //MainApp.main(args);
        //SendEmail.send();
        //SendEmailSMTP.send();
        //SendMessageRe.send();
        //String email = "georgeciubotariu@yahoo.com";
        String email = "ciubotariugeorge99@gmail.com";
        String content = "Am trimis un mesaj.";
        SendEmailUtility.sendmail(email,content);
    }
}

//TODO: rapoarte: change from directory chooser to file saver:- auto save in static path the let the user move the doc.
//TODO: raporate: add file type to the file saver (.pdf)
//TODO: raporate: consider only the students of the logged in teacher
//TODO: tabel note profesori: recheck filter. consider only the students of the logged in teacher
//TODO: admin note: doar drepturi de vizualizare asupra notelor (eventual stergere)
//TODO: mail :(
//TODO: date validation for semester (using static start/end + any deadline during holidays must be set to holiday end)