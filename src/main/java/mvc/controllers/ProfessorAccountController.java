package mvc.controllers;

import domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.StudentAlert;
import services.MasterService;
import utils.Constants;
import utils.events.GradeChangeEvent;
import utils.observer.GradeObserver;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProfessorAccountController implements GradeObserver<GradeChangeEvent> {


    public TableView<Student> tableStudent;
    public TableColumn<Student, String> columnStudentNume;
    public TableColumn<Student, String> columnStudentPrenume;
    public TableColumn<Student, String> columnStudentGrupa;
    public TableColumn<Student, String> columnStudentEmail;
    public TextField textStudentNume;
    public TextField textStudentPrenume;
    public TextField textStudentGrupa;
    public TextField textStudentEmail;


    public TableView<Tema> tableTema;
    public TableColumn<Tema, String> tableTemaNume;
    public TableColumn<Tema, String> tableTemaDescriere;
    public TableColumn<Tema, String> tableTemaInceput;
    public TableColumn<Tema, String> tableTemaSfarsit;
    public TextField dateTemaSfarsit;
    public TextField dateTemaInceput;
    public TextField textTemaNume;
    public TextField textTemaDescriere;

    public TableView<NotaDTO> tableNota;
    public TableColumn<NotaDTO, String> columnNotaId;
    public TableColumn<NotaDTO, String> columnNotaStudent;
    public TableColumn<NotaDTO, String> columnNotaTema;
    public TableColumn<NotaDTO, String> columnNotaData;
    public TableColumn<NotaDTO, String> columnNotaProfesor;
    public TableColumn<NotaDTO, String> columnNotaValoare;
    public TableColumn<NotaDTO, String> columnNotaFeedback;
    public TextField textNotaId;
    public TextField textNotaStudent;
    public TextField textNotaTema;
    public TextField textNotaFeedback;
    public TextField textNotaValoare;
    public TextField textNotaProf;
    public DatePicker dateNotaData;

    public PasswordField textOldPassword;
    public PasswordField textNewPassword;

    public Label labelProfesor;

    public CheckBox checkboxInvert;


    private MasterService service;
    private ObservableList<Student> modelS = FXCollections.observableArrayList();
    private ObservableList<Tema> modelT = FXCollections.observableArrayList();
    private ObservableList<NotaDTO> modelN = FXCollections.observableArrayList();
    private Stage dialogStage;
    private Profesor loggedInProfessor;


    public void setService(MasterService masterService, Stage stage, Profesor loggedInProfessor) {
        this.dialogStage = stage;
        this.loggedInProfessor = loggedInProfessor;
        service = masterService;
        service.addObserverGrade(this);
        //extend to all observer types in the future...
        initModelStudent();
        initModelTema();
        initModelGrade();
        this.labelProfesor.setText(this.loggedInProfessor.toString());
        this.textNotaProf.setText(this.loggedInProfessor.toString());
        this.textNotaTema.setText(getTemaCurenta(this.dateNotaData.getValue()));
    }


    private void initModelStudent() {
        Iterable<Student> students = service.getAllStudent();
        List<Student> studentList = StreamSupport
                .stream(students.spliterator(), false)
                .filter(st -> st.getCadruDidacticIndrumatorLab().equals(this.loggedInProfessor.getNume() + " " + this.loggedInProfessor.getPrenume()))
                .collect(Collectors.toList());
        modelS.setAll(studentList);
    }

    private void initModelTema() {
        Iterable<Tema> tasks = service.getAllTema();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());
        modelT.setAll(taskList);

    }

    private void initModelGrade() {
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(gr -> gr.getProfesor().equals(this.loggedInProfessor.getNume() + " " + this.loggedInProfessor.getPrenume()))
                .collect(Collectors.toList());
        List<NotaDTO> gradeDTOList = convertGradeToDTO(gradeList);
        modelN.setAll(gradeDTOList);
    }






    private int getEffectiveRangeOfDelayMotvation(Student s, Tema t, String date) {//TODO: big workload here
        Iterable<Motivation> motivations = service.getAllMotivation();
        List<Motivation> motivationList = StreamSupport.stream(motivations.spliterator(), false).collect(Collectors.toList());
        List<Interval> intervals = motivationList.stream().filter(mot -> mot.getIdStudent().equals(s.getId())).map(x -> new Interval(x.getStart(),x.getEnd())).collect(Collectors.toList());

        LocalDate end = LocalDate.parse(t.getDeadlineWeek(), Constants.DATE_TIME_FORMATTER);
        LocalDate now = LocalDate.parse(date, Constants.DATE_TIME_FORMATTER);

        if(!Constants.compareDates(now, end)){
            return 0;
        }//task is not overdue => no need for motivation

        int sumMotivated = 0;
        for (Interval interval : intervals) {//task is overdue - so it needs motivation
            //if period between task deadline and now has any common dates with a motivation interval (so that we can motivate the delay)
            if(Constants.intervalsHaveDatesInCommon(interval.getStart(), interval.getEnd(), end, now)){
                sumMotivated += Constants.getIntervalsNumberOfCommonWeeks(interval.getStart(), interval.getEnd(), end, now);
            }
        }
        return sumMotivated;
    }

    private int computeEffectiveDelay(Tema t, String date) {
        LocalDate start = LocalDate.parse(t.getStartWeek(), Constants.DATE_TIME_FORMATTER);
        LocalDate end = LocalDate.parse(t.getDeadlineWeek(), Constants.DATE_TIME_FORMATTER);
        LocalDate now = LocalDate.parse(date, Constants.DATE_TIME_FORMATTER);

        if (Constants.intervalsHaveDatesInCommon(start, end, now, now)) {//task was submitted in time
            return 0;
        }

        int ws = Constants.getWeek(start);
        int we = Constants.getWeek(end);
        int wn = Constants.getWeek(now);

        if (start.getYear() == end.getYear() && end.getYear() == now.getYear()) {// S:2019 E:2019 N:2019 or S:2020 E:2020 N:2020 (trivial case)
            if( we > wn){//last week of the year is tricky
                wn += 52;
            }
            return wn - we;
        }
        if (start.getYear() == end.getYear() && end.getYear() < now.getYear()) {// S:2019 E:2019 N:2020 (delayed over new year)
            wn += 52;
        }
        if (start.getYear() == end.getYear() && end.getYear() > now.getYear()) {// S:2020 E:2020 N:2019 (predata in avans)
            ws += 52;
            we += 52;
        }
        if (start.getYear() == now.getYear() && now.getYear() < end.getYear()) {// S:2019 E:2020 N:2019 (predata in avans)
            we += 52;
        }
        if (start.getYear() < end.getYear() && end.getYear() == now.getYear()) {// S: 2019 E:2020 N:2020 (work over the new year holiday)
            we += 52;
            wn += 52;
        }

        return wn - we;
    }


    private int computeEffectiveGradeConsideringAllCases(Student s, Tema t, String date){
        int motivated = getEffectiveRangeOfDelayMotvation(s,t,date);
        int delay = computeEffectiveDelay(t,date);
        delay -= motivated;
        if(delay > 2){
            return 0;
        }
        return Math.min(10,10 - delay);
    }

    private int computeMaxGrade(Student s, Tema t, String date){//TODO: handle date shift to semester (from holiday)
        int grade = computeEffectiveGradeConsideringAllCases(s, t, date);
        return grade;
    }

    private String computeFeedback(Student s, Tema t, String date) {//TODO: handle date shift to semester (from holiday)
        int grade = computeEffectiveGradeConsideringAllCases(s, t, date);
        if(grade <= 7){
            return "TEMA NU MAI POATE FI PREDATA";
        }
        if(grade == 10){
            return "NICIO OBSERVATIE";
        }
        return "NOTA A FOST DIMINUATA CU " + Integer.toString(10 - grade) + " PUNCTE DATORITA INTARZIERILOR";
    }

    private void initModelGradeINVERTED(){
        filterMergeSearchNota();
    }

    private String getTemaCurenta(LocalDate date){
        String now = date.format(Constants.DATE_TIME_FORMATTER);
        List<Tema> temeCurente = StreamSupport
                .stream(this.service.getAllTema().spliterator(),false)
                .filter(t -> Constants.compareDates(t.getDeadlineWeek(),now) && Constants.compareDates(now, t.getStartWeek()))
                .collect(Collectors.toList());
        if(temeCurente.size() != 0)
            return temeCurente.get(0).toString();
        else{
            return "-";
        }
    }

    private void recomputeGrades(){
        if(this.tableNota.getSelectionModel().getSelectedItem() == null){
            this.textNotaTema.setText(getTemaCurenta(this.dateNotaData.getValue()));
        }
        if(this.checkboxInvert.isSelected()){
            initModelGradeINVERTED();
        }
    }



    private void filterMergeSearchStudent(){
        Predicate<Student> filtered = null;//TODO: asa compunem predicatele

        Iterable<Student> students = service.getAllStudent();
        List<Student> studentList = StreamSupport.stream(students.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Student> filterNume = x -> x.getNume().toLowerCase().contains(textStudentNume.getText().toLowerCase());
        Predicate<Student> filterPrenume = x -> x.getPrenume().toLowerCase().contains(textStudentPrenume.getText().toLowerCase());
        Predicate<Student> filterGrupa = x -> Integer.toString(x.getGrupa()).toLowerCase().contains(textStudentGrupa.getText().toLowerCase());
        Predicate<Student> filterEmail = x -> x.getEmail().toLowerCase().contains(textStudentEmail.getText().toLowerCase());

        if (!textStudentNume.getText().isEmpty()){//TODO: CHANGE ALL PREDICATES WITH {IF TEXTFIELD IS EMPTY => RETURN TRUE; ELSE RETURN THE FILTER}
            if(filtered == null){//TODO: HERE, WE DO NOT TEST IF FILTERED = NULL; WE INITIALIZE FILTERED VARIABLE AS UNION AND(ALL PREDICATES)
                filtered = filterNume;
            }
            else{
                filtered = filtered.and(filterNume);
            }
        }
        if (!textStudentPrenume.getText().isEmpty()){
            if(filtered == null){
                filtered = filterPrenume;
            }
            else{
                filtered = filtered.and(filterPrenume);
            }
        }
        if (!textStudentGrupa.getText().isEmpty()){
            if(filtered == null){
                filtered = filterGrupa;
            }
            else{
                filtered = filtered.and(filterGrupa);
            }
        }
        if (!textStudentEmail.getText().isEmpty()){
            if(filtered == null){
                filtered = filterEmail;
            }
            else{
                filtered = filtered.and(filterEmail);
            }
        }

        if( filtered == null){
            modelS.setAll(studentList);
        }
        else{
            modelS.setAll(studentList.stream().filter(filtered).collect(Collectors.toList()));
        }
    }


    private void filterMergeSearchTema(){
        Predicate<Tema> filtered = null;

        Iterable<Tema> tasks = service.getAllTema();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Tema> filterNume = x -> x.getNume().toLowerCase().contains(textTemaNume.getText().toLowerCase());
        Predicate<Tema> filterDescriere = x -> x.getDescriere().toLowerCase().contains(textTemaDescriere.getText().toLowerCase());
        Predicate<Tema> filterStart = x -> x.getStartWeek().contains(dateTemaInceput.getText());
        Predicate<Tema> filterStop = x -> x.getDeadlineWeek().contains(dateTemaSfarsit.getText());

        if (!textTemaNume.getText().isEmpty()){
            if(filtered == null){
                filtered = filterNume;
            }
            else{
                filtered = filtered.and(filterNume);
            }
        }
        if (!textTemaDescriere.getText().isEmpty()){
            if(filtered == null){
                filtered = filterDescriere;
            }
            else{
                filtered = filtered.and(filterDescriere);
            }
        }
        if (!(dateTemaInceput.getText() == null)){
            if(filtered == null){
                filtered = filterStart;
            }
            else{
                filtered = filtered.and(filterStart);
            }
        }
        if (!(dateTemaSfarsit.getText() == null)){
            if(filtered == null){
                filtered = filterStop;
            }
            else{
                filtered = filtered.and(filterStop);
            }
        }

        if( filtered == null){
            modelT.setAll(taskList);
        }
        else{
            modelT.setAll(taskList.stream().filter(filtered).collect(Collectors.toList()));
        }
    }



    @FXML
    public void initialize() {
        this.dateNotaData.setValue(LocalDate.now());

        columnStudentNume.setCellValueFactory(new PropertyValueFactory<Student, String>("nume"));
        columnStudentPrenume.setCellValueFactory(new PropertyValueFactory<Student, String>("prenume"));
        columnStudentGrupa.setCellValueFactory(new PropertyValueFactory<Student, String>("grupa"));
        columnStudentEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("email"));
        tableStudent.setItems(modelS);

        textStudentPrenume.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textStudentNume.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textStudentGrupa.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textStudentEmail.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));



        //TODO: one day I should refactor "tableTema" into "columnTema"...
        tableTemaNume.setCellValueFactory(new PropertyValueFactory<Tema, String>("nume"));
        tableTemaDescriere.setCellValueFactory(new PropertyValueFactory<Tema, String>("descriere"));
        tableTemaInceput.setCellValueFactory(new PropertyValueFactory<Tema, String>("startWeek"));
        tableTemaSfarsit.setCellValueFactory(new PropertyValueFactory<Tema, String>("deadlineWeek"));
        tableTema.setItems(modelT);

        textTemaNume.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));
        textTemaDescriere.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));

        dateTemaInceput.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));
        dateTemaSfarsit.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));


        columnNotaId.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("idNota"));
        columnNotaData.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("dataNota"));
        columnNotaProfesor.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("profesor"));
        columnNotaValoare.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("valoare"));
        columnNotaStudent.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("studentString"));
        columnNotaTema.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("temaString"));
        columnNotaFeedback.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("feedback"));
        tableNota.setItems(modelN);


        dateNotaData.valueProperty().addListener(((observableValue, s, t1) -> {
            recomputeGrades();
        }));



        textNotaProf.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchNota();
        }));
        textNotaValoare.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchNota();
        }));

        textNotaStudent.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchNota();
        }));
        textNotaTema.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchNota();
        }));


    }


    private void filterMergeSearchNota() {
        List<NotaDTO> gradeDTOList = null;

        if(this.checkboxInvert.isSelected()){
            List<Nota> grades = StreamSupport.stream(service.getAllNota().spliterator(), false).collect(Collectors.toList());;
            List<Student> studenti = StreamSupport.stream(service.getAllStudent().spliterator(), false).collect(Collectors.toList());
            List<Tema> teme = StreamSupport.stream(service.getAllTema().spliterator(), false).collect(Collectors.toList());
            gradeDTOList = new ArrayList<>();
            for (Student student : studenti) {
                if(student.getCadruDidacticIndrumatorLab().equals(this.loggedInProfessor.toString()))
                    for (Tema tema : teme) {
                        String nid = student.getId()+":"+tema.getId();
                        if(grades.stream().noneMatch(gr -> gr.getId().equals(nid))){
                            int valoare = computeMaxGrade(student, tema, this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER));
                            String feedback = computeFeedback(student, tema, this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER));
                            Nota nota = new Nota(student.getId()+":"+tema.getId(), valoare,this.loggedInProfessor.getNume()+" "+this.loggedInProfessor.getPrenume(), this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER),feedback);
                            gradeDTOList.add(new NotaDTO(nota,tema,student));
                        }//else we simply don't add
                    }
            }
        }
        else{
            Iterable<Nota> grades = service.getAllNota();
            List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                    .collect(Collectors.toList());
            gradeDTOList = convertGradeToDTO(gradeList);
        }

        Predicate<NotaDTO> filtered = null;// asa compunem predicatele
        /*Predicate<NotaDTO> filterData = x -> {
            String text = dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER);
            if (text.isEmpty()) {
                return true;
            }
            return x.getDataNota().toLowerCase().contains(text.toLowerCase());
        };*/
        Predicate<NotaDTO> filterProfesor = x -> {
            String text = textNotaProf.getText();
            if (text.isEmpty()) {
                return true;
            }
            return x.getProfesor().toLowerCase().contains(text.toLowerCase());
        };
        Predicate<NotaDTO> filterValoare = x -> {
            String text = textNotaValoare.getText();
            if (text.isEmpty()) {
                return true;
            }
            return Integer.toString(x.getValoare()).toLowerCase().contains(text.toLowerCase());
        };


        Predicate<NotaDTO> filterStudent = x -> {
            String text = textNotaStudent.getText();
            if (text.isEmpty()) {
                return true;
            }
            String studentStr = x.getS().getNume()
                    + " " + x.getS().getPrenume()
                    + " - " + x.getS().getGrupa()
                    + " - " + x.getS().getEmail();
            return studentStr.toLowerCase().contains(text.toLowerCase());
        };
        Predicate<NotaDTO> filterTask = x -> {
            String text = textNotaTema.getText();
            if (text.isEmpty()) {
                return true;
            }
            String taskStr = x.getT().getNume()
                    + " - " + x.getT().getDescriere()
                    + " " + x.getT().getStartWeek()
                    + " " + x.getT().getDeadlineWeek();
            return taskStr.toLowerCase().contains(text.toLowerCase());
        };

        filtered = filterProfesor.and(filterProfesor).and(filterValoare).and(filterStudent).and(filterTask);

        modelN.setAll(gradeDTOList.stream().filter(filtered).collect(Collectors.toList()));
    }



    private List<NotaDTO> convertGradeToDTO(List<Nota> gradeList) {
        return gradeList.stream()
                .map(n -> {
                    String[] parts = n.getId().split(":");
                    Student s = service.findByIdStudent(parts[0]);
                    Tema t = service.findByIdTema(parts[1]);
                    return new NotaDTO(n, t, s);
                })
                .collect(Collectors.toList());
    }


    @Override
    public void updateGrade(GradeChangeEvent gradeChangeEvent) {
        initModelGrade();
    }


    public void handleBackToLoginChoice(ActionEvent actionEvent) {

        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login/LoginChoice.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Log In");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LoginChoiceController loginChoiceController = loader.getController();
            loginChoiceController.setService(service, dialogStage);

            this.dialogStage.close();
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleResetPassword(ActionEvent actionEvent) {

        String oldPsswd = this.textOldPassword.getText();
        String newPsswd = this.textNewPassword.getText();
        String email = this.loggedInProfessor.getEmail();//USERNAME IS THE EMAIL!!!*/
        boolean state = this.service.changeProfessorPassword(email,oldPsswd,newPsswd);
        if(state){
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION,"Resetare","parola a fost actualizata!");
        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.ERROR,"Log In","parola nu a putut fi actualizata!");
        }
    }

    public void handleClearFieldsStudent(ActionEvent actionEvent) {
        this.textStudentEmail.setText("");
        this.textStudentGrupa.setText("");
        this.textStudentNume.setText("");
        this.textStudentPrenume.setText("");
    }

    // gogo@cs.ubbcluj.ro
    public void handleAddNota(ActionEvent actionEvent) {
        if(checkboxInvert.isSelected()){
            if ( !this.textNotaValoare.getText().isEmpty() && !this.textNotaProf.getText().isEmpty() && !this.textNotaFeedback.getText().isEmpty()){
                Nota toBeAdded = new Nota(this.textNotaId.getText(), Integer.parseInt(this.textNotaValoare.getText()), this.textNotaProf.getText(), this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER), this.textNotaFeedback.getText());
                if(this.textNotaId.getText().equals("")){
                    StudentAlert.showMessage(null, Alert.AlertType.ERROR,"Eroare","nu s-a putut crea o nota!");
                }
                else {
                    Student s = this.service.findByIdStudent(toBeAdded.getId().split(":")[0]);
                    Tema t = this.service.findByIdTema(toBeAdded.getId().split(":")[1]);
                    int max = this.computeMaxGrade(s, t, this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER));
                    if (Integer.parseInt(this.textNotaValoare.getText()) > max) {
                        StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Nota nu poate depasi " + max + " !");
                    } else if (!Constants.compareDates(LocalDate.now(), this.dateNotaData.getValue())) {
                        StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Data nu poate fi din viitor!");
                    } else {
                        Nota rez = this.service.addNota(toBeAdded);
                        if (rez == null) {
                            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "nota a fost adaugata!");
                            handleclearFieldsNota();
                        } else {
                            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "nota nu s-a putut adauga!");
                        }
                    }
                }
            }
            else{
                StudentAlert.showMessage(null, Alert.AlertType.ERROR,"Eroare","selectati un rand din tabela!");
            }
        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.WARNING,"Avertisment","pentru a intra in modul de adaugare, bifati casuta!");
        }

    }

    public void handleDeleteNota(ActionEvent actionEvent) {
        if(!checkboxInvert.isSelected()){
            Nota n = this.service.findByIdNota(this.textNotaId.getText());
            if(n == null){
                StudentAlert.showMessage(null, Alert.AlertType.ERROR,"Eroare","selectati o nota din tabel!");
            }
            else{
                this.service.removeByIdNota(n.getId());
                StudentAlert.showMessage(null, Alert.AlertType.INFORMATION,"Succes","nota a fost stearsa cu succes!");
                handleclearFieldsNota();
            }
        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.WARNING,"Avertisment","pentru a intra in modul de stergere, debifati casuta!");
        }
    }

    public void handleUpdateNota(ActionEvent actionEvent) {
        if (!checkboxInvert.isSelected()) {
            Nota n = this.service.findByIdNota(textNotaId.getText());
            if (n == null) {
                StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "nu s-a putut gasit nota!");
            } else {
                Student s = this.service.findByIdStudent(n.getId().split(":")[0]);//TODO RECOMPUTE GRADE CONSIDERING MOTIVATIONS
                Tema t = this.service.findByIdTema(n.getId().split(":")[1]);
                int max = this.computeMaxGrade(s, t, this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER));

                if (Integer.parseInt(this.textNotaValoare.getText()) > max) {
                    StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Nota nu poate depasi " + max + " !");
                } else if (Constants.compareDates(this.dateNotaData.getValue(), LocalDate.now())) {
                    StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Data nu poate fi din viitor!");
                } else {
                    Nota replace = new Nota(n.getId(), Integer.parseInt(this.textNotaValoare.getText()), n.getProfesor(), this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER), this.textNotaFeedback.getText());
                    Nota rez = this.service.updateNota(replace);
                    if (rez == null) {
                        StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "nota a fost modificata!");
                        handleclearFieldsNota();
                    } else {
                        StudentAlert.showMessage(null, Alert.AlertType.WARNING, "Avertisment", "nota nu a putut fi modificata!");
                    }
                }
            }

        } else {
            StudentAlert.showMessage(null, Alert.AlertType.WARNING, "Avertisment", "pentru a intra in modul de modificare, debifati casuta!");
        }
    }


    public void handleClearFieldsTema(ActionEvent actionEvent) {
        this.textTemaDescriere.setText("");
        this.dateTemaInceput.setText("");
        this.textTemaNume.setText("");
        this.dateTemaSfarsit.setText("");
    }

    private void handleclearFieldsNota(){
        this.textNotaId.setText("");
        this.textNotaTema.setText("");
        this.textNotaStudent.setText("");
        this.textNotaValoare.setText("");
        this.textNotaProf.setText(this.loggedInProfessor.toString());
        this.textNotaFeedback.setText("");
        this.dateNotaData.setValue(LocalDate.now());
    }

    public void handleToggleINVERT(ActionEvent actionEvent) {
        initModelGradeINVERTED();
        this.textNotaId.setText("");
        this.textNotaTema.setText(getTemaCurenta(this.dateNotaData.getValue()));
        this.textNotaStudent.setText("");
        this.textNotaValoare.setText("");
        this.textNotaProf.setText(this.loggedInProfessor.toString());
        this.textNotaFeedback.setText("");
        this.dateNotaData.setValue(LocalDate.now());
    }


    public void handleSelectionChanged(MouseEvent mouseEvent) {
        NotaDTO nota = this.tableNota.getSelectionModel().getSelectedItem();
        this.textNotaId.setText(nota.getIdNota());
        this.textNotaStudent.setText(nota.getS().toString());
        this.textNotaProf.setText(nota.getProfesor());
        this.textNotaValoare.setText(Integer.toString(nota.getValoare()));
        this.dateNotaData.setValue(LocalDate.parse(nota.getDataNota(), Constants.DATE_TIME_FORMATTER));
        this.textNotaFeedback.setText(nota.getFeedback());
        this.textNotaTema.setText(nota.getT().toString());
    }

    public void handleClearFieldsNota(ActionEvent actionEvent) {
        handleclearFieldsNota();
    }
}
