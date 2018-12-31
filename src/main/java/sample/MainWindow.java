package sample;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by Maciej on 2018-12-29 15:35
 */
public class MainWindow {


    public Label databaseAndUserLabel;
    public TableView<UserEvent> upcomingEventsTableView;
    public Spinner upcomingNearEventsSpinner;
    public Label currentTimeLabel;
    public Label nextEventLabel;
    public Spinner finishedNearEventsSpinner;
    public TableView<UserEvent> finishedEventsTableView;
    public TableView<UserEvent> customSelectEventsTableView;
    public TextField customSelectionTextBox;

    private Database database;

    public void initDatabase(Database database){
        this.database = database;
        databaseAndUserLabel.setText("("+database.getName()+"; "+database.getUser()+")");
    }

    public void createDB(ActionEvent actionEvent) {

        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");
        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Enter DB name:");
        dialogInputDBName.setContentText("Name:");
        Optional<String> dbNews = dialogInputDBName.showAndWait();
        try {
            String dbNew = dbNews.orElseThrow(NoInputException::new);
            database.createDatabase(dbNew);
            infoBox("Pomyślnie utworzono bazę danych "+dbNew, "");
        }catch(Exception e){
            errorHandler(e);
        }

    }

    public void deleteDB(ActionEvent actionEvent) {
        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");

        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Enter DB name to delete:");
        dialogInputDBName.setContentText("Name:");
        Optional<String> dbNews = dialogInputDBName.showAndWait();

        try {
            String dbNew = dbNews.orElseThrow(NoInputException::new);
            database.deleteDatabase(dbNew);
            infoBox("Succesfuly deleted DB: "+dbNew, "");
        }catch(Exception e){
            errorHandler(e);
        }
    }

    public void checkConnection(ActionEvent actionEvent) {
        try {
            database.checkConnection();
            infoBox("Connection succesful!","");
        } catch (Exception e){
            errorHandler(e);
        }
    }

    public void deleteEventById(ActionEvent actionEvent) {
        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");

        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Delete event:");
        dialogInputDBName.setContentText("ID of event to delete:");
        Optional<String> IDas = dialogInputDBName.showAndWait();

        try {
            String stringId = IDas.orElseThrow(NoInputException::new);
            int id = Integer.parseInt(stringId);
            database.deleteEvent(id);//database.deleteDatabase(dbNew);
            infoBox("Succesfuly deleted record: "+id, "");
        }catch(Exception e){
            errorHandler(e);
        }
    }

    public void deleteOldEvents(ActionEvent actionEvent) {
        try{
            database.deleteOldEvents();
            infoBox("Succesfuly deleted all old events!","");
        }catch(Exception e){
            errorHandler(e);
        }
    }

    public void changeConnection(ActionEvent actionEvent) {

    }

    public void selectWithCustomSettings(ActionEvent actionEvent) {
        try{
            customSelectEventsTableView.setItems(FXCollections.observableArrayList(
                    database.select(customSelectionTextBox.getText())));
        }catch(Exception e){
            errorHandler(e);
        }
    }

    public void selectNearFinishedEvents(ActionEvent actionEvent) {
        try{
            finishedEventsTableView.setItems(FXCollections.observableArrayList(
                    database.getNearbyFinishedEvents(Integer.parseInt(finishedNearEventsSpinner.getValue().toString()))));
        }catch(Exception e){
            errorHandler(e);
        }
    }

    public void selectNearUpcomingEvents(ActionEvent actionEvent) {
        try{
            upcomingEventsTableView.setItems(FXCollections.observableArrayList(
                    database.getNearbyUpcomingEvents(Integer.parseInt(upcomingNearEventsSpinner.getValue().toString()))));
        }catch(Exception e){
            errorHandler(e);
        }

    }



    public static void infoBox(String infoMessage, String titleBar) // DONE
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);

        alert.showAndWait();
    }
    public static void warningBox(String infoMessage, String titleBar) // DONE
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titleBar);
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);

        alert.showAndWait();
    }

    public static void errorBox(String infoMessage, String titleBar, String header) // DONE
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titleBar);
        alert.setHeaderText(header);
        alert.setContentText(infoMessage);

        alert.showAndWait();
    }

    private void errorHandler(Exception exception){
        try{
            throw exception;
        }catch (DatabaseNotFoundException e) {
            warningBox("Given DB doesnt exist. Create it or change to other DB","WARNING");
        } catch (SQLException e) {
            errorBox(e.getMessage(),"ERROR","Database Error");
            System.exit(1);
        } catch (ClassNotFoundException e) {
            errorBox(e.getMessage(),"ERROR","Driver Not Found");
            System.exit(1);
        } catch(NoInputException ignored){

        } catch(Exception e){
            errorBox(e.getMessage(),"ERROR","Other error");
            e.printStackTrace();
            System.exit(1);
        }
    }

}
