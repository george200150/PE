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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.StudentAlert;
import services.MasterService;
import utils.Constants;
import utils.events.GradeChangeEvent;
import utils.events.StudentChangeEvent;
import utils.events.TaskChangeEvent;
import utils.observer.GradeObserver;
import utils.observer.StudentObserver;
import utils.observer.TaskObserver;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//public class AdminAccountController implements Observer<StudentChangeEvent>, Observer<TaskChangeEvent>, Observer<GradeChangeEvent> {
public class AdminAccountController implements GradeObserver, TaskObserver, StudentObserver {


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
            return new ProfesorDTO(x.getId(), x.getNume(), x.getPrenume(), x.getEmail(), password);
        }).collect(Collectors.toList());
    }

    private void initModelS() {
        Iterable<Student> students = service.getAllStudent();
        List<Student> studentList = StreamSupport
                .stream(students.spliterator(), false)
                .collect(Collectors.toList());
        //TODO change model to extended DTO-d
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


        columnTemaId.setCellValueFactory(new PropertyValueFactory<Tema, String>("id"));
        columnTemaNume.setCellValueFactory(new PropertyValueFactory<Tema, String>("nume"));
        columnTemaDescriere.setCellValueFactory(new PropertyValueFactory<Tema, String>("descriere"));
        columnTemaInceput.setCellValueFactory(new PropertyValueFactory<Tema, String>("startWeek"));
        columnTemaSfarsit.setCellValueFactory(new PropertyValueFactory<Tema, String>("deadlineWeek"));
        tableTeme.setItems(modelT);


        columnProfId.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("id"));
        columnProfEmail.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("email"));
        columnProfId.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("id"));
        columnProfNume.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("nume"));
        columnProfParola.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("password"));
        columnProfPrenume.setCellValueFactory(new PropertyValueFactory<ProfesorDTO, String>("prenume"));
        tableProfesori.setItems(modelP);

        /*columnNotaId.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("idNota"));
        columnNotaData.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("dataNota"));
        columnNotaProfesor.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("profesor"));
        columnNotaValoare.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("valoare"));
        columnNotaStudent.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("studentString"));
        columnNotaTema.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("temaString"));
        columnNotaFeedback.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("feedback"));
        tableNota.setItems(modelN);*/


    }


    private void filterMergeSearchStudent() {
        Predicate<Student> filtered = null;//TODO: asa compunem predicatele

        Iterable<Student> students = service.getAllStudent();
        List<Student> studentList = StreamSupport.stream(students.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Student> filterNume = x -> x.getNume().toLowerCase().contains(textNumeStudent.getText().toLowerCase());
        Predicate<Student> filterPrenume = x -> x.getPrenume().toLowerCase().contains(textPrenumeStudent.getText().toLowerCase());
        Predicate<Student> filterGrupa = x -> Integer.toString(x.getGrupa()).toLowerCase().contains(textGrupaStudent.getText().toLowerCase());
        Predicate<Student> filterEmail = x -> x.getEmail().toLowerCase().contains(textEmailStudent.getText().toLowerCase());

        if (!textNumeStudent.getText().isEmpty()) {//TODO: CHANGE ALL PREDICATES WITH {IF TEXTFIELD IS EMPTY => RETURN TRUE; ELSE RETURN THE FILTER}
            if (filtered == null) {//TODO: HERE, WE DO NOT TEST IF FILTERED = NULL; WE INITIALIZE FILTERED VARIABLE AS UNION AND(ALL PREDICATES)
                filtered = filterNume;
            } else {
                filtered = filtered.and(filterNume);
            }
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

        if (filtered == null) {
            modelS.setAll(convertStudentToDTO(studentList));
        } else {
            modelS.setAll(convertStudentToDTO(studentList.stream().filter(filtered).collect(Collectors.toList())));
        }
    }


    private void filterMergeSearchTema() {
        Predicate<Tema> filtered = null;

        Iterable<Tema> tasks = service.getAllTema();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Tema> filterNume = x -> x.getNume().toLowerCase().contains(textNumeTema.getText().toLowerCase());
        Predicate<Tema> filterDescriere = x -> x.getDescriere().toLowerCase().contains(textDescriereTema.getText().toLowerCase());
        //Predicate<Tema> filterStart = x -> x.getStartWeek().contains(dateInceputTema.getText());
        //Predicate<Tema> filterStop = x -> x.getDeadlineWeek().contains(dateSfarsitTema.getText());

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
        /*if (!(dateTemaInceput.getText() == null)){
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
        }*/

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


    public void handleResetPassword(ActionEvent actionEvent) {

        /*String oldPsswd = this.oldPasswordField.getText();
        String newPsswd = this.newPasswordField.getText();
        String email = this.loggedInStudent.getEmail();//USERNAME IS THE EMAIL!!!
        boolean state = this.service.changeStudentPassword(email, oldPsswd, newPsswd);*/
        if (true/*state*/) {
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Resetare", "parola a fost actualizata!");
        } else {
            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Log In", "parola nu a putut fi actualizata!");
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
        Student rez = this.service.addStudent(student);
        if (rez == null) {
            this.service.addStudentPSSWD(student, this.textParolaStudent.getText());
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "adaugare", "studentul a fost adaugat cu succes!");
        } else {
            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "adaugare", "studentul nu a putut fi adaugat!");
        }
    }

    public void handleDeleteStudent(ActionEvent actionEvent) {
        StudentDTO toBeDeleted = this.tableStudenti.getSelectionModel().getSelectedItem();
        if (StreamSupport.stream(this.service.getAllNota().spliterator(), false).anyMatch(x -> x.getId().split(":")[0].equals(toBeDeleted.getId()))) {
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/confirmations/DeleteStudentConfirmView.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Confirmation");
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

    public void deleteStudentForReal(StudentDTO toBeDeleted) {
        Student rez = this.service.removeByIdStudent(toBeDeleted.getId());
        if (rez != null) {
            this.service.deleteStudentPSSWD(toBeDeleted.getStudent());
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "stergere", "studentul a fost sters cu succes!");
        } else {
            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "stergere", "studentul nu a putut fi sters!");
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
                dialogStage.setTitle("Confirmation");
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
        Student rez = this.service.updateStudent(toBeUpdated);
        if (rez != null) {
            this.service.updateStudentPSSWD(toBeUpdated, this.textParolaStudent.getText());
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "modificare", "studentul a fost modificat cu succes!");
        } else {
            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "modificare", "studentul nu a putut fi modificat!");
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


    public void handleAddTema(ActionEvent actionEvent) {
        String id = this.textIdTema.getText();
        String nume = this.textNumeTema.getText();
        String descriere = this.textDescriereTema.getText();
        String start = this.dateInceputTema.getValue().format(Constants.DATE_TIME_FORMATTER);
        String end = this.dateSfarsitTema.getValue().format(Constants.DATE_TIME_FORMATTER);
        Tema tema = new Tema(id, nume, descriere, start, end);
        Tema rez = this.service.addTema(tema);
        if (rez == null) {
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "adaugare", "tema a fost adaugata cu succes!");
        } else {
            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "adaugare", "tema nu a putut fi adaugata!");
        }
    }

    public void handleUpdateTema(ActionEvent actionEvent) {
        String id = this.textIdTema.getText();
        String nume = this.textNumeTema.getText();
        String descriere = this.textDescriereTema.getText();
        String start = this.dateInceputTema.getValue().format(Constants.DATE_TIME_FORMATTER);
        String end = this.dateSfarsitTema.getValue().format(Constants.DATE_TIME_FORMATTER);
        Tema toBeUpdated = new Tema(id, nume, descriere, start, end);
        if (StreamSupport.stream(this.service.getAllNota().spliterator(), false).anyMatch(x -> x.getId().split(":")[1].equals(toBeUpdated.getId()))) {
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/confirmations/UpdateTemaConfirmView.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Confirmation");
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
    }


    public void updateTemaForReal(Tema toBeUpdated) {
        Tema rez = this.service.updateTema(toBeUpdated);
        if (rez != null) {
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "modificare", "tema a fost modificata cu succes!");
        } else {
            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "modificare", "tema nu a putut fi modificata!");
        }
    }


    public void handleDeleteTema(ActionEvent actionEvent) {
        Tema toBeDeleted = this.tableTeme.getSelectionModel().getSelectedItem();
        if (StreamSupport.stream(this.service.getAllNota().spliterator(), false).anyMatch(x -> x.getId().split(":")[1].equals(toBeDeleted.getId()))){
            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/confirmations/DeleteTemaConfirmView.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Confirmation");
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
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION,"stergere","tema a fost stearsa cu succes!");
        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.ERROR,"stergere","tema nu a putut fi stearsa!");
        }
    }


    public void handleClearFieldsTema(ActionEvent actionEvent) {
        this.textIdTema.setText("");
        this.textNumeTema.setText("");
        this.textDescriereTema.setText("");
        this.dateInceputTema.setValue(null);
        this.dateSfarsitTema.setValue(null);
    }







    assert(false)
    //TODO: BUG BUG BUG BUG BUG BUG - ON task with grades DELETE =/=> delete grades ??? (find out why!!!)
    //TODO: go crud for professor
    //TODO: CREATE NEW eventChangeType, Observer, Observable, modify services - for PROFESSOR
    //TODO: encapsulate popup window creation in new function (optional - preferred, however)
    //test all the work


    public void handleAddProf(ActionEvent actionEvent) {
    }

    public void handleDeleteProf(ActionEvent actionEvent) {
    }

    public void handleUpdateProf(ActionEvent actionEvent) {
        //todo : consider password!!!
    }

    public void handleClearFieldsProf(ActionEvent actionEvent) {
    }




    public void handleModificaAdminParola(ActionEvent actionEvent) {
        boolean rez = this.service.changeAdminPassword(this.textAdminParola.getText());
        if(rez){
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION,"Resetare","parola a fost actualizata!");
        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.ERROR,"Log In","parola nu a putut fi actualizata!");
        }
    }




    public void handleClearFieldsGrade(ActionEvent actionEvent) {
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

}

//TODO: lucrul cu notele
//TODO: filtrari pentru profesor si nota (si ce cui mai trebui)
//TODO: adauga buton reset fields pentru Admin
//TODO: CONDITIE: on delete student with note
//TODO: CONDITIE: on delete tema with note
//TODO: CONDITIE: on delete profesor with note
//TODO: CONDITIE: on delete profesor with studenti
//TODO: AUTO: on delete student/profesor - delete username+password
//TODO: AUTO: on select entity from table - load parameters into text fields
//TODO: QUESTION : ce permisiuni sa dau asupra notelor???
//TODO: OBSERVER : implement all observer interface, as changes of students/tasks/grades/teachers will be seen in all table views