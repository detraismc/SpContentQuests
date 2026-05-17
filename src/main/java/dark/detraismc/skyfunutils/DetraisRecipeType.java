package dark.detraismc.skyfunutils;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

public final class DetraisRecipeType {

   public static final RecipeType CROOK_BREAKING;
    public static final RecipeType MECHANIC_INFO;
    public static final RecipeType SKYFUN_SMELTERY;
    public static final RecipeType SKYFUN_CRAFTING_TABLE;
    public static final RecipeType SKYFUN_FURNACE;

   static {
       CROOK_BREAKING = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "crook_breaking"),
               SkyfunItems.SKYFUN_CROOK,
               "&7Broken with a Crook",
               "&7Use a Crook on Leaves"
       );
       MECHANIC_INFO = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "mechanic_info"),
               new SlimefunItemStack("MECHANIC_INFO", Material.BOOK,
                       "&fCustom Mechanic", new String[0]), new String[]{
                       "&7Feature from this addon"
               });

       SKYFUN_SMELTERY = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_smeltery"),
               SlimefunItems.SMELTERY,
               "&7Craft this Item as shown",
               "&7using a Smeltery"
       );

       SKYFUN_CRAFTING_TABLE = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_crafting_table"),
               new SlimefunItemStack("SKYFUN_CRAFTING_TABLE", Material.CRAFTING_TABLE,
                       "&bNormal or Enhanced Crafting Table", new String[0]), new String[]{
               "&7Craft this Item shown",
               "&7in a Normal or Enhanced Crafting",
               "&7Table."});

       SKYFUN_FURNACE = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_furnace"),
               new SlimefunItemStack("SKYFUN_FURNACE", Material.FURNACE,
                       "&bFurnace Recipe", new String[0]), new String[]{
               "&7Smelt this item in a Furnace",
               "&7to craft your desired item"
               });
   }

   private DetraisRecipeType() {
   }
}
