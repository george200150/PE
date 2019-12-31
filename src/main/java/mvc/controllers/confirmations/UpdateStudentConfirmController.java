package mvc.controllers.confirmations;

import domain.Student;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import mvc.controllers.AdminAccountController;

public class UpdateStudentConfirmController {

    private AdminAccountController ctrl;
    private Stage dialogStage;
    private Student toBeUpdated;

    public void setService(AdminAccountController ctrl, Stage stage, Student toBeUpdated) {
        this.ctrl = ctrl;
        this.dialogStage = stage;
        this.toBeUpdated = toBeUpdated;
    }

    @FXML
    public void initialize(){
    }

    public void handleConfirmUpdate(ActionEvent actionEvent) {
        this.dialogStage.close();
        this.ctrl.updateStudentForReal(toBeUpdated);
    }

    public void handleInfirmUpdate(ActionEvent actionEvent) {
        this.dialogStage.close();
    }
}
