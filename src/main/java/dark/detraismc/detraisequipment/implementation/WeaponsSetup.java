package dark.detraismc.detraisequipment.implementation;

import dark.detraismc.detraisequipment.DEItems;
import dark.detraismc.detraisequipment.DetraisEquipment;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class WeaponsSetup {
    public static final WeaponsSetup INSTANCE = new WeaponsSetup();
    private boolean initialised;

    private WeaponsSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;
            DetraisEquipment plugin = DetraisEquipment.getInstance();

            // --- REGISTER COPPER SWORD ---
            // Primary Recipe (Slimefun Copper)
            new SlimefunItem(DEItems.category_weapons, DEItems.COPPER_SWORD, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    null, SlimefunItems.COPPER_INGOT, null,
                    null, SlimefunItems.COPPER_INGOT, null,
                    null, new ItemStack(Material.STICK), null
            }).register(plugin);

            // Secondary Recipe (Vanilla Copper)
            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, new ItemStack(Material.COPPER_INGOT), null,
                    null, new ItemStack(Material.COPPER_INGOT), null,
                    null, new ItemStack(Material.STICK), null
            }, DEItems.COPPER_SWORD);

        }
    }
}