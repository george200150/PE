package domain;

import java.util.Objects;

public class Student extends Entity<String> {
    private String id;
    private String nume;
    private String prenume;
    private int grupa;
    private String email;
    private String cadruDidacticIndrumatorLab;

    public Student(String id, String nume, String prenume, int grupa, String email, String cadruDidacticIndrumatorLab) {
        this.id = id;
        this.nume = nume;
        this.prenume = prenume;
        this.grupa = grupa;
        this.email = email;
        this.cadruDidacticIndrumatorLab = cadruDidacticIndrumatorLab;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return getGrupa() == student.getGrupa() &&
                Objects.equals(getId(), student.getId()) &&
                Objects.equals(getNume(), student.getNume()) &&
                Objects.equals(getPrenume(), student.getPrenume()) &&
                Objects.equals(getEmail(), student.getEmail()) &&
                Objects.equals(getCadruDidacticIndrumatorLab(), student.getCadruDidacticIndrumatorLab());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNume(), getPrenume(), getGrupa(), getEmail(), getCadruDidacticIndrumatorLab());
    }

    @Override
    public String toString() {
        /*return "Student{" +
                "id='" + id + '\'' +
                ", nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", grupa=" + grupa +
                ", email='" + email + '\'' +
                ", cadruDidacticIndrumatorLab='" + cadruDidacticIndrumatorLab + '\'' +
                '}';*/
        return  nume + " " + prenume + " - " + grupa;
    }
}
