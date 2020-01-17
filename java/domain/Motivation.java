package domain;

import java.time.LocalDate;

public class Motivation extends Entity<String> {
    private Student student;

    private String id;
    private String idStudent;
    private String numeStudent;
    private String prenumeStudent;
    private int grupaStudent;
    private String emailStudent;
    private String cadruDidacticIndrumatorLabStudent;

    private Interval interval;
    private String intervalToString;
    private String studentToString;


    public Motivation(String id, String nume, String prenume, int grupa, String email, String profesor, LocalDate start, LocalDate end) {
        this.student = new Student(id, nume, prenume, grupa, email, profesor);
        this.interval = new Interval(start, end);

        this.idStudent = id;
        this.numeStudent = nume;
        this.prenumeStudent = prenume;
        this.grupaStudent = grupa;
        this.emailStudent = email;
        this.cadruDidacticIndrumatorLabStudent = profesor;

        this.intervalToString = interval.toString();
        this.studentToString = student.toString();
        this.id = this.idStudent + this.interval.toString();
    }

    public Motivation(Student student, LocalDate start, LocalDate end) {
        this.student = student;
        this.interval = new Interval(start, end);

        this.idStudent = student.getId();
        this.numeStudent = student.getNume();
        this.prenumeStudent = student.getPrenume();
        this.grupaStudent = student.getGrupa();
        this.emailStudent = student.getEmail();
        this.cadruDidacticIndrumatorLabStudent = student.getCadruDidacticIndrumatorLab();

        this.intervalToString = interval.toString();
        this.studentToString = student.toString();
        this.id = this.idStudent + this.interval.toString();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getStudentToString() {
        return studentToString;
    }

    public void setStudentToString(String studentToString) {
        this.studentToString = studentToString;
    }

    public Interval getInterval() {
        return interval;
    }

    public void setInterval(Interval interval) {
        this.interval = interval;
    }

    public String getIntervalToString() {
        return intervalToString;
    }

    public void setIntervalToString(String toString) {
        this.intervalToString = toString;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }

    public String getNumeStudent() {
        return numeStudent;
    }

    public void setNumeStudent(String numeStudent) {
        this.numeStudent = numeStudent;
    }

    public String getPrenumeStudent() {
        return prenumeStudent;
    }

    public void setPrenumeStudent(String prenumeStudent) {
        this.prenumeStudent = prenumeStudent;
    }

    public int getGrupaStudent() {
        return grupaStudent;
    }

    public void setGrupaStudent(int grupaStudent) {
        this.grupaStudent = grupaStudent;
    }

    public String getEmailStudent() {
        return emailStudent;
    }

    public void setEmailStudent(String emailStudent) {
        this.emailStudent = emailStudent;
    }

    public String getCadruDidacticIndrumatorLabStudent() {
        return cadruDidacticIndrumatorLabStudent;
    }

    public void setCadruDidacticIndrumatorLabStudent(String cadruDidacticIndrumatorLabStudent) {
        this.cadruDidacticIndrumatorLabStudent = cadruDidacticIndrumatorLabStudent;
    }

    public LocalDate getStart() {
        return interval.getStart();
    }



    public LocalDate getEnd() {
        return interval.getEnd();
    }



}
