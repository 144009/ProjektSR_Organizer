package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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

    private Map<String,String> driversMap = new HashMap<>();

    public ServerChoosingForm(){
        driversMap.put("MySQL","com.mysql.jdbc.Driver");
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

    public void submit(ActionEvent actionEvent){
        try{
            checkData();
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
            controller.initDatabase(new Database(dbName.getText(),dbUsername.getText(),dbPassword.getText(),dbUrl.getText()));
        }catch(Exception e){
            showBox(e.getMessage(),"Error","Input Error", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
