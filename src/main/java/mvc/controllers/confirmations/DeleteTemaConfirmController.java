package mvc.controllers.confirmations;

import domain.Tema;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import mvc.controllers.AdminAccountController;

public class DeleteTemaConfirmController {
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

    public void handleConfirmDelete(ActionEvent actionEvent) {
        this.dialogStage.close();
        this.ctrl.deleteTemaForReal(toBeDeleted);
    }

    public void handleInfirmDelete(ActionEvent actionEvent) {
        this.dialogStage.close();
    }
}
