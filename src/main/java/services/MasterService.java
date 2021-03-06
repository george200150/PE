package services;

import domain.*;
import utils.Constants;
import utils.events.*;
import utils.observer.*;
import validators.ValidationException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//public class MasterService implements Observable<GradeChangeEvent> {
public class MasterService implements ObservableGrade, ObservableTask, ObservableStudent, ObservableProfesor {
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
    private List<ProfesorObserver> profObservers = new ArrayList<>();

    public MasterService(ProfesorService profesorService, StudentService studentService, TemaService temaService, NotaService notaService, MotivationService motivationService) {
        this.profesorService = profesorService;
        this.studentService = studentService;
        this.temaService = temaService;
        this.notaService = notaService;
        this.motivationService = motivationService;
        this.loginCredentialsProxy = new LoginCredentialsProxy(profesorService, studentService);

    }

    public MotivationService getMotivationService(){
        return this.motivationService;
    }

    public void deleteAllGradesOfStudent(Student student){
        List<Nota> rez = StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getId().split(":")[0].equals(student.getId()))
                .collect(Collectors.toList());
        rez.stream()
                .forEach(x -> this.removeByIdNota(x.getId()));
    }

    public void deleteAllGradesOfTema(Tema tema){
        List<Nota> tbd = StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getId().split(":")[1].equals(tema.getId()))
                .collect(Collectors.toList());
        //        .forEach(x -> this.removeByIdNota(x.getId())); - !!!!: won't work due to modifications on the fly
        for (Nota n : tbd) {
            this.removeByIdNota(n.getId());
        }
    }

    public void deleteAllStudentsANDGradesOfProfesor(Profesor profesor){
        List<Student> rez = StreamSupport
                .stream(this.getAllStudent().spliterator(), false)
                .filter(x -> x.getCadruDidacticIndrumatorLab().equals(profesor.toString()))
                .collect(Collectors.toList());
        //!!!!! if we delete while loading from file => concurrent modification exception !!!!!!
        rez.stream()
                .forEach(x -> this.removeByIdStudent(x.getId()));

        deleteAllGradesOfProfesor(profesor);
    }

    private void deleteAllGradesOfProfesor(Profesor profesor){
        List<Nota> rez = StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))
                .collect(Collectors.toList());
        rez.stream()
                .forEach(x -> this.removeByIdNota(x.getId()));
    }


    public void updateAllGradesOfStudent(Student student){
        //nu exista update la grade (nota are numai id-ul) - este complicat de zic ca ar avea sens sa schimbi profesorul care a dat nota pe tema... e teoretic imposibil sa se schimbe proful...
        List<Nota> rez = StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getId().split(":")[0].equals(student.getId()))
                .collect(Collectors.toList());
        rez.stream()
                .forEach(x -> this.updateNota(new Nota(student.getId()+":"+x.getId().split(":")[1],x.getValoare(),student.getCadruDidacticIndrumatorLab(),x.getData(),x.getFeedback())));
    }

    public void updateAllGradesOfTema(Tema tema){
        //teoretic, daca nu am schimba id-ul temei, nu ar avea sens update-ul...
        List<Nota> rez = StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getId().split(":")[1].equals(tema.getId()))
                .collect(Collectors.toList());
        rez.stream()
                .forEach(x -> this.updateNota(new Nota(x.getId().split(":")[0]+":"+tema.getId(),x.getValoare(),x.getProfesor(),x.getData(),x.getFeedback())));
    }

    public void updateAllStudentsOfProfesor(Profesor profesor){
        List<Student> rez = StreamSupport
                .stream(this.getAllStudent().spliterator(), false)
                .filter(x -> x.getCadruDidacticIndrumatorLab().equals(profesor.toString()))
                .collect(Collectors.toList());
        rez.stream()
                .forEach(x -> this.updateStudent(new Student(x.getId(),x.getNume(),x.getPrenume(),x.getGrupa(),x.getEmail(),profesor.toString())));

        updateAllGradesOfProfesor(profesor);
    }

    public void updateAllGradesOfProfesor(Profesor profesor){
        List<Nota> rez = StreamSupport
                .stream(this.getAllNota().spliterator(), false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))
                .collect(Collectors.toList());
        rez.stream()
                .forEach(x -> this.updateNota(new Nota(x.getId(),x.getValoare(),profesor.toString(),x.getData(),x.getFeedback())));
    }


    public Profesor findByIdProfesor(String s) {
        return profesorService.findById(s);
    }

    public Iterable<Profesor> getAllProfesor() {
        return profesorService.getAll();
    }

    public Profesor addProfesor(Profesor entity) throws ValidationException {
        if (StreamSupport.stream(this.getAllStudent().spliterator(),false).anyMatch(x -> x.getEmail().equals(entity.getEmail()))){//DO NOT REPEAT MAIL
            throw new ValidationException("MAIL DUPLICATE");
        }
        if (StreamSupport.stream(this.getAllProfesor().spliterator(),false).anyMatch(x -> x.getEmail().equals(entity.getEmail()))){//DO NOT REPEAT MAIL
            throw new ValidationException("MAIL DUPLICATE");
        }
        Profesor r = profesorService.add(entity);
        if(r == null) {
            notifyObserversProf(new ProfesorChangeEvent(ChangeEventType.ADD, entity));
        }
        return r;
    }

    public Profesor removeByIdProfesor(String s) {
        Profesor r = profesorService.removeById(s);
        if(r != null) {
            notifyObserversProf(new ProfesorChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }

    public Profesor updateProfesor(Profesor newEntity) {
        if (StreamSupport.stream(this.getAllStudent().spliterator(),false).anyMatch(x -> x.getEmail().equals(newEntity.getEmail()))){//DO NOT REPEAT MAIL
            throw new ValidationException("MAIL DUPLICATE");
        }
        List<Profesor> profesorList = StreamSupport.stream(this.getAllProfesor().spliterator(), false).collect(Collectors.toList());
        profesorList.remove(newEntity);//remember to exclude self.
        if (profesorList.stream().anyMatch(x -> x.getEmail().equals(newEntity.getEmail()))){//DO NOT REPEAT MAIL
            throw new ValidationException("MAIL DUPLICATE");
        }
        Profesor oldProf = profesorService.findById(newEntity.getId());
        Profesor res = profesorService.update(newEntity);
        if(res == null) {
            notifyObserversProf(new ProfesorChangeEvent(ChangeEventType.UPDATE, newEntity, oldProf));
        }
        return res;
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
        boolean val = this.loginCredentialsProxy.changeStudentPassword(user, oldp, newp);
        if(val){
            notifyObserversStudent(new StudentChangeEvent(ChangeEventType.ADD, null));
        }
        return val;
    }

    public boolean changeProfessorPassword(String user, String oldp, String newp){
        boolean val = this.loginCredentialsProxy.changeProfessorPassword(user, oldp, newp);
        if(val){
            notifyObserversProf(new ProfesorChangeEvent(ChangeEventType.ADD, null));
        }
        return val;
    }

    public boolean changeAdminPassword(String newp){
        return this.loginCredentialsProxy.changeAdminPassword(newp);
    }

    public void changeLinePSSWD(String oldLine, String newLine){
        this.loginCredentialsProxy.changeLinePSSWD(oldLine, newLine);
    }


    public void addStudentPSSWD(Student student, String psswd){
        this.loginCredentialsProxy.addStudentPSSWD(student, psswd);
        this.notifyObserversStudent(new StudentChangeEvent(ChangeEventType.ADD, null));
    }

    public void addProfesorPSSWD(Profesor profesor, String psswd){
        this.loginCredentialsProxy.addProfesorPSSWD(profesor, psswd);
        notifyObserversProf(new ProfesorChangeEvent(ChangeEventType.ADD, null));
    }

    public void updateStudentPSSWD(Student student, String psswd){
        this.loginCredentialsProxy.updateStudentPSSWD(student, psswd);
        notifyObserversStudent(new StudentChangeEvent(ChangeEventType.ADD, null));
    }

    public void updateProfesorPSSWD(Profesor profesor, String psswd){
        this.loginCredentialsProxy.updateProfesorPSSWD(profesor, psswd);
        notifyObserversProf(new ProfesorChangeEvent(ChangeEventType.ADD, null));
    }


    public void deleteStudentPSSWD(Student student){
        this.loginCredentialsProxy.deleteStudentPSSWD(student);
    }

    public void deleteProfesorPSSWD(Profesor profesor){
        this.loginCredentialsProxy.deleteProfesorPSSWD(profesor);
    }


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
        if (StreamSupport.stream(this.getAllStudent().spliterator(),false).anyMatch(x -> x.getEmail().equals(entity.getEmail()))){//DO NOT REPEAT MAIL
            throw new ValidationException("MAIL DUPLICATE");
        }
        if (StreamSupport.stream(this.getAllProfesor().spliterator(),false).anyMatch(x -> x.getEmail().equals(entity.getEmail()))){//DO NOT REPEAT MAIL
            throw new ValidationException("MAIL DUPLICATE");
        }
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
        List<Student> studentStream = StreamSupport.stream(this.getAllStudent().spliterator(), false).collect(Collectors.toList());
        studentStream.remove(newEntity);//remember to exclude self.
        if (studentStream.stream().anyMatch(x -> x.getEmail().equals(newEntity.getEmail()))){//DO NOT REPEAT MAIL
            throw new ValidationException("MAIL DUPLICATE");
        }
        if (StreamSupport.stream(this.getAllProfesor().spliterator(),false).anyMatch(x -> x.getEmail().equals(newEntity.getEmail()))){//DO NOT REPEAT MAIL
            throw new ValidationException("MAIL DUPLICATE");
        }
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
        Nota r = notaService.removeById(s);
        if(r != null) {
            notifyObserversGrade(new GradeChangeEvent(ChangeEventType.DELETE, r));
        }
        return r;
    }
    public Nota updateNota(Nota newEntity) {
        Nota oldGrade = notaService.findById(newEntity.getId());
        Nota res = notaService.update(newEntity);
        if(res == null) {
            notifyObserversGrade(new GradeChangeEvent(ChangeEventType.UPDATE, newEntity, oldGrade));
        }
        return res;
    }



    private NotaDTO getDTOFromNota(Nota n){
        Student s = findByIdStudent(n.getId().split(":")[0]);
        Tema t = findByIdTema(n.getId().split(":")[1]);
        return new NotaDTO(n,t,s);
    }

    public List<WeightedAVGDTO> raport1Student(Profesor profesor){
        Iterable<Nota> grades = getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))//filtered to have only logged in teacher's grades considered
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        Map<Student, List<NotaDTO>> dtoMap = dtoList.stream()
                .collect(Collectors.groupingBy(NotaDTO::getS));

        Map<Student, Double> mediileStudentilor = dtoMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, pair -> pair.getValue().stream().mapToDouble(this::compGrade).sum()));

        return mediileStudentilor.entrySet().stream()
                .map(x -> new WeightedAVGDTO(x.getKey(), x.getValue()))
                .collect(Collectors.toList());
    }

    //Nota la laborator pentru fiecare student (media ponderata a notelor de la
    //temele de laborator;
    // pondere tema = nr de saptamani alocate temei)
    public Double compGrade(NotaDTO sub){
        int value = sub.getValoare();
        int duration = sub.getT().getDuration() - 1;
        double val = (value * (double) (duration)) / 14;
        return val;
    }

    public List<Object> raport1(Profesor profesor){
        Iterable<Nota> grades = getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))//filtered to have only logged in teacher's grades considered
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        Map<Student, List<NotaDTO>> dtoMap = dtoList.stream()
                .collect(Collectors.groupingBy(NotaDTO::getS));

        //Map<Student, Double> mediileStudentilor = getMediiStudenti(profesor);
        Map<Student, Double> mediileStudentilor = dtoMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, pair -> pair.getValue().stream().mapToDouble(this::compGrade).sum()));


        return mediileStudentilor.entrySet().stream()
                .map(x -> new WeightedAVGDTO(x.getKey(), x.getValue()))
                .collect(Collectors.toList());
    }

    public List<Object> raport2(Profesor profesor){

        Iterable<Nota> grades = getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))//filtered by professor
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        Map<Tema, List<NotaDTO>> dtoMap = dtoList.stream()
                .collect(Collectors.groupingBy(NotaDTO::getT));

        Map<Tema, Double> raport = dtoMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue().stream().mapToDouble(NotaDTO::getValoare).sum()) / (double)(e.getValue().size())));
        //TODO posibil sa trebuiasca sa schimb "mapToDouble(NotaDTO::getValoare)" cu "mapToDouble(sub -> 'weighted avg')"...

        List<Map.Entry<Tema, Double>> lowestraport = raport.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .limit(1)
                .collect(Collectors.toList());

        Tema result = lowestraport.get(0).getKey();
        List<Object> resultList = new ArrayList<>();
        resultList.add(result);
        return resultList;
    }

    //Studentii care pot intra in examen (media mai mare sau egala cu 4)
    public List<Object> raport3(Profesor profesor){

        return StreamSupport
                .stream(this.getAllStudent().spliterator(),false)
                .filter(x -> x.getCadruDidacticIndrumatorLab().equals(profesor.toString()))//filtered by professor
                .filter(x -> this.getMedieStudent(x) >= 5)
                .collect(Collectors.toList());
    }

    private double getMedieStudent(Student student){
        List<Nota> noteStudent = StreamSupport
                .stream(this.getAllNota().spliterator(),false)
                .filter(x -> x.getId().split(":")[0].equals(student.getId()))//filtered by professor
                .collect(Collectors.toList());

        List<NotaDTO> dtos = noteStudent.stream().map(this::getDTOFromNota).collect(Collectors.toList());


        double media = dtos.stream().mapToDouble(this::compGrade).sum();
        return media;
    }

    public List<Object> raport4(Profesor profesor){
        Iterable<Nota> grades = getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))//filtered by professor
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        List<Object> allBad = dtoList.stream()
                .filter(x -> !Constants.compareDates(x.getT().getDeadlineWeek(), x.getDataNota()))
                .map(NotaDTO::getS)
                .distinct()
                .collect(Collectors.toList());

        List<Object> allAll = StreamSupport.stream(getAllStudent().spliterator(), false)
                .filter(x -> x.getCadruDidacticIndrumatorLab().equals(profesor.toString()))//filtered by professor
                .collect(Collectors.toList());
        allAll.removeAll(allBad);

        return allAll;
    }


    public Map<Student, Double> getMediiStudenti(Profesor profesor){
        Iterable<Nota> grades = getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))//filtered to have only logged in teacher's grades considered
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        Map<Student, List<NotaDTO>> dtoMap = dtoList.stream()
                .collect(Collectors.groupingBy(NotaDTO::getS));

        return dtoMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, pair -> pair.getValue().stream().mapToDouble(sub -> (sub.getValoare() * (double) (sub.getT().getDuration())) / 14).sum()));
    }

    public Map<Tema, Double> getTemeGrele(Profesor profesor) {
        Iterable<Nota> grades = getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))//filtered by professor
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        Map<Tema, List<NotaDTO>> dtoMap = dtoList.stream()
                .collect(Collectors.groupingBy(NotaDTO::getT));

        return dtoMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue().stream().mapToDouble(NotaDTO::getValoare).sum()) / (double) (e.getValue().size())));
    }

    public Map<String, Integer> getStudentiDupaStatusExamen(Profesor profesor){
        List<Student> l = StreamSupport
                .stream(this.getAllStudent().spliterator(), false)
                .filter(x -> x.getCadruDidacticIndrumatorLab().equals(profesor.toString()))//filtered by professor
                .collect(Collectors.toList());


        Map<String, Integer> freq = new HashMap<String, Integer>();

        l.forEach(x -> {
            if(this.getMedieStudent(x) >= 5)
                freq.merge("promovat", 1, Integer::sum);
            else
                freq.merge("corigent", 1, Integer::sum);
        });

        return freq;
    }

    public Map<String, Integer> getStudentiPrompti(Profesor profesor){


        Iterable<Nota> grades = getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(x -> x.getProfesor().equals(profesor.toString()))//filtered by professor
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        List<Student> allBad = dtoList.stream()//verificam ca macar un deadline sa nu fi fost respectat.
                .filter(x -> !Constants.compareDates(x.getT().getDeadlineWeek(), x.getDataNota()))
                .map(NotaDTO::getS)
                .distinct()
                .collect(Collectors.toList());

        List<Student> allGood = StreamSupport.stream(this.getAllStudent().spliterator(),false)
                .filter(x -> x.getCadruDidacticIndrumatorLab().equals(profesor.toString()))
                .collect(Collectors.toList());
        allGood.removeAll(allBad);

        Map<String, Integer> freq = new HashMap<String, Integer>();

        allBad.forEach(x -> freq.merge("intarziat", 1, Integer::sum));
        allGood.forEach(x -> freq.merge("la timp", 1, Integer::sum));

        return freq;
    }

    private boolean isPunctual(List<NotaDTO> list){//checks if all tasks of one student are submitted on time/
        return list.stream().noneMatch(x -> Constants.compareDates(x.getT().getDeadlineWeek(), x.getDataNota()));
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

    @Override
    public void addObserverProf(ProfesorObserver e) {
        profObservers.add(e);
    }

    @Override
    public void removeObserverProf(ProfesorObserver e) {
        profObservers.remove(e);
    }

    @Override
    public void notifyObserversProf(ProfesorChangeEvent t) {
        profObservers.forEach(x->x.updateProf(t));
    }
}

