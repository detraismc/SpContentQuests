package dark.detraismc.skyfunutils.implementation;

import dark.detraismc.skyfunutils.SkyfunUtils; // Make sure this is your main plugin class
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class FrictionPickaxe extends SimpleSlimefunItem<ToolUseHandler> {

    public FrictionPickaxe(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {

            // Check if the block broken is Cobblestone
            if (e.getBlock().getType() == Material.COBBLESTONE) {

                // Load the chance dynamically from config.yml (defaults to 10 if missing)
                int redstoneChance = SkyfunUtils.getInstance().getConfig().getInt("tools.friction-pickaxe.redstone-chance", 10);

                // Roll the configuration-based chance
                if (ThreadLocalRandom.current().nextInt(100) < redstoneChance) {

                    // Add it directly to the Slimefun 'drops' list parameter.
                    // This lets Slimefun handle the spawning and prevents double-drops or logic issues!
                    drops.add(new ItemStack(Material.REDSTONE));
                }

                // Note: We leave e.setDropItems(false) out entirely so the vanilla
                // cobblestone drops perfectly alongside our extra redstone!
            }
        };
    }
}