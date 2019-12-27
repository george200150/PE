package mvc;

import domain.Tema;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.TemaService;
import utils.Constants;
import validators.ValidationException;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskEditController {
    @FXML
    private TextField textFieldId;
    @FXML
    private TextField textFieldNume;
    @FXML
    private TextField textFieldDescriere;
    @FXML
    private DatePicker datePickerStart;
    @FXML
    private DatePicker datePickerDeadline;

    private TemaService service;
    private Stage dialogStage;
    private Tema task;

    public void setService(TemaService service, Stage stage, Tema t) {
        this.service = service;
        this.dialogStage = stage;
        this.task = t;
        if (null != t) {
            setFields(t);
            textFieldId.setEditable(false);
        }
        else{
            setFieldsAdd();
        }
    }


    private void setFieldsAdd() {

        datePickerStart.setValue(LocalDate.now()); // set the task start date to TODAY.
        datePickerDeadline.setValue(LocalDate.now().plusWeeks(1));// set the effective work time to one week.

        LocalDateTime now = LocalDateTime.now();
        String stringNow = now.format(Constants.DATE_TIME_FORMATTER);

        String semStart = Constants.startSemester;
        String semEnd = Constants.endSemester;
        String vacStart = Constants.beginHolyday;
        String vacEnd = Constants.endHolyday;
        int start = Constants.getWeek(semStart);
        start -= 1;//because mathematics tell us that the number of weeks between j and i equals i - j + 1

        if (Constants.firstDateIsGreaterThanSecondDate(semStart, stringNow)) {//semester not started yet
            //EXCEPTION : SEMESTER NOT STARTED YET
            textFieldNume.setPromptText("semester not started");
        } else {//semester started
            if (Constants.firstDateIsGreaterThanSecondDate(vacStart, stringNow)) {//holiday not started yet
                int date = Constants.getWeek(LocalDateTime.now());
                date -= start;
                textFieldNume.setText("Tema " + date);
                textFieldId.setText(Integer.toString(date));
            } else {//now is after holiday start
                if (Constants.firstDateIsGreaterThanSecondDate(vacEnd, stringNow)) {//now is in holiday
                    int date = Constants.getWeek(vacStart);
                    date += 1;//first task after holiday has the name counter equals to the last task name counter before holiday + 1
                    date -= start;
                    textFieldNume.setText("Tema " + date);
                    textFieldId.setText(Integer.toString(date));
                } else {//holiday finished
                    if (Constants.firstDateIsGreaterThanSecondDate(semEnd, stringNow)) {//semester ended
                        //EXCEPTION : SEMESTER FINISHED ALREADY
                        textFieldNume.setPromptText("semester finished");
                    } else {//in semester after holiday
                        int date = Constants.getWeek(vacStart);
                        int dif = Constants.getWeek(vacEnd) - Constants.getWeek(vacStart) + 1;//get the duration of holiday in weeks
                        date -= dif;//subtract the holiday length from the current week to get the current task name counter
                        date -= start;
                        textFieldNume.setText("Tema " + date);
                        textFieldId.setText(Integer.toString(date));
                    }
                }
            }
        }
    }

    @FXML
    public void handleSave() {
        String id = textFieldId.getText();
        String nume = textFieldNume.getText();
        String descriere = textFieldDescriere.getText();
        String start = datePickerStart.getValue().format(Constants.DATE_TIME_FORMATTER);//TODO : BIG REFACTOR
        String deadline = datePickerDeadline.getValue().format(Constants.DATE_TIME_FORMATTER);//TODO : BIG REFACTOR
        Tema s = new Tema(id, nume, descriere, start, deadline);
        if (null == this.task)
            saveTask(s);
        else
            updateTask(s);
    }

    private void updateTask(Tema t)
    {
        try {
            Tema r = this.service.update(t);
            if (r==null)
                TaskAlert.showMessage(null, Alert.AlertType.INFORMATION,"Modificare tema","Tema a fost modificata");
        } catch (ValidationException e) {
            TaskAlert.showErrorMessage(null,e.getMessage());
        }
        dialogStage.close();
    }

    private void saveTask(Tema t) {
        try {
            Tema r = this.service.add(t);
            if (r == null)
                dialogStage.close();
            TaskAlert.showMessage(null, Alert.AlertType.INFORMATION, "Salvare tema", "Tema a fost salvata");
        } catch (ValidationException e) {
            TaskAlert.showErrorMessage(null, e.getMessage());
        }
    }

    private void setFields(Tema t)
    {
        textFieldId.setText(t.getId());
        textFieldNume.setText(t.getNume());
        textFieldDescriere.setText(t.getDescriere());
        LocalDate startDate = LocalDate.parse(t.getStartWeek(), Constants.DATE_TIME_FORMATTER);
        datePickerStart.setValue(startDate);
        LocalDate endDate = LocalDate.parse(t.getDeadlineWeek(), Constants.DATE_TIME_FORMATTER);
        datePickerDeadline.setValue(endDate);
    }

    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}
