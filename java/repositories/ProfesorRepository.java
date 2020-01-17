package repositories;

import domain.Profesor;
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

public class ProfesorRepository extends AbstracBaseRepository<String, Profesor> {

    private String fileName;

    public ProfesorRepository(AbstractValidator<Profesor> abstractValidator, String fileName) {
        super(abstractValidator);
        this.fileName = fileName;
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
        if(data.length != 4){
            return;
        }
        String id = data[0];
        String nume = data[1];
        String prenume = data[2];
        String email = data[3];
        Profesor student = new Profesor(id,nume,prenume,email);
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

        for (Profesor profesor: this.treeMap.values()) {
            String one = profesor.getId();
            String two = profesor.getNume();
            String three = profesor.getPrenume();
            String four = profesor.getEmail();

            String all = one + ";" + two + ";" + three + ";" + four;
            List<String> lines = Arrays.asList(all);


            try {
                Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Profesor save(Profesor entity) throws ValidationException {
        Profesor ret = super.save(entity);
        this.writeToFile();
        return ret;
    }

    @Override
    public Profesor delete(String s) {
        Profesor ret = super.delete(s);
        this.writeToFile();
        return ret;
    }

    @Override
    public Profesor update(Profesor entity) {
        Profesor ret = super.update(entity);
        this.writeToFile();
        return ret;
    }

}
