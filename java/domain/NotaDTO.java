package domain;

public class NotaDTO extends Entity<String> {
    private Nota n;
    private Tema t;
    private Student s;

    private String feedback;

    private String notaString;
    private String temaString;
    private String studentString;

    private int valoare;
    private String profesor;
    private String dataNota;
    private String idNota;

    private String deadlineTema;
    private String startTema;
    private String descriereTema;
    private String idTema;
    private String numeTema;

    private String emailStudent;
    private int grupaStudent;
    private String prenumeStudent;
    private String numeStudent;
    private String idStudent;

    public NotaDTO(Nota n, Tema t, Student s) {
        this.n = n;
        this.t = t;
        this.s = s;

        this.notaString = n.toString();
        this.temaString = t.toString();
        this.studentString = s.toString();

        this.idNota = n.getId();
        this.dataNota = n.getData();
        this.profesor = n.getProfesor();
        this.valoare = n.getValoare();

        this.deadlineTema = t.getDeadlineWeek();
        this.descriereTema = t.getDescriere();
        this.idTema = t.getId();
        this.numeTema = t.getNume();
        this.startTema = t.getStartWeek();

        this.idStudent = s.getId();
        this.emailStudent = s.getEmail();
        this.grupaStudent = s.getGrupa();
        this.numeStudent = s.getNume();
        this.prenumeStudent = s.getPrenume();

        this.feedback = n.getFeedback();
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getNotaString() {
        return notaString;
    }

    public String getTemaString() {
        return temaString;
    }

    public String getStudentString() {
        return studentString;
    }

    public Nota getN() {
        return n;
    }

    public void setN(Nota n) {
        this.n = n;
    }

    public Tema getT() {
        return t;
    }

    public void setT(Tema t) {
        this.t = t;
    }

    public Student getS() {
        return s;
    }

    public void setS(Student s) {
        this.s = s;
    }

    public int getValoare() {
        return valoare;
    }

    public void setValoare(int valoare) {
        this.valoare = valoare;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String getDataNota() {
        return dataNota;
    }

    public void setDataNota(String dataNota) {
        this.dataNota = dataNota;
    }

    public String getIdNota() {
        return idNota;
    }

    public void setIdNota(String idNota) {
        this.idNota = idNota;
    }

    public String getDeadlineTema() {
        return deadlineTema;
    }

    public void setDeadlineTema(String deadlineTema) {
        this.deadlineTema = deadlineTema;
    }

    public String getStartTema() {
        return startTema;
    }

    public void setStartTema(String startTema) {
        this.startTema = startTema;
    }

    public String getDescriereTema() {
        return descriereTema;
    }

    public void setDescriereTema(String descriereTema) {
        this.descriereTema = descriereTema;
    }

    public String getIdTema() {
        return idTema;
    }

    public void setIdTema(String idTema) {
        this.idTema = idTema;
    }

    public String getNumeTema() {
        return numeTema;
    }

    public void setNumeTema(String numeTema) {
        this.numeTema = numeTema;
    }

    public String getEmailStudent() {
        return emailStudent;
    }

    public void setEmailStudent(String emailStudent) {
        this.emailStudent = emailStudent;
    }

    public int getGrupaStudent() {
        return grupaStudent;
    }

    public void setGrupaStudent(int grupaStudent) {
        this.grupaStudent = grupaStudent;
    }

    public String getPrenumeStudent() {
        return prenumeStudent;
    }

    public void setPrenumeStudent(String prenumeStudent) {
        this.prenumeStudent = prenumeStudent;
    }

    public String getNumeStudent() {
        return numeStudent;
    }

    public void setNumeStudent(String numeStudent) {
        this.numeStudent = numeStudent;
    }

    public String getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(String idStudent) {
        this.idStudent = idStudent;
    }
}
