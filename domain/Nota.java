package domain;

import utils.Constants;

import java.time.LocalDateTime;


public class Nota extends Entity<String> {
    private String id;//this.id = student.id + ":" + tema.id
    private String data;//LOCAL DATE TIME => STRING
    private String profesor;//Profesor profesor;
    private int valoare;
    private String feedback;

    public Nota(String idStudent, String idTema, int valoare, String profesor) {
        this.id = idStudent + ":" +  idTema;
        this.valoare = valoare;
        this.profesor = profesor;
        this.data = LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
    }

    public Nota(String idTotal, int valoare, String profesor, String data) {
        this.id = idTotal;
        this.valoare = valoare;
        this.profesor = profesor;
        this.data = data;//pentru citirea din fisier!!! (si pentru gui)
    }



    public Nota(String idStudent, String idTema, int valoare, String profesor, String feedback) {
        this.id = idStudent + ":" +  idTema;
        this.valoare = valoare;
        this.profesor = profesor;
        this.data = LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
        this.feedback = feedback;
    }

    public Nota(String idTotal, int valoare, String profesor, String data, String feedback) {
        this.id = idTotal;
        this.valoare = valoare;
        this.profesor = profesor;
        this.data = data;//pentru citirea din fisier!!! (si pentru gui)
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public int getValoare() {
        return valoare;
    }

    public String getId() {//this.id = student.id + ":" + tema.id
        return id;
    }

    public String getData() {
        return data;
    }

    public String getProfesor() {
        return profesor;
    }

    @Override
    public String toString() {
        return "In data " + data + ", profesorul " + profesor + " v-a acordat nota " + valoare + " cu observatia urmatoare:\n" + feedback;
    }
}
