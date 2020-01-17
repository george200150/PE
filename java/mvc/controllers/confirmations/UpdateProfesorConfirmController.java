package mvc.controllers.confirmations;

import domain.Profesor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import mvc.controllers.AdminAccountController;

public class UpdateProfesorConfirmController {
    private AdminAccountController ctrl;
    private Stage dialogStage;
    private Profesor toBeUpdated;

    public void setService(AdminAccountController ctrl, Stage stage, Profesor toBeUpdated) {
        this.ctrl = ctrl;
        this.dialogStage = stage;
        this.toBeUpdated = toBeUpdated;
    }

    @FXML
    public void initialize(){
    }

    public void handleConfirmUpdate(ActionEvent actionEvent) {
        this.dialogStage.close();
        this.ctrl.updateProfesorForReal(toBeUpdated);
    }

    public void handleInfirmUpdate(ActionEvent actionEvent) {
        this.dialogStage.close();
    }
}
