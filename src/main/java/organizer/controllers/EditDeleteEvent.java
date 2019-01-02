package organizer.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import organizer.UserEvent;
import organizer.exceptions.ValidationException;


/**
 * Created by Maciej on 2018-12-31 13:09
 */
public class EditDeleteEvent {

    public TextField eventName;
    public DatePicker eventBeginPicker;
    public DatePicker eventEndPicker;
    public TextArea eventDesc;

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
        eventBeginPicker.setValue(((java.sql.Date)userEvent.getEventBegin()).toLocalDate());
        eventEndPicker.setValue(((java.sql.Date)userEvent.getEventEnd()).toLocalDate());
    }

    private void close(Node node){
        ((Stage)node.getScene().getWindow()).close();
    }


    public UserEvent getUserEvent() {
        return userEvent;
    }

    private java.util.Date getDate(java.time.LocalDate date){
        return java.sql.Date.valueOf(date);
    }

    public void editEvent(ActionEvent actionEvent) {

        try{
            validateFields();
            userEvent.setName(eventName.getText());
            userEvent.setDesc(eventDesc.getText());
            userEvent.setEventBegin(getDate(eventBeginPicker.getValue()));
            userEvent.setEventEnd(getDate(eventEndPicker.getValue()));
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
