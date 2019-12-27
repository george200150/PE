package mvc;

import domain.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.StudentService;
import utils.events.StudentChangeEvent;
import utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StudentTableViewController implements Observer<StudentChangeEvent> {
    private StudentService service;
    private ObservableList<Student> model = FXCollections.observableArrayList();

    @FXML
    TableView<Student> tableViewStudent;
    @FXML
    TableColumn<Student,String> tableStudentColumnNume;
    @FXML
    TableColumn<Student,String> tableStudentColumnPrenume;
    @FXML
    TableColumn<Student,String> tableStudentColumnGrupa;
    @FXML
    TableColumn<Student,String> tableStudentColumnEmail;
    @FXML
    TableColumn<Student,String> tableStudentColumnProfIndrumator;


    @FXML
    TextField searchFieldNume;
    @FXML
    TextField searchFieldPrenume;
    @FXML
    TextField searchFieldGrupa;
    @FXML
    TextField searchFieldEmail;
    @FXML
    TextField searchFieldProfesor;





    /**
     * We separately set the service reference in the controller
     * @param studentService - service responsible with CRUD operations
     */
    public void setService(StudentService studentService) {
        service = studentService;
        service.addObserver(this);
        initModel();
    }

    /**
     * Setup of the table view layout. We receive each table column from the FXML file.
     * The PropertyValueFactory will receive as strings the name of the fields of the object.
     */
    @FXML
    public void initialize() {
        tableStudentColumnNume.setCellValueFactory(new PropertyValueFactory<Student, String>("nume"));
        tableStudentColumnPrenume.setCellValueFactory(new PropertyValueFactory<Student, String>("prenume"));
        tableStudentColumnGrupa.setCellValueFactory(new PropertyValueFactory<Student, String>("grupa"));
        tableStudentColumnEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("email"));
        tableStudentColumnProfIndrumator.setCellValueFactory(new PropertyValueFactory<Student, String>("cadruDidacticIndrumatorLab"));
        tableViewStudent.setItems(model);

        searchFieldPrenume.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldNume.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldGrupa.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldEmail.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldProfesor.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
    }

    /**
     * We load all the students from the service in the table view.
     * We set the textFields of the search form as observable objects.
     */
    private void initModel() {
        Iterable<Student> students = service.getAll();
        List<Student> studentList = StreamSupport.stream(students.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(studentList);
    }

    /**
     * Merges all the criteria for search.
     * If there is any text in one of the boxes of the search form,
     * then we consider filtering by that input.
     * Filtering is made with Predicates, we define the base predicates
     * and we use the ".and" method to merge them together.
     * If there isn't any text in none of the boxes, then we return the whole list.
     */
    private void filterMergeSearch(){
        Predicate<Student> filtered = null;//TODO: asa compunem predicatele

        Iterable<Student> students = service.getAll();
        List<Student> studentList = StreamSupport.stream(students.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Student> filterNume = x -> x.getNume().toLowerCase().contains(searchFieldNume.getText().toLowerCase());
        Predicate<Student> filterPrenume = x -> x.getPrenume().toLowerCase().contains(searchFieldPrenume.getText().toLowerCase());
        Predicate<Student> filterGrupa = x -> Integer.toString(x.getGrupa()).toLowerCase().contains(searchFieldGrupa.getText().toLowerCase());
        Predicate<Student> filterEmail = x -> x.getEmail().toLowerCase().contains(searchFieldEmail.getText().toLowerCase());
        Predicate<Student> filterProf = x -> x.getCadruDidacticIndrumatorLab().toLowerCase().contains(searchFieldProfesor.getText().toLowerCase());

        if (!searchFieldNume.getText().isEmpty()){//TODO: CHANGE ALL PREDICATES WITH {IF TEXTFIELD IS EMPTY => RETURN TRUE; ELSE RETURN THE FILTER}
            if(filtered == null){//TODO: HERE, WE DO NOT TEST IF FILTERED = NULL; WE INITIALIZE FILTERED VARIABLE AS UNION AND(ALL PREDICATES)
                filtered = filterNume;
            }
            else{
                filtered = filtered.and(filterNume);
            }
        }
        if (!searchFieldPrenume.getText().isEmpty()){
            if(filtered == null){
                filtered = filterPrenume;
            }
            else{
                filtered = filtered.and(filterPrenume);
            }
        }
        if (!searchFieldGrupa.getText().isEmpty()){
            if(filtered == null){
                filtered = filterGrupa;
            }
            else{
                filtered = filtered.and(filterGrupa);
            }
        }
        if (!searchFieldEmail.getText().isEmpty()){
            if(filtered == null){
                filtered = filterEmail;
            }
            else{
                filtered = filtered.and(filterEmail);
            }
        }
        if (!searchFieldProfesor.getText().isEmpty()){
            if(filtered == null){
                filtered = filterProf;
            }
            else{
                filtered = filtered.and(filterProf);
            }
        }

        if( filtered == null){
            model.setAll(studentList);
        }
        else{
            model.setAll(studentList.stream().filter(filtered).collect(Collectors.toList()));
        }
    }


    /**
     * Observer specific overriden method.
     * When something changes, the table view is reloaded.
     * @param studentChangeEvent - custom change event
     */
    @Override
    public void update(StudentChangeEvent studentChangeEvent) {
        initModel();
    }

    /**
     * We handle the delete event. A selected object in the given GUI list is needed to perform this task.
     * @param actionEvent - triggered event
     */
    public void handleDeleteStudent(ActionEvent actionEvent) {
        Student selected = (Student) tableViewStudent.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Student deleted = service.removeById(selected.getId());
            if (null != deleted)
                StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Studentul a fost sters cu succes!");
        } else StudentAlert.showErrorMessage(null, "Nu ati selectat niciun student!");
    }


    /**
     * We handle the add event. We just need to press the "Add" button to trigger this event.
     * We send a "null" value as input parameter to the method call in order to tell the controller
     * that the operation to be performed is going to be "Add" and not "Update".
     *
     * @param actionEvent - triggered event
     */
    @FXML
    public void handleAddStudent(ActionEvent actionEvent) {
        showStudentEditDialog(null);
    }

    /**
     * We handle the ADD OR UPDATE event. If the "Add" button is pressed, the input parameter, "student" is null.
     * If the "Update" button is pressed (and also a line from the table is selected), the input value of student
     * is the one from the table view and will be shown in the text fields of the add/update form.
     *
     * @param student - student object to be added/updated
     */
    public void showStudentEditDialog(Student student) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/studenteditview.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Student");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            StudentEditController editStudentViewController = loader.getController();
            editStudentViewController.setService(service, dialogStage, student);
            //HERE, WE SEND TO THE CONTROLLER THE INFORMATION ABOUT THE SERVICE,
            //THE WINDOW TYPE THE FORM SHOULD USE AND THE STUDENT THAT DECIDES IF
            //THE OPERATION PERFORMED WILL BE AN "ADD" OR "UPDATE".

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * We handle the update event. If there is no line selected in the table view, then we show a warning message.
     * Else, we perform the "Update" operation having as input parameter the selected student from the table view.
     *
     * @param actionEvent - triggered event
     */
    @FXML
    public void handleUpdateStudent(ActionEvent actionEvent) {
        Student selected = tableViewStudent.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showStudentEditDialog(selected);
        } else
            StudentAlert.showErrorMessage(null, "Nu ati selectat niciun student");
    }
}
