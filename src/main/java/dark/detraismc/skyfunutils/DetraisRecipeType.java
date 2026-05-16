package dark.detraismc.skyfunutils;

import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;

public final class DetraisRecipeType {

   public static final RecipeType CROOK_BREAKING;
    public static final RecipeType MECHANIC_INFO;
    public static final RecipeType SKYFUN_HEATED_PRESSURE_CHAMBER;
    public static final RecipeType SKYFUN_CRAFTING_TABLE;

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

       SKYFUN_HEATED_PRESSURE_CHAMBER = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_heated_pressure_chamber"),
               SlimefunItems.HEATED_PRESSURE_CHAMBER,
               "&bHeated Pressure Chamber",
               "&bCraft this Item as shown",
               "&busing a Heated Pressure Chamber"
       );

       SKYFUN_CRAFTING_TABLE = new RecipeType(
               new NamespacedKey(SkyfunUtils.getInstance(), "skyfun_crafting_table"),
               new SlimefunItemStack("SKYFUN_CRAFTING_TABLE", Material.CRAFTING_TABLE,
                       "&bNormal or Enhanced Crafting Table", new String[0]), new String[]{
               "&7Craft this Item shown",
               "&7in a Normal or Enhanced Crafting",
               "&7Table."});
   }

   private DetraisRecipeType() {
   }
}
