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
        return "Nota{" +
                "id='" + id + '\'' +
                ", data='" + data + '\'' +
                ", profesor='" + profesor + '\'' +
                ", valoare=" + valoare +
                '}';
    }
}

/*
autocomplete combo box - pentru studenti
combo box in care sa avem pusa tema curenta
(dupa ce am selectat studentul, vor disparea acele teme la care studentul are nota)
(dupa ce am selectat data, vor disparea acele teme la care s-a depasit termenul maxim de predare (2 sapt in plus) )
TODO: cand schimbam tema, vom actualiza si daca exista penalitati sau nu la nota
TODO: apare un mesaj cu ROSU care spune ca depunctam (si apare o bifa - checkbox)
TODO: in acest caz, va trebui sa bifam un checkbox
vor fi doua checkbox-uri pentru diferite intarzieri: cand profesorul intarzie si cand studentul intarzie
(daca nu trece nici proful la timp si nici studentul nu preda doua saptamani si are si scutire o saptamana... si ni se prind urechile...)


--alta varianta:
avem un calendar - selectam data cand a predat studentul tema
(((avem al doilea calendar - selectam de cand pana cand a lipsit motivat studentul...)))
avem niste checkbox-uri cu saptamanile de la termenul de start al temei pana in saptamana curenta, si sa selectam saptamanile de motivare

 */