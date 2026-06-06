package me.detraismc.ftbquests.managers;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.models.PlayerQuestData;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    private final FTBQuests plugin;
    private final Map<UUID, Map<String, PlayerQuestData>> playerCache = new ConcurrentHashMap<>();

    public PlayerDataManager(FTBQuests plugin) {
        this.plugin = plugin;
    }

    public void loadPlayer(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Map<String, PlayerQuestData> dataMap = new HashMap<>();
            try (Connection conn = plugin.getDatabaseManager().getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT quest_id, points, completed, claimed FROM ftbquests_player_quests WHERE uuid = ?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    dataMap.put(rs.getString("quest_id"), new PlayerQuestData(
                            rs.getString("quest_id"),
                            rs.getInt("points"),
                            rs.getBoolean("completed"),
                            rs.getBoolean("claimed")
                    ));
                }
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to load data for player " + uuid);
                e.printStackTrace();
            }
            playerCache.put(uuid, dataMap);
        });
    }

    public void savePlayer(UUID uuid, boolean remove) {
        Map<String, PlayerQuestData> dataMap = playerCache.get(uuid);
        if (dataMap == null) return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            saveDataToDB(uuid, dataMap);
            if (remove) {
                playerCache.remove(uuid);
            }
        });
    }

    public void saveAllSync() {
        for (Map.Entry<UUID, Map<String, PlayerQuestData>> entry : playerCache.entrySet()) {
            saveDataToDB(entry.getKey(), entry.getValue());
        }
    }

    public void saveAllAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            for (Map.Entry<UUID, Map<String, PlayerQuestData>> entry : playerCache.entrySet()) {
                saveDataToDB(entry.getKey(), entry.getValue());
            }
        });
    }

    private void saveDataToDB(UUID uuid, Map<String, PlayerQuestData> dataMap) {
        try (Connection conn = plugin.getDatabaseManager().getConnection()) {
            PreparedStatement update = conn.prepareStatement(
                    "UPDATE ftbquests_player_quests SET points = ?, completed = ?, claimed = ? WHERE uuid = ? AND quest_id = ?");
            PreparedStatement insert = conn.prepareStatement(
                    "INSERT INTO ftbquests_player_quests (uuid, quest_id, points, completed, claimed) VALUES (?, ?, ?, ?, ?)");

            for (PlayerQuestData data : dataMap.values()) {
                if (!data.isModified()) continue;

                // Try update first
                update.setInt(1, data.getPoints());
                update.setBoolean(2, data.isCompleted());
                update.setBoolean(3, data.isClaimed());
                update.setString(4, uuid.toString());
                update.setString(5, data.getQuestId());
                
                if (update.executeUpdate() == 0) {
                    // Row didn't exist, insert it
                    insert.setString(1, uuid.toString());
                    insert.setString(2, data.getQuestId());
                    insert.setInt(3, data.getPoints());
                    insert.setBoolean(4, data.isCompleted());
                    insert.setBoolean(5, data.isClaimed());
                    insert.executeUpdate();
                }

                data.setModified(false);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to save data for player " + uuid);
            e.printStackTrace();
        }
    }

    public Map<String, PlayerQuestData> getQuestData(UUID uuid) {
        return playerCache.getOrDefault(uuid, new HashMap<>());
    }

    public PlayerQuestData getOrCreateQuestData(UUID uuid, String questId) {
        Map<String, PlayerQuestData> map = playerCache.computeIfAbsent(uuid, k -> new HashMap<>());
        return map.computeIfAbsent(questId, k -> {
            PlayerQuestData data = new PlayerQuestData(questId, 0, false, false);
            data.setModified(true); // new data should be saved
            return data;
        });
    }

    public void removePlayer(UUID uuid) {
        playerCache.remove(uuid);
    }

    public void purgePlayer(UUID uuid) {
        playerCache.remove(uuid);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getDatabaseManager().getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM ftbquests_player_quests WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to purge data for player " + uuid);
                e.printStackTrace();
            }
        });
    }

    public void purgeAll() {
        playerCache.clear();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getDatabaseManager().getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM ftbquests_player_quests")) {
                ps.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to purge all player data");
                e.printStackTrace();
            }
        });
    }

    public void resetQuests(UUID uuid, List<String> questIds) {
        Map<String, PlayerQuestData> dataMap = playerCache.get(uuid);
        if (dataMap != null) {
            questIds.forEach(dataMap::remove);
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getDatabaseManager().getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM ftbquests_player_quests WHERE uuid = ? AND quest_id = ?")) {
                for (String questId : questIds) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, questId);
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to reset quests for player " + uuid);
                e.printStackTrace();
            }
        });
    }

    public void resetQuestsAll(List<String> questIds) {
        for (Map.Entry<UUID, Map<String, PlayerQuestData>> entry : playerCache.entrySet()) {
            questIds.forEach(entry.getValue()::remove);
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection conn = plugin.getDatabaseManager().getConnection();
                 PreparedStatement ps = conn.prepareStatement("DELETE FROM ftbquests_player_quests WHERE quest_id = ?")) {
                for (String questId : questIds) {
                    ps.setString(1, questId);
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to reset quests for all players");
                e.printStackTrace();
            }
        });
    }
}
