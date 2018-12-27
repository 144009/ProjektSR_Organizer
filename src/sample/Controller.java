package sample;

import java.net.URL;
import java.time.LocalDate;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.ActionEvent;
import javafx.scene.input.KeyEvent;
import java.util.Date;
import java.sql.*;
import java.time.LocalTime;

public class Controller {

    private String dbName;//="organizatordb";
    private String userDB = "root";
    private String passwordDB = "root";
    //Zmienne do tworzenia bazy danych
    private String text ="";
    private String urlDB ="jdbc:mysql://localhost/";

    //zmienne pomocnicze do wykonywania poleceń SQL
    private int result;
    private String query;

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





        userDB = userDBs.get();
        passwordDB = passwordDBs.get();
        dbName = dbNames.get();

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

    @FXML
    void Button_AddEvent_OnAction(ActionEvent event) {
        // TODO add your handling code here:
        int check = 0;

        //CHECK IF DATABASE GIVEN ALREADY EXISTS
        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
        //    JOptionPane.showMessageDialog(null,
        //            classNotFound.getMessage(), "Driver Not Found",
        //            JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        try {

            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if(databaseName.equals(dbName)){check=1;}
            }
            resultSet.close();

            connection.close();
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
        //    JOptionPane.showMessageDialog(null,
        //            , "Database Error",
        //            JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        if(dbName.equals("")){
            warningBox("You need to set a DB that will hold your data!","WARNING");
        //    JOptionPane.showMessageDialog( null,"You need to set a DB that will hold your data!");
        }
        else if(check==0){
            warningBox("Given DB doesnt exist. Create it or change to other DB","WARNING");
         //   JOptionPane.showMessageDialog( null,"Given DB doesnt exist. Create it or change to other DB");
        }
        else{
            if(Input_Add_EventName.getText().equals("") || Input_Date_EventDay.getValue()==null || Input_Date_EventTime.getValue()==null){
                warningBox("Fill all needed fields","Error");
            } else{

                //ZAPISANIE DO BAZY DANYCH - event_title/desc/date/time + id

                //nazwa wydarzenia
                String eventTitle = Input_Add_EventName.getText();
                //opis wydarzenia
                String eventDesc = Input_Add_EventDesc.getText();
                //data wydarzenia
                //Date oDate = Input_Date_EventDay.getValue();

                java.sql.Date sqlDate = java.sql.Date.valueOf(Input_Date_EventDay.getValue());

                //godzina wydarzenia https://github.com/LGoodDatePicker/LGoodDatePicker/blob/master/Project/src/main/java/com/github/lgooddatepicker/demo/FullDemo.java
               // String oTime = timePicker.getTimeStringOrSuppliedString("(null)");
                String oTime = Input_Date_EventTime.getValue().toString();

                try {
                    Class.forName("com.mysql.jdbc.Driver");// load database driver class
                } catch (ClassNotFoundException classNotFound) {
                    errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
                    System.exit(1);
                }
                //połączenie z bazą danych
                try {
                    Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
                    // Connection connection = DriverManager.getConnection(connectionUrl);
                    // create Statement to query database
                    Statement statement = connection.createStatement();
                    query ="USE "+dbName;
                    result = statement.executeUpdate(query);

                    query = "INSERT INTO dbTable"
                            + "(event_title, event_desc, event_date, event_time) VALUES"
                            + "(?,?,?,?)";
                    PreparedStatement statement1 = connection.prepareStatement(query);
                    statement1.setString(1, eventTitle );
                    statement1.setString(2, eventDesc );
                    statement1.setDate(3, sqlDate);
                    statement1.setString(4, oTime );

                    result = statement1.executeUpdate();

                    infoBox("Successfully saved new event","Info");
                    //zamykamy połączenie i statement
                    statement.close();
                    connection.close();
                } catch (SQLException sqlException) {
                    errorBox(sqlException.getMessage(),"ERROR","Database Error");
                    System.exit(1);
                }

                //DateFormat oDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                //String szDate = oDateFormat.format(oDate);
            }

        }
        Input_Date_EventDay.setValue(null);
        Input_Date_EventTime.setValue(null);
        Input_Add_EventDesc.setText("");
        Input_Add_EventName.setText("");
    } //DONE

    @FXML
    void Button_ClearResults_OnAction(ActionEvent event) { //DONE
        Input_ShowResults.setText(null);
    } //DONE

    @FXML
    void Button_Exit_OnAction(ActionEvent event) {
        System.exit(0);
    } //DONE

    @FXML
    void Button_FreeSelect_OnAction(ActionEvent event) {
        //custom select

        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
            //System.exit(1);
        }
        //połączenie z bazą danych
        try {
            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            // Connection connection = DriverManager.getConnection(connectionUrl);
            // create Statement to query database
            Statement statement = connection.createStatement();
            query ="USE "+dbName;
            result = statement.executeUpdate(query);

            query = "SELECT * FROM dbTable "+Input_FreeSelect.getText();

            // query = "SELECT * FROM dbTable ORDER BY DATE(event_date)<DATE(NOW()) LIMIT";
            ResultSet rs = statement.executeQuery(query);
            text="";
            text = text + "ID\t" + "Nazwa\t" + "Tresc\t" + "Data\t" + "Godzina\n";

            while (rs.next()) {
                int id = rs.getInt("id");
                String str1 = rs.getString("event_title");
                String str2 = rs.getString("event_desc");
                Date str3 = rs.getDate("event_date");
                String str4 = rs.getString("event_time");
                text = text + id + "\t" + str1 + "\t" + str2 + "\t" + str3 + "\t" + str4 + "\n";
            }
            Input_ShowResults.setText(text);

            //zamykamy połączenie i statement
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
            //System.exit(1);
        }
    } //DONE

    @FXML
    void Button_LoadEventById_OnAction(ActionEvent event) {
        //load data from DB to fields to modify
        int ID = Integer.parseInt(Input_Spin_LoadEventById.getValue().toString());

        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
            System.exit(1);
        }
        //połączenie z bazą danych
        try {
            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            // Connection connection = DriverManager.getConnection(connectionUrl);
            // create Statement to query database
            Statement statement = connection.createStatement();
            query ="USE "+dbName;
            result = statement.executeUpdate(query);

            query = "SELECT * FROM dbTable WHERE ID="+ID+"";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String str1 = rs.getString("event_title");
                String str2 = rs.getString("event_desc");
                Date str3 = rs.getDate("event_date");
                Date str4 = rs.getDate("event_time");

                Input_Date_EventDay.setValue(((java.sql.Date) str3).toLocalDate());
                Input_Date_EventTime.setValue(((java.sql.Date) str4).toLocalDate());
                Input_Add_EventName.setText(str1);
                Input_Add_EventDesc.setText(str2);

            }

            Button_ModifyEvent.setDisable(false);
            Label_Info.setText("Modify event:");
            //zamykamy połączenie i statement
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
            System.exit(1);
        }
    }  //DONE

    @FXML
    void Button_ModifyEvent_OnAction(ActionEvent event) {
        int check = 0;
        int ID = Integer.parseInt(Input_Spin_LoadEventById.getValue().toString());
        //CHECK IF DATABASE GIVEN ALREADY EXISTS
        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
            System.exit(1);
        }
        try {

            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if(databaseName.equals(dbName)){check=1;}
            }
            resultSet.close();

            connection.close();
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
            System.exit(1);
        }

        if(dbName.equals("")){
            warningBox("You need to set a DB that will hold your data!", "Warning");
        }
        else if(check==0){
            warningBox("Given DB doesnt exist. Create it or change to other DB", "Warning");
        }
        else{
            if(Input_Add_EventName.getText().equals("") || Input_Date_EventDay.getValue()==null || Input_Date_EventTime.getValue()==null){
                warningBox("Fill all needed fields!", "Warning");

            } else{

                //ZAPISANIE DO BAZY DANYCH - event_title/desc/date/time + id

                //nazwa wydarzenia
                String eventTitle = Input_Add_EventName.getText();
                //opis wydarzenia
                String eventDesc = Input_Add_EventDesc.getText();
                //data wydarzenia
                java.sql.Date sqlDate = java.sql.Date.valueOf(Input_Date_EventDay.getValue());

                //godzina wydarzenia https://github.com/LGoodDatePicker/LGoodDatePicker/blob/master/Project/src/main/java/com/github/lgooddatepicker/demo/FullDemo.java
                String oTime = Input_Date_EventTime.getValue().toString();

                try {
                    Class.forName("com.mysql.jdbc.Driver");// load database driver class
                } catch (ClassNotFoundException classNotFound) {
                    errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
                    System.exit(1);
                }
                //połączenie z bazą danych
                try {
                    Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
                    // Connection connection = DriverManager.getConnection(connectionUrl);
                    // create Statement to query database
                    Statement statement = connection.createStatement();
                    query ="USE "+dbName;
                    result = statement.executeUpdate(query);

                    query = "UPDATE dbTable SET event_title = ?, event_desc = ?, "
                            + "event_date = ?, event_time = ? WHERE ID = "+ID+"";

                    PreparedStatement statement1 = connection.prepareStatement(query);
                    statement1.setString(1, eventTitle );
                    statement1.setString(2, eventDesc );
                    statement1.setDate(3, sqlDate);
                    statement1.setString(4, oTime );

                    result = statement1.executeUpdate();

                    infoBox("Successfully modified event!", "Info");
                    Button_ModifyEvent.setDisable(true);
                    Label_Info.setText("Add new event: ");
                    //zamykamy połączenie i statement
                    statement.close();
                    connection.close();
                } catch (SQLException sqlException) {
                    errorBox(sqlException.getMessage(),"ERROR","Database Error");
                    System.exit(1);
                }

                //DateFormat oDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                //String szDate = oDateFormat.format(oDate);
            }

        }

        Input_Date_EventDay.setValue(null);
        Input_Date_EventTime.setValue(null);
        Input_Add_EventDesc.setText("");
        Input_Add_EventName.setText("");
    } //DONE

    @FXML
    void Button_ShowXNearEvents_OnAction(ActionEvent event) {
        //pobierz z database i wyświetl BY DATE + num

        int number = Integer.parseInt(Input_Spin_XNearEvents.getValue().toString());
        Input_ShowResults.setText(null);

        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
            System.exit(1);
        }
        //połączenie z bazą danych
        try {
            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            // Connection connection = DriverManager.getConnection(connectionUrl);
            // create Statement to query database
            Statement statement = connection.createStatement();
            query ="USE "+dbName;
            result = statement.executeUpdate(query);

            query = "SELECT * FROM dbTable ORDER BY DATE(event_date)>DATE(NOW()) DESC LIMIT "+number+"";
            ResultSet rs = statement.executeQuery(query);
            text="";
            text = text + "ID\t" + "Nazwa\t" + "Tresc\t" + "Data\t" + "Godzina\n";

            while (rs.next()) {
                int id = rs.getInt("id");
                String str1 = rs.getString("event_title");
                String str2 = rs.getString("event_desc");
                Date str3 = rs.getDate("event_date");
                String str4 = rs.getString("event_time");
                text = text + id + "\t" + str1 + "\t" + str2 + "\t" + str3 + "\t" + str4 + "\n";
            }
            Input_ShowResults.setText(text);

            //zamykamy połączenie i statement
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
            System.exit(1);
        }
    } //DONE

    @FXML
    void Input_QuickSearchEventTitle_OnKeyReleased(KeyEvent event) {
        int check = 0;

        //CHECK IF DATABASE GIVEN ALREADY EXISTS
        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
            System.exit(1);
        }
        try {

            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            while (resultSet.next()) {
                String databaseName = resultSet.getString(1);
                if(databaseName.equals(dbName)){check=1;}
            }
            resultSet.close();

            connection.close();
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
            System.exit(1);
        }

        if(check==0){

        }
        else{

            try {
                Class.forName("com.mysql.jdbc.Driver");// load database driver class
            } catch (ClassNotFoundException classNotFound) {
                errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
                System.exit(1);
            }
            //połączenie z bazą danych
            try {
                Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
                Statement statement = connection.createStatement();
                query ="USE "+dbName;
                result = statement.executeUpdate(query);
                query ="SELECT * FROM dbTable WHERE event_title LIKE '"+Input_FreeSelect.getText()+"%'";
                ResultSet rs = statement.executeQuery(query);
                text="";
                text = text + "ID\t" + "Nazwa\t" + "Tresc\t" + "Data\t" + "Godzina\n";

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String str1 = rs.getString("event_title");
                    String str2 = rs.getString("event_desc");
                    Date str3 = rs.getDate("event_date");
                    String str4 = rs.getString("event_time");
                    text = text + id + "\t" + str1 + "\t" + str2 + "\t" + str3 + "\t" + str4 + "\n";
                }
                Input_ShowResults.setText(text);

                //zamykamy połączenie i statement
                statement.close();
                connection.close();
            } catch (SQLException sqlException) {
                errorBox(sqlException.getMessage(),"ERROR","Database Error");
                System.exit(1);
            }

        }
    } //DONE

    @FXML
    void MenuItem_CheckConnection_OnAction(ActionEvent event) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(urlDB, userDB, passwordDB);
            infoBox("Connection succesful!","");
        } catch (SQLException e) {
            infoBox("SQL Exception: "+ e.toString(),"");
        } catch (ClassNotFoundException cE) {
            infoBox("Class Not Found Exception: "+ cE.toString(),"");
        }
    } //DONE

    @FXML
    void MenuItem_CreateDB_OnAction(ActionEvent event) {
        //utworzenie bazy danych jeśli nie istnieje
        //userDB = JOptionPane.showInputDialog("Użytkownik mysql, domyślnie: root");
        //passwordDB = JOptionPane.showInputDialog("Hasło mysql, domyślnie:[blank]");

        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");

        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Enter DB name:");
        dialogInputDBName.setContentText("Name:");
        Optional<String> dbNews = dialogInputDBName.showAndWait();

        String dbNew = dbNews.get();

        //setTitle("Organizator "+dbName);
        //ładujemy driver java db class
        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
            System.exit(1);
        }
        //połączenie z bazą danych
        try {
            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            // Connection connection = DriverManager.getConnection(connectionUrl);
            // create Statement to query database
            Statement statement = connection.createStatement();
            //String query = "DROP TABLE IF EXISTS myDatabaseTable";
            result = statement.executeUpdate("CREATE DATABASE IF NOT EXISTS "+dbNew);
            query ="USE "+dbNew;
            result = statement.executeUpdate(query);
            query = "CREATE TABLE dbTable (id INT NOT NULL" +
                    " AUTO_INCREMENT PRIMARY KEY, " +
                    "event_title VARCHAR(30) NOT NULL, " +
                    "event_desc VARCHAR(255), " +
                    "event_date DATE NOT NULL, " +
                    "event_time DATE NOT NULL)";
            result = statement.executeUpdate(query);

            infoBox("Pomyślnie utworzono bazę danych "+dbNew, "");
            //zamykamy połączenie i statement
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
            System.exit(1);
        }
    } //DONE

    @FXML
    void MenuItem_DeleteDB_OnAction(ActionEvent event) {
        //usunięcie bazy danych
        //userDB = JOptionPane.showInputDialog("Użytkownik mysql, domyślnie: root");
        //passwordDB = JOptionPane.showInputDialog("Hasło mysql, domyślnie:[blank]");
        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");

        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Enter DB name to delete:");
        dialogInputDBName.setContentText("Name:");
        Optional<String> dbNews = dialogInputDBName.showAndWait();

        String dbNew = dbNews.get();
        //ładujemy driver java db class
        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
            System.exit(1);
        }
        //połączenie z bazą danych
        try {
            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            // Connection connection = DriverManager.getConnection(connectionUrl);
            // create Statement to query database
            Statement statement = connection.createStatement();
            //String query = "DROP TABLE IF EXISTS myDatabaseTable";

            result = statement.executeUpdate("DROP DATABASE IF EXISTS "+dbNew);

            //zamykamy połączenie i statement
            statement.close();
            connection.close();
            infoBox("Succesfuly deleted DB:"+dbNew, "");
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
            System.exit(1);
        }
    } //DONE

    @FXML
    void MenuItem_DeleteEventID_OnAction(ActionEvent event) {
        TextInputDialog dialogInputDBName = new TextInputDialog("DB name");

        dialogInputDBName.setTitle("");
        dialogInputDBName.setHeaderText("Delete event:");
        dialogInputDBName.setContentText("ID of event to delete:");
        Optional<String> IDas = dialogInputDBName.showAndWait();

        String IDa = IDas.get();

        int ID = Integer.parseInt(IDa);
        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
            System.exit(1);
        }
        //połączenie z bazą danych
        try {
            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            Statement statement = connection.createStatement();
            query ="USE "+dbName;
            result = statement.executeUpdate(query);

            query = "DELETE FROM dbTable WHERE ID="+ID+"";

            result = statement.executeUpdate(query);
            infoBox("Succesfuly deleted record:"+ID, "");
            //zamykamy połączenie i statement
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
            System.exit(1);
        }
    } //DONE

    @FXML
    void MenuItem_DeleteOldEvents_OnAction(ActionEvent event) {
        try {
            Class.forName("com.mysql.jdbc.Driver");// load database driver class
        } catch (ClassNotFoundException classNotFound) {
            errorBox(classNotFound.getMessage(),"ERROR","Driver Not Found");
            System.exit(1);
        }
        //połączenie z bazą danych
        try {
            Connection connection = DriverManager.getConnection(urlDB, userDB, passwordDB);
            Statement statement = connection.createStatement();
            query ="USE "+dbName;
            result = statement.executeUpdate(query);

            query = "DELETE FROM dbTable WHERE DATE(event_date)<DATE(NOW())";

            result = statement.executeUpdate(query);
            infoBox("Succesfuly deleted all old events!","");
            //zamykamy połączenie i statement
            statement.close();
            connection.close();
        } catch (SQLException sqlException) {
            errorBox(sqlException.getMessage(),"ERROR","Database Error");
            System.exit(1);
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


        userDB = userDBs.get();
        passwordDB = passwordDBs.get();

        Label_CurrDBLoaded.setText("("+dbName+"; "+userDB+")");
    }

    @FXML
    void MenuItem_SetCurrentDB_OnAction(ActionEvent event) {

        TextInputDialog dialogInputDBpass = new TextInputDialog("DB name");
        dialogInputDBpass.setTitle("");
        dialogInputDBpass.setHeaderText("Enter DB name:");
        dialogInputDBpass.setContentText("DB name that will hold data:");
        Optional<String> dbNames = dialogInputDBpass.showAndWait();

        dbName = dbNames.get();

        Label_CurrDBLoaded.setText("("+dbName+"; "+userDB+")");

    }

}
