package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/server_choosing_form.fxml"));
        primaryStage.setTitle("Choose server");
        primaryStage.setScene(new Scene(root, 350, 250));
        primaryStage.show();
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        Application.launch(args);
    }
}
