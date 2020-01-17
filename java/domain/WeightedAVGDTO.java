package domain;

import java.text.DecimalFormat;

public class WeightedAVGDTO {
    private Student s;
    private String studentString;
    private double avg;
    private DecimalFormat df = new DecimalFormat("#.00");

    public WeightedAVGDTO(Student s, Double avg) {
        this.s = s;
        this.avg=avg;
        this.studentString=s.toString();
    }

    public String getStudentString() {
        return studentString;
    }

    public void setStudentString(String studentString) {
        this.studentString = studentString;
    }

    public Student getS() {
        return s;
    }

    public void setS(Student s) {
        this.s = s;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    @Override
    public String toString() {

        return studentString + " : " + df.format(avg);
    }
}
