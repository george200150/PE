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
import mvc.CustomAlert;
import mvc.controllers.confirmations.*;
import mvc.controllers.login.LoginChoiceController;
import services.MasterService;
import utils.Constants;
import utils.events.GradeChangeEvent;
import utils.events.ProfesorChangeEvent;
import utils.events.StudentChangeEvent;
import utils.events.TaskChangeEvent;
import utils.observer.GradeObserver;
import utils.observer.ProfesorObserver;
import utils.observer.StudentObserver;
import utils.observer.TaskObserver;
import validators.ValidationException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//public class AdminAccountController implements Observer<StudentChangeEvent>, Observer<TaskChangeEvent>, Observer<GradeChangeEvent> {
public class AdminAccountController implements GradeObserver, TaskObserver, StudentObserver, ProfesorObserver {


    public TableView<StudentDTO> tableStudenti;
    public TableColumn columnStudentId;
    public TableColumn columnStudentNume;
    public TableColumn columnStudentPrenume;
    public TableColumn columnStudentGrupa;
    public TableColumn columnStudentProf;
    public TableColumn columnStudentEmail;
    public TableColumn columnStudentParola;

    public TextField textIdStudent;
    public TextField textGrupaStudent;
    public TextField textPrenumeStudent;
    public TextField textNumeStudent;
    public TextField textEmailStudent;
    public TextField textProfStudent;
    public TextField textParolaStudent;


    public TableView<Tema> tableTeme;
    public TableColumn columnTemaId;
    public TableColumn columnTemaNume;
    public TableColumn columnTemaDescriere;
    public TableColumn columnTemaInceput;
    public TableColumn columnTemaSfarsit;
    public TextField textIdTema;
    public TextField textDescriereTema;
    public TextField textNumeTema;
    public DatePicker dateInceputTema;
    public DatePicker dateSfarsitTema;


    public TableView<NotaDTO> tableNote;
    public TableColumn<NotaDTO, String> columnNoteId;
    public TableColumn<NotaDTO, String> columnNoteTema;
    public TableColumn<NotaDTO, String> columnNoteData;
    public TableColumn<NotaDTO, String> columnNoteStudent;
    public TableColumn<NotaDTO, String> columnNoteValoare;
    public TableColumn<NotaDTO, String> columnNoteProf;
    public TableColumn<NotaDTO, String> columnNoteFeedback;


    public TableView<ProfesorDTO> tableProfesori;
    public TableColumn<ProfesorDTO, String> columnProfId;
    public TableColumn<ProfesorDTO, String> columnProfNume;
    public TableColumn<ProfesorDTO, String> columnProfPrenume;
    public TableColumn<ProfesorDTO, String> columnProfEmail;
    public TableColumn<ProfesorDTO, String> columnProfParola;
    public TextField textIdProf;
    public TextField textEmailProf;
    public TextField textPrenumeProf;
    public TextField textNumeProf;
    public TextField textParolaProf;


    public TextField textAdminParola;
    public TextField textFieldStudentId;
    public TextField textFieldStudentNume;
    public TextField textFieldStudentPrenume;
    public TextField textFieldStudentGrupa;
    public TextField textFieldStudentEmail;
    public TextField textFieldStudentProf;
    public TextField textFieldTemaId;
    public TextField textFieldTemaNume;
    public TextField textFieldTemaDescriere;
    public DatePicker datePickerTemaStart;
    public DatePicker datePickerTemaStop;
    public TextField textFieldNotaId;
    public DatePicker datePickerNotaData;
    public TextField textFieldNotaProf;
    public TextField textFieldNotaValoare;
    public TextField textFieldNotaFeedback;


    private MasterService service;
    private ObservableList<NotaDTO> modelN = FXCollections.observableArrayList();
    private ObservableList<StudentDTO> modelS = FXCollections.observableArrayList();
    private ObservableList<Tema> modelT = FXCollections.observableArrayList();
    private ObservableList<ProfesorDTO> modelP = FXCollections.observableArrayList();
    private Stage dialogStage;


    public void setService(MasterService masterService, Stage stage) {
        this.dialogStage = stage;
        service = masterService;
        service.addObserverGrade(this);
        service.addObserverTask(this);
        service.addObserverStudent(this);
        service.addObserverProf(this);
        initModelS();
        initModelT();
        initModelN();
        initModelP();
        textAdminParola.setText(this.service.getAdminPassword());
    }

