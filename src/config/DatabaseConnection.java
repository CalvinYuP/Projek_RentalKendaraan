package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet; // Ditambahkan
import java.sql.SQLException;
import java.sql.Statement; // Ditambahkan
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for managing database connections.
 * This class provides a singleton connection to the MySQL database.
 * It ensures that database credentials are managed in one place and
 * connections are properly closed.
 */
public class DatabaseConnection {
    // Logger for logging database connection events and errors.
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());

    // Database connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/rental_kendaraan";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Consider using environment variables or a config file for production

    // Private constructor to prevent instantiation (Singleton pattern)
    private DatabaseConnection() {
        // Prevent instantiation
    }

    /**
     * Establishes and returns a connection to the database.
     * This method ensures that the JDBC driver is loaded and a connection
     * is created successfully.
     *
     * @return A valid {@link Connection} object to the database.
     * @throws SQLException if a database access error occurs or the URL is null.
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            // Load the MySQL JDBC driver. This is typically not needed explicitly since JDBC 4.0
            // but is good practice for older environments or specific setups.
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Establish the connection
            connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            LOGGER.log(Level.INFO, "Database connection established successfully.");
        } catch (ClassNotFoundException e) {
            // Log a severe error if the JDBC driver is not found.
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found: " + e.getMessage(), e);
            throw new SQLException("MySQL JDBC Driver not found.", e);
        } catch (SQLException e) {
            // Log a severe error if a SQL exception occurs during connection.
            LOGGER.log(Level.SEVERE, "Failed to connect to database: " + e.getMessage(), e);
            throw e; // Re-throw the exception to be handled by the caller
        }
        return connection;
    }

    /**
     * Closes the given database connection, statement, and result set.
     * This is a utility method to ensure all JDBC resources are properly released.
     *
     * @param conn The {@link Connection} object to close. Can be null.
     * @param stmt The {@link Statement} object to close. Can be null.
     * @param rs The {@link ResultSet} object to close. Can be null.
     */
    public static void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null && !rs.isClosed()) {
                rs.close();
                LOGGER.log(Level.FINE, "ResultSet closed.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing ResultSet: " + e.getMessage(), e);
        }
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
                LOGGER.log(Level.FINE, "Statement closed.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing Statement: " + e.getMessage(), e);
        }
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                LOGGER.log(Level.FINE, "Connection closed.");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing Connection: " + e.getMessage(), e);
        }
    }
}