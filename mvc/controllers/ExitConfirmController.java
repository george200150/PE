package mvc.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ExitConfirmController {

    private LoginChoiceController ctrl;
    private Stage dialogStage;


    @FXML
    private void initialize() {
    }



    public void setService(LoginChoiceController ctrl, Stage dialogStage){
        this.ctrl = ctrl;
        this.dialogStage = dialogStage;
    }

    public void handleInfirmExit(ActionEvent actionEvent) {
        this.dialogStage.close();
    }

    public void handleConfirmExit(ActionEvent actionEvent) {
        this.dialogStage.close();
        this.ctrl.getDialogStage().close();
    }
}
