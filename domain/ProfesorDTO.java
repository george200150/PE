package domain;

public class ProfesorDTO {
    private String id;
    private String nume;
    private String prenume;
    private String email;
    private String password;

    public ProfesorDTO(String id, String nume, String prenume, String email, String password) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.email = email;
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
}
