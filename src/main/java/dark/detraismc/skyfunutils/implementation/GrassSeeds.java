package dark.detraismc.skyfunutils.implementation;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.api.events.PlayerRightClickEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public class GrassSeeds extends SlimefunItem {

    public GrassSeeds(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        // Use ItemUseHandler for right-click functionality
        addItemHandler((ItemUseHandler) e -> {
            // Optional: check if they clicked a block
            if (e.getClickedBlock().isPresent()) {

                // 1. PROTECTION CHECK: If WorldGuard/Bentobox cancelled the interaction, stop here!
                if (e.getInteractEvent().isCancelled()) {
                    return;
                }

                Block b = e.getClickedBlock().get();

                if (b.getType() == Material.DIRT) {
                    // Change Dirt to Grass
                    b.setType(Material.GRASS_BLOCK);

                    // Play a little effect so it feels nice
                    b.getWorld().playEffect(b.getLocation(), org.bukkit.Effect.STEP_SOUND, Material.GRASS_BLOCK);

                    // 2. Consume 1 seed from the player's hand safely
                    e.getItem().subtract(1);

                    // 3. Cancel the vanilla event so the item doesn't try to trigger anything else
                    e.cancel();
                }
            }
        });
    }
}