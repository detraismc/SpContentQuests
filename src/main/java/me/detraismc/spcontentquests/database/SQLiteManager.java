package me.detraismc.spcontentquests.database;

import me.detraismc.spcontentquests.SpContentQuests;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteManager implements DatabaseManager {
    private final SpContentQuests plugin;
    private Connection connection;

    public SQLiteManager(SpContentQuests plugin) {
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
            stmt.execute("CREATE TABLE IF NOT EXISTS spcontentquests_users (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "name VARCHAR(16) NOT NULL" +
                    ");");
            
            stmt.execute("CREATE TABLE IF NOT EXISTS spcontentquests_player_quests (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "quest_id VARCHAR(64) NOT NULL, " +
                    "objectives_data TEXT DEFAULT '', " +
                    "completed BOOLEAN DEFAULT 0, " +
                    "claimed BOOLEAN DEFAULT 0, " +
                    "PRIMARY KEY (uuid, quest_id), " +
                    "FOREIGN KEY (uuid) REFERENCES spcontentquests_users(uuid) ON DELETE CASCADE" +
                    ");");
                    
            try {
                stmt.execute("ALTER TABLE spcontentquests_player_quests ADD COLUMN objectives_data TEXT DEFAULT ''");
            } catch (SQLException ignored) {
                // Column already exists
            }
        }
    }
}
