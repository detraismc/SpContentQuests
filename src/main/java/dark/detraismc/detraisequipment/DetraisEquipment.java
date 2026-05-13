package dark.detraismc.detraisequipment;

import dark.detraismc.detraisequipment.implementation.WeaponsSetup;
import dark.detraismc.detraisequipment.implementation.ArmorSetup;
import dark.detraismc.detraisequipment.utils.ItemsUtils;
import dark.detraismc.detraisequipment.utils.MathUtils;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;

import org.bukkit.plugin.java.JavaPlugin;

public class DetraisEquipment extends JavaPlugin implements SlimefunAddon {
   private static DetraisEquipment instance;
   public MathUtils mtk = new MathUtils();
   public ItemsUtils iutils = new ItemsUtils();

   public void onEnable() {
      instance = this;
      WeaponsSetup.INSTANCE.init();
      ArmorSetup.INSTANCE.init();
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

   public static DetraisEquipment getInstance() {
      return instance;
   }
}
