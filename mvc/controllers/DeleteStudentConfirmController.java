package mvc.controllers;

import domain.StudentDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class DeleteStudentConfirmController {

    private AdminAccountController ctrl;
    private Stage dialogStage;
    private StudentDTO toBeDeleted;

    public void setService(AdminAccountController ctrl, Stage stage, StudentDTO toBeDeleted) {
        this.ctrl = ctrl;
        this.dialogStage = stage;
        this.toBeDeleted = toBeDeleted;
    }

    @FXML
    public void initialize(){
    }

    public void handleConfirmDelete(ActionEvent actionEvent) {
        this.ctrl.deleteStudentForReal(toBeDeleted);
        this.dialogStage.close();
    }

    public void handleInfirmDelete(ActionEvent actionEvent) {
        this.dialogStage.close();
    }
}
