package dark.detraismc.skyfunutils.implementation;

import dark.detraismc.skyfunutils.SkyfunItems;
import dark.detraismc.skyfunutils.SkyfunUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Crook extends SimpleSlimefunItem<ToolUseHandler> {

    public Crook(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {

            // Check if the block is any type of leaves
            if (Tag.LEAVES.isTagged(e.getBlock().getType())) {

                int silkwormChance = SkyfunUtils.getInstance().getConfig().getInt("tools.crook.silkworm-chance", 10);

                // 1. Roll the 10% chance for the Silkworm
                if (ThreadLocalRandom.current().nextInt(100) < silkwormChance) {
                    drops.add(SkyfunItems.SKYFUN_SILKWORM.clone());
                }

                // 2. Double the vanilla drops (Saplings, Sticks, Apples)
                // We create a copy of the list first to avoid a ConcurrentModificationException
                List<ItemStack> extraDrops = new ArrayList<>(drops);

                // Add the copy back into the main drops list
                drops.addAll(extraDrops);
            }
        };
    }
}