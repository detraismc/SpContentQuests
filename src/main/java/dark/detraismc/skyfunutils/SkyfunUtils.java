package dark.detraismc.skyfunutils;

import dark.detraismc.skyfunutils.setup.MachineSetup;
import dark.detraismc.skyfunutils.setup.MechanicSetup;
import dark.detraismc.skyfunutils.setup.RecipeTypeSetup;
import dark.detraismc.skyfunutils.setup.ToolsSetup;
import dark.detraismc.skyfunutils.utils.GitHubUpdateChecker;
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

       MechanicSetup.INSTANCE.init();
      ToolsSetup.INSTANCE.init();
      MachineSetup.INSTANCE.init();
      //RecipeTypeSetup.INSTANCE.init();
      ListenerSetup.INSTANCE.init();

   }

   public void onDisable() {
       // 1. Unregister the Furnace Smelting Recipe
       Bukkit.removeRecipe(new NamespacedKey(this, "clay_bucket_smelting"));

       // 2. Unregister the Crooks
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_crook"));
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_bone_crook"));

       // 3. Unregister ALL Wooden Hammer variants (Matches the 0-10 loop from ToolsSetup)
       for (int i = 0; i < 11; i++) {
           Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_wooden_hammer_" + i));
       }

       // 4. Unregister Stone Hammer variants
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_stone_hammer_cobble"));
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_stone_hammer_deepslate"));
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_stone_hammer_blackstone"));

       // 5. Unregister Remaining Metal/Gem Hammers
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_iron_hammer"));
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_gold_hammer"));
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_diamond_hammer"));

       // 6. Unregister Miscellaneous Recipes
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_grass_seeds"));
       Bukkit.removeRecipe(new NamespacedKey(this, "skyfun_clay_bucket"));
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
