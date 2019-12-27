package mvc;

import domain.NotaDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.MasterService;


public class GradeConfirmController {

    @FXML
    TextField textConfirmID;
    @FXML
    TextField textConfirmData;
    @FXML
    TextField textConfirmFeedback;
    @FXML
    TextField textConfirmProfesor;
    @FXML
    TextField textConfirmValoare;

    private GradeEditController ctrl;
    private MasterService service;
    private Stage dialogStage;
    private NotaDTO nota;


    @FXML
    private void initialize() {
    }

    public void setService(GradeEditController ctrl, MasterService service, Stage stage, NotaDTO n) {
        this.service = service;
        this.dialogStage = stage;
        this.nota = n;
        this.ctrl = ctrl;

        this.textConfirmID.setText((n.getIdNota()));
        this.textConfirmData.setText(n.getDataNota());
        this.textConfirmFeedback.setText(n.getFeedback());
        this.textConfirmProfesor.setText(n.getProfesor());
        this.textConfirmValoare.setText(Integer.toString(n.getValoare()));
    }

    public void handleConfirm(ActionEvent actionEvent) {
        this.service.addNota(nota.getN());
        this.dialogStage.close();
        this.ctrl.getDialogStage().close();//TODO: okay..?
        GradeAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare nota","Nota a fost salvata");
    }

    public void handleDeny(ActionEvent actionEvent) {
        this.dialogStage.close();
        GradeAlert.showMessage(null, Alert.AlertType.INFORMATION,"Salvare nota","Nota a fost anulata");
    }
}
