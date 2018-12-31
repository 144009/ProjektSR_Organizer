package organizer.dialects;

import organizer.DialectQueries;

/**
 * Created by Maciej on 2018-12-31 19:04
 */
public class MySQLDialectQueries implements DialectQueries {
    @Override
    public String createDatabaseQuery() {
        return "CREATE DATABASE IF NOT EXISTS ";
    }

    @Override
    public String createTableQuery() {
        return "CREATE TABLE dbTable (id INT NOT NULL" +
                " AUTO_INCREMENT PRIMARY KEY, " +
                "event_title VARCHAR(30) NOT NULL, " +
                "event_desc VARCHAR(255), " +
                "event_date DATE NOT NULL, " +
                "event_time DATE NOT NULL)";
    }

    @Override
    public String urlDriverName() {
        return "mysql";
    }
}
