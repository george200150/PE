package domain;

import utils.Constants;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


public class Tema extends Entity<String> {
    private String id;
    private String nume;
    private String descriere;
    private String startWeek;
    private String deadlineWeek;


    public Tema(String id, String nume, String descriere, LocalDateTime deadlineWeek) {
        this.id = id;
        this.nume = nume;
        this.descriere = descriere;
        this.startWeek = LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
        this.deadlineWeek = deadlineWeek.format(Constants.DATE_TIME_FORMATTER);
    }

    public Tema(String id, String nume, String descriere, LocalDateTime startWeek, LocalDateTime deadlineWeek) {
        this.id = id;
        this.nume = nume;
        this.descriere = descriere;
        this.startWeek = startWeek.format(Constants.DATE_TIME_FORMATTER);
        this.deadlineWeek = deadlineWeek.format(Constants.DATE_TIME_FORMATTER);
    }

    public Tema(String id, String nume, String descriere, String startWeek, String deadlineWeek) {
        this.id = id;
        this.nume = nume;
        this.descriere = descriere;
        this.startWeek = startWeek;
        this.deadlineWeek = deadlineWeek;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getDescriere() {
        return descriere;
    }

    public void setDescriere(String descriere) {
        this.descriere = descriere;
    }

    public String getStartWeek() {
        return startWeek;
    }

    public String getDeadlineWeek() {
        return deadlineWeek;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tema)) return false;
        Tema tema = (Tema) o;
        return getStartWeek().equals(tema.getStartWeek()) &&
                getDeadlineWeek().equals(tema.getDeadlineWeek()) &&
                Objects.equals(getId(), tema.getId()) &&
                Objects.equals(getNume(), tema.getNume()) &&
                Objects.equals(getDescriere(), tema.getDescriere());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNume(), getDescriere(), getStartWeek(), getDeadlineWeek());
    }

    @Override
    public String toString() {
        return  nume + " - " + descriere;
    }

    public int getDuration(){
        LocalDate strt = LocalDate.parse(this.startWeek,Constants.DATE_TIME_FORMATTER);
        LocalDate dead = LocalDate.parse(this.deadlineWeek,Constants.DATE_TIME_FORMATTER);
        if(strt.getYear() == dead.getYear()){
            return Constants.getWeek(this.deadlineWeek) - Constants.getWeek(this.startWeek) + 1;
        }
        else{
            return Constants.getWeek(this.deadlineWeek) - Constants.getWeek(this.startWeek) + 1 + 52;
        }
    }
}
