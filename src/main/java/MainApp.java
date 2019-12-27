import domain.Nota;
import domain.Profesor;
import domain.Student;
import domain.Tema;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mvc.ReportController;
import mvc.backup4new.LoginChoiceController;
import repositories.*;
import services.*;
import validators.NotaValidator;
import validators.ProfesorValidator;
import validators.StudentValidator;
import validators.TemaValidator;

import java.io.IOException;

public class MainApp extends Application {

    private AbstracBaseRepository<String, Student> studentRepository;
    private StudentService studentService;
    private AbstracBaseRepository<String, Tema> temaRepository;
    private TemaService temaService;
    private AbstracBaseRepository<String, Nota> notaRepository;
    private NotaService notaService;

    private AbstracBaseRepository<String, Profesor> profesorRepository;
    private ProfesorService profesorService;

    private MasterService masterService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        //TODO: NOT WORKING !!! - */studentRepository = new StudentRepository(StudentValidator.getInstance(), ApplicationContext.getPROPERTIES().getProperty("data.txt.studenti"));
        studentRepository = new StudentRepository(StudentValidator.getInstance(), "data/STUDENTI.txt");
        studentService = new StudentService(studentRepository);

        temaRepository = new TemaRepository(TemaValidator.getInstance(), "data/TEME.txt");
        temaService = new TemaService(temaRepository);

        notaRepository = new NotaRepository(NotaValidator.getInstance(), "data/NOTE.txt");
        notaService = new NotaService(notaRepository);

        profesorRepository = new ProfesorRepository(ProfesorValidator.getInstance(), "data/PROFESORI.txt");
        profesorService = new ProfesorService(profesorRepository);

        masterService = new MasterService(profesorService, studentService, temaService, notaService);

        init1(primaryStage);
        primaryStage.show();


    }

    private void init1(Stage primaryStage) throws IOException {

        FXMLLoader gradeLoader = new FXMLLoader();
        gradeLoader.setLocation(getClass().getResource("/views/backup4new/LoginChoice.fxml"));
        AnchorPane gradeLayout = gradeLoader.load();
        primaryStage.setScene(new Scene(gradeLayout));

        LoginChoiceController loginChoiceController = gradeLoader.getController();
        loginChoiceController.setService(masterService, primaryStage);

    }
}
