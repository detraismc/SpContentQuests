package me.detraismc.spcontentquests.database;

import java.sql.Connection;
import java.sql.SQLException;

public interface DatabaseManager {
    
    /**
     * Connect to the database.
     * @throws SQLException If an error occurs while connecting.
     */
    void connect() throws SQLException;
    
    /**
     * Disconnect from the database.
     */
    void disconnect();
    
    /**
     * Get a connection to the database.
     * @return The database connection.
     * @throws SQLException If an error occurs while getting the connection.
     */
    Connection getConnection() throws SQLException;
    
    /**
     * Initialize the necessary tables for the plugin.
     * @throws SQLException If an error occurs while creating tables.
     */
    void initTables() throws SQLException;
}
