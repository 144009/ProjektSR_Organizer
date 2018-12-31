package sample;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Maciej on 2018-12-28 10:39
 */
public class Database {
    private String name;
    private String user;
    private String password;
    private String url;

    public Database(String name, String user, String password, String url) {
        this.name = name;
        this.user = user;
        this.password = password;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public Connection connect() throws ClassNotFoundException, SQLException, DatabaseNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");// load database driver class
        Connection connection = DriverManager.getConnection(url,user,password);
        ResultSet resultSet = connection.getMetaData().getCatalogs();
        boolean check = false;
        while (resultSet.next()) {
            String databaseName = resultSet.getString(1);
            if(databaseName.equals(name)){check=true;}
        }
        if(!check)
            throw new DatabaseNotFoundException();
        resultSet.close();
        return connection;
    }

    private void useDB(Connection conn,String name) throws SQLException {
        try(Statement statement = conn.createStatement()){
            statement.executeUpdate("USE " + name);
        }
    }

    public int addEvent(String eventName, String eventDesc, LocalDate eventDay, LocalDate eventTime) throws DatabaseNotFoundException, SQLException, ClassNotFoundException {
        try(Connection connection = connect()){
            useDB(connection,name);
            String query = "INSERT INTO dbTable"
                    + "(event_title, event_desc, event_date, event_time) VALUES"
                    + "(?,?,?,?)";
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, eventName );
                statement.setString(2, eventDesc );
                statement.setDate(3, java.sql.Date.valueOf(eventDay));
                statement.setString(4, eventTime.toString() );
                int result = statement.executeUpdate();
                return result;
            }
        }
    }

    public static String eventToString(UserEvent event){
        return event.getId() + "\t" + event.getName() + "\t" + event.getDesc() + "\t"
                + event.getEventDate() + "\t" + event.getEventTime() + "\n";
    }

    public static List<String> setAsString(List<UserEvent> events){
        return events.stream().map(Database::eventToString).collect(Collectors.toList());
    }


    public List<UserEvent> select(String additionalQuery) throws DatabaseNotFoundException, SQLException, ClassNotFoundException {
        try(Connection connection = connect()){
            useDB(connection,name);
            List<UserEvent> list = new ArrayList<>();
            try(Statement statement = connection.createStatement()){
                String query = "SELECT * FROM dbTable "+additionalQuery;
                try(ResultSet rs = statement.executeQuery(query)){
                    while(rs.next()){
                        UserEvent event = new UserEvent(
                                rs.getInt("id"),
                                rs.getString("event_title"),
                                rs.getString("event_desc"),
                                rs.getDate("event_date"),
                                rs.getDate("event_time")
                        );
                        list.add(event);
                    }
                    return list;
                }
            }

        }
    }

    public UserEvent getEventById(int id) throws DatabaseNotFoundException, SQLException, ClassNotFoundException {
        try(Connection connection = connect()){
            useDB(connection, name);
            try(Statement statement = connection.createStatement()) {
                String query = "SELECT * FROM dbTable WHERE ID="+id+"";
                try(ResultSet rs = statement.executeQuery(query)){
                    UserEvent event = null;
                    while(rs.next()){
                        event = new UserEvent(
                                rs.getInt("id"),
                                rs.getString("event_title"),
                                rs.getString("event_desc"),
                                rs.getDate("event_date"),
                                rs.getDate("event_time")
                        );
                    }
                    return event;
                }
            }
        }
    }

    public int modifyEvent(UserEvent event) throws DatabaseNotFoundException, SQLException, ClassNotFoundException {
        try(Connection connection = connect()){
            useDB(connection, name);
            String query = "UPDATE dbTable SET event_title = ?, event_desc = ?, "
                    + "event_date = ?, event_time = ? WHERE ID = "+event.getId()+"";
            try(PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1,event.getName());
                statement.setString(2,event.getDesc());
                statement.setDate(3, (Date) event.getEventDate());
                statement.setDate(4, (Date) event.getEventTime());
                return statement.executeUpdate();
            }
        }
    }

    public List<UserEvent> getNearbyEvents(int count) throws ClassNotFoundException, SQLException, DatabaseNotFoundException {
        return select(" ORDER BY ABS( DATEDIFF( event_date, NOW() ) ) \n LIMIT "+count+"");
    }

    public List<UserEvent> getNearbyUpcomingEvents(int count) throws ClassNotFoundException, SQLException, DatabaseNotFoundException {
        return select("WHERE event_date >= NOW() ORDER BY event_date ASC LIMIT "+count+" ");
    }

    public List<UserEvent> getNearbyFinishedEvents(int count) throws ClassNotFoundException, SQLException, DatabaseNotFoundException {
        return select("WHERE event_date < NOW() ORDER BY event_date DESC LIMIT "+count);
    }

    public List<UserEvent> searchByName(String searchString) throws ClassNotFoundException, SQLException, DatabaseNotFoundException {
        return select(" WHERE event_title LIKE '" + searchString + "%'");
    }

    public void checkConnection() throws DatabaseNotFoundException, SQLException, ClassNotFoundException {
        //noinspection EmptyTryBlock
        try(Connection ignored = connect()){
        }
    }

    public int createDatabase(String name) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");// load database driver class
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            try(Statement createDBStatement = connection.createStatement()){
                createDBStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS "+name);
                useDB(connection,name);
                String query = "CREATE TABLE dbTable (id INT NOT NULL" +
                        " AUTO_INCREMENT PRIMARY KEY, " +
                        "event_title VARCHAR(30) NOT NULL, " +
                        "event_desc VARCHAR(255), " +
                        "event_date DATE NOT NULL, " +
                        "event_time DATE NOT NULL)";
                try(Statement addTableStatement = connection.createStatement()){
                    return addTableStatement.executeUpdate(query);
                }
            }
        }
    }

    public int deleteDatabase(String name) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");// load database driver class
        try(Connection connection = DriverManager.getConnection(url,user,password)){
            try(Statement createDBStatement = connection.createStatement()){
                return createDBStatement.executeUpdate("DROP DATABASE IF EXISTS "+name);
            }
        }
    }

    public int deleteEvent(int id) throws DatabaseNotFoundException, SQLException, ClassNotFoundException {
        try (Connection connection = connect()) {
            useDB(connection,name);
            try(Statement statement = connection.createStatement()){
                return statement.executeUpdate("DELETE FROM dbTable WHERE ID="+id+"");
            }
        }
    }

    public int deleteOldEvents() throws DatabaseNotFoundException, SQLException, ClassNotFoundException {
        try (Connection connection = connect()) {
            useDB(connection,name);
            try(Statement statement = connection.createStatement()){
                return statement.executeUpdate("DELETE FROM dbTable WHERE DATE(event_date)<DATE(NOW())");
            }
        }
    }


}
