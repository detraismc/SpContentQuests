package me.detraismc.spcontentquests.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.detraismc.spcontentquests.SpContentQuests;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLManager implements DatabaseManager {
    private final SpContentQuests plugin;
    private HikariDataSource dataSource;

    public MySQLManager(SpContentQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public void connect() throws SQLException {
        HikariConfig config = new HikariConfig();
        
        String host = plugin.getConfig().getString("mysql-settings.hostname", "localhost");
        int port = plugin.getConfig().getInt("mysql-settings.port", 3306);
        String database = plugin.getConfig().getString("mysql-settings.database-name", "SpContentQuests");
        String username = plugin.getConfig().getString("mysql-settings.user-name", "root");
        String password = plugin.getConfig().getString("mysql-settings.user-password", "");
        boolean useSSL = plugin.getConfig().getBoolean("mysql-settings.use-ssl", false);
        int poolSize = plugin.getConfig().getInt("mysql-settings.connection-pool-size", 3);

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSSL);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(poolSize);
        config.setPoolName("SpContentQuests-MySQL-Pool");
        
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
        
        initTables();
    }

    @Override
    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is null. Did you call connect()?");
        }
        return dataSource.getConnection();
    }

    @Override
    public void initTables() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS spcontentquests_users (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "name VARCHAR(16) NOT NULL" +
                    ");");
            
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS spcontentquests_player_quests (" +
                    "uuid VARCHAR(36) NOT NULL, " +
                    "quest_id VARCHAR(64) NOT NULL, " +
                    "points INT DEFAULT 0, " +
                    "completed BOOLEAN DEFAULT FALSE, " +
                    "claimed BOOLEAN DEFAULT FALSE, " +
                    "PRIMARY KEY (uuid, quest_id), " +
                    "FOREIGN KEY (uuid) REFERENCES spcontentquests_users(uuid) ON DELETE CASCADE" +
                    ");");
                    
            try {
                stmt.executeUpdate("ALTER TABLE spcontentquests_player_quests ADD COLUMN claimed BOOLEAN DEFAULT FALSE");
            } catch (SQLException ignored) {
                // Column already exists
            }
        }
    }
}
