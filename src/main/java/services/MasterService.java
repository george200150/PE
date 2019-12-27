package services;

import domain.Nota;
import domain.Profesor;
import domain.Student;
import domain.Tema;
import utils.Constants;
import utils.events.ChangeEventType;
import utils.events.GradeChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MasterService implements Observable<GradeChangeEvent> {
    private ProfesorService profesorService = null;
    private StudentService studentService = null;
    private TemaService temaService = null;
    private NotaService notaService = null;
    private List<Observer<GradeChangeEvent>> observers = new ArrayList<>();

    public MasterService(ProfesorService profesorService, StudentService studentService, TemaService temaService, NotaService notaService) {//TODO as putea face un service comunn,
        this.profesorService = profesorService;
        this.studentService = studentService;
        this.temaService = temaService;
        this.notaService = notaService;
    }
    //TODO: pentru baza de date nu mai pun id la student/tema, ci direct referinte la obiecte


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

        assert lines != null;//TODO: LINES MAY BE NULL IS FILE DOES NOT OPEN PROPERLY

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
        return null;
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
        assert list != null;//TODO: list of lines must not be null (might never be though; else file is broken)
        return list.stream().anyMatch(x -> matchLineAdmin(x, password));
    }












    public Student findByIdStudent(String s) {
        return studentService.findById(s);
    }
    public Iterable<Student> getAllStudent() {
        return studentService.getAll();
    }
    public Student addStudent(Student entity) throws ValidationException { return studentService.add(entity); }
    public Student removeByIdStudent(String s) {
        return studentService.removeById(s);
    }
    public Student updateStudent(Student entity) {
        return studentService.update(entity);
    }




    public Tema findByIdTema(String s) { return temaService.findById(s); }
    public Iterable<Tema> getAllTema() {
        return temaService.getAll();
    }
    public Tema addTema(Tema entity) throws ValidationException { return temaService.add(entity); }
    public Tema removeByIdTema(String s) {
        return temaService.removeById(s);
    }
    public Tema updateTema(Tema entity) {
        return temaService.update(entity);
    }



    //TODO: pentru nota service , voi lua toate informatiile despre studenti si teme, pentru a gasi datele fara sa avem un service in care sa grupam totul
    public Nota findByIdNota(String s) { return notaService.findById(s); }
    public Iterable<Nota> getAllNota() { return notaService.getAll(); }
    public Nota addNota(Nota entity) throws ValidationException {
        Nota r = notaService.add(entity);
        if(r == null) {
            notifyObservers(new GradeChangeEvent(ChangeEventType.ADD, entity));
        }
        return r;
    }
    public Nota removeByIdNota(String s) {
        //return notaService.removeById(s);
        Nota r = notaService.removeById(s);
        if(r != null) {
            notifyObservers(new GradeChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }
    public Nota updateNota(Nota newEntity) {
        //return notaService.update(entity);
        Nota oldStudent = notaService.findById(newEntity.getId());
        Nota res = notaService.update(newEntity);
        if(res == null) {
            notifyObservers(new GradeChangeEvent(ChangeEventType.UPDATE, newEntity, oldStudent));
        }
        return res;
    }



    public int getWeekNow(){
        String input = LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER);
        String format = "yyyy/MM/dd";
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date;
        int current;
        try {
            date = df.parse(input);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            current = cal.get(Calendar.WEEK_OF_YEAR);
        } catch (ParseException e) {
            current = -1;
        }
        return current;
    }

    public int getDelayForTaskNow(String tid){
        Tema t = this.temaService.findById(tid);
        int current = getWeekNow();
        int deadline = getWeekByDate(t.getDeadlineWeek());
        return Math.max(0, current - deadline);
    }

    public int getMaxGradeNow(String tid){
        int delay = getDelayForTaskNow(tid);
        return 10 - delay;
    }








    /**
     * input must be a - String input = LocalDateTime.now().format(Constants.DATE_TIME_FORMATTER) - type of string
     */
    public int getWeekByDate(String input){
        String format = "yyyy/MM/dd";
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date;
        int current;
        try {
            date = df.parse(input);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            current = cal.get(Calendar.WEEK_OF_YEAR);
        } catch (ParseException e) {
            current = -1;
        }
        return current;
    }

    public int getDelayForTaskByDate(String tid, String date){
        Tema t = this.temaService.findById(tid);
        int current = getWeekByDate(date);
        int deadline = getWeekByDate(t.getDeadlineWeek());
        return Math.max(0, current - deadline);
    }

    public int getMaxGradeByDate(String tid, String date){
        int delay = getDelayForTaskByDate(tid, date);
        return 10 - delay;
    }

    public int getMaxGradeByDelay(int delay){
        return Math.max(10-delay,0);
    }

    public int getActualDelay(int delay){
        //check if task was handed in sooner than deadline.
        return Math.max(delay,0);
    }

    public boolean isGradeStillAvailableToHandInNow(String tid){
        Tema t = temaService.findById(tid);
        int week = getWeekNow();
        int delay = getWeekByDate(t.getDeadlineWeek()) - week;
        return isDelayOverdue(delay);
    }

    public boolean isDelayOverdue(int delay){
        return delay > 2;
    }








    public void addGradePlusChecks(String sid, String tid, int valoare, String profesor, String feedback){
        Nota nota = new Nota(sid,tid,valoare,profesor);
        //TODO: check if proffessor exists (later)

        boolean foundStudent = false;
        boolean foundTask = false;

        for (Student st : this.studentService.getAll()) {
            if(st.getId().equals(nota.getId().split(":")[0])){
                foundStudent = true;
                break;
            }
        }

        for (Tema st : this.temaService.getAll()) {
            if(st.getId().equals(nota.getId().split(":")[1])){
                foundTask = true;
                break;
            }
        }

        String exception = "";//TODO : ? extract validation class ?
        if(!foundStudent){
            exception += "THE STUDENT DOES NOT EXIST IN THE DATABASE!";
        }
        if(!foundTask){
            exception += "THE TASK DOES NOT EXIST IN THE DATABASE!";
        }
        if(exception.length() > 0){
            throw new ValidationException(exception);
        }

        notaService.add(nota);

        writeToFile(nota,feedback);
    }


    private void writeToFile(Nota nota, String feedback){
        String[] ids = nota.getId().split(":");
        Student student = this.studentService.findById(ids[0]);
        Tema tema = this.temaService.findById(ids[1]);


        Path path = Paths.get("files/" + student.getNume() + "_" + student.getPrenume() + ".txt");


        String one = "TEMA: " + tema.getNume();
        String two = "NOTA: " + nota.getValoare();
        String three = "PREDATA IN SAPTAMANA: " + nota.getData();
        String four = "DEADLINE: " + tema.getDeadlineWeek();
        String five = "FEEDBACK: " + feedback;

        String all = one + "\n" + two + "\n" + three + "\n" + four + ";\n" + five;
        List<String> lines = Arrays.asList(all);

        try {
            Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();//TODO: o sa cream fisierul doar daca nu exista (...?cum?...)
        }
    }



    // o 1: Toți studenții unei grupe
    public List<Student> filterGroup(int grupa) {
        List<Student> list = new ArrayList<>((Collection<? extends Student>) this.studentService.getAll());

        return list.stream()
                .filter(x -> x.getGrupa() == grupa)
                .collect(Collectors.toList());
    }


    // o 2: Toți studenții care au predat o anumita tema
    public List<Student> filterTaskDone(String tid) {
        List<Nota> list = new ArrayList<>((Collection<? extends Nota>) this.notaService.getAll());

        return list.stream()
                .filter(x -> x.getId().split(":")[1].equals(tid))// luam doar temele cautate
                .map(x -> this.studentService.findById(x.getId().split(":")[0]))// mapam din Nota in Stuednt
                .collect(Collectors.toList());// colectam rezultatul
    }


    // o 3: Toți studenții care au predat o anumita tema unui profesor anume
    public List<Student> filterTaskDoneProf(String tid, String prof) {
        List<Nota> list = new ArrayList<>((Collection<? extends Nota>) this.notaService.getAll());

        /*return list.stream()
                .filter(x-> x.getProfesor().equals(prof)) // AICI TRIEM LISTA, SA AVEM DOAR PROFESORUL CAUTAT
                .filter(x -> x.getId().split(":")[1].equals(tid)) // AICI FILTRAM SA AVEM DOAR TEMA CAUTATA
                .map(x -> new NotaDTO(x.getId().split(":")[1], this.studentService.findById(x.getId().split(":")[0]))) // AICI MAPAM O NOTA INTR-O "PERECHE" <tid,Student>
                .map(NotaDTO::getS) // AICI MAPAM DIN NotaDTO LA STUDENT (extragem din NotaDTO info utila)
                .collect(Collectors.toList()); // AICI COLECTAM IN LISTA STUDENTII*/
        return null;
    }


    // o 4: Toate notele la o anumita tema, dintr-o saptamana data
    public List<Nota> filterGradesForTaskInWeek(String tid, int week) {
        List<Nota> list = new ArrayList<Nota>((Collection<? extends Nota>) this.notaService.getAll());

        return list.stream()
                .filter(x -> x.getId().split(":")[1].equals(tid))//filtram tema
                .filter(x -> {int v = this.getWeekByDate(x.getData()); return v==week;})//filtram data
                .collect(Collectors.toList());//colectam notele din rezultat
    }



    /*public Map<Student, Integer> raport1(){
        Iterable<Nota> grades = notaService.getAll();
        List<NotaDTO> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .map(x -> new NotaDTO(x, temaService.findById(x.getId().split(":")[1]), studentService.findById(x.getId().split(":")[0])))
                .collect(Collectors.toList());

        Map<Student,List<NotaDTO>> grouped = gradeList.stream()
                .collect(Collectors.groupingBy(NotaDTO::getS));


        grouped.values().stream()//TODO: get weighted average (weights represent week count / task)
                .map(notaDTOS -> {//there are 14 weeks / semester
                    double medie = notaDTOS.stream()
                            .map(x -> x.getValoare() * ((Constants.getWeek(x.getDeadlineTema()) - Constants.getWeek(x.getStartTema())) / 14))
                            .reduce(0, Integer::sum);
                }).collect(Collectors.toList());//TODO: lista de note - trebuie eventual imperecheate -!!! CU UN NOU DTO !!!- pentru a putea afisa un raport al studentilor + mediilor lor.
        //TODO:     .map( new DTOnou(Student, mediedouble).collect(Collectors.toList());
    }*/


    @Override
    public void addObserver(Observer<GradeChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<GradeChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(GradeChangeEvent t) {
        observers.forEach(x->x.update(t));
    }


}

