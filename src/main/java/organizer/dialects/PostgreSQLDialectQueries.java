package organizer.dialects;

import organizer.DialectQueries;

/**
 * Created by Maciej on 2018-12-31 19:07
 */
public class PostgreSQLDialectQueries implements DialectQueries {
    @Override
    public String createDatabaseQuery() {
        return "CREATE DATABASE ";
    }

    @Override
    public String createTableQuery() {
        return "CREATE TABLE dbTable (id SERIAL PRIMARY KEY, " +
                "event_title VARCHAR(30) NOT NULL, " +
                "event_desc VARCHAR(255), " +
                "event_begin timestamp NOT NULL, " +
                "event_end timestamp NOT NULL)";
    }

    @Override
    public String urlDriverName() {
        return "postgresql";
    }
}
