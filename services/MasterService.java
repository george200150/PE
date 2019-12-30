package services;

import domain.*;
import utils.Constants;
import utils.events.ChangeEventType;
import utils.events.GradeChangeEvent;
import utils.events.StudentChangeEvent;
import utils.events.TaskChangeEvent;
import utils.observer.*;
import utils.observer.Observable;
import utils.observer.Observer;
import validators.ValidationException;

import java.io.IOException;
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
import java.util.stream.StreamSupport;

//public class MasterService implements Observable<GradeChangeEvent> {
public class MasterService implements ObservableGrade, ObservableTask, ObservableStudent {
    private ProfesorService profesorService = null;
    private StudentService studentService = null;
    private TemaService temaService = null;
    private NotaService notaService = null;
    private MotivationService motivationService = null;
    private LoginCredentialsProxy loginCredentialsProxy = null;
    //private List<Observer<GradeChangeEvent>> observers = new ArrayList<>();
    private List<GradeObserver> gradeObservers = new ArrayList<>();
    private List<TaskObserver> taskObservers = new ArrayList<>();
    private List<StudentObserver> studentObservers = new ArrayList<>();

    public MasterService(ProfesorService profesorService, StudentService studentService, TemaService temaService, NotaService notaService, MotivationService motivationService) {
        this.profesorService = profesorService;
        this.studentService = studentService;
        this.temaService = temaService;
        this.notaService = notaService;
        this.motivationService = motivationService;
        this.loginCredentialsProxy = new LoginCredentialsProxy(profesorService, studentService);

    }
    //TODO: pentru baza de date nu mai pun id la student/tema, ci direct referinte la obiecte


