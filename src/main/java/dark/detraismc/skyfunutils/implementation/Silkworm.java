package dark.detraismc.skyfunutils.implementation;

import dark.detraismc.skyfunutils.SkyfunUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

public class Silkworm extends SlimefunItem implements Listener {

    // This set stores the locations of leaves that are currently transforming
    private static final Set<Location> INFESTING_LEAVES = new HashSet<>();

    public Silkworm(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        // Register this class as a listener so we can block the breaking of the glass
        Bukkit.getPluginManager().registerEvents(this, SkyfunUtils.getInstance());

        addItemHandler((ItemUseHandler) e -> {
            if (e.getClickedBlock().isPresent()) {

                // PROTECTION CHECK
                if (e.getInteractEvent().isCancelled()) {
                    return;
                }

                Block b = e.getClickedBlock().get();
                Location loc = b.getLocation();

                // Check if it's leaves AND make sure it's not already being infested
                if (Tag.LEAVES.isTagged(b.getType()) && !INFESTING_LEAVES.contains(loc)) {

                    if (BlockStorage.hasBlockInfo(b)) {
                        return;
                    }

                    // 1. Transform into White Stained Glass immediately
                    b.setType(Material.WHITE_STAINED_GLASS);
                    b.getWorld().playEffect(loc, org.bukkit.Effect.STEP_SOUND, Material.WHITE_WOOL);

                    // 2. Lock the block so it can't be broken
                    INFESTING_LEAVES.add(loc);

                    // 3. Consume the Silkworm safely
                    e.getItem().subtract(1);
                    e.cancel();

                    // 4. Schedule the transformation to Cobweb after 5 seconds (100 ticks)
                    Bukkit.getScheduler().runTaskLater(SkyfunUtils.getInstance(), () -> {

                        // Just in case something else changed the block, double-check it's still glass
                        if (b.getType() == Material.WHITE_STAINED_GLASS) {
                            b.setType(Material.COBWEB);
                            // Play a nice sound/effect when it finishes
                            b.getWorld().playEffect(loc, org.bukkit.Effect.STEP_SOUND, Material.COBWEB);
                        }

                        // Unlock the block so it can be broken normally again
                        INFESTING_LEAVES.remove(loc);

                    }, 100L); // 20 ticks = 1 second. 100 ticks = 5 seconds.
                }
            }
        });
    }

    // This event listens for ANY block break on the server
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // If the broken block is in our locked list, cancel the break
        if (INFESTING_LEAVES.contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "The silkworm is still infesting this block!");
        }
    }
}