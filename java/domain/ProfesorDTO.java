package domain;

public class ProfesorDTO {
    private String id;
    private String nume;
    private String prenume;
    private String email;
    private String password;
    private Profesor profesor;

    public ProfesorDTO(Profesor profesor, String password) {
        this.profesor = profesor;
        this.id = profesor.getId();
        this.nume = profesor.getNume();
        this.prenume = profesor.getPrenume();
        this.email = profesor.getEmail();
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getNume() {
        return nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }
}