    private List<StudentDTO> convertStudentToDTO(List<Student> list) {
        return list.stream().map(x -> {
            String password = this.service.getStudentPassword(x);
            return new StudentDTO(x, password);
        }).collect(Collectors.toList());
    }

    private List<ProfesorDTO> convertProfesorToDTO(List<Profesor> list) {
        return list.stream().map(x -> {
            String password = this.service.getProfesorPassword(x);
            return new ProfesorDTO(x, password);
        }).collect(Collectors.toList());
    }

    private void initModelS() {
        Iterable<Student> students = service.getAllStudent();
        List<Student> studentList = StreamSupport
                .stream(students.spliterator(), false)
                .collect(Collectors.toList());
        modelS.setAll(convertStudentToDTO(studentList));
    }

    private void initModelT() {
        Iterable<Tema> tasks = service.getAllTema();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());
        modelT.setAll(taskList);
    }

    private void initModelN() {
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());
        List<NotaDTO> gradeDTOList = convertGradeToDTO(gradeList);
        modelN.setAll(gradeDTOList);
    }

    private void initModelP() {
        Iterable<Profesor> professors = service.getAllProfesor();
        List<Profesor> professorList = StreamSupport.stream(professors.spliterator(), false)
                .collect(Collectors.toList());
        modelP.setAll(convertProfesorToDTO(professorList));
    }

    @FXML
    public void initialize() {
        columnStudentId.setCellValueFactory(new PropertyValueFactory<StudentDTO, String>("id"));
        columnStudentNume.setCellValueFactory(new PropertyValueFactory<StudentDTO, String>("nume"));
        columnStudentPrenume.setCellValueFactory(new PropertyValueFactory<StudentDTO, String>("prenume"));
        columnStudentGrupa.setCellValueFactory(new PropertyValueFactory<StudentDTO, String>("grupa"));
        columnStudentEmail.setCellValueFactory(new PropertyValueFactory<StudentDTO, String>("email"));
        columnStudentProf.setCellValueFactory(new PropertyValueFactory<StudentDTO, String>("cadruDidacticIndrumatorLab"));
        columnStudentParola.setCellValueFactory(new PropertyValueFactory<StudentDTO, String>("password"));
        tableStudenti.setItems(modelS);

        textPrenumeStudent.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textNumeStudent.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textGrupaStudent.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textEmailStudent.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textProfStudent.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));



        columnTemaId.setCellValueFactory(new PropertyValueFactory<Tema, String>("id"));
        columnTemaNume.setCellValueFactory(new PropertyValueFactory<Tema, String>("nume"));
        columnTemaDescriere.setCellValueFactory(new PropertyValueFactory<Tema, String>("descriere"));
        columnTemaInceput.setCellValueFactory(new PropertyValueFactory<Tema, String>("startWeek"));
        columnTemaSfarsit.setCellValueFactory(new PropertyValueFactory<Tema, String>("deadlineWeek"));
        tableTeme.setItems(modelT);

        textNumeTema.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));
        textDescriereTema.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));
        dateInceputTema.valueProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));
        dateSfarsitTema.valueProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));


        columnProfId.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("id"));
        columnProfEmail.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("email"));
        columnProfId.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("id"));
        columnProfNume.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("nume"));
        columnProfParola.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("password"));
        columnProfPrenume.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("prenume"));
        tableProfesori.setItems(modelP);

        textNumeProf.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchProf();
        }));
        textPrenumeProf.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchProf();
        }));
        textEmailProf.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchProf();
        }));


        columnNoteId.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("idNota"));
        columnNoteData.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("dataNota"));
        columnNoteProf.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("profesor"));
        columnNoteValoare.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("valoare"));
        columnNoteStudent.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("studentString"));
        columnNoteTema.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("temaString"));
        columnNoteFeedback.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("feedback"));
        tableNote.setItems(modelN);

    }


    private void filterMergeSearchStudent() {
        Predicate<Student> filtered = null;//asa compunem predicatele

        Iterable<Student> students = service.getAllStudent();
        List<Student> studentList = StreamSupport.stream(students.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Student> filterNume = x -> x.getNume().toLowerCase().contains(textNumeStudent.getText().toLowerCase());
        Predicate<Student> filterPrenume = x -> x.getPrenume().toLowerCase().contains(textPrenumeStudent.getText().toLowerCase());
        Predicate<Student> filterGrupa = x -> Integer.toString(x.getGrupa()).toLowerCase().contains(textGrupaStudent.getText().toLowerCase());
        Predicate<Student> filterEmail = x -> x.getEmail().toLowerCase().contains(textEmailStudent.getText().toLowerCase());
        Predicate<Student> filterProf = x -> x.getCadruDidacticIndrumatorLab().toLowerCase().contains(textProfStudent.getText().toLowerCase());

        if (!textNumeStudent.getText().isEmpty()) {
            filtered = filterNume;// WE INITIALIZE FILTERED VARIABLE AS UNION AND(ALL PREDICATES)
        }
        if (!textPrenumeStudent.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterPrenume;
            } else {
                filtered = filtered.and(filterPrenume);
            }
        }
        if (!textGrupaStudent.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterGrupa;
            } else {
                filtered = filtered.and(filterGrupa);
            }
        }
        if (!textEmailStudent.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterEmail;
            } else {
                filtered = filtered.and(filterEmail);
            }
        }
        if (!textProfStudent.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterProf;
            } else {
                filtered = filtered.and(filterProf);
            }
        }

        if (filtered == null) {
            modelS.setAll(convertStudentToDTO(studentList));
        } else {
            modelS.setAll(convertStudentToDTO(studentList.stream().filter(filtered).collect(Collectors.toList())));
        }
    }

    private void filterMergeSearchProf(){
        Predicate<ProfesorDTO> filtered = null;

        Iterable<Profesor> profs = service.getAllProfesor();
        List<Profesor> profList = StreamSupport.stream(profs.spliterator(), false)
                .collect(Collectors.toList());
        List<ProfesorDTO> profesori = this.convertProfesorToDTO(profList);

        Predicate<ProfesorDTO> filterNume = x -> x.getNume().toLowerCase().contains(textNumeProf.getText().toLowerCase());
        Predicate<ProfesorDTO> filterPrenume = x -> x.getPrenume().toLowerCase().contains(textPrenumeProf.getText().toLowerCase());
        Predicate<ProfesorDTO> filterEmail = x -> x.getEmail().toLowerCase().contains(textEmailProf.getText().toLowerCase());

        if (!textNumeProf.getText().isEmpty()) {
            filtered = filterNume;
        }
        if (!textPrenumeProf.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterPrenume;
            } else {
                filtered = filtered.and(filterPrenume);
            }
        }
        if (!textEmailProf.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterEmail;
            } else {
                filtered = filtered.and(filterEmail);
            }
        }

        if (filtered == null) {
            modelP.setAll(profesori);
        } else {
            modelP.setAll(profesori.stream().filter(filtered).collect(Collectors.toList()));
        }
    }

    private void filterMergeSearchTema() {
        Predicate<Tema> filtered = null;

        Iterable<Tema> tasks = service.getAllTema();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Tema> filterNume = x -> x.getNume().toLowerCase().contains(textNumeTema.getText().toLowerCase());
        Predicate<Tema> filterDescriere = x -> x.getDescriere().toLowerCase().contains(textDescriereTema.getText().toLowerCase());
        Predicate<Tema> filterStart = x -> x.getStartWeek().contains(dateInceputTema.getValue().format(Constants.DATE_TIME_FORMATTER));
        Predicate<Tema> filterStop = x -> x.getDeadlineWeek().contains(dateSfarsitTema.getValue().format(Constants.DATE_TIME_FORMATTER));

        if (!textNumeTema.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterNume;
            } else {
                filtered = filtered.and(filterNume);
            }
        }
        if (!textDescriereTema.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterDescriere;
            } else {
                filtered = filtered.and(filterDescriere);
            }
        }
        if (!(dateInceputTema.getValue() == null)){
            if(filtered == null){
                filtered = filterStart;
            }
            else{
                filtered = filtered.and(filterStart);
            }
        }
        if (!(dateSfarsitTema.getValue() == null)){
            if(filtered == null){
                filtered = filterStop;
            }
            else{
                filtered = filtered.and(filterStop);
            }
        }

        if (filtered == null) {
            modelT.setAll(taskList);
        } else {
            modelT.setAll(taskList.stream().filter(filtered).collect(Collectors.toList()));
        }
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


    public void handleBackToLogInChoice(ActionEvent actionEvent) {
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


    public void handleAddStudent(ActionEvent actionEvent) {
        String id = this.textIdStudent.getText();
        String nume = this.textNumeStudent.getText();
        String prenume = this.textPrenumeStudent.getText();
        int grupa = Integer.parseInt(this.textGrupaStudent.getText());
        String email = this.textEmailStudent.getText();
        String prof = this.textProfStudent.getText();
        Student student = new Student(id, nume, prenume, grupa, email, prof);
        try{
            Student rez = this.service.addStudent(student);
            if (rez == null) {
                this.service.addStudentPSSWD(student, this.textParolaStudent.getText());
                CustomAlert.showMessage(null, Alert.AlertType.INFORMATION, "adaugare", "studentul a fost adaugat cu succes!");
            } else {
                CustomAlert.showMessage(null, Alert.AlertType.ERROR, "adaugare", "studentul nu a putut fi adaugat!");
            }
        }
        catch (ValidationException | IllegalArgumentException ex){
            CustomAlert.showMessage(null, Alert.AlertType.ERROR, "adaugare", ex.getMessage());
        }

    }

    public void handleDeleteStudent(ActionEvent actionEvent) {
        //StudentDTO toBeDeleted = this.tableStudenti.getSelectionModel().getSelectedItem();
        Student toBeDeleted = this.service.findByIdStudent(this.textIdStudent.getText());
        if (StreamSupport.stream(this.service.getAllNota().spliterator(), false).anyMatch(x -> x.getId().split(":")[0].equals(toBeDeleted.getId()))) {
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/confirmations/DeleteStudentConfirmView.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Confirmare");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                DeleteStudentConfirmController deleteStudentConfirmController = loader.getController();
                deleteStudentConfirmController.setService(this, dialogStage, toBeDeleted);

                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            deleteStudentForReal(toBeDeleted);
        }
    }

    public void deleteStudentForReal(Student toBeDeleted) {
        Student rez = this.service.removeByIdStudent(toBeDeleted.getId());
        if (rez != null) {
            this.service.deleteAllGradesOfStudent(toBeDeleted);
            this.service.deleteStudentPSSWD(toBeDeleted);
            CustomAlert.showMessage(null, Alert.AlertType.INFORMATION, "stergere", "studentul a fost sters cu succes!");
        } else {
            CustomAlert.showMessage(null, Alert.AlertType.ERROR, "stergere", "studentul nu a putut fi sters!");
        }
    }


    public void handleUpdateStudent(ActionEvent actionEvent) {
        String id = this.textIdStudent.getText();
        String nume = this.textNumeStudent.getText();
        String prenume = this.textPrenumeStudent.getText();
        int grupa = Integer.parseInt(this.textGrupaStudent.getText());
        String email = this.textEmailStudent.getText();
        String prof = this.textProfStudent.getText();
        Student toBeUpdated = new Student(id, nume, prenume, grupa, email, prof);
        if (StreamSupport.stream(this.service.getAllNota().spliterator(), false).anyMatch(x -> x.getId().split(":")[0].equals(toBeUpdated.getId()))) {
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/confirmations/UpdateStudentConfirmView.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Confirmare");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                UpdateStudentConfirmController updateStudentConfirmController = loader.getController();
                updateStudentConfirmController.setService(this, dialogStage, toBeUpdated);

                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            updateStudentForReal(toBeUpdated);
        }
    }

    public void updateStudentForReal(Student toBeUpdated) {
        try {
            Student rez = this.service.updateStudent(toBeUpdated);
            if (rez != null) {
                this.service.updateAllGradesOfStudent(toBeUpdated);
                this.service.updateStudentPSSWD(toBeUpdated, this.textParolaStudent.getText());
                CustomAlert.showMessage(null, Alert.AlertType.INFORMATION, "modificare", "studentul a fost modificat cu succes!");
            } else {
                CustomAlert.showMessage(null, Alert.AlertType.ERROR, "modificare", "studentul nu a putut fi modificat!");
            }
        }
        catch (ValidationException | IllegalArgumentException ex){
            CustomAlert.showMessage(null, Alert.AlertType.ERROR, "modificare", ex.getMessage());
        }
    }

    public void handleClearFieldsStudent(ActionEvent actionEvent) {
        this.textIdStudent.setText("");
        this.textNumeStudent.setText("");
        this.textPrenumeStudent.setText("");
        this.textGrupaStudent.setText("");
        this.textEmailStudent.setText("");
        this.textProfStudent.setText("");
        this.textParolaStudent.setText("");
    }

    private boolean verifyTaskInterval(Tema tema, Tema except){//verify that the tasks are in a contiguous sequence along the semester (intervals for tasks not like: 1-5 2-3 3-4 1-6 2-4)
        List<Tema> teme = StreamSupport.stream(this.service.getAllTema().spliterator(), false).collect(Collectors.toList());
        if(except != null){//except that homework (works for update where there is already that interval occupied
            teme.remove(except);
        }
        LocalDate is2 = LocalDate.parse(tema.getStartWeek(), Constants.DATE_TIME_FORMATTER);
        LocalDate ie2 = LocalDate.parse(tema.getStartWeek(), Constants.DATE_TIME_FORMATTER);

        for (Tema t: teme) {
            LocalDate is1 = LocalDate.parse(t.getStartWeek(), Constants.DATE_TIME_FORMATTER);
            LocalDate ie1 = LocalDate.parse(t.getDeadlineWeek(), Constants.DATE_TIME_FORMATTER);
            if(Constants.getIntervalsNumberOfCommonWeeks(is1, ie1, is2, ie2) > 1){
                return false;
            }
        }
        return true;
    }

    public void handleAddTema(ActionEvent actionEvent) {
        String id = this.textIdTema.getText();
        String nume = this.textNumeTema.getText();
        String descriere = this.textDescriereTema.getText();
        String start = this.dateInceputTema.getValue().format(Constants.DATE_TIME_FORMATTER);
        String end = this.dateSfarsitTema.getValue().format(Constants.DATE_TIME_FORMATTER);
        Tema tema = new Tema(id, nume, descriere, start, end);
        if(verifyTaskInterval(tema, null)){
            try{
                Tema rez = this.service.addTema(tema);
                if (rez == null) {
                    CustomAlert.showMessage(null, Alert.AlertType.INFORMATION, "adaugare", "tema a fost adaugata cu succes!");
                } else {
                    CustomAlert.showMessage(null, Alert.AlertType.ERROR, "adaugare", "tema nu a putut fi adaugata!");
                }
            }
            catch (ValidationException | IllegalArgumentException ex){
                CustomAlert.showMessage(null, Alert.AlertType.ERROR, "adaugare", ex.getMessage());
            }
        }
        else{
            CustomAlert.showMessage(null, Alert.AlertType.ERROR, "adaugare", "tema introdusa se suprapune cu alta tema!");
        }

    }

    public void handleUpdateTema(ActionEvent actionEvent) {
        String id = this.textIdTema.getText();
        String nume = this.textNumeTema.getText();
        String descriere = this.textDescriereTema.getText();
        String start = this.dateInceputTema.getValue().format(Constants.DATE_TIME_FORMATTER);
        String end = this.dateSfarsitTema.getValue().format(Constants.DATE_TIME_FORMATTER);
        Tema toBeUpdated = new Tema(id, nume, descriere, start, end);
        if (verifyTaskInterval(toBeUpdated, this.service.findByIdTema(id))) {
            if (StreamSupport.stream(this.service.getAllNota().spliterator(), false).anyMatch(x -> x.getId().split(":")[1].equals(toBeUpdated.getId()))) {
                try {
                    // create a new stage for the popup dialog.
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/views/confirmations/UpdateTemaConfirmView.fxml"));

                    AnchorPane root = (AnchorPane) loader.load();

                    // Create the dialog Stage.
                    Stage dialogStage = new Stage();
                    dialogStage.setTitle("Confirmare");
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    Scene scene = new Scene(root);
                    dialogStage.setScene(scene);

                    UpdateTemaConfirmController updateTemaConfirmController = loader.getController();
                    updateTemaConfirmController.setService(this, dialogStage, toBeUpdated);

                    dialogStage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                updateTemaForReal(toBeUpdated);
            }
        } else {
            CustomAlert.showMessage(null, Alert.AlertType.ERROR, "modificare", "tema introdusa se suprapune cu o alta tema!");
        }
    }


    public void updateTemaForReal(Tema toBeUpdated) {
        try {
            Tema rez = this.service.updateTema(toBeUpdated);
            if (rez != null) {
                this.service.updateAllGradesOfTema(toBeUpdated);
                CustomAlert.showMessage(null, Alert.AlertType.INFORMATION, "modificare", "tema a fost modificata cu succes!");
            } else {
                CustomAlert.showMessage(null, Alert.AlertType.ERROR, "modificare", "tema nu a putut fi modificata!");
            }
        }
        catch (ValidationException | IllegalArgumentException ex){
            CustomAlert.showMessage(null, Alert.AlertType.ERROR, "modificare", ex.getMessage());
        }
    }


    public void handleDeleteTema(ActionEvent actionEvent) {
        //Tema toBeDeleted = this.tableTeme.getSelectionModel().getSelectedItem();
        Tema toBeDeleted = this.service.findByIdTema(this.textIdTema.getText());
        if (StreamSupport.stream(this.service.getAllNota().spliterator(), false).anyMatch(x -> x.getId().split(":")[1].equals(toBeDeleted.getId()))){
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/confirmations/DeleteTemaConfirmView.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Confirmare");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                DeleteTemaConfirmController deleteTemaConfirmController = loader.getController();
                deleteTemaConfirmController.setService(this, dialogStage, toBeDeleted);

                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            deleteTemaForReal(toBeDeleted);
        }
    }


    public void deleteTemaForReal(Tema toBeDeleted){
        Tema rez = this.service.removeByIdTema(toBeDeleted.getId());
        if(rez != null){
            this.service.deleteAllGradesOfTema(toBeDeleted);
            CustomAlert.showMessage(null, Alert.AlertType.INFORMATION,"stergere","tema a fost stearsa cu succes!");
        }
        else{
            CustomAlert.showMessage(null, Alert.AlertType.ERROR,"stergere","tema nu a putut fi stearsa!");
        }
    }


    public void handleClearFieldsTema(ActionEvent actionEvent) {
        this.textIdTema.setText("");
        this.textNumeTema.setText("");
        this.textDescriereTema.setText("");
        this.dateInceputTema.setValue(null);
        this.dateSfarsitTema.setValue(null);
    }



    public void handleAddProf(ActionEvent actionEvent) {
        String id = this.textIdProf.getText();
        String nume = this.textNumeProf.getText();
        String prenume = this.textPrenumeProf.getText();
        String email = this.textEmailProf.getText();
        String psswd = this.textParolaProf.getText();

        Profesor profesor = new Profesor(id,nume,prenume,email);
        try {
            Profesor rez = this.service.addProfesor(profesor);
            if (rez == null) {
                this.service.addProfesorPSSWD(profesor, psswd);
                CustomAlert.showMessage(null, Alert.AlertType.INFORMATION, "adaugare", "profesorul a fost adaugat cu succes!");
            } else {
                CustomAlert.showMessage(null, Alert.AlertType.ERROR, "adaugare", "profesorul nu a putut fi adaugat!");
            }
        }
        catch (ValidationException | IllegalArgumentException ex){
            CustomAlert.showMessage(null, Alert.AlertType.ERROR, "adaugare", ex.getMessage());
        }
    }

    public void handleDeleteProf(ActionEvent actionEvent) {
        //ProfesorDTO toBeDeleted = this.tableProfesori.getSelectionModel().getSelectedItem(); - !!! will reset on selection automatic filter
        Profesor toBeDeleted = this.service.findByIdProfesor(this.textIdProf.getText());
        if (StreamSupport.stream(this.service.getAllStudent().spliterator(), false).anyMatch(x -> x.getCadruDidacticIndrumatorLab().equals(toBeDeleted.toString())) ||
                StreamSupport.stream(this.service.getAllNota().spliterator(), false).anyMatch(x -> x.getProfesor().equals(toBeDeleted.toString()))
        ) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/confirmations/DeleteProfesorConfirmView.fxml"));
                AnchorPane root = (AnchorPane) loader.load();
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Confirmare");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                DeleteProfesorConfirmController deleteProfesorConfirmController = loader.getController();
                deleteProfesorConfirmController.setService(this, dialogStage, toBeDeleted);

                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            deleteProfForReal(toBeDeleted);
        }
    }

    public void deleteProfForReal(Profesor toBeDeleted){
        Profesor rez = this.service.removeByIdProfesor(toBeDeleted.getId());
        if(rez != null){
            this.service.deleteAllStudentsANDGradesOfProfesor(toBeDeleted);
            this.service.deleteProfesorPSSWD(toBeDeleted);
            CustomAlert.showMessage(null, Alert.AlertType.INFORMATION,"stergere","profesorul a fost sters cu succes!");
        }
        else{
            CustomAlert.showMessage(null, Alert.AlertType.ERROR,"stergere","profesorul nu a putut fi sters!");
        }

    }

    public void handleUpdateProf(ActionEvent actionEvent) {
        String id = this.textIdProf.getText();
        String nume = this.textNumeProf.getText();
        String prenume = this.textPrenumeProf.getText();
        String email = this.textEmailProf.getText();
        Profesor toBeUpdated = new Profesor(id, nume, prenume, email);
        if (StreamSupport.stream(this.service.getAllNota().spliterator(), false).anyMatch(x -> x.getProfesor().equals(toBeUpdated.toString())) ||
                StreamSupport.stream(this.service.getAllStudent().spliterator(), false).anyMatch(x -> x.getCadruDidacticIndrumatorLab().equals(toBeUpdated.toString()))
        ) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/confirmations/UpdateProfesorConfirmView.fxml"));
                AnchorPane root = (AnchorPane) loader.load();
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Confirmare");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                UpdateProfesorConfirmController updateProfesorConfirmController = loader.getController();
                updateProfesorConfirmController.setService(this, dialogStage, toBeUpdated);

                dialogStage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            updateProfesorForReal(toBeUpdated);
        }
    }

    public void updateProfesorForReal(Profesor toBeUpdated) {
        try {
            Profesor rez = this.service.updateProfesor(toBeUpdated);
            if (rez != null) {
                this.service.updateAllGradesOfProfesor(toBeUpdated);
                this.service.updateProfesorPSSWD(toBeUpdated, this.textParolaStudent.getText());
                CustomAlert.showMessage(null, Alert.AlertType.INFORMATION, "modificare", "profesorul a fost modificat cu succes!");
            } else {
                CustomAlert.showMessage(null, Alert.AlertType.ERROR, "modificare", "profesorul nu a putut fi modificat!");
            }
        }
        catch (ValidationException | IllegalArgumentException ex){
            CustomAlert.showMessage(null, Alert.AlertType.ERROR, "modificare", ex.getMessage());
        }
    }


    public void handleClearFieldsProf(ActionEvent actionEvent) {
        this.textIdProf.setText("");
        this.textNumeProf.setText("");
        this.textPrenumeProf.setText("");
        this.textEmailProf.setText("");
        this.textParolaProf.setText("");
    }




    public void handleModificaAdminParola(ActionEvent actionEvent) {
        boolean rez = this.service.changeAdminPassword(this.textAdminParola.getText());
        if(rez){
            CustomAlert.showMessage(null, Alert.AlertType.INFORMATION,"Resetare","parola a fost actualizata!");
        }
        else{
            CustomAlert.showMessage(null, Alert.AlertType.ERROR,"Resetare","parola nu a putut fi actualizata!");
        }
    }



    public void handleClearFieldsGrade(ActionEvent actionEvent) {
        this.textFieldNotaFeedback.setText("");
        this.textFieldNotaId.setText("");
        this.textFieldNotaProf.setText("");
        this.textFieldNotaValoare.setText("");
        this.textFieldStudentEmail.setText("");
        this.textFieldStudentGrupa.setText("");
        this.textFieldStudentId.setText("");
        this.textFieldStudentNume.setText("");
        this.textFieldStudentPrenume.setText("");
        this.textFieldStudentProf.setText("");
        this.textFieldTemaDescriere.setText("");
        this.textFieldTemaId.setText("");
        this.textFieldTemaNume.setText("");
        this.datePickerNotaData.setValue(null);
        this.datePickerTemaStart.setValue(null);
        this.datePickerTemaStop.setValue(null);
    }

    public void handleDelete(ActionEvent actionEvent) {
        NotaDTO n = this.tableNote.getSelectionModel().getSelectedItem();
        if(n == null){
            CustomAlert.showMessage(null, Alert.AlertType.ERROR,"stergere","Nu ati selectat din tabel o nota!");
        }
        else {
            Nota rez = this.service.removeByIdNota(n.getIdNota());
            if (rez == null) {
                CustomAlert.showMessage(null, Alert.AlertType.ERROR,"stergere","Nota nu a putut fi stearsa!");
            } else {
                CustomAlert.showMessage(null, Alert.AlertType.CONFIRMATION,"stergere","Nota a fost stearsa cu succes!");
                //TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!
                //TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!
                //TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!
                //TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!
                //TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!//TODO: email student!!!
            }
        }
    }


    @Override
    public void updateGrade(GradeChangeEvent event) {
        initModelN();
    }

    @Override
    public void updateStudent(StudentChangeEvent event) {
        initModelS();
    }

    @Override
    public void updateTask(TaskChangeEvent event) {
        initModelT();
    }

    @Override
    public void updateProf(ProfesorChangeEvent profesorChangeEvent) { initModelP(); }

    public void handleLoadModelStudenti(MouseEvent mouseEvent) {
        StudentDTO studentDTO = this.tableStudenti.getSelectionModel().getSelectedItem();
        if(studentDTO != null){
            this.textProfStudent.setText(studentDTO.getCadruDidacticIndrumatorLab());
            this.textEmailStudent.setText(studentDTO.getEmail());
            this.textGrupaStudent.setText(Integer.toString(studentDTO.getGrupa()));
            this.textNumeStudent.setText(studentDTO.getNume());
            this.textPrenumeStudent.setText(studentDTO.getPrenume());
            this.textIdStudent.setText(studentDTO.getId());
            this.textParolaStudent.setText(studentDTO.getPassword());
        }
    }

    public void handleLoadModelTeme(MouseEvent mouseEvent) {
        Tema tema = this.tableTeme.getSelectionModel().getSelectedItem();
        if(tema != null){
            this.textIdTema.setText(tema.getId());
            this.textDescriereTema.setText(tema.getDescriere());
            this.textNumeTema.setText(tema.getNume());
            this.dateInceputTema.setValue(LocalDate.parse(tema.getStartWeek(),Constants.DATE_TIME_FORMATTER));
            this.dateSfarsitTema.setValue(LocalDate.parse(tema.getDeadlineWeek(),Constants.DATE_TIME_FORMATTER));
        }
    }

    public void handleLoadModelProfi(MouseEvent mouseEvent) {
        ProfesorDTO profesorDTO = this.tableProfesori.getSelectionModel().getSelectedItem();
        if(profesorDTO != null){
            this.textIdProf.setText(profesorDTO.getId());
            this.textPrenumeProf.setText(profesorDTO.getPrenume());
            this.textNumeProf.setText(profesorDTO.getNume());
            this.textEmailProf.setText(profesorDTO.getEmail());
            this.textParolaProf.setText(profesorDTO.getPassword());
        }
    }

    public void handleLoadModelNote(MouseEvent mouseEvent) {
        NotaDTO notaDTO = this.tableNote.getSelectionModel().getSelectedItem();
        if(notaDTO != null){
            this.textFieldNotaFeedback.setText(notaDTO.getFeedback());
            this.textFieldNotaId.setText(notaDTO.getIdNota());
            this.textFieldNotaProf.setText(notaDTO.getProfesor());
            this.textFieldNotaValoare.setText(Integer.toString(notaDTO.getValoare()));
            this.textFieldStudentEmail.setText(notaDTO.getEmailStudent());
            this.textFieldStudentGrupa.setText(Integer.toString(notaDTO.getGrupaStudent()));
            this.textFieldStudentId.setText(notaDTO.getIdStudent());
            this.textFieldStudentNume.setText(notaDTO.getNumeStudent());
            this.textFieldStudentPrenume.setText(notaDTO.getPrenumeStudent());
            this.textFieldStudentProf.setText(notaDTO.getProfesor());
            this.textFieldTemaDescriere.setText(notaDTO.getDescriereTema());
            this.textFieldTemaId.setText(notaDTO.getIdTema());
            this.textFieldTemaNume.setText(notaDTO.getNumeTema());
            this.datePickerNotaData.setValue(LocalDate.parse(notaDTO.getDataNota(),Constants.DATE_TIME_FORMATTER));
            this.datePickerTemaStart.setValue(LocalDate.parse(notaDTO.getStartTema(),Constants.DATE_TIME_FORMATTER));
            this.datePickerTemaStop.setValue(LocalDate.parse(notaDTO.getDeadlineTema(),Constants.DATE_TIME_FORMATTER));
        }
    }
}
