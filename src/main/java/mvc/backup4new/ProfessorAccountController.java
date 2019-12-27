package mvc.backup4new;

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
import utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProfessorAccountController implements Observer<GradeChangeEvent> {


    public TableView<Student> tableStudent;
    public TableColumn<Student,String> columnStudentNume;
    public TableColumn<Student,String> columnStudentPrenume;
    public TableColumn<Student,String> columnStudentGrupa;
    public TableColumn<Student,String> columnStudentEmail;
    public TextField textStudentNume;
    public TextField textStudentPrenume;
    public TextField textStudentGrupa;
    public TextField textStudentEmail;
    public TextField textStudentProf;
    
    
    public TableView<Tema> tableTema;
    public TableColumn<Tema,String> tableTemaNume;
    public TableColumn<Tema,String> tableTemaDescriere;
    public TableColumn<Tema,String> tableTemaInceput;
    public TableColumn<Tema,String> tableTemaSfarsit;
    public TextField dateTemaSfarsit;
    public TextField dateTemaInceput;
    public TextField textTemaNume;
    public TextField textTemaDescriere;

    public TableView<NotaDTO> tableNota;
    public TableColumn<NotaDTO, String> columnNotaStudent;
    public TableColumn<NotaDTO, String> columnNotaTema;
    public TableColumn<NotaDTO, String> columnNotaData;
    public TableColumn<NotaDTO, String> columnNotaProfesor;
    public TableColumn<NotaDTO, String> columnNotaValoare;
    public TableColumn<NotaDTO, String> columnNotaFeedback;
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
        service.addObserver(this);
        initModelStudent();
        initModelTema();
        initModelGrade();
        this.labelProfesor.setText(this.loggedInProfessor.toString());
    }



    private void initModelStudent() {
        Iterable<Student> students = service.getAllStudent();
        List<Student> studentList = StreamSupport
                .stream(students.spliterator(), false)
                .filter(st -> st.getCadruDidacticIndrumatorLab().equals(this.loggedInProfessor.getNume()+" "+this.loggedInProfessor.getPrenume()))
                .collect(Collectors.toList());
        modelS.setAll(studentList);
    }

    private void initModelTema(){
        Iterable<Tema> tasks = service.getAllTema();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());
        modelT.setAll(taskList);

    }

    private void initModelGrade(){
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(gr -> gr.getProfesor().equals(this.loggedInProfessor.getNume()+" "+this.loggedInProfessor.getPrenume()))
                .collect(Collectors.toList());
        List<NotaDTO> gradeDTOList = convertGradeToDTO(gradeList);
        modelN.setAll(gradeDTOList);
    }


    private void initModelGradeINVERTED(){
        if(this.checkboxInvert.isSelected()){
            List<Nota> grades = StreamSupport.stream(service.getAllNota().spliterator(), false).collect(Collectors.toList());;
            List<Student> studenti = StreamSupport.stream(service.getAllStudent().spliterator(), false).collect(Collectors.toList());
            List<Tema> teme = StreamSupport.stream(service.getAllTema().spliterator(), false).collect(Collectors.toList());
            List<NotaDTO> fictionalGrades = new ArrayList<>();
            for (Student student : studenti) {
                for (Tema tema : teme) {
                    String nid = student.getId()+":"+tema.getId();
                    if(grades.stream().noneMatch(gr -> gr.getId().equals(nid)))
                        fictionalGrades.add(new NotaDTO(new Nota(student.getId(),tema.getId(),0,this.loggedInProfessor.getNume()+" "+this.loggedInProfessor.getPrenume()),tema,student));
                }
            }
            modelN.setAll(fictionalGrades);
        }
        else{
            initModelGrade();
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


        columnNotaData.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("dataNota"));
        columnNotaProfesor.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("profesor"));
        columnNotaValoare.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("valoare"));

        columnNotaStudent.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("studentString"));
        columnNotaTema.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("temaString"));
        columnNotaFeedback.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("feedback"));

        tableNota.setItems(modelN);

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
    public void update(GradeChangeEvent gradeChangeEvent) {
        initModelGrade();
    }


    public void handleBackToLoginChoice(ActionEvent actionEvent) {

        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/backup4new/LoginChoice.fxml"));

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
        this.textStudentProf.setText("");
    }

    // gogo@cs.ubbcluj.ro
    public void handleAddNota(ActionEvent actionEvent) {
        if(checkboxInvert.isSelected()){
            if ( !this.textNotaValoare.getText().isEmpty() && !this.textNotaProf.getText().isEmpty() && !this.textNotaFeedback.getText().isEmpty()){
                NotaDTO futureNota = this.tableNota.getSelectionModel().getSelectedItem();
                futureNota.setValoare(Integer.parseInt(this.textNotaValoare.getText()));
                //TODO: ... futureNota.set
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

        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.WARNING,"Avertisment","pentru a intra in modul de stergere, debifati casuta!");
        }
    }

    public void handleUpdateNota(ActionEvent actionEvent) {
        if(!checkboxInvert.isSelected()){

        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.WARNING,"Avertisment","pentru a intra in modul de modificare, debifati casuta!");
        }
    }


    public void handleClearFieldsTema(ActionEvent actionEvent) {
        this.textTemaDescriere.setText("");
        this.dateTemaInceput.setText("");
        this.textTemaNume.setText("");
        this.dateTemaSfarsit.setText("");
    }

    public void handleToggleINVERT(ActionEvent actionEvent) {
        initModelGradeINVERTED();
        this.textTemaDescriere.setText("");
        this.dateTemaInceput.setText("");
        this.textTemaNume.setText("");
        this.dateTemaSfarsit.setText("");
    }


    public void handleSelectionChanged(MouseEvent mouseEvent) {
        NotaDTO nota = this.tableNota.getSelectionModel().getSelectedItem();

        if(!this.checkboxInvert.isSelected()){//ready to update : WARNING: ON UPDATE FULL CONTROLL ON GRADE PARAMETERS!
            this.textNotaStudent.setText(nota.getS().toString());
            this.textNotaProf.setText(nota.getProfesor());
            this.textNotaValoare.setText(Integer.toString(nota.getValoare()));
            this.dateNotaData.setValue(LocalDate.parse(nota.getDataNota(),Constants.DATE_TIME_FORMATTER));
            this.textNotaFeedback.setText(nota.getFeedback());
            this.textNotaTema.setText(nota.getT().toString());
        }
        else{
            //TODO: compute 
        }

    }
}
