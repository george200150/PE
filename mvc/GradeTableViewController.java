package mvc;

import domain.Nota;
import domain.NotaDTO;
import domain.Student;
import domain.Tema;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.MasterService;
import utils.events.GradeChangeEvent;
import utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GradeTableViewController implements Observer<GradeChangeEvent> {
    private MasterService service;
    private ObservableList<NotaDTO> model = FXCollections.observableArrayList();

    @FXML
    TableView<NotaDTO> tableViewGrade;
    @FXML
    TableColumn<NotaDTO, String> tableGradeColumnID;
    @FXML
    TableColumn<NotaDTO, String> tableGradeColumnData;
    @FXML
    TableColumn<NotaDTO, String> tableGradeColumnProfesor;
    @FXML
    TableColumn<NotaDTO, String> tableGradeColumnValoare;
    @FXML
    TableColumn<NotaDTO, String> tableGradeColumnStudent;
    @FXML
    TableColumn<NotaDTO, String> tableGradeColumnTask;
    @FXML
    TableColumn<NotaDTO, String> tableGradeColumnFeedback;


    @FXML
    TextField searchFieldID;
    @FXML
    TextField searchFieldData;
    @FXML
    TextField searchFieldProfesor;
    @FXML
    TextField searchFieldValoare;
    @FXML
    TextField searchFieldStudent;
    @FXML
    TextField searchFieldTask;


    public void setService(MasterService masterService) {
        service = masterService;
        service.addObserver(this);//TODO: set observer on AllService ???
        initModel();
    }

    private List<NotaDTO> getNotaDTOList() {
        Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());
        return gradeList.stream()
                .map(n -> {
                    String[] parts = n.getId().split(":");
                    Student s = service.findByIdStudent(parts[0]);
                    Tema t = service.findByIdTema(parts[1]);
                    return new NotaDTO(n, t, s);
                })
                .collect(Collectors.toList());
    }


    @FXML
    public void initialize() {
        tableGradeColumnID.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("idNota"));
        tableGradeColumnData.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("dataNota"));
        tableGradeColumnProfesor.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("profesor"));
        tableGradeColumnValoare.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("valoare"));

        tableGradeColumnStudent.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("studentString"));
        tableGradeColumnTask.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("temaString"));
        tableGradeColumnFeedback.setCellValueFactory(new PropertyValueFactory<NotaDTO, String>("feedback"));

        tableViewGrade.setItems(model);

        searchFieldID.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldData.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldProfesor.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldValoare.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));

        searchFieldStudent.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldTask.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
    }

    private void initModel() {
        List<NotaDTO> gradeDTOList = getNotaDTOList();
        model.setAll(gradeDTOList);
    }

    private void filterMergeSearch() {
        Predicate<NotaDTO> filtered = null;// asa compunem predicatele

        /*Iterable<Nota> grades = service.getAllNota();
        List<Nota> gradeList = StreamSupport.stream(grades.spliterator(), false)
                .collect(Collectors.toList());*/
        List<NotaDTO> gradeDTOList = getNotaDTOList();

        Predicate<NotaDTO> filterID = x -> {
            String text = searchFieldID.getText();
            if (text.isEmpty()) {
                return true;
            }
            return x.getIdNota().toLowerCase().contains(text.toLowerCase());
        };
        Predicate<NotaDTO> filterData = x -> {
            String text = searchFieldData.getText();
            if (text.isEmpty()) {
                return true;
            }
            return x.getDataNota().toLowerCase().contains(text.toLowerCase());
        };
        Predicate<NotaDTO> filterProfesor = x -> {
            String text = searchFieldProfesor.getText();
            if (text.isEmpty()) {
                return true;
            }
            return x.getProfesor().toLowerCase().contains(text.toLowerCase());
        };
        Predicate<NotaDTO> filterValoare = x -> {
            String text = searchFieldValoare.getText();
            if (text.isEmpty()) {
                return true;
            }
            return Integer.toString(x.getValoare()).toLowerCase().contains(text.toLowerCase());
        };


        Predicate<NotaDTO> filterStudent = x -> {
            String text = searchFieldStudent.getText();
            if (text.isEmpty()) {
                return true;
            }
            String studentStr = x.getS().getNume()
                    + " " + x.getS().getPrenume()
                    + " " + x.getS().getEmail()
                    + " " + x.getS().getGrupa();//aici grupa e deja string
            return studentStr.toLowerCase().contains(text.toLowerCase());
        };
        Predicate<NotaDTO> filterTask = x -> {
            String text = searchFieldTask.getText();
            if (text.isEmpty()) {
                return true;
            }
            String taskStr = x.getT().getNume()
                    + " " + x.getT().getDescriere()
                    + " " + x.getT().getStartWeek()
                    + " " + x.getT().getDeadlineWeek();
            return taskStr.toLowerCase().contains(text.toLowerCase());
        };

        filtered = filterID.and(filterData).and(filterProfesor).and(filterValoare).and(filterStudent).and(filterTask);

        model.setAll(gradeDTOList.stream().filter(filtered).collect(Collectors.toList()));
    }

    @Override
    public void update(GradeChangeEvent gradeChangeEvent) {
        initModel();
    }

    public void handleDeleteGrade(ActionEvent actionEvent) {
        NotaDTO selectedDTO = (NotaDTO) tableViewGrade.getSelectionModel().getSelectedItem();
        if (selectedDTO != null) {
            Nota selected = selectedDTO.getN();
            Nota deleted = service.removeByIdNota(selected.getId());
            if (null != deleted)
                GradeAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Nota a fost stearsa cu succes!");
        } else GradeAlert.showErrorMessage(null, "Nu ati selectat nicio nota!");
    }

    @FXML
    public void handleAddGrade(ActionEvent actionEvent) {
        showGradeEditDialog(null);
    }


    public void showGradeEditDialog(NotaDTO grade) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/gradeeditview.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Grade");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            GradeEditController editGradeViewController = loader.getController();
            editGradeViewController.setService(service, dialogStage, grade);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateGrade(ActionEvent actionEvent) {
        NotaDTO selectedDTO = tableViewGrade.getSelectionModel().getSelectedItem();
        if (selectedDTO != null) {
            NotaDTO selected = selectedDTO;
            showGradeEditDialog(selected);
        } else
            GradeAlert.showErrorMessage(null, "Nu ati selectat nicio nota");
    }

}


