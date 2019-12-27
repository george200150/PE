package repositories;


import domain.Nota;
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


public class NotaRepository extends AbstracBaseRepository<String, Nota> {
    private String fileName;
    public NotaRepository(AbstractValidator<Nota> validator, String fileName) {
        super(validator);
        this.fileName=fileName;
        loadData();
    }

    private void loadData() {
        Path path = Paths.get(fileName);
        try {
            List<String> lines= Files.readAllLines(path);
//            lines.forEach(x->{
//                E entity= parseLine(x);
//            });
            lines.forEach(this::parseLine);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parseLine(String o) {
        String[] data=o.split(";");
        String id = data[0];
        String date = data[1];
        String profesor = data[2];
        int valoare = Integer.parseInt(data[3]);
        String feedback = data[4];
        Nota nota = new Nota(id,valoare,profesor,date,feedback);
        super.save(nota);
    }

    private void writeToFile(){

        Path path = Paths.get(fileName);

        try{
            List<String> lines = Arrays.asList();
            Files.write(path,lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Nota nota: this.treeMap.values()) {
            String one = nota.getId();
            String two = nota.getData();
            String three = nota.getProfesor();
            String four = Integer.toString(nota.getValoare());
            String five = nota.getFeedback();

            String all = one + ";" + two + ";" + three + ";" + four + ";" + five;
            List<String> lines = Arrays.asList(all);

            try {
                Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public Nota save(Nota entity) throws ValidationException {
        Nota found = super.findOne(entity.getId());
        if(found == null){
            Nota ret = super.save(entity);
            this.writeToFile();
            return ret;
        }
        else{
            throw new ValidationException("DUPLICATE VALUES CANNOT EXIST FOR GRADES!!!");
        }
    }

    @Override
    public Nota delete(String s) {
        Nota ret = super.delete(s);
        this.writeToFile();
        return ret;
    }

    @Override
    public Nota update(Nota entity) {
        Nota ret = super.update(entity);
        this.writeToFile();
        return ret;
    }
}
