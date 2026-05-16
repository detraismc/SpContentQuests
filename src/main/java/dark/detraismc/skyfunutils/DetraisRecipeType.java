package dark.detraismc.skyfunutils;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

public final class DetraisRecipeType {

   public static final RecipeType CROOK_BREAKING;
    public static final RecipeType MECHANIC_INFO;

   static {
       CROOK_BREAKING = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "crook_breaking"),
               SkyfunItems.SKYFUN_CROOK,
               "&fBroken with a Crook",
               "&7Use a Crook on Leaves"
       );
       MECHANIC_INFO = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "mechanic_info"),
               SlimefunItems.MAGICAL_BOOK_COVER,
               "&fCustom Mechanic",
               "&7Feature from this addon"
       );
   }

   private DetraisRecipeType() {
   }
}
