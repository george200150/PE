package services;

import domain.Profesor;
import domain.Student;
import validators.ValidationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoginCredentialsProxy {
    private ProfesorService profesorService = null;
    private StudentService studentService = null;


    public LoginCredentialsProxy(ProfesorService profesorService, StudentService studentService) {
        this.profesorService = profesorService;
        this.studentService = studentService;
    }

    public String getStudentPassword(Student student){
        for (String line : getPSSWDContent()) {
            String[] components = line.split(":");
            if(components.length != 0 && components[0].equals("student") && components[3].equals(student.getId()))
                return components[2];
        }
        return null;
    }

    public String getProfesorPassword(Profesor profesor){
        for (String line : getPSSWDContent()) {
            String[] components = line.split(":");
            if(components.length != 0 && components[0].equals("profesor") && components[3].equals(profesor.getId()))
                return components[2];
        }
        return null;
    }

    public String getAdminPassword(){
        for (String line : getPSSWDContent()) {
            String[] components = line.split(":");
            if(components.length != 0 && components[0].equals("admin"))
                return components[2];
        }
        return null;
    }


    public Profesor findByIdProfesor(String s) {
        return profesorService.findById(s);
    }

    public Iterable<Profesor> getAllProfesor() {
        return profesorService.getAll();
    }

    public Profesor addProfesor(Profesor entity) throws ValidationException {
        return profesorService.add(entity);
    }

    public Profesor removeByIdProfesor(String s) {
        return profesorService.removeById(s);
    }

    public Profesor updateProfesor(Profesor newEntity) {
        return profesorService.update(newEntity);
    }





    public boolean changeStudentPassword(String user, String oldp, String newp){

        Student student = findStudentByCredentials(user,oldp);
        if(student == null){
            return false;
        }
        else{
            String oldLine = "student:"+user+":"+oldp+":"+student.getId();
            String newLine = "student:"+user+":"+newp+":"+student.getId();
            changeLinePSSWD(oldLine,newLine);
            return true;
        }
    }

    public boolean changeProfessorPassword(String user, String oldp, String newp){

        Profesor profesor = findProfessorByCredentials(user,oldp);
        if(profesor == null){
            return false;
        }
        else{
            String oldLine = "profesor:"+user+":"+oldp+":"+profesor.getId();
            String newLine = "profesor:"+user+":"+newp+":"+profesor.getId();
            changeLinePSSWD(oldLine,newLine);
            return true;
        }
    }

    public boolean changeAdminPassword(String newp){
        String oldp = getAdminPassword();
        String oldLine = "admin:"+""+":"+oldp+":"+"0";
        String newLine = "admin:"+""+":"+newp+":"+"0";
        changeLinePSSWD(oldLine,newLine);
        return true;
    }




    public void changeLinePSSWD(String oldLine, String newLine){

        Path path = Paths.get("data/PSSWD.txt");
        List<String> lines = getPSSWDContent();


        try {
            File file = new File("data/PSSWD.txt");
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        try{
            List<String> empty = new ArrayList<>();
            Files.write(path, empty, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> modifiedLines = new ArrayList<>();

        for (String line : lines) {
            if(line.equals(oldLine)){
                modifiedLines.add(newLine);
            }
            else{
                modifiedLines.add(line);
            }
        }

        try {
            Files.write(path, modifiedLines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> getPSSWDContent() {
        Path path = Paths.get("data/PSSWD.txt");
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }



    private Student getStudentFromPSSWD(String line) {
        String[] data = line.split(":");
        if (data.length != 4) {
            return null;
        }
        String id = data[3];
        return this.studentService.findById(id);
    }

    private Profesor getProfessorFromPSSWD(String line) {
        String[] data = line.split(":");
        if (data.length != 4) {
            return null;
        }
        String id = data[3];
        return this.profesorService.findById(id);
    }



    private boolean matchLineStudent(String line, String usr, String psswd) {
        String[] data = line.split(":");
        if (data.length != 4) {
            return false;
        }
        String type = data[0];
        String username = data[1];
        String password = data[2];
        //String id = data[3]; - we don't use it now
        return type.equals("student") && usr.equals(username) && psswd.equals(password);
    }

    private boolean matchLineProfessor(String line, String usr, String psswd){
        String[] data = line.split(":");
        if (data.length != 4) {
            return false;
        }
        String type = data[0];
        String username = data[1];
        String password = data[2];
        //String id = data[3]; - we don't use it now
        return type.equals("profesor") && usr.equals(username) && psswd.equals(password);
    }

    private boolean matchLineAdmin(String line, String psswd){
        String[] data = line.split(":");
        if (data.length != 4) {
            return false;
        }
        String type = data[0];
        String username = data[1];
        String password = data[2];
        //String id = data[3]; - we don't use it now
        return type.equals("admin") && psswd.equals(password);
    }



    public Student findStudentByCredentials(String usr, String psswd){
        List<String> lines = getPSSWDContent();
        if(lines != null){
            List<String> found = lines.stream().filter(line -> matchLineStudent(line, usr, psswd)).collect(Collectors.toList());
            if(found.size() == 0)
                return null;
            else
                return getStudentFromPSSWD(found.get(0));
        }
        else{
            return null;
        }
    }

    public Profesor findProfessorByCredentials(String usr, String psswd){
        List<String> lines = getPSSWDContent();
        if(lines != null){
            List<String> found = lines.stream().filter(line -> matchLineProfessor(line, usr, psswd)).collect(Collectors.toList());
            if(found.size() == 0)
                return null;
            else
                return getProfessorFromPSSWD(found.get(0));
        }
        else{
            return null;
        }
    }

    public boolean findAdminByCredentials(String password) {
        List<String> list = this.getPSSWDContent();
        return list.stream().anyMatch(x -> matchLineAdmin(x, password));
    }

}
