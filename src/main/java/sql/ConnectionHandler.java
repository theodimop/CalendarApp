package sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to provide connection to the database.
 */
public class ConnectionHandler {
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://irs6.host.cs.st-andrews.ac.uk:3306/irs6_cal";

    // Database credentials
    private static final String USER = "irs6";
    private static final String PASS = "rbUp4vf3!5CVFq";

    private static ConnectionHandler instance;
    private Connection connection;

    /**
     * Private constructor to enforce singleton.
     */
    private ConnectionHandler() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * To be used instead of the constructor.
     * @return a ConnectionHandler instance.
     */
    public static ConnectionHandler getInstance() {
        if (instance == null) {
            instance = new ConnectionHandler();
        }
        return instance;
    }

    /**
     * Returns the actual connection to the DB.
     * @return the connection object.
     */
    public java.sql.Connection getConnection() {
        return connection;
    }
}
