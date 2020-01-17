package mvc.controllers.login;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.controllers.confirmations.ExitConfirmController;
import services.MasterService;

import java.io.IOException;

public class LoginChoiceController {


    private Stage dialogStage;
    private MasterService masterService;


    public Stage getDialogStage(){
        return this.dialogStage;

    }

    @FXML
    private void initialize() {

    }

    public void setService(MasterService masterService, Stage stage) {
        this.dialogStage = stage;
        this.masterService = masterService;
    }


    public void handleAdmin(ActionEvent actionEvent) {
        openAdminLogInForm();
    }

    public void handleProfessor(ActionEvent actionEvent) {
        openProfLogInForm();
    }

    public void handleStudent(ActionEvent actionEvent) {
        openStudentLogInForm();
    }

    public void handleExit(ActionEvent actionEvent) {
        openExitConfirmationWindow();
    }


    public void openExitConfirmationWindow() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/confirmations/ExitConfirmView.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Iesire");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            ExitConfirmController exitConfirmController = loader.getController();
            exitConfirmController.setService(this, dialogStage);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    public void openStudentLogInForm() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login/LoginFormStudent.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Log In Student");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LoginFormStudentController loginFormStudentController = loader.getController();
            loginFormStudentController.setService(masterService, dialogStage);


            this.dialogStage.close();
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void openProfLogInForm() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login/LoginFormProfessor.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Log In Profesor");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LoginFormProfessorController loginFormProfessorController = loader.getController();
            loginFormProfessorController.setService(masterService,dialogStage);


            this.dialogStage.close();
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void openAdminLogInForm() {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login/LoginFormAdmin.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Log In Student");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LoginFormAdminController loginFormAdminController = loader.getController();
            loginFormAdminController.setService(masterService, dialogStage);


            this.dialogStage.close();
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
