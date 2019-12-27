package mvc.backup4new;

import domain.Nota;
import domain.NotaDTO;
import domain.Student;
import domain.Tema;
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
import utils.events.GradeChangeEvent;
import utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StudentAccountController implements Observer<GradeChangeEvent> {

    @FXML
    PasswordField newPasswordField;
    @FXML
    PasswordField oldPasswordField;

    @FXML
    private Label labelStudent;

    @FXML
    TableColumn<NotaDTO, String> studentViewGradesTaskColumn;
    @FXML
    TableColumn<NotaDTO, String> studentViewGradesDescriptionColumn;
    @FXML
    TableColumn<NotaDTO, String> studentViewGradesValueColumn;
    @FXML
    TableColumn<NotaDTO, String> studentViewGradesFeedbackColumn;
    @FXML
    TableColumn<NotaDTO, String> studentViewGradesProfessorColumn;
    @FXML
    TableView<NotaDTO> tableViewStudentAccountGrades;


    private Student loggedInStudent;//LOGIN CREDENTIALS
    private MasterService service;
    private ObservableList<NotaDTO> model = FXCollections.observableArrayList();
    private Stage dialogStage;



    public void setService(MasterService masterService, Stage stage, Student loggedInStudent) {
        this.dialogStage = stage;
        this.loggedInStudent = loggedInStudent;
        service = masterService;
        service.addObserver(this);
        initModel();
        this.labelStudent.setText(loggedInStudent.toString());
    }

    private void initModel() {

        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false).filter(st -> st.getId().split(":")[0].equals(this.loggedInStudent.getId()))
                .collect(Collectors.toList());

        model.setAll(convertGradeToDTO(gradeList));
    }

    @FXML
    public void initialize() {
        studentViewGradesTaskColumn.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("numeTema"));
        studentViewGradesDescriptionColumn.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("descriereTema"));
        studentViewGradesValueColumn.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("valoare"));
        studentViewGradesFeedbackColumn.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("feedback"));
        studentViewGradesProfessorColumn.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("profesor"));

        tableViewStudentAccountGrades.setItems(model);

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
        initModel();
    }


    public void handleBackToLogInChoice(ActionEvent actionEvent) {

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

        String oldPsswd = this.oldPasswordField.getText();
        String newPsswd = this.newPasswordField.getText();
        String email = this.loggedInStudent.getEmail();//USERNAME IS THE EMAIL!!!
        boolean state = this.service.changeStudentPassword(email, oldPsswd, newPsswd);
        if(state){
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION,"Resetare","parola a fost actualizata!");
        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.ERROR,"Log In","parola nu a putut fi actualizata!");
        }
    }
}
