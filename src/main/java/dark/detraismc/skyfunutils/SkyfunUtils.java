package dark.detraismc.skyfunutils;

import dark.detraismc.skyfunutils.setup.*;
import dark.detraismc.skyfunutils.utils.GitHubUpdateChecker;
import dark.detraismc.skyfunutils.utils.SieveRegistry;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyfunUtils extends JavaPlugin implements SlimefunAddon {
   private static SkyfunUtils instance;

   public void onEnable() {
      instance = this;

       saveDefaultConfig();

       if (getConfig().getBoolean("update-checker", true)) {
           new GitHubUpdateChecker(this, "detraismc/SkyfunUtils").check();
       }

       SieveRegistry.load(this);

       MechanicSetup.INSTANCE.init();
      ToolsSetup.INSTANCE.init();
      MachineSetup.INSTANCE.init();
      //RecipeTypeSetup.INSTANCE.init();
      ListenerSetup.INSTANCE.init();

       // Patches applied on first tick to ensure everything has loaded
       Bukkit.getScheduler().scheduleSyncDelayedTask(this, AllowVanillaCraftingSetup.INSTANCE::init, 1);

   }

   public void onDisable() {
       DeleteVanillaCraftingSetup.INSTANCE.init();
   }

   public String getBugTrackerURL() {
      return null;
   }

   public JavaPlugin getJavaPlugin() {
      return this;
   }

   public static SkyfunUtils getInstance() {
      return instance;
   }
}
