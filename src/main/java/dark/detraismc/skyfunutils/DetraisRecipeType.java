package dark.detraismc.skyfunutils;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

public final class DetraisRecipeType {

   public static final RecipeType CROOK_BREAKING;

   static {
       CROOK_BREAKING = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "crook_breaking"),
               SkyfunItems.SKYFUN_CROOK,
               "&fBroken with a Crook",
               "&7Use a Crook on Leaves"
       );
   }

   private DetraisRecipeType() {
   }
}
