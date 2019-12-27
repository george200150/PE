package mvc;

import domain.Nota;
import domain.NotaDTO;
import domain.Student;
import domain.Tema;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.MasterService;
import utils.Constants;
import validators.ValidationException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GradeEditController {


    @FXML
    private ComboBox<Tema> comboBoxTema;
    @FXML
    private DatePicker datePickerData;
    @FXML
    private TextField textFieldProfesor;
    @FXML
    private TextField textFieldValoare;


    @FXML
    private ComboBox<Student> comboBoxStudent;

    @FXML
    private TextField textFieldFeedback;
    //private TextArea textAreaFeedback;

    @FXML
    private CheckBox checkboxDelayStudent;

    @FXML
    DatePicker datePickerDataStart;
    @FXML
    DatePicker datePickerDataStop;


    private MasterService service;
    private Stage dialogStage;
    private NotaDTO nota;

    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private ObservableList<Tema> temeList = FXCollections.observableArrayList();
    //private ObservableList<Integer> modelGrade = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
    }

    public Stage getDialogStage(){
        return this.dialogStage;
    }


    private List<Student> getStudents(){//TODO: set collection of students to combobox (on the fly updates)
        Iterable<Student> students = service.getAllStudent();
        return StreamSupport.stream(students.spliterator(), false)
                .collect(Collectors.toList());
    }
    private List<Tema> getTeme(){//TODO: set collection of tasks to combobox (on the fly updates)
        Iterable<Tema> teme = service.getAllTema();
        return StreamSupport.stream(teme.spliterator(), false)
                .collect(Collectors.toList());
    }



    private List<NotaDTO> getNotaDTOList() {//TODO: extract in service?
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());
        return gradeList.stream()
                .map(n -> {
                    String[] parts = n.getId().split(":");
                    Student s = service.findByIdStudent(parts[0]);
                    Tema t = service.findByIdTema(parts[1]);
                    return new NotaDTO(n,t,s);
                })
                .collect(Collectors.toList());
    }


    /*private Set<Integer> getGroups() {
        Iterable<Student> students1 = service.getAllStudent();

        List<Student> students = StreamSupport.stream(students1.spliterator(), false)
                .collect(Collectors.toList());

        //return students.stream().collect(Collectors.groupingBy
        //        (x->x.getGrupa())).keySet();
        return students.stream()
                .map(x -> x.getGrupa())
                .collect(Collectors.toSet());
    }*/

    /**
     * We consider the intersection between the [task.deadline+1 , task.deadline+3) , which is the maximum delay allowed for a task;
     * in the previous interval, the grade is considered submittable, but there will be a penalty when turning it in.
     * Then, we consider the interval in which the student was gratified from the penalty, which could be effective for
     * all the interval, for half of it, or for none of it.
     * So we get the common weeks that are both penalized for the task and gratified by the student's motivations.
     * @return - value of effectively ignored delay (TODO: a better measure with using days rather than weeks ???)
     */
    private int computeEffectiveRangeOfDelayMotvation(Tema t){
        LocalDate dead = LocalDate.parse(t.getDeadlineWeek(),Constants.DATE_TIME_FORMATTER);
        LocalDate startPenalty = dead.plusWeeks(1);
        LocalDate endPenalty = dead.plusWeeks(3);

        LocalDate startMotivated = this.datePickerDataStart.getValue();
        LocalDate endMotivated = this.datePickerDataStop.getValue();

        LocalDate startEffective;
        LocalDate endEffective;

        if(startPenalty.compareTo(startMotivated) >= 0)//get the maximum date from start dates
            startEffective = startPenalty;
        else
            startEffective = startMotivated;

        if(endPenalty.compareTo(endMotivated) >= 0)//get the minimum date from end dates
            endEffective = endMotivated;
        else
            endEffective = endPenalty;

        int startWeek = Constants.getWeek(startEffective.format(Constants.DATE_TIME_FORMATTER));
        int endWeek = Constants.getWeek(endEffective.format(Constants.DATE_TIME_FORMATTER));

        return Math.max(0,(endWeek-startWeek));
    }


    private int computeMaxGrade(Tema t, String date){//TODO: change from week comparison to day comparison ??? maybe ???
        if(t == null){
            return -1;
        }

        int dif = 0;
        if (this.checkboxDelayStudent.isSelected() && this.datePickerDataStart.getValue() != null && this.datePickerDataStop.getValue() != null) {
            dif = computeEffectiveRangeOfDelayMotvation(t);
        }

        int deadline = Constants.getWeek(t.getDeadlineWeek());
        int week = Constants.getWeek(date);
        int grade = 10 - (week-deadline);
        grade = Math.min(10, grade+dif);

        if(grade == 10){
            this.textFieldFeedback.setText("NICIO OBSERVATIE");
        }
        else{
            this.textFieldFeedback.setText("NOTA A FOST DIMINUATA CU "+ dif + " PUNCTE DATORITA INTARZIERILOR");
        }
        return grade;
    }


    @FXML
    private void handleMotivation() {
        Tema t = comboBoxTema.getValue();
        int grade = this.computeMaxGrade(t, this.datePickerData.getValue().format(Constants.DATE_TIME_FORMATTER));
        if (t != null) {
            if (grade == 10) {
                this.textFieldFeedback.setText("NICIO OBSERVATIE");
                this.textFieldValoare.setText(Integer.toString(grade));
            } else {
                this.textFieldFeedback.setText("NOTA A FOST DIMINUATA CU " + (10 - grade) + " PUNCTE DATORITA INTARZIERILOR");
                this.textFieldValoare.setText(Integer.toString(grade));
            }
        }
        else{
            this.textFieldValoare.setText("");
            this.textFieldFeedback.setText("");
        }
    }


    /**
     * We suppose that the deadline is not met. The submission is overdue.
     * Therefore, we subtract two weeks from the "date" and check if the
     * deadline is met in those conditions. If it is, the date is still in
     * the additional 2 weeks period when we can still submit the task.
     * If not, then the assignment is forever and ever overdue, resulting
     * in a grade of 1 for it.
     * @param t - task - we use it to get the deadline date
     * @param date - the date we input to be compared with the deadline
     * @return true if the task is still submittable for the input date and false if not.
     */
    private boolean isSubmittable(Tema t, String date) {
        String deadline = t.getDeadlineWeek();
        LocalDate date1 = LocalDate.parse(deadline, Constants.DATE_TIME_FORMATTER);
        LocalDate date2 = LocalDate.parse(date, Constants.DATE_TIME_FORMATTER);

        int dif = 0;
        if (this.checkboxDelayStudent.isSelected() && this.datePickerDataStart.getValue() != null && this.datePickerDataStop.getValue() != null) {
            int start = Constants.getWeek(this.datePickerDataStart.getValue().format(Constants.DATE_TIME_FORMATTER));
            int stop = Constants.getWeek(this.datePickerDataStop.getValue().format(Constants.DATE_TIME_FORMATTER));
            dif = Math.max(0, Math.abs(stop - start));
            //TODO: rethink this : submitting interval should be [task.start , motivation.end] IF MOTIVATION.START < TASK.END (continuous interval)
        }
        date2 = date2.minusWeeks(2);
        date2 = date2.minusWeeks(dif);
        return date1.compareTo(date2) >= 0;
    }

    /**
     * TREBUIE SA SETAM NOTELE LA CARE NU ARE DEJA NOTA IN COMBO BOX PENTRU STUDENTUL ALES
     */
    @FXML
    public void handleSetStudent(){
        //if(comboBoxTema.getValue() == null && comboBoxStudent.getValue() != null){
        //TODO: ar trebui sa am in memorie [studemtul + lista de allTasks] - eventual hash-uite, ca sa nu ocupe 5 hectare de memorie..
        //TODO: si sa compar valorile de tip PREV -cached- cu cele obtinute din functie. Daca cele doua sunt egale, nu mai schimb nimic.
        //totusi, pare destul de ineficient, ca mai intra o data in functie si face toate operatiile din nou ... !!! ...
        if(comboBoxTema.getValue() == null && comboBoxStudent.getValue() != null){

            Tema t = this.comboBoxTema.getValue();

            Set<Tema> studentsGradedTasks = getNotaDTOList().stream()
                    .filter(x -> x.getIdStudent().equals(comboBoxStudent.getValue().getId()))//toate notele primite de studentul "s"
                    .map(NotaDTO::getT)//toate temele predate de studentul "s"
                    .collect(Collectors.toSet());

            List<Tema> allTasks = getTeme();

            allTasks.removeAll(studentsGradedTasks);
            //nevermind...//allTasks.add(new Tema("-1","null","null",Constants.startSemester,Constants.endSemester));

            this.comboBoxTema.getItems().setAll(allTasks);

            /*if(t != null){
                if(allTasks.contains(t)){
                    this.comboBoxTema.setValue(t);
                }
            }*/

        }
    }


    /**
     * TREBUIE SA SETAM STUDENTII CARE NU AU DEJA NOTA IN COMBO BOX PENTRU TEMA ALEASA
     * TODO: ALSO CHANGE THE MAXIMUM GRADE WHEN DELAYED.
     * TODO: DO NOT SHOW TASKS THAT CAN NO LONGER BE SUBMITTED (3+ WEEKS DELAY) - separate function
     */
    @FXML
    public void handleSetTask() {
        if (this.comboBoxStudent.getValue() == null && comboBoxTema.getValue() != null) {

            //Student st = this.comboBoxStudent.getValue();


            Set<Student> tasksAcomplishedByStudents = getNotaDTOList().stream()
                    .filter(x -> x.getIdTema().equals(comboBoxTema.getValue().getId()))//toti studentii care au nota la tema "t"
                    .map(NotaDTO::getS)//toti studentii care au predat tema "t"
                    .collect(Collectors.toSet());

            List<Student> allStudents = getStudents();

            allStudents.removeAll(tasksAcomplishedByStudents);

            this.comboBoxStudent.getItems().setAll(allStudents);
            this.textFieldValoare.setText(Integer.toString(computeMaxGrade(comboBoxTema.getValue(), datePickerData.getValue().format(Constants.DATE_TIME_FORMATTER))));

            int grade = Integer.parseInt(this.textFieldValoare.getText());
            if (grade != 10) {
                int dif = 10 - grade;

                //this.textAreaFeedback.setText("“NOTA A FOST DIMINUATĂ CU "+ dif + "\n" + "PUNCTE DATORITĂ ÎNTÂRZIERILOR");
                this.textFieldFeedback.setText("NOTA A FOST DIMINUATA CU " + dif + " PUNCTE DATORITA INTARZIERILOR");
            }


           /* if (st != null) {

                if (allStudents.contains(st)){
                    this.comboBoxStudent.setValue(st);
                }
            }*/
        }
    }

    @FXML
    public void handleDateChange(){

        List<Tema> teme = getTeme();
        temeList.setAll(teme);
        this.temeList.setAll(this.temeList.stream()
                .filter(t -> isSubmittable(t,datePickerData.getValue().format(Constants.DATE_TIME_FORMATTER)))
                .collect(Collectors.toList()));
        comboBoxTema.getItems().setAll(temeList);

        //this.textFieldValoare.setText(Integer.toString(computeMaxGrade(comboBoxTema.getValue(),datePickerData.getValue().format(Constants.DATE_TIME_FORMATTER))));
    }

    public void setService(MasterService service, Stage stage, NotaDTO n) {
        this.service = service;
        this.dialogStage=stage;
        this.nota=n;
        if (null != n) {//update
            setFields(n);
            comboBoxStudent.setEditable(false);
            comboBoxTema.setEditable(false);
            textFieldFeedback.setText(n.getFeedback());
        }
        else{//add //TODO: add more automatic features
            datePickerData.setValue(LocalDate.now());
            comboBoxTema.setValue(service.findByIdTema(Integer.toString(Constants.sem1.getSemesterWeek(LocalDateTime.now()))));
            textFieldFeedback.setText("NICIO OBSERVATIE");
            //TODO: poate nu trebuie asta //textFieldValoare.setText("10");
        }

        List<Student> students = getStudents();
        studentList.setAll(students);
        comboBoxStudent.getItems().setAll(studentList);

        List<Tema> teme = getTeme();
        temeList.setAll(teme);
        this.temeList.setAll(this.temeList.stream()
                .filter(t -> isSubmittable(t,datePickerData.getValue().format(Constants.DATE_TIME_FORMATTER)))
                .collect(Collectors.toList()));
        comboBoxTema.getItems().setAll(temeList);



        //int start = Constants.getWeek(this.datePickerDataStart.getValue());
        //int stop = Constants.getWeek(this.datePickerDataStop.getValue());

        /*Integer[] integers = new Integer[15];
        for (int i = start; i <= stop ; i++) {
            integers[i-start]=i;
        }*/
//TODO: check bounds for delayed motivation
//TODO: check grade computation process



        /*this.comboBoxTema.setOnKeyPressed(keyEvent -> {TODO: do sometheing to empty the
            if(keyEvent.getCode().equals(KeyCode.BACK_SPACE)){
                this.comboBoxTema.setValue(null);
            }
        });

        this.comboBoxStudent.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode().equals(KeyCode.BACK_SPACE)){
                this.comboBoxStudent.setValue(null);
            }
        });*/



    }


    @FXML
    public void handleSave() {
        if (comboBoxStudent.getValue() == null || comboBoxTema.getValue() == null || datePickerData.getValue() == null || textFieldProfesor.getText().equals("") || textFieldFeedback.getText() == null){
            GradeAlert.showMessage(null, Alert.AlertType.WARNING, "Editare nota", "Nota nu are completate toate campurile");
        }
        else{
            String ids = comboBoxStudent.getValue().getId();
            String idt = comboBoxTema.getValue().getId();
            String data = datePickerData.getValue().format(Constants.DATE_TIME_FORMATTER);
            String profesor = textFieldProfesor.getText();
            String valoare = textFieldValoare.getText();
            //String feedback = textAreaFeedback.getText();
            String feedback = textFieldFeedback.getText();

            String idTotal = ids + ":" + idt;
            Nota n = new Nota(idTotal, Integer.parseInt(valoare), profesor, data, feedback);
            if (null == this.nota)
                saveGrade(n);
            else
                updateGrade(n);
        }

    }


    private void updateGrade(Nota n) {
        try {
            Nota r = this.service.updateNota(n);
            if (r == null)
                GradeAlert.showMessage(null, Alert.AlertType.INFORMATION, "Modificare nota", "Nota a fost modificata");
        } catch (ValidationException e) {
            GradeAlert.showErrorMessage(null, e.getMessage());
        }
        dialogStage.close();
    }



    public void showGradeConfirmDialog(NotaDTO grade) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/gradeconfirmview.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirm Grade");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            GradeConfirmController confirmGradeViewController = loader.getController();
            confirmGradeViewController.setService(this, service, dialogStage, grade);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private NotaDTO getDTOFromNota(Nota n){
        Student s = this.service.findByIdStudent(n.getId().split(":")[0]);
        Tema t = this.service.findByIdTema(n.getId().split(":")[1]);
        return new NotaDTO(n,t,s);
    }

    private void saveGrade(Nota n)
    {

        try {
            showGradeConfirmDialog(this.getDTOFromNota(n));//Nota r = this.service.addNota(n);

        } catch (ValidationException e) {
            GradeAlert.showErrorMessage(null,e.getMessage());
        }
    }


    private void setFields(NotaDTO n)
    {
        String idNota = n.getIdNota();
        String[] parts = idNota.split(":");
        String ids = parts[0];
        String idt = parts[1];
        comboBoxStudent.setValue(service.findByIdStudent(ids));
        comboBoxTema.setValue(service.findByIdTema(idt));
        datePickerData.setValue(LocalDate.parse(n.getDataNota(),Constants.DATE_TIME_FORMATTER));
        textFieldProfesor.setText(n.getProfesor());
        textFieldValoare.setText(Integer.toString(n.getValoare()));
    }


    @FXML
    public void handleCancel(){
        dialogStage.close();
    }
}


