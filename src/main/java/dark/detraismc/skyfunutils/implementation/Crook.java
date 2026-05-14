package dark.detraismc.skyfunutils.implementation;

import dark.detraismc.skyfunutils.SkyfunItems;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import org.bukkit.Tag;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Crook extends SlimefunItem {

    public Crook(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockBreakHandler(false, false) {
            @Override
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                if (Tag.LEAVES.isTagged(e.getBlock().getType())) {
                    // 20% chance for a Silkworm
                    if (ThreadLocalRandom.current().nextInt(100) < 20) {
                        drops.add(SkyfunItems.SKYFUN_SILKWORM.clone());
                    }
                    // Double the drops (saplings)
                    drops.addAll(drops);
                }
            }
        });
    }
}
