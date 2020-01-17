package repositories;

import domain.Motivation;
import domain.Student;
import utils.Constants;
import validators.AbstractValidator;
import validators.ValidationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class MotivationRepository extends AbstracBaseRepository<String, Motivation> {

    private String fileName;
    public MotivationRepository(AbstractValidator<Motivation> validator, String fileName) {
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
        if(data.length != 8){
            return;
        }
        String id = data[0];
        String nume = data[1];
        String prenume = data[2];
        int grupa = Integer.parseInt(data[3]);
        String email = data[4];
        String cadru = data[5];
        Student student = new Student(id,nume,prenume,grupa,email,cadru);

        LocalDate start = LocalDate.parse(data[6], Constants.DATE_TIME_FORMATTER);
        LocalDate end = LocalDate.parse(data[7], Constants.DATE_TIME_FORMATTER);

        Motivation motivation = new Motivation(student,start,end);
        super.save(motivation);
    }

    private void writeToFile(){

        Path path = Paths.get(fileName);

        try{
            List<String> lines = Arrays.asList();
            Files.write(path,lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Motivation motivation: this.treeMap.values()) {
            String one = motivation.getIdStudent();
            String two = motivation.getNumeStudent();
            String three = motivation.getPrenumeStudent();
            String four = Integer.toString(motivation.getGrupaStudent());
            String five = motivation.getEmailStudent();
            String six = motivation.getCadruDidacticIndrumatorLabStudent();

            String seven = motivation.getStart().format(Constants.DATE_TIME_FORMATTER);
            String eight = motivation.getEnd().format(Constants.DATE_TIME_FORMATTER);

            String all = one + ";" + two + ";" + three + ";" + four + ";" + five + ";" + six + ";" + seven + ";" + eight;
            List<String> lines = Arrays.asList(all);

            try {
                Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Motivation save(Motivation entity) throws ValidationException {
        Motivation ret = super.save(entity);
        this.writeToFile();
        return ret;
    }

    @Override
    public Motivation delete(String s) {
        Motivation ret = super.delete(s);
        this.writeToFile();
        return ret;
    }

    @Override
    public Motivation update(Motivation entity) {
        Motivation ret = super.update(entity);
        this.writeToFile();
        return ret;
    }
}
