package mvc.backup4new;

import domain.Profesor;
import domain.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.StudentAlert;
import services.MasterService;

import java.io.IOException;

public class LoginFormProfessorController {
    @FXML
    TextField textFieldUserName;
    @FXML
    PasswordField passwordFieldUserPassword;

    private Stage dialogStage;
    private MasterService masterService;

    @FXML
    private void initialize() {
    }

    public void setService(MasterService masterService, Stage stage){
        this.masterService = masterService;
        this.dialogStage = stage;
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
            loginChoiceController.setService(masterService, dialogStage);

            this.dialogStage.close();
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleLogIn(ActionEvent actionEvent) {

        String userName = this.textFieldUserName.getText();
        String password = this.passwordFieldUserPassword.getText();
        Profesor loggedInProfessor = this.masterService.findProfessorByCredentials(userName, password);

        if(loggedInProfessor != null){// check if account acces is granted

            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/backup4new/ProfessorAccountView.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Log In Profesor");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                ProfessorAccountController professorAccountController = loader.getController();
                professorAccountController.setService(masterService, dialogStage, loggedInProfessor);

                this.dialogStage.close();
                dialogStage.show();
                dialogStage.setMaximized(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.ERROR,"Log In","Nu ati introdus corect numele de utilizator sau parola!");
        }
    }
}
