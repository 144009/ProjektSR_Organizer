package sample;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> select(String additionalQuery) throws DatabaseNotFoundException, SQLException, ClassNotFoundException {
        try(Connection connection = connect()){
            useDB(connection,name);
            List<String> list = new ArrayList<>();
            try(Statement statement = connection.createStatement()){
                String query = "SELECT * FROM dbTable "+additionalQuery;
                try(ResultSet rs = statement.executeQuery(query)){
                    while(rs.next()){
                        int id = rs.getInt("id");
                        String str1 = rs.getString("event_title");
                        String str2 = rs.getString("event_desc");
                        java.util.Date str3 = rs.getDate("event_date");
                        String str4 = rs.getString("event_time");
                        list.add(id + "\t" + str1 + "\t" + str2 + "\t" + str3 + "\t" + str4 + "\n");
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
                        /*
                        int id = rs.getInt("id");
                        String str1 = rs.getString("event_title");
                        String str2 = rs.getString("event_desc");
                        java.util.Date str3 = rs.getDate("event_date");
                        String str4 = rs.getString("event_time");
                        list.add(id + "\t" + str1 + "\t" + str2 + "\t" + str3 + "\t" + str4 + "\n");*/
                    /*    int id = rs.getInt("id");
                        String str1 = rs.getString("event_title");
                        String str2 = rs.getString("event_desc");
                        java.util.Date str3 = rs.getDate("event_date");
                        java.util.Date str4 = rs.getDate("event_time");*/
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
}
