package dark.detraismc.skyfunutils.implementation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.BlockBreakHandler;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Hammer extends SlimefunItem {

    public Hammer(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        addItemHandler(new BlockBreakHandler(false, false) {

            @Override
            public void onPlayerBreak(BlockBreakEvent e, ItemStack item, List<ItemStack> drops) {
                Block b = e.getBlock();
                if (b.getType() == Material.COBBLESTONE) {
                    b.setType(Material.AIR);
                    b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.GRAVEL));
                }
                else if (b.getType() == Material.GRAVEL) {
                    b.setType(Material.AIR);
                    b.getWorld().dropItemNaturally(b.getLocation(), new ItemStack(Material.SAND));
                }
            }
        });
    }
}