    public void deleteAllGradesOfStudent(Student student){
        StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getId().split(":")[0].equals(student.getId()))
                .forEach(x -> this.removeByIdNota(x.getId()));
    }

    public void deleteAllGradesOfTema(Tema tema){
        StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getId().split(":")[1].equals(tema.getId()))
                .forEach(x -> this.removeByIdNota(x.getId()));
    }

    public void deleteAllStudentsOfProfesor(Profesor profesor){
        StreamSupport
                .stream(this.getAllStudent().spliterator(),false)
                .filter(x -> x.getCadruDidacticIndrumatorLab().equals(profesor.toString()))
                .forEach(x -> this.removeByIdStudent(x.getId()));
        deleteAllGradesOfProfesor(profesor);
    }

    public void deleteAllGradesOfProfesor(Profesor profesor){
        StreamSupport
                .stream(this.getAllNota().spliterator(),false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))
                .forEach(x -> this.removeByIdNota(x.getId()));
    }


    public void updateAllGradesOfStudent(Student student){
        //nu exista update la grade (nota are numai id-ul) - este complicat de zic ca ar avea sens sa schimbi profesorul care a dat nota pe tema... e teoretic imposibil sa se schimbe proful...
        StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getId().split(":")[0].equals(student.getId()))
                .forEach(x -> this.updateNota(new Nota(student.getId()+":"+x.getId().split(":")[1],x.getValoare(),student.getCadruDidacticIndrumatorLab(),x.getData(),x.getFeedback())));
    }

    public void updateAllGradesOfTema(Tema tema){
        //teoretic, daca nu am schimba id-ul temei, nu ar avea sens update-ul...
        StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getId().split(":")[1].equals(tema.getId()))
                .forEach(x -> this.updateNota(new Nota(x.getId().split(":")[0]+":"+tema.getId(),x.getValoare(),x.getProfesor(),x.getData(),x.getFeedback())));
    }

    public void updateAllStudentsOfProfesor(Profesor profesor){
        StreamSupport
                .stream(this.getAllStudent().spliterator(),false)
                .filter(x -> x.getCadruDidacticIndrumatorLab().equals(profesor.toString()))
                .forEach(x -> this.updateStudent(new Student(x.getId(),x.getNume(),x.getPrenume(),x.getGrupa(),x.getEmail(),profesor.toString())));
        updateAllGradesOfProfesor(profesor);
    }

    public void updateAllGradesOfProfesor(Profesor profesor){
        StreamSupport
                .stream(this.getAllNota().spliterator(),false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))
                .forEach(x -> this.updateNota(new Nota(x.getId(),x.getValoare(),profesor.toString(),x.getData(),x.getFeedback())));
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




    public Motivation findByIdMotivation(String s) {
        return motivationService.findById(s);
    }

    public Iterable<Motivation> getAllMotivation() {
        return motivationService.getAll();
    }

    public Motivation addMotivation(Motivation entity) throws ValidationException {
        return motivationService.add(entity);
    }

    public Motivation removeByIdMotivation(String s) {
        return motivationService.removeById(s);
    }

    public Motivation updateMotivation(Motivation newEntity) {
        return motivationService.update(newEntity);
    }





    public String getStudentPassword(Student student){
        return this.loginCredentialsProxy.getStudentPassword(student);
    }

    public String getProfesorPassword(Profesor profesor){
        return this.loginCredentialsProxy.getProfesorPassword(profesor);
    }

    public String getAdminPassword(){
        return this.loginCredentialsProxy.getAdminPassword();
    }

    public boolean changeStudentPassword(String user, String oldp, String newp){
        return this.loginCredentialsProxy.changeStudentPassword(user, oldp, newp);
    }

    public boolean changeProfessorPassword(String user, String oldp, String newp){
        return this.loginCredentialsProxy.changeProfessorPassword(user, oldp, newp);
    }

    public boolean changeAdminPassword(String newp){
        return this.loginCredentialsProxy.changeAdminPassword(newp);
    }

    public void changeLinePSSWD(String oldLine, String newLine){
        this.loginCredentialsProxy.changeLinePSSWD(oldLine, newLine);
    }


    public void addStudentPSSWD(Student student, String psswd){
        this.loginCredentialsProxy.addStudentPSSWD(student, psswd);
    }

    public void addProfesorPSSWD(Profesor profesor, String psswd){
        this.loginCredentialsProxy.addProfesorPSSWD(profesor, psswd);
    }

    public void updateStudentPSSWD(Student student, String psswd){
        this.loginCredentialsProxy.updateStudentPSSWD(student, psswd);
    }

    public void updateProfesorPSSWD(Profesor profesor, String psswd){
        this.loginCredentialsProxy.updateProfesorPSSWD(profesor, psswd);
    }


    public void deleteStudentPSSWD(Student student){
        this.loginCredentialsProxy.deleteStudentPSSWD(student);
    }

    public void deleteProfesorPSSWD(Profesor profesor){
        this.loginCredentialsProxy.deleteProfesorPSSWD(profesor);
    }

    /*private List<String> getPSSWDContent() {
        return this.loginCredentialsProxy.getPSSWDContent();
    }*/
   /* private Student getStudentFromPSSWD(String line) {
    }*/
    /*private Profesor getProfessorFromPSSWD(String line) {
    }*/
    /*private boolean matchLineStudent(String line, String usr, String psswd) {
    }*/
    /*private boolean matchLineProfessor(String line, String usr, String psswd){
    }*/
    /*private boolean matchLineAdmin(String line, String psswd){
    }*/

    public Student findStudentByCredentials(String usr, String psswd){
        return this.loginCredentialsProxy.findStudentByCredentials(usr, psswd);
    }

    public Profesor findProfessorByCredentials(String usr, String psswd){
        return this.loginCredentialsProxy.findProfessorByCredentials(usr, psswd);
    }

    public boolean findAdminByCredentials(String password) {
        return this.loginCredentialsProxy.findAdminByCredentials(password);
    }












    public Student findByIdStudent(String s) {
        return studentService.findById(s);
    }
    public Iterable<Student> getAllStudent() {
        return studentService.getAll();
    }
    public Student addStudent(Student entity) throws ValidationException {
        Student r = studentService.add(entity);
        if(r == null) {
            notifyObserversStudent(new StudentChangeEvent(ChangeEventType.ADD, entity));
        }
        return r;
    }
    public Student removeByIdStudent(String s) {
        Student r = studentService.removeById(s);
        if(r != null) {
            notifyObserversStudent(new StudentChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }
    public Student updateStudent(Student newEntity) {
        Student oldStudent = studentService.findById(newEntity.getId());
        Student res = studentService.update(newEntity);
        if(res == null) {
            notifyObserversStudent(new StudentChangeEvent(ChangeEventType.UPDATE, newEntity, oldStudent));
        }
        return res;
    }




    public Tema findByIdTema(String s) { return temaService.findById(s); }
    public Iterable<Tema> getAllTema() {
        return temaService.getAll();
    }
    public Tema addTema(Tema entity) throws ValidationException {
        Tema r = temaService.add(entity);
        if(r == null) {
            notifyObserversTask(new TaskChangeEvent(ChangeEventType.ADD, entity));
        }
        return r;
    }
    public Tema removeByIdTema(String s) {
        Tema r = temaService.removeById(s);
        if(r != null) {
            notifyObserversTask(new TaskChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }
    public Tema updateTema(Tema newEntity) {
        Tema oldTask = temaService.findById(newEntity.getId());
        Tema res = temaService.update(newEntity);
        if(res == null) {
            notifyObserversTask(new TaskChangeEvent(ChangeEventType.UPDATE, newEntity, oldTask));
        }
        return res;
    }



    //TODO: pentru nota service , voi lua toate informatiile despre studenti si teme, pentru a gasi datele fara sa avem un service in care sa grupam totul
    public Nota findByIdNota(String s) { return notaService.findById(s); }
    public Iterable<Nota> getAllNota() { return notaService.getAll(); }
    public Nota addNota(Nota entity) throws ValidationException {
        Nota r = notaService.add(entity);
        if(r == null) {
            notifyObserversGrade(new GradeChangeEvent(ChangeEventType.ADD, entity));
        }
        return r;
    }
    public Nota removeByIdNota(String s) {
        //return notaService.removeById(s);
        Nota r = notaService.removeById(s);
        if(r != null) {
            notifyObserversGrade(new GradeChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }
    public Nota updateNota(Nota newEntity) {
        //return notaService.update(entity);
        Nota oldGrade = notaService.findById(newEntity.getId());
        Nota res = notaService.update(newEntity);
        if(res == null) {
            notifyObserversGrade(new GradeChangeEvent(ChangeEventType.UPDATE, newEntity, oldGrade));
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


    @Override
    public void addObserverGrade(GradeObserver e) {
        gradeObservers.add(e);
    }

    @Override
    public void removeObserverGrade(GradeObserver e) {
        gradeObservers.remove(e);
    }

    @Override
    public void notifyObserversGrade(GradeChangeEvent t) {
        gradeObservers.forEach(x->x.updateGrade(t));
    }

    @Override
    public void addObserverStudent(StudentObserver e) {
        studentObservers.add(e);
    }

    @Override
    public void removeObserverStudent(StudentObserver e) {
        studentObservers.remove(e);
    }

    @Override
    public void notifyObserversStudent(StudentChangeEvent t) {
        studentObservers.forEach(x->x.updateStudent(t));
    }

    @Override
    public void addObserverTask(TaskObserver e) {
        taskObservers.add(e);
    }

    @Override
    public void removeObserverTask(TaskObserver e) {
        taskObservers.remove(e);
    }

    @Override
    public void notifyObserversTask(TaskChangeEvent t) {
        taskObservers.forEach(x->x.updateTask(t));
    }
}

