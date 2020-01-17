package mvc.controllers.confirmations;

import domain.Profesor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import mvc.controllers.AdminAccountController;

public class DeleteProfesorConfirmController {

    private AdminAccountController ctrl;
    private Stage dialogStage;
    private Profesor toBeDeleted;

    public void setService(AdminAccountController ctrl, Stage stage, Profesor toBeDeleted) {
        this.ctrl = ctrl;
        this.dialogStage = stage;
        this.toBeDeleted = toBeDeleted;
    }

    @FXML
    public void initialize(){
    }

    public void handleConfirmDelete(ActionEvent actionEvent) {
        this.dialogStage.close();
        this.ctrl.deleteProfForReal(toBeDeleted);
    }

    public void handleInfirmDelete(ActionEvent actionEvent) {
        this.dialogStage.close();
    }
}
