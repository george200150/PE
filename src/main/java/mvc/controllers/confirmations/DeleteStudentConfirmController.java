package mvc.controllers.confirmations;

import domain.StudentDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import mvc.controllers.AdminAccountController;

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
        this.dialogStage.close();
        this.ctrl.deleteStudentForReal(toBeDeleted);
    }

    public void handleInfirmDelete(ActionEvent actionEvent) {
        this.dialogStage.close();
    }
}
