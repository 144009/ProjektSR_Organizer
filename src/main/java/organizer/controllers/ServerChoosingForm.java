package organizer.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import organizer.Database;
import organizer.exceptions.DatabaseNotFoundException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maciej on 2018-12-28 17:30
 */
public class ServerChoosingForm {
    public ChoiceBox databaseChooser;
    public TextField dbUrl;
    public TextField dbUsername;
    public PasswordField dbPassword;
    public CheckBox savePassword;
    public TextField dbName;

    private static Map<String,String> driversMap = new HashMap<>();

    private boolean forEdit = false;
    private boolean confirmed = false;

    static{
        driversMap.put("MySQL","com.mysql.jdbc.Driver");
    }

    public void setControllerForEdit(Database database){
        forEdit = true;
        confirmed = false;
        dbUrl.setText(database.getUrl());
        dbUsername.setText(database.getUser());
        dbPassword.setText(database.getPassword());
        dbName.setText(database.getName());
    }

    private void showBox(String infoMessage, String titleBar, String header, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(titleBar);
        alert.setHeaderText(header);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    public void initialize(){
        databaseChooser.getSelectionModel().select(0);
        dbUrl.setText("jdbc:mysql://localhost/");
        dbUsername.setText("root");
        dbPassword.setText("MACsql10DB10");
        dbName.setText("newdb");
    }

    private void checkData() throws Exception {
        if(databaseChooser.getSelectionModel().isEmpty())
            throw new Exception("Database is not selected");
        String item = (String) databaseChooser.getSelectionModel().getSelectedItem();
        if(!driversMap.containsKey(item))
            throw new Exception("Invalid database value");
        if(dbUrl.getText().trim().isEmpty())
            throw new Exception("Url field is empty");
        if(dbUsername.getText().trim().isEmpty())
            throw new Exception("Username field is empty");
        if(dbPassword.getText().isEmpty())
            throw new Exception("Password field is empty");
        if(dbName.getText().trim().isEmpty())
            throw new Exception("Database name field is empty");
    }

    public void checkConnection(ActionEvent actionEvent) {
        try{
            checkData();
            @SuppressWarnings("RedundantCast")
            String driver = driversMap.get((String) databaseChooser.getSelectionModel().getSelectedItem());
            (new Database(dbName.getText(),dbUsername.getText(),dbPassword.getText(),dbUrl.getText())).checkConnection();
            showBox("Connection successful!","Success","Success", Alert.AlertType.INFORMATION);
        }catch (DatabaseNotFoundException e) {
            showBox("Given DB doesnt exist.","Error","Database Error", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            showBox(e.getMessage(),"ERROR","Database Error", Alert.AlertType.ERROR);
        } catch (ClassNotFoundException e) {
            showBox(e.getMessage(),"Error","Driver Not Found", Alert.AlertType.ERROR);
        } catch(Exception e){
            showBox(e.getMessage(),"Error","Input Error", Alert.AlertType.ERROR);
        }
    }

    private void close(Node node){
        ((Stage)node.getScene().getWindow()).close();
    }

    public void submit(ActionEvent actionEvent){
        try{
            checkData();
            if(forEdit){
                confirmed = true;
                close((Node) actionEvent.getSource());
                return;
            }
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/sample.fxml"));

            Stage stage = (Stage)dbName.getScene().getWindow();
            stage.setScene(
                    new Scene(
                            loader.load()
                    )
            );
            Controller controller =
                    loader.getController();
            controller.initDatabase(getDatabase());
        }catch(Exception e){
            showBox(e.getMessage(),"Error","Input Error", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void launchTestView(ActionEvent actionEvent) {
        try{
            if(forEdit)
                return;
            checkData();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/main_window.fxml"));

            Stage stage = (Stage)dbName.getScene().getWindow();
            stage.setScene(
                    new Scene(
                            loader.load()
                    )
            );
            MainWindow controller =
                    loader.getController();
            controller.initDatabase(new Database(dbName.getText(),dbUsername.getText(),dbPassword.getText(),dbUrl.getText()));
        }catch(Exception e){
            showBox(e.getMessage(),"Error","Input Error", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Database getDatabase(){
        return new Database(dbName.getText(),dbUsername.getText(),dbPassword.getText(),dbUrl.getText());
    }
}
