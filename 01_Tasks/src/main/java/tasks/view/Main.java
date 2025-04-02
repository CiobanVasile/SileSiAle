package tasks.view;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import tasks.controller.Controller;
import tasks.controller.TaskNotifier;
import tasks.model.ArrayTaskList;
import tasks.persistence.TaskFileHandler;
import tasks.services.TasksService;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    private static Stage primaryStage;
    private static final int DEFAULT_WIDTH = 820;
    private static final int DEFAULT_HEIGHT = 520;
    private static final Logger log = Logger.getLogger(Main.class.getName());

    public ArrayTaskList savedTasksList = new ArrayTaskList();

    public static File savedTasksFile = new File("data/tasks.txt");

    private TasksService service = new TasksService(savedTasksList);

    public static Stage getPrimaryStage( ) {
        return primaryStage;
    }

    public static void setPrimaryStage( Stage primaryStage ) {
        Main.primaryStage = primaryStage;
    }

    public static File getSavedTasksFile( ) {
        return savedTasksFile;
    }

    public static void setSavedTasksFile( File savedTasksFile ) {
        Main.savedTasksFile = savedTasksFile;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        log.info("saved data reading");
        if ( getSavedTasksFile( ).length() != 0) {
            TaskFileHandler.readBinary(savedTasksList, getSavedTasksFile( ) );
        }
        try {
            log.info("application start");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            Controller ctrl= loader.getController();
            service = new TasksService(savedTasksList);

            ctrl.setService(service);
            primaryStage.setTitle("Task Manager");
            primaryStage.setScene(new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT ));
            primaryStage.setMinWidth( DEFAULT_WIDTH );
            primaryStage.setMinHeight( DEFAULT_HEIGHT );
            primaryStage.show();
        }
        catch (IOException e){
            e.printStackTrace();
            log.error("error reading main.fxml");
        }
        primaryStage.setOnCloseRequest(we -> System.exit(0) );
        new TaskNotifier(FXCollections.observableArrayList(service.getObservableList())).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}