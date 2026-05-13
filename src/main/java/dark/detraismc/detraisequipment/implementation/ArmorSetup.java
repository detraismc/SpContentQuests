package dark.detraismc.detraisequipment.implementation;

import dark.detraismc.detraisequipment.DEItems;
import dark.detraismc.detraisequipment.DetraisEquipment;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ArmorSetup {
    public static final ArmorSetup INSTANCE = new ArmorSetup();
    private boolean initialised;

    private ArmorSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;
            DetraisEquipment plugin = DetraisEquipment.getInstance();


            // --- REGISTER COPPER HELMET ---
            new SlimefunItem(DEItems.category_armor, DEItems.COPPER_HELMET, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT,
                    SlimefunItems.COPPER_INGOT, null, SlimefunItems.COPPER_INGOT,
                    null, null, null
            }).register(plugin);

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT),
                    new ItemStack(Material.COPPER_INGOT), null, new ItemStack(Material.COPPER_INGOT),
                    null, null, null
            }, DEItems.COPPER_HELMET);


            // --- REGISTER COPPER CHESTPLATE ---
            new SlimefunItem(DEItems.category_armor, DEItems.COPPER_CHESTPLATE, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.COPPER_INGOT, null, SlimefunItems.COPPER_INGOT,
                    SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT,
                    SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT
            }).register(plugin);

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    new ItemStack(Material.COPPER_INGOT), null, new ItemStack(Material.COPPER_INGOT),
                    new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT),
                    new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT)
            }, DEItems.COPPER_CHESTPLATE);


            // --- REGISTER COPPER LEGGINGS ---
            new SlimefunItem(DEItems.category_armor, DEItems.COPPER_LEGGINGS, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT, SlimefunItems.COPPER_INGOT,
                    SlimefunItems.COPPER_INGOT, null, SlimefunItems.COPPER_INGOT,
                    SlimefunItems.COPPER_INGOT, null, SlimefunItems.COPPER_INGOT
            }).register(plugin);

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT),
                    new ItemStack(Material.COPPER_INGOT), null, new ItemStack(Material.COPPER_INGOT),
                    new ItemStack(Material.COPPER_INGOT), null, new ItemStack(Material.COPPER_INGOT)
            }, DEItems.COPPER_LEGGINGS);


            // --- REGISTER COPPER BOOTS ---
            new SlimefunItem(DEItems.category_armor, DEItems.COPPER_BOOTS, RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
                    null, null, null,
                    SlimefunItems.COPPER_INGOT, null, SlimefunItems.COPPER_INGOT,
                    SlimefunItems.COPPER_INGOT, null, SlimefunItems.COPPER_INGOT
            }).register(plugin);

            RecipeType.ENHANCED_CRAFTING_TABLE.register(new ItemStack[] {
                    null, null, null,
                    new ItemStack(Material.COPPER_INGOT), null, new ItemStack(Material.COPPER_INGOT),
                    new ItemStack(Material.COPPER_INGOT), null, new ItemStack(Material.COPPER_INGOT)
            }, DEItems.COPPER_BOOTS);

        }
    }
}