package mvc;

import domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import services.MasterService;
import utils.Constants;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ReportController {


    @FXML
    private TextField textField1;

    @FXML
    TableView<Student> tableViewStudents1;
    @FXML
    TableColumn<Student, String> tableStudentColumnNume;
    @FXML
    TableColumn<Student, String> tableStudentColumnPrenume;
    @FXML
    TableColumn<Student, String> tableStudentColumnGrupa;
    @FXML
    TableColumn<Student, String> tableStudentColumnEmail;
    @FXML
    TableColumn<Student, String> tableStudentColumnProfIndrumator;

    @FXML
    private TableView<WeightedAVGDTO> tableViewRaport;
    @FXML
    TableColumn<WeightedAVGDTO,String> tableColumnStudent;
    @FXML
    TableColumn<WeightedAVGDTO,String> tableColumnAVG;


    private MasterService service;
    private ObservableList<WeightedAVGDTO> modeldto = FXCollections.observableArrayList();
    private ObservableList<Student> modelStud = FXCollections.observableArrayList();


    public void setService(MasterService studentService) {
        service = studentService;
        //initModel();
    }

    @FXML
    public void initialize() {
        tableStudentColumnNume.setCellValueFactory(new PropertyValueFactory<Student, String>("nume"));
        tableStudentColumnPrenume.setCellValueFactory(new PropertyValueFactory<Student, String>("prenume"));
        tableStudentColumnGrupa.setCellValueFactory(new PropertyValueFactory<Student, String>("grupa"));
        tableStudentColumnEmail.setCellValueFactory(new PropertyValueFactory<Student, String>("email"));
        tableStudentColumnProfIndrumator.setCellValueFactory(new PropertyValueFactory<Student, String>("cadruDidacticIndrumatorLab"));
        tableViewStudents1.setItems(modelStud);

        tableColumnStudent.setCellValueFactory(new PropertyValueFactory<WeightedAVGDTO, String>("studentString"));
        tableColumnAVG.setCellValueFactory(new PropertyValueFactory<WeightedAVGDTO, String>("avg"));
        tableViewRaport.setItems(modeldto);

    }


    /*private void initModel() {
        Iterable<Student> students = service.getAll();
        List<Student> studentList = StreamSupport.stream(students.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(studentList);
    }*/

    private NotaDTO getDTOFromNota(Nota n){
        Student s = this.service.findByIdStudent(n.getId().split(":")[0]);
        Tema t = this.service.findByIdTema(n.getId().split(":")[1]);
        return new NotaDTO(n,t,s);
    }


    public void handle1(ActionEvent actionEvent) {
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        Map<Student, List<NotaDTO>> dtoMap = dtoList.stream()
                .collect(Collectors.groupingBy(NotaDTO::getS));

        Map<Student, Double> mediileStudentilor = dtoMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, pair -> pair.getValue().stream().mapToDouble(sub -> (sub.getValoare() * (double) (sub.getT().getDuration())) / 14).sum()));

        List<WeightedAVGDTO> rez = mediileStudentilor.entrySet().stream()
                .map(x -> new WeightedAVGDTO(x.getKey(), x.getValue()))
                .collect(Collectors.toList());

        this.modeldto.setAll(rez);
    }


    public void handle2(ActionEvent actionEvent) {//Cea mai grea tema: media notelor la tema respectiva este cea mai mica.
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        Map<Tema, List<NotaDTO>> dtoMap = dtoList.stream()
                .collect(Collectors.groupingBy(NotaDTO::getT));

        Map<Tema, Double> raport = dtoMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue().stream().mapToDouble(NotaDTO::getValoare).sum()) / (double)(e.getValue().size())));
        //TODO posibil sa trebuiasca sa schimb "mapToDouble(NotaDTO::getValoare)" cu "mapToDouble(sub -> 'weighted avg')"...

        List<Map.Entry<Tema, Double>> lowestraport = raport.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getValue))
                .limit(1)
                .collect(Collectors.toList());

        this.textField1.setText(lowestraport.get(0).getKey().toString());
    }

    public void handle3(ActionEvent actionEvent) {
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        Map<Student, List<NotaDTO>> dtoMap = dtoList.stream().
                collect(Collectors.groupingBy(NotaDTO::getS));

        Map<Student, Double> rap = dtoMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> (e.getValue().stream().mapToDouble(NotaDTO::getValoare).sum()) / (double) (e.getValue().size())));

        List<Student> okForExam = rap.entrySet().stream()
                .filter(pair -> pair.getValue() >= 4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        modelStud.setAll(okForExam);
    }

    public void handle4(ActionEvent actionEvent) {// Studentii care au predat la timp toate temele
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());

        List<NotaDTO> dtoList = gradeList.stream()
                .map(this::getDTOFromNota)
                .collect(Collectors.toList());

        List<Student> OkStudents = dtoList.stream()
                .filter(x -> Constants.firstDateIsGreaterThanSecondDate(x.getT().getDeadlineWeek(), x.getDataNota()))
                .map(NotaDTO::getS)
                .collect(Collectors.toList());

        modelStud.setAll(OkStudents);
    }
}
