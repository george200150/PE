package mvc.backup4new;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.StudentAlert;
import services.MasterService;

import java.io.IOException;

public class LoginFormAdminController {

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

        String password = this.passwordFieldUserPassword.getText();
        boolean granted = this.masterService.findAdminByCredentials(password);
        //TODO: check if account acces is granted
        if(granted){

            try {
                // create a new stage for the popup dialog.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/backup4new/AdminAccountView.fxml"));

                AnchorPane root = (AnchorPane) loader.load();

                // Create the dialog Stage.
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Log In Admin");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);

                AdminAccountController studentAccountController = loader.getController();
                studentAccountController.setService(masterService,dialogStage);

                this.dialogStage.close();
                dialogStage.show();
                dialogStage.setMaximized(true);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            StudentAlert.showMessage(null, Alert.AlertType.ERROR,"Log In","Parola este gresita!");
        }
    }
}
