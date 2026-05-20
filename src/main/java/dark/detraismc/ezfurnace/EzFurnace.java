package dark.detraismc.ezfurnace;

import dark.detraismc.ezfurnace.setup.*;
import dark.detraismc.ezfurnace.utils.GitHubUpdateChecker;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EzFurnace extends JavaPlugin implements SlimefunAddon {
   private static EzFurnace instance;

   public void onEnable() {
      instance = this;

       saveDefaultConfig();

       if (getConfig().getBoolean("update-checker", true)) {
           new GitHubUpdateChecker(this, "detraismc/EzFurnace").check();
       }

      MachineSetup.INSTANCE.init();

       // Patches applied on first tick to ensure everything has loaded
       if (getConfig().getBoolean("allow-vanilla-craft", false)) {
           Bukkit.getScheduler().scheduleSyncDelayedTask(this, AllowVanillaCraftingSetup.INSTANCE::init, 1);
       }

   }

   public void onDisable() {
       if (getConfig().getBoolean("allow-vanilla-craft", false)) {
           DeleteVanillaCraftingSetup.INSTANCE.init();
       }
   }

   public String getBugTrackerURL() {
      return null;
   }

   public JavaPlugin getJavaPlugin() {
      return this;
   }

   public static EzFurnace getInstance() {
      return instance;
   }
}
