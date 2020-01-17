package repositories;


import domain.Student;
import validators.AbstractValidator;
import validators.ValidationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class StudentRepository extends AbstracBaseRepository<String, Student> {
    private String fileName;
    public StudentRepository(AbstractValidator<Student> validator, String fileName) {
        super(validator);
        this.fileName=fileName;
        loadData();
    }

    private void loadData() {
        Path path = Paths.get(fileName);
        try {
            List<String> lines= Files.readAllLines(path);
            lines.forEach(this::parseLine);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine(String o) {
        String[] data=o.split(";");
        if(data.length != 6){
            return;
        }
        String id = data[0];
        String nume = data[1];
        String prenume = data[2];
        int grupa = Integer.parseInt(data[3]);
        String email = data[4];
        String cadru = data[5];
        Student student = new Student(id,nume,prenume,grupa,email,cadru);
        super.save(student);
    }

    private void writeToFile(){

        Path path = Paths.get(fileName);

        try{
            List<String> lines = Arrays.asList();
            Files.write(path,lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Student student: this.treeMap.values()) {
            String one = student.getId();
            String two = student.getNume();
            String three = student.getPrenume();
            String four = Integer.toString(student.getGrupa());
            String five = student.getEmail();
            String six = student.getCadruDidacticIndrumatorLab();

            String all = one + ";" + two + ";" + three + ";" + four + ";" + five + ";" + six;
            List<String> lines = Arrays.asList(all);

            /*try {
                BufferedWriter bufferedWriter = new Files.newBufferedWriter(path,StandardOpenOption.APPEND);
                bufferedWriter.write(student.getId() + ";" + student.getNume() + ";" + student.getPrenume() + ";" + student.getGrupa() + ";" + student.getEmail() + ";" + student.getCadruDidacticIndrumatorLab());
                bufferedWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            try {
                Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Student save(Student entity) throws ValidationException {
        Student ret = super.save(entity);
        this.writeToFile();
        return ret;
    }

    @Override
    public Student delete(String s) {
        Student ret = super.delete(s);
        this.writeToFile();
        return ret;
    }

    @Override
    public Student update(Student entity) {
        Student ret = super.update(entity);
        this.writeToFile();
        return ret;
    }
}
