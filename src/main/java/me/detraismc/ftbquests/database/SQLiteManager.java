package me.detraismc.ftbquests.database;

import me.detraismc.ftbquests.FTBQuests;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteManager implements DatabaseManager {
    private final FTBQuests plugin;
    private Connection connection;

    public SQLiteManager(FTBQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public void connect() throws SQLException {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        File databaseFile = new File(dataFolder, "database.db");
        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create database.db!");
            }
        }

        String url = "jdbc:sqlite:" + databaseFile;
        connection = DriverManager.getConnection(url);
        
        initTables();
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to close SQLite connection: " + e.getMessage());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection;
    }

    @Override
    public void initTables() throws SQLException {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS ftbquests_users (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "name VARCHAR(16) NOT NULL" +
                    ");");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS ftbquests_player_quests (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "quest_id VARCHAR(64) NOT NULL, " +
                    "points INT DEFAULT 0, " +
                    "completed BOOLEAN DEFAULT 0, " +
                    "claimed BOOLEAN DEFAULT 0, " +
                    "PRIMARY KEY (uuid, quest_id), " +
                    "FOREIGN KEY (uuid) REFERENCES ftbquests_users(uuid) ON DELETE CASCADE" +
                    ");");
                    
            try {
                stmt.execute("ALTER TABLE ftbquests_player_quests ADD COLUMN claimed BOOLEAN DEFAULT 0");
            } catch (SQLException ignored) {
                // Column already exists
            }
        }
    }
}
