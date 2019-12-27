package mvc;

import domain.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.StudentService;
import validators.ValidationException;


public class StudentEditController {
    @FXML
    private TextField textFieldId;//VOM FOLOSI CAMPUL ID NEEDITABIL
    @FXML
    private TextField textFieldNume;
    @FXML
    private TextField textFieldPrenume;
    @FXML
    private TextField textFieldGrupa;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private TextField textFieldProfIndrumator;

    private StudentService service;
    private Stage dialogStage;
    private Student student;

    @FXML
    private void initialize() {
    }

    /**
     * The "fancy stuff" with this controller is that when we initialize it, we will also
     * give a Student object as input or a null value. We do that to avoid duplicate code
     * for add() and update() methods. When the add event is triggered in the GUI, the student
     * in the controller is set to null. If the update event is triggered, we collect the
     * already selected Student from the table view and use it as default value to be modified.
     *
     * @param service - the service referenced in MVC responsible with CRUD of students
     * @param stage - graphic window item
     * @param s - the student: when not null, we update; when null we add a new student
     */
    public void setService(StudentService service, Stage stage, Student s) {
        this.service = service;
        this.dialogStage=stage;
        this.student=s;
        if (null != s) {
            setFields(s);
            textFieldId.setEditable(false);
        }
        else{
            textFieldEmail.setPromptText("example@mail.com");
        }
    }

    /**
     * Gets all the text inputs from the form window, and then creates a student.
     * If this.student was set to an existing object, then we consider an update.
     * Else, we consider that the newly created student will be added in the service.
     */
    @FXML
    public void handleSave() {
        String id = textFieldId.getText();
        String nume = textFieldNume.getText();
        String prenume = textFieldPrenume.getText();
        String grupa = textFieldGrupa.getText();
        String email = textFieldEmail.getText();
        String prof = textFieldProfIndrumator.getText();
        if(grupa.isEmpty()){
            grupa = "0";
        }
        Student s = new Student(id, nume, prenume, Integer.parseInt(grupa), email, prof);
        if (null == this.student)
            saveStudent(s);
        else
            updateStudent(s);
    }

    /**
     * Calls "update" method from service using the created student as parameter
     * @param s - student
     */
    private void updateStudent(Student s) {
        try {
            Student r = this.service.update(s);
            if (r == null)
                StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Modificare student", "Studentul a fost modificat");
        } catch (ValidationException e) {
            StudentAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStage.close();
    }

    /**
     * Calls "save" method from service using the created student as parameter
     * @param s - student
     */
    private void saveStudent(Student s)
    {
        try {
            Student r = this.service.add(s);
            if (r == null)
                dialogStage.close();
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare student","Studentul a fost salvat");
        } catch (ValidationException e) {
            StudentAlert.showErrorMessage(null,e.getMessage());
        }
    }

    /*private void clearFields() {
        textFieldId.setText("");
        textFieldNume.setText("");
        textFieldPrenume.setText("");
        textFieldGrupa.setText("");
        textFieldEmail.setText("");
        textFieldProfIndrumator.setText("");
    }*/

    /**
     * shows all the fields of a Student in the table view
     * @param s - student (not null)
     */
    private void setFields(Student s)
    {
        textFieldId.setText(s.getId());
        textFieldNume.setText(s.getNume());
        textFieldPrenume.setText(s.getPrenume());
        textFieldGrupa.setText(Integer.toString(s.getGrupa()));
        textFieldEmail.setText(s.getEmail());
        textFieldProfIndrumator.setText(s.getCadruDidacticIndrumatorLab());
    }

    /**
     * closes the form window
     */
    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
