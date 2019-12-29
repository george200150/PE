package domain;

public class StudentDTO {
    private String id;
    private String nume;
    private String prenume;
    private int grupa;
    private String email;
    private String cadruDidacticIndrumatorLab;
    private String password;

    public StudentDTO(String id, String nume, String prenume, int grupa, String email, String cadruDidacticIndrumatorLab, String password) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.grupa = grupa;
        this.email = email;
        this.cadruDidacticIndrumatorLab = cadruDidacticIndrumatorLab;
        this.password = password;
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

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public int getGrupa() {
        return grupa;
    }

    public void setGrupa(int grupa) {
        this.grupa = grupa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCadruDidacticIndrumatorLab() {
        return cadruDidacticIndrumatorLab;
    }

    public void setCadruDidacticIndrumatorLab(String cadruDidacticIndrumatorLab) {
        this.cadruDidacticIndrumatorLab = cadruDidacticIndrumatorLab;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
