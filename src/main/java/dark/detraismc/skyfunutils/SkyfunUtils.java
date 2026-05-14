package dark.detraismc.skyfunutils;

import dark.detraismc.skyfunutils.implementation.ToolsSetup;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

import org.bukkit.plugin.java.JavaPlugin;

public class SkyfunUtils extends JavaPlugin implements SlimefunAddon {
   private static SkyfunUtils instance;

   public void onEnable() {
      instance = this;
      ToolsSetup.INSTANCE.init();
      RecipeTypeSetup.INSTANCE.init();
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
