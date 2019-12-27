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

public class AdminAccountController implements Observer<GradeChangeEvent> {


    public TableView tableStudenti;
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


    public TableView tableTeme;
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


    public TableView tableNote;
    public TableColumn columnNoteId;
    public TableColumn columnNoteTema;
    public TableColumn columnNoteStudent;
    public TableColumn columnNoteValoare;
    public TableColumn columnNoteProf;
    public TableColumn columnNoteFeedback;


    public TableView tableProfesori;
    public TableColumn columnProfId;
    public TableColumn columnProfNume;
    public TableColumn columnProfPrenume;
    public TableColumn columnProfEmail;
    public TableColumn columnProfParola;
    public TextField textIdProf;
    public TextField textEmailProf;
    public TextField textPrenumeProf;
    public TextField textNumeProf;
    public TextField textParolaProf;


    public TextField textAdminParola;


    private MasterService service;
    private ObservableList<NotaDTO> model = FXCollections.observableArrayList();
    private Stage dialogStage;



    public void setService(MasterService masterService, Stage stage) {
        this.dialogStage = stage;
        service = masterService;
        service.addObserver(this);
        initModel();
    }

    private void initModel() {


    }

    @FXML
    public void initialize() {


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

        /*String oldPsswd = this.oldPasswordField.getText();
        String newPsswd = this.newPasswordField.getText();
        String email = this.loggedInStudent.getEmail();//USERNAME IS THE EMAIL!!!
        boolean state = this.service.changeStudentPassword(email, oldPsswd, newPsswd);*/
        if(true/*state*/){
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION,"Resetare","parola a fost actualizata!");
        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.ERROR,"Log In","parola nu a putut fi actualizata!");
        }

    }

    public void handleAddStudent(ActionEvent actionEvent) {
    }

    public void handleDeleteStudent(ActionEvent actionEvent) {
    }

    public void handleUpdateStudent(ActionEvent actionEvent) {
    }

    public void handleClearFieldsStudent(ActionEvent actionEvent) {
    }

    public void handleAddTema(ActionEvent actionEvent) {
    }

    public void handleUpdateTema(ActionEvent actionEvent) {
    }

    public void handleDeleteTema(ActionEvent actionEvent) {
    }

    public void handleClearFieldsTema(ActionEvent actionEvent) {
    }

    public void handleAddProf(ActionEvent actionEvent) {
    }

    public void handleDeleteProf(ActionEvent actionEvent) {
    }

    public void handleUpdateProf(ActionEvent actionEvent) {
    }

    public void handleClearFieldsProf(ActionEvent actionEvent) {
    }


    public void handleModificaAdminParola(ActionEvent actionEvent) {
    }
}
