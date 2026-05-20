package dark.detraismc.ezfurnace.setup;

import dark.detraismc.ezfurnace.EzFurnaceItems;
import dark.detraismc.ezfurnace.EzFurnace;
import io.github.thebusybiscuit.slimefun4.implementation.items.blocks.EnhancedFurnace;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class MachineSetup {
    public static final MachineSetup INSTANCE = new MachineSetup();
    private boolean initialised;

    private MachineSetup() {
    }

    public void init() {
        if (!this.initialised) {
            this.initialised = true;
            EzFurnace plugin = EzFurnace.getInstance();


            new EnhancedFurnace(EzFurnaceItems.ezfurnace_category, 2, 2, 1, EzFurnaceItems.EZFURNACE_COPPER_FURNACE, new ItemStack[]{
                    new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_INGOT),
                    new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.FURNACE), new ItemStack(Material.COPPER_INGOT),
                    new ItemStack(Material.COPPER_INGOT), new ItemStack(Material.COPPER_BLOCK), new ItemStack(Material.COPPER_INGOT)
            }).register(plugin);

            new EnhancedFurnace(EzFurnaceItems.ezfurnace_category, 3, 3, 1, EzFurnaceItems.EZFURNACE_IRON_FURNACE, new ItemStack[]{
                    new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_INGOT),
                    new ItemStack(Material.IRON_INGOT), EzFurnaceItems.EZFURNACE_COPPER_FURNACE, new ItemStack(Material.IRON_INGOT),
                    new ItemStack(Material.IRON_INGOT), new ItemStack(Material.IRON_BLOCK), new ItemStack(Material.IRON_INGOT)
            }).register(plugin);

            new EnhancedFurnace(EzFurnaceItems.ezfurnace_category, 4, 4, 1, EzFurnaceItems.EZFURNACE_GOLDEN_FURNACE, new ItemStack[]{
                    new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.GOLD_INGOT),
                    new ItemStack(Material.GOLD_INGOT), EzFurnaceItems.EZFURNACE_IRON_FURNACE, new ItemStack(Material.GOLD_INGOT),
                    new ItemStack(Material.GOLD_INGOT), new ItemStack(Material.GOLD_BLOCK), new ItemStack(Material.GOLD_INGOT)
            }).register(plugin);

            new EnhancedFurnace(EzFurnaceItems.ezfurnace_category, 5, 5, 2, EzFurnaceItems.EZFURNACE_LAPIS_FURNACE, new ItemStack[]{
                    new ItemStack(Material.LAPIS_LAZULI), new ItemStack(Material.LAPIS_LAZULI), new ItemStack(Material.LAPIS_LAZULI),
                    new ItemStack(Material.LAPIS_LAZULI), EzFurnaceItems.EZFURNACE_GOLDEN_FURNACE, new ItemStack(Material.LAPIS_LAZULI),
                    new ItemStack(Material.LAPIS_LAZULI), new ItemStack(Material.LAPIS_BLOCK), new ItemStack(Material.LAPIS_LAZULI)
            }).register(plugin);

            new EnhancedFurnace(EzFurnaceItems.ezfurnace_category, 6, 6, 2, EzFurnaceItems.EZFURNACE_REDSTONE_FURNACE, new ItemStack[]{
                    new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE),
                    new ItemStack(Material.REDSTONE), EzFurnaceItems.EZFURNACE_LAPIS_FURNACE, new ItemStack(Material.REDSTONE),
                    new ItemStack(Material.REDSTONE), new ItemStack(Material.REDSTONE_BLOCK), new ItemStack(Material.REDSTONE)
            }).register(plugin);

            new EnhancedFurnace(EzFurnaceItems.ezfurnace_category, 7, 7, 2, EzFurnaceItems.EZFURNACE_DIAMOND_FURNACE, new ItemStack[]{
                    new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND),
                    new ItemStack(Material.DIAMOND), EzFurnaceItems.EZFURNACE_REDSTONE_FURNACE, new ItemStack(Material.DIAMOND),
                    new ItemStack(Material.DIAMOND), new ItemStack(Material.DIAMOND_BLOCK), new ItemStack(Material.DIAMOND)
            }).register(plugin);

            new EnhancedFurnace(EzFurnaceItems.ezfurnace_category, 8, 8, 2, EzFurnaceItems.EZFURNACE_EMERALD_FURNACE, new ItemStack[]{
                    new ItemStack(Material.EMERALD), new ItemStack(Material.EMERALD), new ItemStack(Material.EMERALD),
                    new ItemStack(Material.EMERALD), EzFurnaceItems.EZFURNACE_DIAMOND_FURNACE, new ItemStack(Material.EMERALD),
                    new ItemStack(Material.EMERALD), new ItemStack(Material.EMERALD_BLOCK), new ItemStack(Material.EMERALD)
            }).register(plugin);

            new EnhancedFurnace(EzFurnaceItems.ezfurnace_category, 10, 10, 3, EzFurnaceItems.EZFURNACE_NETHERITE_FURNACE, new ItemStack[]{
                    new ItemStack(Material.NETHERITE_INGOT), new ItemStack(Material.NETHERITE_INGOT), new ItemStack(Material.NETHERITE_INGOT),
                    new ItemStack(Material.NETHERITE_INGOT), EzFurnaceItems.EZFURNACE_EMERALD_FURNACE, new ItemStack(Material.NETHERITE_INGOT),
                    new ItemStack(Material.NETHERITE_INGOT), new ItemStack(Material.NETHERITE_BLOCK), new ItemStack(Material.NETHERITE_INGOT)
            }).register(plugin);



            if (plugin.getConfig().getBoolean("allow-vanilla-craft", true)) {

                // --- Copper Furnace ---
                ItemStack copperResult = EzFurnaceItems.EZFURNACE_COPPER_FURNACE.clone();
                NamespacedKey copperKey = new NamespacedKey(plugin, "ezfurnace_copper_furnace");
                ShapedRecipe copperRecipe = new ShapedRecipe(copperKey, copperResult);
                copperRecipe.shape(
                        "TTT",
                        "TFT",
                        "TBT");
                copperRecipe.setIngredient('T', Material.COPPER_INGOT);
                copperRecipe.setIngredient('B', Material.COPPER_BLOCK);
                copperRecipe.setIngredient('F', Material.FURNACE);
                Bukkit.addRecipe(copperRecipe);

                // --- Iron Furnace ---
                ItemStack ironResult = EzFurnaceItems.EZFURNACE_IRON_FURNACE.clone();
                NamespacedKey ironKey = new NamespacedKey(plugin, "ezfurnace_iron_furnace");
                ShapedRecipe ironRecipe = new ShapedRecipe(ironKey, ironResult);
                ironRecipe.shape(
                        "TTT",
                        "TFT",
                        "TBT");
                ironRecipe.setIngredient('T', Material.IRON_INGOT);
                ironRecipe.setIngredient('B', Material.IRON_BLOCK);
                ironRecipe.setIngredient('F', new org.bukkit.inventory.RecipeChoice.ExactChoice(EzFurnaceItems.EZFURNACE_COPPER_FURNACE));
                Bukkit.addRecipe(ironRecipe);

                // --- Golden Furnace ---
                ItemStack goldResult = EzFurnaceItems.EZFURNACE_GOLDEN_FURNACE.clone();
                NamespacedKey goldKey = new NamespacedKey(plugin, "ezfurnace_golden_furnace");
                ShapedRecipe goldRecipe = new ShapedRecipe(goldKey, goldResult);
                goldRecipe.shape(
                        "TTT",
                        "TFT",
                        "TBT");
                goldRecipe.setIngredient('T', Material.GOLD_INGOT);
                goldRecipe.setIngredient('B', Material.GOLD_BLOCK);
                goldRecipe.setIngredient('F', new org.bukkit.inventory.RecipeChoice.ExactChoice(EzFurnaceItems.EZFURNACE_IRON_FURNACE));
                Bukkit.addRecipe(goldRecipe);

                // --- Lapis Furnace ---
                ItemStack lapisResult = EzFurnaceItems.EZFURNACE_LAPIS_FURNACE.clone();
                NamespacedKey lapisKey = new NamespacedKey(plugin, "ezfurnace_lapis_furnace");
                ShapedRecipe lapisRecipe = new ShapedRecipe(lapisKey, lapisResult);
                lapisRecipe.shape(
                        "TTT",
                        "TFT",
                        "TBT");
                lapisRecipe.setIngredient('T', Material.LAPIS_LAZULI);
                lapisRecipe.setIngredient('B', Material.LAPIS_BLOCK);
                lapisRecipe.setIngredient('F', new org.bukkit.inventory.RecipeChoice.ExactChoice(EzFurnaceItems.EZFURNACE_GOLDEN_FURNACE));
                Bukkit.addRecipe(lapisRecipe);

                // --- Redstone Furnace ---
                ItemStack redstoneResult = EzFurnaceItems.EZFURNACE_REDSTONE_FURNACE.clone();
                NamespacedKey redstoneKey = new NamespacedKey(plugin, "ezfurnace_redstone_furnace");
                ShapedRecipe redstoneRecipe = new ShapedRecipe(redstoneKey, redstoneResult);
                redstoneRecipe.shape(
                        "TTT",
                        "TFT",
                        "TBT");
                redstoneRecipe.setIngredient('T', Material.REDSTONE);
                redstoneRecipe.setIngredient('B', Material.REDSTONE_BLOCK);
                redstoneRecipe.setIngredient('F', new org.bukkit.inventory.RecipeChoice.ExactChoice(EzFurnaceItems.EZFURNACE_LAPIS_FURNACE));
                Bukkit.addRecipe(redstoneRecipe);

                // --- Diamond Furnace ---
                ItemStack diamondResult = EzFurnaceItems.EZFURNACE_DIAMOND_FURNACE.clone();
                NamespacedKey diamondKey = new NamespacedKey(plugin, "ezfurnace_diamond_furnace");
                ShapedRecipe diamondRecipe = new ShapedRecipe(diamondKey, diamondResult);
                diamondRecipe.shape(
                        "TTT",
                        "TFT",
                        "TBT");
                diamondRecipe.setIngredient('T', Material.DIAMOND);
                diamondRecipe.setIngredient('B', Material.DIAMOND_BLOCK);
                diamondRecipe.setIngredient('F', new org.bukkit.inventory.RecipeChoice.ExactChoice(EzFurnaceItems.EZFURNACE_REDSTONE_FURNACE));
                Bukkit.addRecipe(diamondRecipe);

                // --- Emerald Furnace ---
                ItemStack emeraldResult = EzFurnaceItems.EZFURNACE_EMERALD_FURNACE.clone();
                NamespacedKey emeraldKey = new NamespacedKey(plugin, "ezfurnace_emerald_furnace");
                ShapedRecipe emeraldRecipe = new ShapedRecipe(emeraldKey, emeraldResult);
                emeraldRecipe.shape(
                        "TTT",
                        "TFT",
                        "TBT");
                emeraldRecipe.setIngredient('T', Material.EMERALD);
                emeraldRecipe.setIngredient('B', Material.EMERALD_BLOCK);
                emeraldRecipe.setIngredient('F', new org.bukkit.inventory.RecipeChoice.ExactChoice(EzFurnaceItems.EZFURNACE_DIAMOND_FURNACE));
                Bukkit.addRecipe(emeraldRecipe);

                // --- Netherite Furnace ---
                ItemStack netheriteResult = EzFurnaceItems.EZFURNACE_NETHERITE_FURNACE.clone();
                NamespacedKey netheriteKey = new NamespacedKey(plugin, "ezfurnace_netherite_furnace");
                ShapedRecipe netheriteRecipe = new ShapedRecipe(netheriteKey, netheriteResult);
                netheriteRecipe.shape(
                        "TTT",
                        "TFT",
                        "TBT");
                netheriteRecipe.setIngredient('T', Material.NETHERITE_INGOT);
                netheriteRecipe.setIngredient('B', Material.NETHERITE_BLOCK);
                netheriteRecipe.setIngredient('F', new org.bukkit.inventory.RecipeChoice.ExactChoice(EzFurnaceItems.EZFURNACE_EMERALD_FURNACE));
                Bukkit.addRecipe(netheriteRecipe);
            }




        }
    }
}
