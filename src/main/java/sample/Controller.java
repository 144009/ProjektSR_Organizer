package sample;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import java.sql.*;

public class Controller {

    private String dbName;//="organizatordb";
    private String userDB = "root";
    private String passwordDB = "root";
    //Zmienne do tworzenia bazy danych
    private String text ="";
    private String urlDB ="jdbc:mysql://localhost/";

    private Database database;

    //zmienne pomocnicze do wykonywania poleceń SQL
    private int result;

    // Add a public no-args constructor
    public Controller()
    {
    }

    @FXML
    private void initialize()
    {

        //Okienka pojawiające się przed główną aplikacją. Podaje się user+pass+DB name
        TextInputDialog dialogInputUserDB = new TextInputDialog("DB user");
        dialogInputUserDB.setTitle("");
        dialogInputUserDB.setHeaderText("MySQL user, default: root");
        dialogInputUserDB.setContentText("DB user name:");
        Optional<String> userDBs = dialogInputUserDB.showAndWait();

        TextInputDialog dialogInputDBpass = new TextInputDialog("DB name");
        dialogInputDBpass.setTitle("");
        dialogInputDBpass.setHeaderText("MySQL pass, default:[blank]");
        dialogInputDBpass.setContentText("DB password:");
        Optional<String> passwordDBs = dialogInputDBpass.showAndWait();

        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");
        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Enter your DB name:");
        dialogInputDBName.setContentText("DB name:");
        Optional<String> dbNames = dialogInputDBName.showAndWait();



        userDB = userDBs.orElse("root");
        passwordDB = passwordDBs.orElse("root");
        dbName = dbNames.orElse("");

        database = new Database(dbName,userDB,passwordDB,urlDB);


        Label_CurrDBLoaded.setText("("+dbName+"; "+userDB+")");
    }

    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;
    @FXML
    private Label Label_CurrDBLoaded;

    @FXML
    private MenuItem MenuItem_CreateDB;

    @FXML
    private MenuItem MenuItem_DeleteDB;

    @FXML
    private MenuItem MenuItem_SetCurrentDB;

    @FXML
    private MenuItem MenuItem_SetAuthInfo;

    @FXML
    private MenuItem MenuItem_CheckConnection;

    @FXML
    private MenuItem MenuItem_DeleteEventID;

    @FXML
    private MenuItem MenuItem_DeleteOldEvents;

    @FXML
    private DatePicker Input_Date_EventDay;

    @FXML
    private DatePicker Input_Date_EventTime;

    @FXML
    private TextArea Input_Add_EventDesc;

    @FXML
    private Button Button_LoadEventById;

    @FXML
    private Button Button_AddEvent;

    @FXML
    private Button Button_ModifyEvent;

    @FXML
    private Spinner<?> Input_Spin_LoadEventById;

    @FXML
    private TextField Input_Add_EventName;

    @FXML
    private TextField Input_FreeSelect;

    @FXML
    private Button Button_FreeSelect;

    @FXML
    private Label Label_Info;

    @FXML
    private Spinner<?> Input_Spin_XNearEvents;

    @FXML
    private Button Button_ShowXNearEvents;

    @FXML
    private TextField Input_QuickSearchEventTitle;

    @FXML
    private TextArea Input_ShowResults;

    @FXML
    private Button Button_ClearResults;

    @FXML
    private Button Button_Exit;


    //okienko wyskakujące z informacją
    //użycie MainWindowUI.infoBox("YOUR INFORMATION HERE", "TITLE BAR MESSAGE");
    public static void infoBox(String infoMessage, String titleBar) // DONE
    {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);

