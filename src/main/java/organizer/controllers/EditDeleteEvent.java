package organizer.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.scene.control.LocalTimeTextField;
import organizer.UserEvent;
import organizer.exceptions.ValidationException;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;


/**
 * Created by Maciej on 2018-12-31 13:09
 */
public class EditDeleteEvent {

    public TextField eventName;
    public DatePicker eventBeginPicker;
    public DatePicker eventEndPicker;
    public TextArea eventDesc;
    public LocalTimeTextField eventBeginPickerTime;
    public LocalTimeTextField eventEndPickerTime;

    private UserEvent userEvent;

    private boolean toEdit = false;
    private boolean toDelete = false;

    private void validateFields() throws ValidationException {
        if(eventName.getText().trim().isEmpty())
            throw new ValidationException("'Event name' field is empty");
        if(eventBeginPicker.getValue() == null)
            throw new ValidationException("'Event day' field is empty");
        if(eventEndPicker.getValue() == null)
            throw new ValidationException("'Event time' field is empty");
    }


    public void initializeEvent(UserEvent userEvent){
        this.userEvent = userEvent;
        eventName.setText(userEvent.getName());
        eventDesc.setText(userEvent.getDesc());


        eventBeginPickerTime.setLocalTime(((java.sql.Timestamp)userEvent.getEventBegin()).toLocalDateTime().toLocalTime());
        eventEndPickerTime.setLocalTime(((java.sql.Timestamp)userEvent.getEventEnd()).toLocalDateTime().toLocalTime());

        eventBeginPicker.setValue(((java.sql.Timestamp)userEvent.getEventBegin()).toLocalDateTime().toLocalDate());
        eventEndPicker.setValue(((java.sql.Timestamp)userEvent.getEventEnd()).toLocalDateTime().toLocalDate());


    }

    private void close(Node node){
        ((Stage)node.getScene().getWindow()).close();
    }


    public UserEvent getUserEvent() {
        return userEvent;
    }

    private java.sql.Timestamp getDate(java.time.LocalDateTime date){
        return java.sql.Timestamp.valueOf(date);
    }

    public void editEvent(ActionEvent actionEvent) {

        try{
            validateFields();

            // convert LocalDate to LocalDateTime and combine it with LocalTime
            LocalDate dateStart = eventBeginPicker.getValue();
            LocalDate dateEnd = eventEndPicker.getValue();
            LocalDateTime dateTimeStart = dateStart.atTime(eventBeginPickerTime.getLocalTime());
            LocalDateTime dateTimeEnd = dateEnd.atTime(eventEndPickerTime.getLocalTime());

            userEvent.setName(eventName.getText());
            userEvent.setDesc(eventDesc.getText());
            userEvent.setEventBegin(getDate(dateTimeStart));
            userEvent.setEventEnd(getDate(dateTimeEnd));
            toEdit = true;
            close((Node)actionEvent.getSource());
        }catch(ValidationException ve){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Validation error");
            alert.setContentText(ve.getMessage());
            alert.showAndWait();
        }

    }

    public void deleteEvent(ActionEvent actionEvent) {
        Alert yesNoDialog = new Alert(Alert.AlertType.CONFIRMATION,
                "Do you want to delete event '" + userEvent.getName() + "'?", ButtonType.YES,ButtonType.NO);
        yesNoDialog.showAndWait().filter(response -> response == ButtonType.YES)
                .ifPresent(buttonType -> {
                    toDelete = true;
                    close((Node)actionEvent.getSource());
                });
    }

    public boolean isToEdit() {
        return toEdit;
    }

    public boolean isToDelete() {
        return toDelete;
    }
}
