package mvc.controllers;

import domain.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class UpdateStudentConfirmController {

    private AdminAccountController ctrl;
    private Stage dialogStage;
    private Student toBeDeleted;

    public void setService(AdminAccountController ctrl, Stage stage, Student toBeDeleted) {
        this.ctrl = ctrl;
        this.dialogStage = stage;
        this.toBeDeleted = toBeDeleted;
    }

    @FXML
    public void initialize(){
    }

    public void handleConfirmUpdate(ActionEvent actionEvent) {
        this.ctrl.updateStudentForReal(toBeDeleted);
        this.dialogStage.close();
    }

    public void handleInfirmUpdate(ActionEvent actionEvent) {
        this.dialogStage.close();
    }
}
