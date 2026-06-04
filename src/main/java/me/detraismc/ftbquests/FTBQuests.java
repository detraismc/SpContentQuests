package me.detraismc.ftbquests;

import me.detraismc.ftbquests.commands.FTBQuestsCommand;
import me.detraismc.ftbquests.database.DatabaseManager;
import me.detraismc.ftbquests.database.MySQLManager;
import me.detraismc.ftbquests.database.SQLiteManager;
import me.detraismc.ftbquests.listener.MenuListener;
import me.detraismc.ftbquests.managers.MenuManager;
import me.detraismc.ftbquests.managers.QuestManager;
import me.detraismc.ftbquests.utils.GitHubUpdateChecker;

import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class FTBQuests extends JavaPlugin {
   private static FTBQuests instance;
   private DatabaseManager databaseManager;
   private QuestManager questManager;
   private MenuManager menuManager;

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
       
       questManager.loadAll();

       // Register Listeners
       getServer().getPluginManager().registerEvents(new MenuListener(this), this);
       getServer().getPluginManager().registerEvents(new me.detraismc.ftbquests.listener.ObjectiveListener(this), this);

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
}
