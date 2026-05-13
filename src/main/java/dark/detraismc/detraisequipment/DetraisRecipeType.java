package dark.detraismc.detraisequipment;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

public final class DetraisRecipeType {
   public static final RecipeType FORGE_STATION;

   static {
       FORGE_STATION = new RecipeType(new NamespacedKey(DetraisEquipment.getInstance(), "forge_station"), new SlimefunItemStack("FORGE_STATION", Material.GRINDSTONE, "&bForge Station", new String[0]), new String[]{"&7Craft this Item as shown", "&7using an Forge Station"});
   }

   private DetraisRecipeType() {
   }
}
