package mvc.controllers;

import domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mvc.StudentAlert;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import services.MasterService;
import utils.Constants;
import utils.events.GradeChangeEvent;
import utils.observer.GradeObserver;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ProfessorAccountController implements GradeObserver<GradeChangeEvent> {


    public TableView<Student> tableStudent;
    public TableColumn<Student, String> columnStudentNume;
    public TableColumn<Student, String> columnStudentPrenume;
    public TableColumn<Student, String> columnStudentGrupa;
    public TableColumn<Student, String> columnStudentEmail;
    public TextField textStudentNume;
    public TextField textStudentPrenume;
    public TextField textStudentGrupa;
    public TextField textStudentEmail;


    public TableView<Tema> tableTema;
    public TableColumn<Tema, String> tableTemaNume;
    public TableColumn<Tema, String> tableTemaDescriere;
    public TableColumn<Tema, String> tableTemaInceput;
    public TableColumn<Tema, String> tableTemaSfarsit;
    public TextField dateTemaSfarsit;
    public TextField dateTemaInceput;
    public TextField textTemaNume;
    public TextField textTemaDescriere;

    public TableView<NotaDTO> tableNota;
    public TableColumn<NotaDTO, String> columnNotaId;
    public TableColumn<NotaDTO, String> columnNotaStudent;
    public TableColumn<NotaDTO, String> columnNotaTema;
    public TableColumn<NotaDTO, String> columnNotaData;
    public TableColumn<NotaDTO, String> columnNotaProfesor;
    public TableColumn<NotaDTO, String> columnNotaValoare;
    public TableColumn<NotaDTO, String> columnNotaFeedback;
    public TextField textNotaId;
    public TextField textNotaStudent;
    public TextField textNotaTema;
    public TextField textNotaFeedback;
    public TextField textNotaValoare;
    public TextField textNotaProf;
    public DatePicker dateNotaData;

    public PasswordField textOldPassword;
    public PasswordField textNewPassword;

    public Label labelProfesor;

    public CheckBox checkboxInvert;

    public PieChart classesPieChart;
    public PieChart itemsPieChart;
    public PieChart itemsPieChart2;
    public PieChart itemsPieChart3;
    public PieChart itemsPieChart4;
    public BarChart histo1;
    public BarChart histo2;
    public BarChart histo3;
    public BarChart histo4;

    public ToggleButton toggleCharts;
    public AnchorPane anchor;

    private void initializeClassesPieChart()
    {
        List<PieChart.Data> dataList = new ArrayList<>();
        StreamSupport.stream(service.getAllStudent().spliterator(),false)
                .filter(x -> x.getCadruDidacticIndrumatorLab().equals(this.loggedInProfessor.toString()))
                .collect(Collectors.groupingBy(Student::getGrupa, Collectors.counting()))
                .forEach((x, y) -> dataList.add(new PieChart.Data(x.toString(), y)));
        classesPieChart.setData(FXCollections.observableArrayList(dataList));
        classesPieChart.setLabelLineLength(10);
        classesPieChart.setLegendSide(Side.TOP);

    }

    private void pieChartRaport1() {
        List<PieChart.Data> dataList = new ArrayList<>();
        Map<Student, Double> l = this.service.getMediiStudenti(this.loggedInProfessor);
        l.forEach((x, y) -> dataList.add(new PieChart.Data(x.toString(), y)));
        itemsPieChart.setTitle("raport1");
        itemsPieChart.setData(FXCollections.observableArrayList(dataList));
        itemsPieChart.setLabelLineLength(10);
        itemsPieChart.setLegendSide(Side.BOTTOM);
    }

    private void pieChartRaport2() {
        List<PieChart.Data> dataList = new ArrayList<>();
        Map<Tema, Double> l = this.service.getTemeGrele(this.loggedInProfessor);
        l.forEach((x, y) -> dataList.add(new PieChart.Data(x.toString(), y)));
        itemsPieChart2.setTitle("raport2");
        itemsPieChart2.setData(FXCollections.observableArrayList(dataList));
        itemsPieChart2.setLabelLineLength(10);
        itemsPieChart2.setLegendSide(Side.BOTTOM);
    }

    private void pieChartRaport3() {
        List<PieChart.Data> dataList = new ArrayList<>();
        Map<String, Integer> l = this.service.getStudentiDupaStatusExamen(this.loggedInProfessor);
        l.forEach((x, y) -> dataList.add(new PieChart.Data(x, y)));
        itemsPieChart3.setTitle("raport3");
        itemsPieChart3.setData(FXCollections.observableArrayList(dataList));
        itemsPieChart3.setLabelLineLength(10);
        itemsPieChart3.setLegendSide(Side.BOTTOM);
    }

    private void pieChartRaport4() {
        List<PieChart.Data> dataList = new ArrayList<>();
        Map<String, Integer> l = this.service.getStudentiPrompti(this.loggedInProfessor);
        l.forEach((x, y) -> dataList.add(new PieChart.Data(x, y)));
        itemsPieChart4.setTitle("raport4");
        itemsPieChart4.setData(FXCollections.observableArrayList(dataList));
        itemsPieChart4.setLabelLineLength(10);
        itemsPieChart4.setLegendSide(Side.BOTTOM);
    }

    private void histoChartRaport1() {

        List<WeightedAVGDTO> allStudentGrades = this.service.raport1Student(this.loggedInProfessor);
        List<String> names = allStudentGrades.stream().map(x ->x.getS().toString()).collect(Collectors.toList());
        List<Double> values = allStudentGrades.stream().map(x ->x.getAvg()).collect(Collectors.toList());
        //Defining the axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>
                observableArrayList(names));
        xAxis.setLabel("categorie");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("numar");
        //Creating the Bar chart
        histo1.setTitle("raport1");
        //Prepare XYChart.Series objects by setting data
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Studentii dumneavoastra");

        for (int i = 0; i < names.size(); i++) {
            series1.getData().add(new XYChart.Data<>(names.get(i), values.get(i)));
        }
        //Setting the data to bar chart
        histo1.getData().addAll(series1);
    }

    private void histoChartRaport2() {
        //Defining the axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>
                observableArrayList(Arrays.asList("0-4", "5-7", "8-10")));
        xAxis.setLabel("nota");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("numar");
        //Creating the Bar chart
        histo2.setTitle("raport2");
        long count04 = (long) this.service.getTemeGrele(this.loggedInProfessor).entrySet().stream().filter(x -> x.getValue() <= 4).count();
        long count57 = (long) this.service.getTemeGrele(this.loggedInProfessor).entrySet().stream().filter(x -> x.getValue() > 4 && x.getValue() <= 7).count();
        long count810 = (long) this.service.getTemeGrele(this.loggedInProfessor).entrySet().stream().filter(x -> x.getValue() > 7).count();


        //Prepare XYChart.Series objects by setting data
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Temele existente");
        series1.getData().add(new XYChart.Data<>("0-4", count04));
        series1.getData().add(new XYChart.Data<>("5-7", count57));
        series1.getData().add(new XYChart.Data<>("8-10", count810));
        //Setting the data to bar chart
        histo2.getData().addAll(series1);
    }

    private void histoChartRaport3() {
        //Defining the axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>
                observableArrayList(Arrays.asList("Promovat", "Corigent")));
        xAxis.setLabel("categorie");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("numar");
        //Creating the Bar chart
        histo3.setTitle("raport3");
        long countOk = (long) this.service.raport3(this.loggedInProfessor).size();
        long countALL = StreamSupport.stream(this.service.getAllStudent().spliterator(), false).filter(x -> x.getCadruDidacticIndrumatorLab().equals(this.loggedInProfessor.toString())).count();
        long countNOT = countALL - countOk;
        //Prepare XYChart.Series objects by setting data
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Studentii dumneavoastra");
        series1.getData().add(new XYChart.Data<>("Promovat", countOk));
        series1.getData().add(new XYChart.Data<>("Corigent", countNOT));
        //Setting the data to bar chart
        histo3.getData().addAll(series1);
    }

    private void histoChartRaport4() {// Studentii care au predat la timp toate temele
        //Defining the axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(FXCollections.<String>
                observableArrayList(Arrays.asList("La Timp", "Intarziat")));
        xAxis.setLabel("categorie");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("numar");
        //Creating the Bar chart
        histo4.setTitle("raport4");
        long countOk = (long) this.service.raport4(this.loggedInProfessor).size();
        long countALL = StreamSupport.stream(this.service.getAllStudent().spliterator(), false).filter(x -> x.getCadruDidacticIndrumatorLab().equals(this.loggedInProfessor.toString())).count();
        long countNOT = countALL - countOk;
        //Prepare XYChart.Series objects by setting data
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("Studentii dumneavoastra");
        series1.getData().add(new XYChart.Data<>("La Timp", countOk));
        series1.getData().add(new XYChart.Data<>("Intarziat", countNOT));
        //Setting the data to bar chart
        histo4.getData().addAll(series1);
    }


    private MasterService service;
    private ObservableList<Student> modelS = FXCollections.observableArrayList();
    private ObservableList<Tema> modelT = FXCollections.observableArrayList();
    private ObservableList<NotaDTO> modelN = FXCollections.observableArrayList();
    private Stage dialogStage;
    private Profesor loggedInProfessor;


    public void setService(MasterService masterService, Stage stage, Profesor loggedInProfessor) {
        this.dialogStage = stage;
        this.loggedInProfessor = loggedInProfessor;
        service = masterService;
        service.addObserverGrade(this);
        //extend to all observer types in the future...
        initModelStudent();
        initModelTema();
        initModelGrade();
        this.labelProfesor.setText(this.loggedInProfessor.toString());
        this.textNotaProf.setText(this.loggedInProfessor.toString());
        this.textNotaTema.setText(getTemaCurenta(this.dateNotaData.getValue()));

        initializeClassesPieChart();

        //by default -> bar graphs
        histoChartRaport1();
        histoChartRaport2();
        histoChartRaport3();
        histoChartRaport4();

        pieChartRaport1();
        pieChartRaport2();
        pieChartRaport3();
        pieChartRaport4();
        itemsPieChart.setVisible(false);
        itemsPieChart2.setVisible(false);
        itemsPieChart3.setVisible(false);
        itemsPieChart4.setVisible(false);
    }


    private void initModelStudent() {
        Iterable<Student> students = service.getAllStudent();
        List<Student> studentList = StreamSupport
                .stream(students.spliterator(), false)
                .filter(st -> st.getCadruDidacticIndrumatorLab().equals(this.loggedInProfessor.getNume() + " " + this.loggedInProfessor.getPrenume()))
                .collect(Collectors.toList());
        modelS.setAll(studentList);
    }

    private void initModelTema() {
        Iterable<Tema> tasks = service.getAllTema();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());
        modelT.setAll(taskList);

    }

    private void initModelGrade() {
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .filter(gr -> gr.getProfesor().equals(this.loggedInProfessor.getNume() + " " + this.loggedInProfessor.getPrenume()))
                .collect(Collectors.toList());
        List<NotaDTO> gradeDTOList = convertGradeToDTO(gradeList);
        modelN.setAll(gradeDTOList);
    }


    private int getEffectiveRangeOfDelayMotvation(Student s, Tema t, String date) {//TODO: big workload here
        Iterable<Motivation> motivations = service.getAllMotivation();
        List<Motivation> motivationList = StreamSupport.stream(motivations.spliterator(), false).collect(Collectors.toList());
        List<Interval> intervals = motivationList.stream().filter(mot -> mot.getIdStudent().equals(s.getId())).map(x -> new Interval(x.getStart(), x.getEnd())).collect(Collectors.toList());

        LocalDate end = LocalDate.parse(t.getDeadlineWeek(), Constants.DATE_TIME_FORMATTER);
        LocalDate now = LocalDate.parse(date, Constants.DATE_TIME_FORMATTER);

        if (!Constants.compareDates(now, end)) {
            return 0;
        }//task is not overdue => no need for motivation

        int sumMotivated = 0;
        for (Interval interval : intervals) {//task is overdue - so it needs motivation
            //if period between task deadline and now has any common dates with a motivation interval (so that we can motivate the delay)
            if (Constants.intervalsHaveDatesInCommon(interval.getStart(), interval.getEnd(), end, now)) {
                sumMotivated += Constants.getIntervalsNumberOfCommonWeeks(interval.getStart(), interval.getEnd(), end, now);
            }
        }
        return sumMotivated;
    }

    private int computeEffectiveDelay(Tema t, String date) {
        LocalDate start = LocalDate.parse(t.getStartWeek(), Constants.DATE_TIME_FORMATTER);
        LocalDate end = LocalDate.parse(t.getDeadlineWeek(), Constants.DATE_TIME_FORMATTER);
        LocalDate now = LocalDate.parse(date, Constants.DATE_TIME_FORMATTER);

        if (Constants.intervalsHaveDatesInCommon(start, end, now, now)) {//task was submitted in time
            return 0;
        }

        int ws = Constants.getWeek(start);
        int we = Constants.getWeek(end);
        int wn = Constants.getWeek(now);

        if (start.getYear() == end.getYear() && end.getYear() == now.getYear()) {// S:2019 E:2019 N:2019 or S:2020 E:2020 N:2020 (trivial case)
            if (we > wn) {//last week of the year is tricky
                wn += 52;
            }
            return wn - we;
        }
        if (start.getYear() == end.getYear() && end.getYear() < now.getYear()) {// S:2019 E:2019 N:2020 (delayed over new year)
            wn += 52;
        }
        if (start.getYear() == end.getYear() && end.getYear() > now.getYear()) {// S:2020 E:2020 N:2019 (predata in avans)
            ws += 52;
            we += 52;
        }
        if (start.getYear() == now.getYear() && now.getYear() < end.getYear()) {// S:2019 E:2020 N:2019 (predata in avans)
            we += 52;
        }
        if (start.getYear() < end.getYear() && end.getYear() == now.getYear()) {// S: 2019 E:2020 N:2020 (work over the new year holiday)
            we += 52;
            wn += 52;
        }

        return wn - we;
    }


    private int computeEffectiveGradeConsideringAllCases(Student s, Tema t, String date) {
        int motivated = getEffectiveRangeOfDelayMotvation(s, t, date);
        int delay = computeEffectiveDelay(t, date);
        delay -= motivated;
        if (delay > 2) {
            return 0;
        }
        return Math.min(10, 10 - delay);
    }

    private int computeMaxGrade(Student s, Tema t, String date) {//TODO: handle date shift to semester (from holiday)
        int grade = computeEffectiveGradeConsideringAllCases(s, t, date);
        return grade;
    }

    private String computeFeedback(Student s, Tema t, String date) {//TODO: handle date shift to semester (from holiday)
        int grade = computeEffectiveGradeConsideringAllCases(s, t, date);
        if (grade <= 7) {
            return "TEMA NU MAI POATE FI PREDATA";
        }
        if (grade == 10) {
            return "NICIO OBSERVATIE";
        }
        return "NOTA A FOST DIMINUATA CU " + Integer.toString(10 - grade) + " PUNCTE DATORITA INTARZIERILOR";
    }

    private void initModelGradeINVERTED() {
        filterMergeSearchNota();
    }

    private String getTemaCurenta(LocalDate date) {
        String now = date.format(Constants.DATE_TIME_FORMATTER);
        List<Tema> temeCurente = StreamSupport
                .stream(this.service.getAllTema().spliterator(), false)
                .filter(t -> Constants.compareDates(t.getDeadlineWeek(), now) && Constants.compareDates(now, t.getStartWeek()))
                .collect(Collectors.toList());
        if (temeCurente.size() != 0)
            return temeCurente.get(0).toString();
        else {
            return "-";
        }
    }

    private void recomputeGrades() {
        if (this.tableNota.getSelectionModel().getSelectedItem() == null) {
            this.textNotaTema.setText(getTemaCurenta(this.dateNotaData.getValue()));
        }
        if (this.checkboxInvert.isSelected()) {
            initModelGradeINVERTED();
        }
    }


    private void filterMergeSearchStudent() {
        Predicate<Student> filtered = null;//TODO: asa compunem predicatele

        Iterable<Student> students = service.getAllStudent();
        List<Student> studentList = StreamSupport.stream(students.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Student> filterNume = x -> x.getNume().toLowerCase().contains(textStudentNume.getText().toLowerCase());
        Predicate<Student> filterPrenume = x -> x.getPrenume().toLowerCase().contains(textStudentPrenume.getText().toLowerCase());
        Predicate<Student> filterGrupa = x -> Integer.toString(x.getGrupa()).toLowerCase().contains(textStudentGrupa.getText().toLowerCase());
        Predicate<Student> filterEmail = x -> x.getEmail().toLowerCase().contains(textStudentEmail.getText().toLowerCase());

        if (!textStudentNume.getText().isEmpty()) {//TODO: CHANGE ALL PREDICATES WITH {IF TEXTFIELD IS EMPTY => RETURN TRUE; ELSE RETURN THE FILTER}
            if (filtered == null) {//TODO: HERE, WE DO NOT TEST IF FILTERED = NULL; WE INITIALIZE FILTERED VARIABLE AS UNION AND(ALL PREDICATES)
                filtered = filterNume;
            } else {
                filtered = filtered.and(filterNume);
            }
        }
        if (!textStudentPrenume.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterPrenume;
            } else {
                filtered = filtered.and(filterPrenume);
            }
        }
        if (!textStudentGrupa.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterGrupa;
            } else {
                filtered = filtered.and(filterGrupa);
            }
        }
        if (!textStudentEmail.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterEmail;
            } else {
                filtered = filtered.and(filterEmail);
            }
        }

        if (filtered == null) {
            modelS.setAll(studentList);
        } else {
            modelS.setAll(studentList.stream().filter(filtered).collect(Collectors.toList()));
        }
    }


    private void filterMergeSearchTema() {
        Predicate<Tema> filtered = null;

        Iterable<Tema> tasks = service.getAllTema();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Tema> filterNume = x -> x.getNume().toLowerCase().contains(textTemaNume.getText().toLowerCase());
        Predicate<Tema> filterDescriere = x -> x.getDescriere().toLowerCase().contains(textTemaDescriere.getText().toLowerCase());
        Predicate<Tema> filterStart = x -> x.getStartWeek().contains(dateTemaInceput.getText());
        Predicate<Tema> filterStop = x -> x.getDeadlineWeek().contains(dateTemaSfarsit.getText());

        if (!textTemaNume.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterNume;
            } else {
                filtered = filtered.and(filterNume);
            }
        }
        if (!textTemaDescriere.getText().isEmpty()) {
            if (filtered == null) {
                filtered = filterDescriere;
            } else {
                filtered = filtered.and(filterDescriere);
            }
        }
        if (!(dateTemaInceput.getText() == null)) {
            if (filtered == null) {
                filtered = filterStart;
            } else {
                filtered = filtered.and(filterStart);
            }
        }
        if (!(dateTemaSfarsit.getText() == null)) {
            if (filtered == null) {
                filtered = filterStop;
            } else {
                filtered = filtered.and(filterStop);
            }
        }

        if (filtered == null) {
            modelT.setAll(taskList);
        } else {
            modelT.setAll(taskList.stream().filter(filtered).collect(Collectors.toList()));
        }
    }


    @FXML
    public void initialize() {
        this.dateNotaData.setValue(LocalDate.now());
        this.toggleCharts.setSelected(true);
        //this.textSavePath.setText("C:\\Users\\George\\Desktop\\PDFsPE");

        columnStudentNume.setCellValueFactory(new PropertyValueFactory<Student, String>("nume"));
        columnStudentPrenume.setCellValueFactory(new PropertyValueFactory<Student, String>("prenume"));
        columnStudentGrupa.setCellValueFactory(new PropertyValueFactory<Student, String>("grupa"));
        columnStudentEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("email"));
        tableStudent.setItems(modelS);

        textStudentPrenume.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textStudentNume.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textStudentGrupa.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));
        textStudentEmail.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchStudent();
        }));


        //TODO: one day I should refactor "tableTema" into "columnTema"...
        tableTemaNume.setCellValueFactory(new PropertyValueFactory<Tema, String>("nume"));
        tableTemaDescriere.setCellValueFactory(new PropertyValueFactory<Tema, String>("descriere"));
        tableTemaInceput.setCellValueFactory(new PropertyValueFactory<Tema, String>("startWeek"));
        tableTemaSfarsit.setCellValueFactory(new PropertyValueFactory<Tema, String>("deadlineWeek"));
        tableTema.setItems(modelT);

        textTemaNume.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));
        textTemaDescriere.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));

        dateTemaInceput.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));
        dateTemaSfarsit.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchTema();
        }));


        columnNotaId.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("idNota"));
        columnNotaData.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("dataNota"));
        columnNotaProfesor.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("profesor"));
        columnNotaValoare.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("valoare"));
        columnNotaStudent.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("studentString"));
        columnNotaTema.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("temaString"));
        columnNotaFeedback.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("feedback"));
        tableNota.setItems(modelN);


        dateNotaData.valueProperty().addListener(((observableValue, s, t1) -> {
            recomputeGrades();
        }));


        textNotaProf.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchNota();
        }));
        textNotaValoare.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchNota();
        }));

        textNotaStudent.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchNota();
        }));
        textNotaTema.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearchNota();
        }));


    }


    private void filterMergeSearchNota() {
        List<NotaDTO> gradeDTOList = null;

        if (this.checkboxInvert.isSelected()) {
            List<Nota> grades = StreamSupport.stream(service.getAllNota().spliterator(), false).collect(Collectors.toList());
            List<Student> studenti = StreamSupport
                    .stream(service.getAllStudent().spliterator(), false)
                    .filter(x -> x.getCadruDidacticIndrumatorLab().equals(this.loggedInProfessor.toString()))
                    .collect(Collectors.toList());
            List<Tema> teme = StreamSupport.stream(service.getAllTema().spliterator(), false).collect(Collectors.toList());
            gradeDTOList = new ArrayList<>();
            for (Student student : studenti) {
                if (student.getCadruDidacticIndrumatorLab().equals(this.loggedInProfessor.toString()))
                    for (Tema tema : teme) {
                        String nid = student.getId() + ":" + tema.getId();
                        if (grades.stream().noneMatch(gr -> gr.getId().equals(nid))) {
                            int valoare = computeMaxGrade(student, tema, this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER));
                            String feedback = computeFeedback(student, tema, this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER));
                            Nota nota = new Nota(student.getId() + ":" + tema.getId(), valoare, this.loggedInProfessor.getNume() + " " + this.loggedInProfessor.getPrenume(), this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER), feedback);
                            gradeDTOList.add(new NotaDTO(nota, tema, student));
                        }//else we simply don't add
                    }
            }
        } else {
            Iterable<Nota> grades = service.getAllNota();
            List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                    .collect(Collectors.toList());
            gradeDTOList = convertGradeToDTO(gradeList);
        }

        Predicate<NotaDTO> filtered = null;// asa compunem predicatele

        Predicate<NotaDTO> filterProfesor = x -> {
            String text = textNotaProf.getText();
            if (text.isEmpty()) {
                return true;
            }
            return x.getProfesor().toLowerCase().contains(text.toLowerCase());
        };
        Predicate<NotaDTO> filterValoare = x -> {
            String text = textNotaValoare.getText();
            if (text.isEmpty()) {
                return true;
            }
            return Integer.toString(x.getValoare()).toLowerCase().contains(text.toLowerCase());
        };


        Predicate<NotaDTO> filterStudent = x -> {
            String text = textNotaStudent.getText();
            if (text.isEmpty()) {
                return true;
            }
            String studentStr = x.getS().getNume()
                    + " " + x.getS().getPrenume()
                    + " - " + x.getS().getGrupa()
                    + " - " + x.getS().getEmail();
            return studentStr.toLowerCase().contains(text.toLowerCase());
        };
        Predicate<NotaDTO> filterTask = x -> {
            String text = textNotaTema.getText();
            if (text.isEmpty()) {
                return true;
            }
            String taskStr = x.getT().getNume()
                    + " - " + x.getT().getDescriere()
                    + " " + x.getT().getStartWeek()
                    + " " + x.getT().getDeadlineWeek();
            return taskStr.toLowerCase().contains(text.toLowerCase());
        };

        filtered = filterProfesor.and(filterProfesor).and(filterValoare).and(filterStudent).and(filterTask);

        modelN.setAll(gradeDTOList.stream().filter(filtered).collect(Collectors.toList()));
    }


    private List<NotaDTO> convertGradeToDTO(List<Nota> gradeList) {
        return gradeList.stream()
                .map(n -> {
                    String[] parts = n.getId().split(":");
                    Student s = service.findByIdStudent(parts[0]);
                    Tema t = service.findByIdTema(parts[1]);
                    return new NotaDTO(n, t, s);
                })
                .collect(Collectors.toList());
    }


    @Override
    public void updateGrade(GradeChangeEvent gradeChangeEvent) {
        initModelGrade();
    }


    public void handleBackToLoginChoice(ActionEvent actionEvent) {

        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/login/LoginChoice.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Log In");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            LoginChoiceController loginChoiceController = loader.getController();
            loginChoiceController.setService(service, dialogStage);

            this.dialogStage.close();
            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleResetPassword(ActionEvent actionEvent) {

        String oldPsswd = this.textOldPassword.getText();
        String newPsswd = this.textNewPassword.getText();
        String email = this.loggedInProfessor.getEmail();//USERNAME IS THE EMAIL!!!*/
        boolean state = this.service.changeProfessorPassword(email, oldPsswd, newPsswd);
        if (state) {
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Resetare", "parola a fost actualizata!");
        } else {
            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Log In", "parola nu a putut fi actualizata!");
        }
    }

    public void handleClearFieldsStudent(ActionEvent actionEvent) {
        this.textStudentEmail.setText("");
        this.textStudentGrupa.setText("");
        this.textStudentNume.setText("");
        this.textStudentPrenume.setText("");
    }


    public void handleAddNota(ActionEvent actionEvent) {
        if (checkboxInvert.isSelected()) {
            if (!this.textNotaValoare.getText().isEmpty() && !this.textNotaProf.getText().isEmpty() && !this.textNotaFeedback.getText().isEmpty()) {
                Nota toBeAdded = new Nota(this.textNotaId.getText(), Integer.parseInt(this.textNotaValoare.getText()), this.textNotaProf.getText(), this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER), this.textNotaFeedback.getText());
                if (this.textNotaId.getText().equals("")) {
                    StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "nu s-a putut crea o nota!");
                } else {
                    Student s = this.service.findByIdStudent(toBeAdded.getId().split(":")[0]);
                    Tema t = this.service.findByIdTema(toBeAdded.getId().split(":")[1]);
                    int max = this.computeMaxGrade(s, t, this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER));
                    if (Integer.parseInt(this.textNotaValoare.getText()) > max) {
                        StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Nota nu poate depasi " + max + " !");
                    } else if (!Constants.compareDates(LocalDate.now(), this.dateNotaData.getValue())) {
                        StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Data nu poate fi din viitor!");
                    } else {
                        Nota rez = this.service.addNota(toBeAdded);
                        if (rez == null) {
                            List<Student> toBeEmailed = new ArrayList<>();
                            toBeEmailed.add(s);
                            //TODO: SendEmailUtility.sendEmail(toBeEmailed, toBeAdded.toString());
                            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "nota a fost adaugata!");
                            handleclearFieldsNota();

                        } else {
                            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "nota nu s-a putut adauga!");
                        }
                    }
                }
            } else {
                StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "selectati un rand din tabela!");
            }
        } else {
            StudentAlert.showMessage(null, Alert.AlertType.WARNING, "Avertisment", "pentru a intra in modul de adaugare, bifati casuta!");
        }

    }

    public void handleDeleteNota(ActionEvent actionEvent) {
        if (!checkboxInvert.isSelected()) {
            Nota n = this.service.findByIdNota(this.textNotaId.getText());
            if (n == null) {
                StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "selectati o nota din tabel!");
            } else {
                this.service.removeByIdNota(n.getId());
                StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "nota a fost stearsa cu succes!");
                handleclearFieldsNota();
            }
        } else {
            StudentAlert.showMessage(null, Alert.AlertType.WARNING, "Avertisment", "pentru a intra in modul de stergere, debifati casuta!");
        }
    }

    public void handleUpdateNota(ActionEvent actionEvent) {
        if (!checkboxInvert.isSelected()) {
            Nota n = this.service.findByIdNota(textNotaId.getText());
            if (n == null) {
                StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "nu s-a putut gasit nota!");
            } else {
                Student s = this.service.findByIdStudent(n.getId().split(":")[0]);//TODO RECOMPUTE GRADE CONSIDERING MOTIVATIONS
                Tema t = this.service.findByIdTema(n.getId().split(":")[1]);
                int max = this.computeMaxGrade(s, t, this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER));

                if (Integer.parseInt(this.textNotaValoare.getText()) > max) {
                    StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Nota nu poate depasi " + max + " !");
                } else if (Constants.compareDates(this.dateNotaData.getValue(), LocalDate.now())) {
                    StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "Data nu poate fi din viitor!");
                } else {
                    Nota replace = new Nota(n.getId(), Integer.parseInt(this.textNotaValoare.getText()), n.getProfesor(), this.dateNotaData.getValue().format(Constants.DATE_TIME_FORMATTER), this.textNotaFeedback.getText());
                    Nota rez = this.service.updateNota(replace);
                    if (rez == null) {
                        StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "nota a fost modificata!");
                        handleclearFieldsNota();
                    } else {
                        StudentAlert.showMessage(null, Alert.AlertType.WARNING, "Avertisment", "nota nu a putut fi modificata!");
                    }
                }
            }

        } else {
            StudentAlert.showMessage(null, Alert.AlertType.WARNING, "Avertisment", "pentru a intra in modul de modificare, debifati casuta!");
        }
    }


    public void handleClearFieldsTema(ActionEvent actionEvent) {
        this.textTemaDescriere.setText("");
        this.dateTemaInceput.setText("");
        this.textTemaNume.setText("");
        this.dateTemaSfarsit.setText("");
    }

    private void handleclearFieldsNota() {
        this.textNotaId.setText("");
        this.textNotaTema.setText("");
        this.textNotaStudent.setText("");
        this.textNotaValoare.setText("");
        this.textNotaProf.setText(this.loggedInProfessor.toString());
        this.textNotaFeedback.setText("");
        this.dateNotaData.setValue(LocalDate.now());
    }

    public void handleToggleINVERT(ActionEvent actionEvent) {
        initModelGradeINVERTED();
        this.textNotaId.setText("");
        this.textNotaTema.setText(getTemaCurenta(this.dateNotaData.getValue()));
        this.textNotaStudent.setText("");
        this.textNotaValoare.setText("");
        this.textNotaProf.setText(this.loggedInProfessor.toString());
        this.textNotaFeedback.setText("");
        this.dateNotaData.setValue(LocalDate.now());
    }


    public void handleSelectionChanged(MouseEvent mouseEvent) {
        NotaDTO nota = this.tableNota.getSelectionModel().getSelectedItem();
        this.textNotaId.setText(nota.getIdNota());
        this.textNotaStudent.setText(nota.getS().toString());
        this.textNotaProf.setText(nota.getProfesor());
        this.textNotaValoare.setText(Integer.toString(nota.getValoare()));
        this.dateNotaData.setValue(LocalDate.parse(nota.getDataNota(), Constants.DATE_TIME_FORMATTER));
        this.textNotaFeedback.setText(nota.getFeedback());
        this.textNotaTema.setText(nota.getT().toString());
    }

    public void handleClearFieldsNota(ActionEvent actionEvent) {
        handleclearFieldsNota();
    }


    public void handleExport1(ActionEvent actionEvent) {
        List<Object> lines = this.service.raport1(this.loggedInProfessor);
        String title = "Nota la laborator pentru fiecare student";
        String pdfName;
        handleOpenFileChooserWithContent(title, lines);
    }

    public void handleExport2(ActionEvent actionEvent) {
        List<Object> lines = this.service.raport2(this.loggedInProfessor);
        String title = "Cea mai grea tema";
        String pdfName;
        handleOpenFileChooserWithContent(title, lines);
    }

    public void handleExport3(ActionEvent actionEvent) {
        List<Object> lines = this.service.raport3(this.loggedInProfessor);
        String title = "Studentii care pot intra in examen";
        String pdfName;
        handleOpenFileChooserWithContent(title, lines);
    }

    public void handleExport4(ActionEvent actionEvent) {
        List<Object> lines = this.service.raport4(this.loggedInProfessor);
        String title = "Studentii care au predat la timp toate temele";
        String pdfName;
        handleOpenFileChooserWithContent(title, lines);
    }

    private void createPDFFromRaport(String pdfPath, String title, String subtitle, List<Object> lines) throws IOException {
        PDFont font = PDType1Font.HELVETICA;
        int marginTop = 60;
        int fontSize = 18;

        final PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        PDRectangle mediaBox = page.getMediaBox();
        doc.addPage(page);

        PDPageContentStream stream = new PDPageContentStream(doc, page);

        float subtitleWidth = font.getStringWidth(subtitle) / 1000 * fontSize;
        float subtitleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        float startXX = (mediaBox.getWidth() - subtitleWidth) / 2;
        float startYY = mediaBox.getHeight() - marginTop - subtitleHeight;

        stream.beginText();
        stream.setFont(font, fontSize);
        stream.newLineAtOffset(startXX, startYY);
        stream.showText(subtitle);
        stream.endText();

        marginTop = 30;
        float titleWidth = font.getStringWidth(title) / 1000 * fontSize;
        float titleHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;

        float startX = (mediaBox.getWidth() - titleWidth) / 2;
        float startY = mediaBox.getHeight() - marginTop - titleHeight;

        stream.beginText();
        stream.setFont(font, fontSize);
        stream.newLineAtOffset(startX, startY);
        stream.showText(title);
        stream.endText();

        int tx = 50;
        int ty = 700;

        stream.setFont(PDType1Font.HELVETICA, 10);

        for (Object line : lines) {
            String lineText = line.toString();

            stream.beginText();
            stream.newLineAtOffset(tx, ty);
            stream.showText(lineText);
            stream.newLine();
            stream.endText();
            ty -= 20;
        }

        stream.close();




        WritableImage snapshot = this.anchor.snapshot(new SnapshotParameters(), null);

        String path = "C:\\Users\\George\\Desktop\\PDFsPE\\ceva.png";
        File file = new File(path);

        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);


        PDRectangle rect = new PDRectangle(980,560);
        PDPage page1 = new PDPage(rect);
        doc.addPage(page1);

        PDImageXObject pdImage = PDImageXObject.createFromFile(path, doc);

        try (PDPageContentStream contentStream = new PDPageContentStream(doc,
                page1,
                PDPageContentStream.AppendMode.APPEND,
                true,
                true))
        {
            contentStream.drawImage(pdImage, 0, 0, 980, 560);
        }
        doc.save(pdfPath);

        file.delete();

        doc.save(new File(pdfPath));
    }


    private void handleOpenFileChooserWithContent(String title, List<Object> lines) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );
        File file = fileChooser.showSaveDialog(this.dialogStage);
        if (file != null) {
            try {
                createPDFFromRaport(file.getAbsolutePath(), title, this.loggedInProfessor.toString(), lines);
                StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "raportul a fost salvat cu succes!");
            } catch (IOException ex) {
                StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "raportul a nu a putut fi salvat!");
            }
        }
    }


    public void handleChangeRadio(ActionEvent actionEvent) {
        if(toggleCharts.isSelected()){
            toggleCharts.setText("   PIE   ");

            itemsPieChart.setVisible(false);
            itemsPieChart2.setVisible(false);
            itemsPieChart3.setVisible(false);
            itemsPieChart4.setVisible(false);

            histo1.setVisible(true);
            histo2.setVisible(true);
            histo3.setVisible(true);
            histo4.setVisible(true);
        }
        else{
            toggleCharts.setText("HISTO");

            histo1.setVisible(false);
            histo2.setVisible(false);
            histo3.setVisible(false);
            histo4.setVisible(false);

            itemsPieChart.setVisible(true);
            itemsPieChart2.setVisible(true);
            itemsPieChart3.setVisible(true);
            itemsPieChart4.setVisible(true);
        }
    }
}



        /*try {
            List<Object> lines = this.service.raport2();
            String title = "Cea mai grea tema";
            String pdfName;
            if(this.textFileName2.getText().isEmpty()){
                pdfName = this.textFileName2.getPromptText();
            }
            else{
                pdfName = this.textFileName2.getText();
            }
            createPDFFromRaport(this.textSavePath.getText() + "\\" + pdfName, this.loggedInProfessor.toString(), title, lines);
            StudentAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "raportul a fost salvat cu succes!");
        } catch (IOException e) {
            StudentAlert.showMessage(null, Alert.AlertType.ERROR, "Eroare", "nu s-a putut salva raportul!");
            //e.printStackTrace();
        }*/

        /*final DirectoryChooser directoryChooser =
                new DirectoryChooser();
        final File selectedDirectory =
                directoryChooser.showDialog(dialogStage);
        if (selectedDirectory != null) {
            this.textSavePath.setText(selectedDirectory.getAbsolutePath());
        }*/