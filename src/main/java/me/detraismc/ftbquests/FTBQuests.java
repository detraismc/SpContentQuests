package me.detraismc.ftbquests;

import me.detraismc.ftbquests.commands.FTBQuestsCommand;
import me.detraismc.ftbquests.database.DatabaseManager;
import me.detraismc.ftbquests.database.MySQLManager;
import me.detraismc.ftbquests.database.SQLiteManager;
import me.detraismc.ftbquests.listener.MenuListener;
import me.detraismc.ftbquests.listener.ObjectiveListener;
import me.detraismc.ftbquests.listener.PlayerJoinQuitListener;
import me.detraismc.ftbquests.managers.MenuManager;
import me.detraismc.ftbquests.managers.PlayerDataManager;
import me.detraismc.ftbquests.managers.QuestManager;
import me.detraismc.ftbquests.utils.GitHubUpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class FTBQuests extends JavaPlugin {
   private static FTBQuests instance;
   private DatabaseManager databaseManager;
   private QuestManager questManager;
   private MenuManager menuManager;
   private PlayerDataManager playerDataManager;

   public void onEnable() {
      instance = this;

       saveDefaultConfig();

       if (getConfig().getBoolean("update-checker", true)) {
           new GitHubUpdateChecker(this, "detraismc/FTBQuests").check();
       }

       boolean useMySQL = getConfig().getBoolean("mysql-settings.enabled", false);
       if (useMySQL) {
           databaseManager = new MySQLManager(this);
       } else {
           databaseManager = new SQLiteManager(this);
       }

       try {
           databaseManager.connect();
           getLogger().info("Successfully connected to the database!");
       } catch (SQLException e) {
           getLogger().severe("Failed to connect to the database! " + e.getMessage());
           getServer().getPluginManager().disablePlugin(this);
           return;
       }

       // Save default resources
       saveResourceIfNotExists("category/introduction.yml");
       saveResourceIfNotExists("category/overworld.yml");
       saveResourceIfNotExists("quests/example-quests.yml");

       // Initialize Managers
       questManager = new QuestManager(this);
       menuManager = new MenuManager(this);
       playerDataManager = new PlayerDataManager(this);
       
       questManager.loadAll();

       // Register Listeners
       getServer().getPluginManager().registerEvents(new MenuListener(this), this);
       getServer().getPluginManager().registerEvents(new ObjectiveListener(this), this);
       getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);

       // Auto-save task (config: auto-save-interval in minutes)
       int interval = getConfig().getInt("auto-save-interval", 5);
       Bukkit.getScheduler().runTaskTimer(this, () -> playerDataManager.saveAllAsync(), interval * 1200L, interval * 1200L);

       // Register Commands
       getCommand("ftbquests").setExecutor(new FTBQuestsCommand(this));
       getCommand("ftbquests").setTabCompleter(new FTBQuestsCommand(this));

   }

   private void saveResourceIfNotExists(String path) {
       java.io.File file = new java.io.File(getDataFolder(), path);
       if (!file.exists()) {
           saveResource(path, false);
       }
   }

   public void onDisable() {
       if (playerDataManager != null) {
           playerDataManager.saveAllSync();
       }
       if (databaseManager != null) {
           databaseManager.disconnect();
           getLogger().info("Disconnected from the database.");
       }
   }

   //public String getBugTrackerURL() {
   //   return null;
   //}

   public JavaPlugin getJavaPlugin() {
      return this;
   }

   public static FTBQuests getInstance() {
      return instance;
   }

   public DatabaseManager getDatabaseManager() {
       return databaseManager;
   }

   public QuestManager getQuestManager() {
       return questManager;
   }

   public MenuManager getMenuManager() {
       return menuManager;
   }

   public PlayerDataManager getPlayerDataManager() {
       return playerDataManager;
   }

   public String msg(String key, String... replacements) {
       String message = getConfig().getString("messages." + key, "&cMissing message: " + key);
       message = message.replace("&", "§");
       for (int i = 0; i < replacements.length - 1; i += 2) {
           message = message.replace(replacements[i], replacements[i + 1]);
       }
       return message;
   }
}