        alert.showAndWait();
    }
    public static void warningBox(String infoMessage, String titleBar) // DONE
    {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(titleBar);
        alert.setHeaderText(null);
        alert.setContentText(infoMessage);

        alert.showAndWait();
    }

    public static void errorBox(String infoMessage, String titleBar, String header) // DONE
    {
        Alert alert = new Alert(AlertType.ERROR);
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
        } catch(Exception e){
            errorBox(e.getMessage(),"ERROR","Other error");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @FXML
    void Button_AddEvent_OnAction(ActionEvent event) {
        if(Input_Add_EventName.getText().equals("") || Input_Date_EventDay.getValue()==null || Input_Date_EventTime.getValue()==null) {
            warningBox("Fill all needed fields", "Error");
            return;
        }
        try {
            result = database.addEvent(Input_Add_EventName.getText(),Input_Add_EventDesc.getText(),Input_Date_EventDay.getValue(),Input_Date_EventTime.getValue());
            infoBox("Successfully saved new event","Info");
        } catch(Exception e){
            errorHandler(e);
        }
        Input_Date_EventDay.setValue(null);
        Input_Date_EventTime.setValue(null);
        Input_Add_EventDesc.setText("");
        Input_Add_EventName.setText("");
    } //DONE

    @FXML
    void Button_ClearResults_OnAction(ActionEvent event) { //DONE
        Input_ShowResults.setText(null);
        text = "";
    } //DONE

    @FXML
    void Button_Exit_OnAction(ActionEvent event) {
        System.exit(0);
    } //DONE



    @FXML
    void Button_FreeSelect_OnAction(ActionEvent event) {
        try {
            List<String> results = database.select(Input_FreeSelect.getText());
            StringBuilder builder = new StringBuilder("ID\t" + "Nazwa\t" + "Tresc\t" + "Data\t" + "Godzina\n");
            results.forEach(builder::append);
            text += builder.toString();
            Input_ShowResults.setText(text);
        } catch(Exception e){
            errorHandler(e);
        }

    } //DONE

    @FXML
    void Button_LoadEventById_OnAction(ActionEvent event) {
        try{
            int id = Integer.parseInt(Input_Spin_LoadEventById.getValue().toString());
            UserEvent userEvent = database.getEventById(id);
            Input_Date_EventDay.setValue(((java.sql.Date)userEvent.getEventDate()).toLocalDate());
            Input_Date_EventTime.setValue(((java.sql.Date)userEvent.getEventTime()).toLocalDate());
            Input_Add_EventName.setText(userEvent.getName());
            Input_Add_EventDesc.setText(userEvent.getDesc());
            Button_ModifyEvent.setDisable(false);
        } catch(Exception e){
            errorHandler(e);
        }
    }  //DONE

    @FXML
    void Button_ModifyEvent_OnAction(ActionEvent event) {
        try{
            UserEvent userEvent = new UserEvent(
                    Integer.parseInt(Input_Spin_LoadEventById.getValue().toString()),
                    Input_Add_EventName.getText(),
                    Input_Add_EventDesc.getText(),
                    java.sql.Date.valueOf(Input_Date_EventDay.getValue()),
                    java.sql.Date.valueOf(Input_Date_EventTime.getValue())
            );
            database.modifyEvent(userEvent);
            infoBox("Successfully modified event!", "Info");
            Button_ModifyEvent.setDisable(true);
        }catch(Exception e){
            errorHandler(e);
        }

        Input_Date_EventDay.setValue(null);
        Input_Date_EventTime.setValue(null);
        Input_Add_EventDesc.setText("");
        Input_Add_EventName.setText("");
    } //DONE

    @FXML
    void Button_ShowXNearEvents_OnAction(ActionEvent event) {
        //pobierz z database i wyświetl BY DATE + num
        try{
            StringBuilder builder = new StringBuilder("ID\t" + "Nazwa\t" + "Tresc\t" + "Data\t" + "Godzina\n");
            database.getNearbyEvents(Integer.parseInt(Input_Spin_XNearEvents.getValue().toString()))
                    .forEach(builder::append);
            text += builder.toString();
            Input_ShowResults.setText(text);
        }catch(Exception e){
            errorHandler(e);
        }
    } //DONE

    @FXML
    void Input_QuickSearchEventTitle_OnKeyReleased(KeyEvent event) {
        try{
            StringBuilder builder = new StringBuilder("ID\t" + "Nazwa\t" + "Tresc\t" + "Data\t" + "Godzina\n");
            Input_ShowResults.setText(null);
            text = "";
            database.searchByName(Input_QuickSearchEventTitle.getText()).forEach(builder::append);
            text += builder.toString();
            Input_ShowResults.setText(text);
        }catch(Exception e){
            errorHandler(e);
        }
    } //DONE

    @FXML
    void MenuItem_CheckConnection_OnAction(ActionEvent event) {
        try {
            database.checkConnection();
            infoBox("Connection succesful!","");
        } catch (Exception e){
            errorHandler(e);
        }
    } //DONE

    @FXML
    void MenuItem_CreateDB_OnAction(ActionEvent event) {
        //utworzenie bazy danych jeśli nie istnieje

        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");

        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Enter DB name:");
        dialogInputDBName.setContentText("Name:");
        Optional<String> dbNews = dialogInputDBName.showAndWait();

        try {
            String dbNew = dbNews.orElseThrow(() -> new Exception("Invalid input"));
            result = database.createDatabase(dbNew);
            infoBox("Pomyślnie utworzono bazę danych "+dbNew, "");
        }catch(Exception e){
            errorHandler(e);
        }

    } //DONE

    @FXML
    void MenuItem_DeleteDB_OnAction(ActionEvent event) {
        //usunięcie bazy danych
        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");

        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Enter DB name to delete:");
        dialogInputDBName.setContentText("Name:");
        Optional<String> dbNews = dialogInputDBName.showAndWait();

        try {
            String dbNew = dbNews.orElseThrow(() -> new Exception("Invalid input"));
            result = database.deleteDatabase(dbNew);
            infoBox("Succesfuly deleted DB: "+dbNew, "");
        }catch(Exception e){
            errorHandler(e);
        }


    } //DONE

    @FXML
    void MenuItem_DeleteEventID_OnAction(ActionEvent event) {
        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");

        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Delete event:");
        dialogInputDBName.setContentText("ID of event to delete:");
        Optional<String> IDas = dialogInputDBName.showAndWait();

        try {
            String stringId = IDas.orElseThrow(() -> new Exception("Invalid input"));

            int id = Integer.parseInt(stringId);
            result = database.deleteEvent(id);//database.deleteDatabase(dbNew);
            infoBox("Succesfuly deleted record: "+id, "");
        }catch(Exception e){
            errorHandler(e);
        }
    } //DONE

    @FXML
    void MenuItem_DeleteOldEvents_OnAction(ActionEvent event) {
        try{
            result = database.deleteOldEvents();
            infoBox("Succesfuly deleted all old events!","");
        }catch(Exception e){
            errorHandler(e);
        }
    }

    @FXML
    void MenuItem_SetAuthInfo_OnAction(ActionEvent event) {
        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");
        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Enter DB user name:");
        dialogInputDBName.setContentText("DB user name:");
        Optional<String> userDBs = dialogInputDBName.showAndWait();

        TextInputDialog dialogInputDBpass = new TextInputDialog("DB name");
        dialogInputDBpass.setTitle("");
        dialogInputDBpass.setHeaderText("Enter DB pass:");
        dialogInputDBpass.setContentText("DB password:");
        Optional<String> passwordDBs = dialogInputDBpass.showAndWait();


        database.setUser(userDBs.orElse("root"));
        database.setPassword(passwordDBs.orElse(""));

        Label_CurrDBLoaded.setText("("+database.getName()+"; "+database.getUser()+")");
    }

    @FXML
    void MenuItem_SetCurrentDB_OnAction(ActionEvent event) {

        TextInputDialog dialogInputDBpass = new TextInputDialog("DB name");
        dialogInputDBpass.setTitle("");
        dialogInputDBpass.setHeaderText("Enter DB name:");
        dialogInputDBpass.setContentText("DB name that will hold data:");
        Optional<String> dbNames = dialogInputDBpass.showAndWait();

        database.setName(dbNames.orElse(""));

        Label_CurrDBLoaded.setText("("+database.getName()+"; "+database.getUser()+")");
    }

}
