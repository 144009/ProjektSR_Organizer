package organizer.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.scene.control.LocalTimeTextField;
import organizer.exceptions.DatabaseNotFoundException;
import organizer.exceptions.NoInputException;
import organizer.Database;
import organizer.UserEvent;
import organizer.exceptions.ValidationException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
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
    public TextField eventName;
    public DatePicker eventBeginPicker;
    public DatePicker eventEndPicker;
    public TextArea eventDesc;
    public LocalTimeTextField eventBeginPickerTime;
    public LocalTimeTextField eventEndPickerTime;


    private Database database;


    private void showEventInfo(TableRow<UserEvent> userEventTableRow){
        UserEvent event = userEventTableRow.getItem();
        Stage dialog = new Stage();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/edit_delete_event.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            errorBox(e.getMessage(),"ERROR","Error");
            System.exit(1);
        }
        EditDeleteEvent ede = loader.getController();
        ede.initializeEvent(event);
        dialog.setScene(new Scene(root,root.prefWidth(800),root.prefHeight(600)));
        dialog.setTitle(event.getName());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(databaseAndUserLabel.getScene().getWindow());
        dialog.showAndWait();
        try{
            if(ede.isToEdit()){
                UserEvent editedUserEvent = ede.getUserEvent();
                database.modifyEvent(editedUserEvent);
                userEventTableRow.setItem(editedUserEvent);
                infoBox("Successfully modified event!", "Info");
                userEventTableRow.getTableView().refresh();
            }
            if(ede.isToDelete()){
                UserEvent userEventToDelete = ede.getUserEvent();
                database.deleteEvent(userEventToDelete.getId());
                userEventTableRow.getTableView().getItems().remove(userEventToDelete);
                infoBox("Successfully deleted event!", "Info");
            }
        }catch(Exception e){
            errorHandler(e);
        }
    }

    private void setRowFactory(TableView<UserEvent> tableView){
        tableView.setRowFactory(param -> {
            TableRow<UserEvent> tableRow = new TableRow<>();
            tableRow.setOnMouseClicked(
                    event -> {
                        if(event.getClickCount() == 2 && !(tableRow.isEmpty())){
                            showEventInfo(tableRow);
                        }
                    }
            );
            return tableRow;
        });
    }

    private String timeString;


    public void initialize(){
        setRowFactory(upcomingEventsTableView);
        setRowFactory(finishedEventsTableView);
        setRowFactory(customSelectEventsTableView);

        eventBeginPickerTime.setLocalTime(LocalTime.of(0,0,0,0));
        eventEndPickerTime.setLocalTime(LocalTime.of(0,0,0,0));

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO,event -> {
            timeString = LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,FormatStyle.MEDIUM));
            currentTimeLabel.setText(timeString);
        }),new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

    }


    private void refreshClosestEvent(){
        try{
            UserEvent userEvent = database.getClosestEvent();
            if(userEvent == null){
                nextEventLabel.setText("No upcoming events.");
            }else{
                nextEventLabel.setText("Next event: " + userEvent.getName());
            }
        }catch(Exception e){
            nextEventLabel.setText("Error.");
            e.printStackTrace();
        }
    }


    public void initDatabase(Database database){
        this.database = database;
        refreshClosestEvent();

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
            database.deleteEvent(id);
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

    public void changeConnection(ActionEvent actionEvent) throws IOException {
        Stage dialog = new Stage();
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "/server_choosing_form.fxml"));
        Parent root = loader.load();
        ServerChoosingForm scf = loader.getController();
        scf.setControllerForEdit(database);
        dialog.setScene(new Scene(root,root.prefWidth(600),root.prefHeight(400)));
        dialog.setTitle("Choose server");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(databaseAndUserLabel.getScene().getWindow());
        dialog.showAndWait();
        if(scf.isConfirmed()){
            database = scf.getDatabase();
            scf.saveToFile();
            databaseAndUserLabel.setText("("+database.getName()+"; "+database.getUser()+")");
            refreshClosestEvent();
            finishedEventsTableView.getItems().clear();
            finishedEventsTableView.refresh();
            upcomingEventsTableView.getItems().clear();
            upcomingEventsTableView.refresh();
            customSelectEventsTableView.getItems().clear();
            customSelectEventsTableView.refresh();
        }
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

    public void showClosestEvent(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount() == 2){
            try{
                UserEvent event = database.getClosestEvent();
                String showString =
                        event.getName() + "\n"
                        + event.getDesc() + "\n"
                        + "Begin: " + event.getEventBegin() + "\n"
                        + "End: " + event.getEventEnd() + "\n";
                (new Alert(Alert.AlertType.INFORMATION,showString)).showAndWait();
            }catch(Exception e){
                errorHandler(e);
            }
        }
    }


    private void validateFields() throws ValidationException {
        if(eventName.getText().trim().isEmpty())
            throw new ValidationException("'Event name' field is empty");
        if(eventBeginPicker.getValue() == null)
            throw new ValidationException("'Event start day' field is empty");
        if(eventEndPicker.getValue() == null)
            throw new ValidationException("'Event end day' field is empty");
    }

    public void addEvent(ActionEvent actionEvent) {
        try{
            validateFields();

            // convert LocalDate to LocalDateTime and combine it with LocalTime
            LocalDate dateStart = eventBeginPicker.getValue();
            LocalDate dateEnd = eventEndPicker.getValue();
            LocalDateTime dateTimeStart = dateStart.atTime(eventBeginPickerTime.getLocalTime());
            LocalDateTime dateTimeEnd = dateEnd.atTime(eventEndPickerTime.getLocalTime());

            database.addEvent(eventName.getText(),
                    eventDesc.getText(),
                    dateTimeStart,
                    dateTimeEnd);
            infoBox("Successfully saved new event","Info");
            eventName.setText("");
            eventDesc.setText("");
            eventBeginPicker.setValue(null);
            eventEndPicker.setValue(null);
            eventBeginPickerTime.setLocalTime(LocalTime.of(0,0,0,0));
            eventEndPickerTime.setLocalTime(LocalTime.of(0,0,0,0));
        }catch (Exception e){
            errorHandler(e);
        }
    }

    private static void infoBox(String infoMessage, String titleBar) // DONE
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);

        alert.showAndWait();
    }
    private static void warningBox(String infoMessage, String titleBar) // DONE
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titleBar);
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);

        alert.showAndWait();
    }

    private static void errorBox(String infoMessage, String titleBar, String header) // DONE
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
        } catch (ValidationException e){
            errorBox(e.getMessage(),"ERROR","Validation error");
        }
        catch (DatabaseNotFoundException e) {
            warningBox("Given DB doesnt exist. Create it or change to other DB","WARNING");
        } catch (SQLException e) {
            errorBox(e.getMessage(),"ERROR","Database Error");
        } catch (ClassNotFoundException e) {
            errorBox(e.getMessage(),"ERROR","Driver Not Found");
        } catch(NoInputException ignored){

        } catch(Exception e){
            errorBox(e.getMessage(),"ERROR","Other error");
            e.printStackTrace();
        }
    }



}
