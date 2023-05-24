package at.disys.db;

import java.sql.*;

/**
 * This class is responsible for the connection to the database POSTGRES.
 */
public class DatabaseConnector {
    private final String username = "postgres";
    private final String password = "postgres";
    private final String host = "jdbc:postgresql://localhost:30002/stationdb";
    private Connection connection;

    /**
     * Connects to the database POSTGRES
     */
    public void connect() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(host, username, password);
            System.out.println(" \nConnected to the PostgreSQL server successfully.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Disconnects from the database POSTGRES
     */
    public void disconnect() {
        try {
            connection.close();
            System.out.println("Disconnected from the PostgreSQL server " + host + " successfully. \n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Executes an SQL query and returns the result set
     */
    public ResultSet executeSQLQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

