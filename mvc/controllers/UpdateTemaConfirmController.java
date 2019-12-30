package mvc.controllers;

import domain.Tema;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class UpdateTemaConfirmController {
    private AdminAccountController ctrl;
    private Stage dialogStage;
    private Tema toBeDeleted;

    public void setService(AdminAccountController ctrl, Stage stage, Tema toBeDeleted) {
        this.ctrl = ctrl;
        this.dialogStage = stage;
        this.toBeDeleted = toBeDeleted;
    }

    @FXML
    public void initialize(){
    }

    public void handleConfirmUpdate(ActionEvent actionEvent) {
        this.ctrl.updateTemaForReal(toBeDeleted);
        this.dialogStage.close();
    }

    public void handleInfirmUpdate(ActionEvent actionEvent) {
        this.dialogStage.close();
    }
}
