package me.detraismc.ftbquests;

import me.detraismc.ftbquests.utils.GitHubUpdateChecker;

import org.bukkit.plugin.java.JavaPlugin;

public class FTBQuests extends JavaPlugin {
   private static FTBQuests instance;

   public void onEnable() {
      instance = this;

       saveDefaultConfig();

       if (getConfig().getBoolean("update-checker", true)) {
           new GitHubUpdateChecker(this, "detraismc/FTBQuests").check();
       }



   }

   public void onDisable() {


   }

   public String getBugTrackerURL() {
      return null;
   }

   public JavaPlugin getJavaPlugin() {
      return this;
   }

   public static FTBQuests getInstance() {
      return instance;
   }
}
