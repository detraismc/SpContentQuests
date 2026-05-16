package dark.detraismc.skyfunutils.implementation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

// 1. Extend SimpleSlimefunItem<ToolUseHandler>
public class Hammer extends SimpleSlimefunItem<ToolUseHandler> {

    public Hammer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    // 2. Override getItemHandler to return the ToolUseHandler
    @Override
    public ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {

            Block b = e.getBlock();
            ItemStack customDrop = getDrop(b.getType());

            // If the block broken has a custom drop in our list...
            if (customDrop != null) {
                // Drop our custom item
                b.getWorld().dropItemNaturally(b.getLocation(), customDrop);

                // Prevent the vanilla block from dropping!
                e.setDropItems(false);
            }
        };
    }

    // 3. Keep the drop logic clean and separated
    private ItemStack getDrop(Material m) {
        switch (m) {
            case STONE:
            case COBBLESTONE:
                return new ItemStack(Material.GRAVEL);
            case GRAVEL:
                return new ItemStack(Material.SAND);
            default:
                return null;
        }
    }
}