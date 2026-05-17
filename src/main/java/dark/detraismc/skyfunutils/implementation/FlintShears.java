package dark.detraismc.skyfunutils.implementation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ToolUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class FlintShears extends SimpleSlimefunItem<ToolUseHandler> {

    public FlintShears(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public ToolUseHandler getItemHandler() {
        return (e, tool, fortune, drops) -> {

            // We check the tool's durability meta
            if (tool.hasItemMeta() && tool.getItemMeta() instanceof Damageable damageable) {

                // Add 1 extra damage. (Vanilla block breaking will automatically add 1 on its own,
                // so this makes it lose 2 total durability per block broken!)
                int newDamage = damageable.getDamage() + 1;
                int maxDurability = tool.getType().getMaxDurability();

                // If the item exceeds its maximum durability, break it cleanly
                if (newDamage >= maxDurability) {
                    e.getPlayer().getInventory().setItemInMainHand(null);
                    e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                } else {
                    // Otherwise, save the extra durability loss back to the item
                    damageable.setDamage(newDamage);
                    tool.setItemMeta(damageable);
                }
            }
        };
    }
}
