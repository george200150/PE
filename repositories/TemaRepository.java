package repositories;


import domain.Tema;
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

public class TemaRepository extends AbstracBaseRepository<String, Tema> {
    private String fileName;
    public TemaRepository(AbstractValidator<Tema> validator, String fileName) {
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
        String nume = data[1];
        String descriere = data[2];

        String startingWeek = data[3];//we read date forma: dd/ww/yyyy
        String deadlineWeek = data[4];//we read date forma: dd/ww/yyyy

        Tema tema = new Tema(id,nume,descriere,startingWeek,deadlineWeek);
        super.save(tema);
    }

    private void writeToFile(){

        Path path = Paths.get(fileName);

        try{
            List<String> lines = Arrays.asList();
            Files.write(path,lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Tema tema: this.treeMap.values()) {
            String one = tema.getId();
            String two = tema.getNume();
            String three = tema.getDescriere();
            String four = tema.getStartWeek();
            String five = tema.getDeadlineWeek();

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
    public Tema save(Tema entity) throws ValidationException {
        Tema ret = super.save(entity);
        this.writeToFile();
        return ret;
    }

    @Override
    public Tema delete(String s) {
        Tema ret = super.delete(s);
        this.writeToFile();
        return ret;
    }

    @Override
    public Tema update(Tema entity) {
        Tema ret = super.update(entity);
        this.writeToFile();
        return ret;
    }
}