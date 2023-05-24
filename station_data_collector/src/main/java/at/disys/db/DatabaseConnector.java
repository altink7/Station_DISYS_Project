package at.disys.db;

import java.sql.*;

/**
 * This class is responsible for the connection to the database POSTGRES.
 */
public class DatabaseConnector {
    private final String username = "postgres";
    private final String password = "postgres";
    private Connection connection;

    /**
     * Connects to the database POSTGRES
     */
    public void connect(String connectionURL) {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(connectionURL, username, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
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
            System.out.println("Disconnected from the PostgreSQL server successfully.");
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

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * we use the urls provided from the central database
     * @param hostPort the host and port of the station database
     * @return the url of the station database
     */
    public static String getStationUrl(String hostPort) {
        return "jdbc:postgresql://" + hostPort + "/stationdb";
    }
}

