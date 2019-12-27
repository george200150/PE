package mvc;

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
import services.TemaService;
import utils.events.TaskChangeEvent;
import utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TaskTableViewController implements Observer<TaskChangeEvent> {
    private TemaService service;
    private ObservableList<Tema> model = FXCollections.observableArrayList();

    @FXML
    TableView<Tema> tableViewTask;
    @FXML
    TableColumn<Tema,String> tableTaskColumnNume;
    @FXML
    TableColumn<Tema,String> tableTaskColumnDescriere;
    @FXML
    TableColumn<Tema,String> tableTaskColumnStart;
    @FXML
    TableColumn<Tema,String> tableTaskColumnDeadline;


    @FXML
    TextField searchFieldNume;
    @FXML
    TextField searchFieldDescriere;

    @FXML
    TextField searchFieldStart;
    @FXML
    TextField searchFieldStop;


    public void setService(TemaService temaService) {
        service = temaService;
        service.addObserver(this);
        initModel();
    }

    @FXML
    public void initialize() {
        tableTaskColumnNume.setCellValueFactory(new PropertyValueFactory<Tema, String>("nume"));
        tableTaskColumnDescriere.setCellValueFactory(new PropertyValueFactory<Tema, String>("descriere"));
        tableTaskColumnStart.setCellValueFactory(new PropertyValueFactory<Tema, String>("startWeek"));
        tableTaskColumnDeadline.setCellValueFactory(new PropertyValueFactory<Tema, String>("deadlineWeek"));
        tableViewTask.setItems(model);

        searchFieldNume.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldDescriere.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));

        searchFieldStart.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
        searchFieldStop.textProperty().addListener(((observableValue, s, t1) -> {
            filterMergeSearch();
        }));
    }

    private void initModel() {
        Iterable<Tema> tasks = service.getAll();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());
        model.setAll(taskList);
    }


    private void filterMergeSearch(){
        Predicate<Tema> filtered = null;

        Iterable<Tema> tasks = service.getAll();
        List<Tema> taskList = StreamSupport.stream(tasks.spliterator(), false)
                .collect(Collectors.toList());

        Predicate<Tema> filterNume = x -> x.getNume().toLowerCase().contains(searchFieldNume.getText().toLowerCase());
        Predicate<Tema> filterDescriere = x -> x.getDescriere().toLowerCase().contains(searchFieldDescriere.getText().toLowerCase());
        Predicate<Tema> filterStart = x -> x.getStartWeek().contains(searchFieldStart.getText());
        Predicate<Tema> filterStop = x -> x.getDeadlineWeek().contains(searchFieldStop.getText());

        if (!searchFieldNume.getText().isEmpty()){
            if(filtered == null){
                filtered = filterNume;
            }
            else{
                filtered = filtered.and(filterNume);
            }
        }
        if (!searchFieldDescriere.getText().isEmpty()){
            if(filtered == null){
                filtered = filterDescriere;
            }
            else{
                filtered = filtered.and(filterDescriere);
            }
        }
        if (!searchFieldStart.getText().isEmpty()){
            if(filtered == null){
                filtered = filterStart;
            }
            else{
                filtered = filtered.and(filterStart);
            }
        }
        if (!searchFieldStop.getText().isEmpty()){
            if(filtered == null){
                filtered = filterStop;
            }
            else{
                filtered = filtered.and(filterStop);
            }
        }


        if( filtered == null){
            model.setAll(taskList);
        }
        else{
            model.setAll(taskList.stream().filter(filtered).collect(Collectors.toList()));
        }
    }

    @Override
    public void update(TaskChangeEvent taskChangeEvent) {
            initModel();
    }

    public void handleDeleteTask(ActionEvent actionEvent) {
        Tema selected = (Tema) tableViewTask.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Tema deleted = service.removeById(selected.getId());
            if (null != deleted)
                TaskAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Tema a fost stearsa cu succes!");
        } else TaskAlert.showErrorMessage(null, "Nu ati selectat nicio tema!");
    }

    @FXML
    public void handleAddTask(ActionEvent actionEvent) {
        showTaskEditDialog(null);
    }

    public void showTaskEditDialog(Tema task) {
        try {
            // create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/taskeditview.fxml"));

            AnchorPane root = (AnchorPane) loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Task");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            //dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);

            TaskEditController editTaskViewController = loader.getController();
            editTaskViewController.setService(service, dialogStage, task);

            dialogStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleUpdateTask(ActionEvent actionEvent) {
        Tema selected = tableViewTask.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showTaskEditDialog(selected);
        } else
            TaskAlert.showErrorMessage(null, "Nu ati selectat nicio tema");
    }

}
