package dark.detraismc.skyfunutils;

import dark.detraismc.skyfunutils.setup.MachineSetup;
import dark.detraismc.skyfunutils.setup.MechanicSetup;
import dark.detraismc.skyfunutils.setup.RecipeTypeSetup;
import dark.detraismc.skyfunutils.setup.ToolsSetup;
import dark.detraismc.skyfunutils.utils.GitHubUpdateChecker;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

import org.bukkit.plugin.java.JavaPlugin;

public class SkyfunUtils extends JavaPlugin implements SlimefunAddon {
   private static SkyfunUtils instance;

   public void onEnable() {
      instance = this;

       saveDefaultConfig();

       if (getConfig().getBoolean("update-checker", true)) {
           new GitHubUpdateChecker(this, "detraismc/SkyfunUtils").check();
       }

       MechanicSetup.INSTANCE.init();
      ToolsSetup.INSTANCE.init();
      MachineSetup.INSTANCE.init();
      //RecipeTypeSetup.INSTANCE.init();
      ListenerSetup.INSTANCE.init();

   }

   public void onDisable() {
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
