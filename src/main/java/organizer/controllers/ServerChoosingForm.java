package organizer.controllers;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import organizer.ChoosingFormFields;
import organizer.Database;
import organizer.exceptions.DatabaseNotFoundException;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Maciej on 2018-12-28 17:30
 */

public class ServerChoosingForm {
    public ChoiceBox<String> databaseChooser;
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
        driversMap.put("PostgreSQL","org.postgresql.Driver");
    }

    private void selectByDriverClass(String driverClassString){
        Optional<Map.Entry<String, String>> lookForEntry = driversMap.entrySet().stream()
                .filter(stringStringEntry -> driverClassString.equals(stringStringEntry.getValue()))
                .findFirst();
        if(lookForEntry.isPresent()){
            String databaseType = lookForEntry.get().getKey();
            databaseChooser.getItems().stream().filter(o -> o.equals(databaseType))
                    .findFirst().ifPresent( o -> databaseChooser.getSelectionModel().select(o));
        }
    }

    public void setControllerForEdit(Database database){
        forEdit = true;
        confirmed = false;
        dbUrl.setText(database.getUrl());
        dbUsername.setText(database.getUser());
        dbPassword.setText(database.getPassword());
        dbName.setText(database.getName());
        selectByDriverClass(database.getDriver());
    }

    private void showBox(String infoMessage, String titleBar, String header, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(titleBar);
        alert.setHeaderText(header);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    public void initialize(){
        File file = new File("last_form_fields.xml");
        if(file.exists()) {
            XmlMapper mapper = new XmlMapper();
            try {
                ChoosingFormFields lastFormFields = mapper.readValue(file,ChoosingFormFields.class);
                databaseChooser.getItems().stream()
                        .filter(s -> s.equals(lastFormFields.getType()))
                        .findFirst().ifPresent(s -> databaseChooser.getSelectionModel().select(s));
                dbUrl.setText(lastFormFields.getUrl());
                dbUsername.setText(lastFormFields.getUserName());
                dbPassword.setText(lastFormFields.getPassword());
                if(!dbPassword.getText().isEmpty()){
                    savePassword.setSelected(true);
                }
                dbName.setText(lastFormFields.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkData() throws Exception {
        if(databaseChooser.getSelectionModel().isEmpty())
            throw new Exception("Database is not selected");
        String item = databaseChooser.getSelectionModel().getSelectedItem();
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

    public void saveToFile(){
        String password = dbPassword.getText();
        if(!savePassword.isSelected()){
            password = "";
        }
        ChoosingFormFields lastFormFields =
                new ChoosingFormFields(databaseChooser.getSelectionModel().getSelectedItem(),
                        dbUrl.getText(),dbName.getText(),dbUsername.getText(),password);
        XmlMapper mapper = new XmlMapper();
        try {
            mapper.writeValue(new File("last_form_fields.xml"),lastFormFields);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkConnection(ActionEvent actionEvent) {
        try{
            checkData();
            @SuppressWarnings("RedundantCast")
            String driver = driversMap.get((String) databaseChooser.getSelectionModel().getSelectedItem());
            (new Database(dbName.getText(),dbUsername.getText(),dbPassword.getText(),dbUrl.getText(),driver)).checkConnection();
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

    public void submit(ActionEvent actionEvent) {
        try{
            if(forEdit){
                confirmed = true;
                close((Node) actionEvent.getSource());
                return;
            }
            checkData();
            String driver = driversMap.get(databaseChooser.getSelectionModel().getSelectedItem());
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
            saveToFile();
            controller.initDatabase(new Database(dbName.getText(),dbUsername.getText(),dbPassword.getText(),dbUrl.getText(),driver));

        }catch(Exception e){
            showBox(e.getMessage(),"Error","Input Error", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Database getDatabase(){
        String driver = driversMap.get(databaseChooser.getSelectionModel().getSelectedItem());
        return new Database(dbName.getText(),dbUsername.getText(),dbPassword.getText(),dbUrl.getText(),driver);
    }
}
