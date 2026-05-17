package dark.detraismc.skyfunutils.implementation;

import dark.detraismc.skyfunutils.SkyfunUtils;
import dark.detraismc.skyfunutils.utils.SieveRegistry;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Sieve extends SlimefunItem implements Listener {

    // Define the side directions we want to check
    private static final BlockFace[] SIDE_FACES = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

    public Sieve(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        Bukkit.getPluginManager().registerEvents(this, SkyfunUtils.getInstance());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;

        Block b = e.getClickedBlock();
        if (b == null) return;

        if (!BlockStorage.check(b, getId())) return;

        // Cancel event immediately to suppress vanilla Loom GUI
        e.setCancelled(true);

        Player p = e.getPlayer();
        ItemStack itemInHand = e.getItem();
        if (itemInHand == null) {
            p.sendMessage("§cYou must hold a siftable material to use the Sieve!");
            return;
        }

        Material inputType = itemInHand.getType();

        // Check registry validity dynamically via config mapping
        if (!SieveRegistry.getRewards(inputType).isEmpty()) {

            // 1. Consume 1 item from the stack immediately
            if (itemInHand.getAmount() > 1) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            } else {
                p.getInventory().setItemInMainHand(null);
            }

            Location loc = b.getLocation();

            // 2. Play processing audio and particles
            Sound siftSound = getSound(inputType);
            b.getWorld().playSound(loc, siftSound, 1.0f, 1.2f);
            b.getWorld().playEffect(loc.clone().add(0.5, 0.5, 0.5), org.bukkit.Effect.STEP_SOUND, inputType);

            // 3. Roll drop from configuration maps
            ItemStack drop = SieveRegistry.rollDrop(inputType);
            if (drop != null) {

                // ============================================================
                // SIDE OUTPUT LOGIC
                // Loop through all adjacent horizontal block faces
                // ============================================================
                boolean inserted = false;

                for (BlockFace face : SIDE_FACES) {
                    Block sideBlock = b.getRelative(face);

                    if (sideBlock.getState() instanceof Container container) {
                        // Try adding the item into this side container's inventory
                        Map<Integer, ItemStack> leftovers = container.getInventory().addItem(drop);

                        // If it successfully inserted everything, break the loop
                        if (leftovers.isEmpty()) {
                            inserted = true;
                            break;
                        } else {
                            // If it only fit partially, update the drop stack to reflect the remainder
                            // and keep searching the other side blocks
                            drop = leftovers.get(0);
                        }
                    }
                }

                // If no side containers were found Or they were completely full,
                // spit the item out naturally on top of the sieve block.
                if (!inserted) {
                    b.getWorld().dropItemNaturally(loc.clone().add(0.5, 1.2, 0.5), drop);
                }
                // ============================================================
            }

        } else {
            p.sendMessage("§cYou cannot sift this item!");
        }
    }

    private Sound getSound(Material m) {
        if (m == Material.SOUL_SAND || m == Material.SOUL_SOIL) {
            return Sound.BLOCK_SOUL_SAND_HIT;
        } else if (m == Material.DIRT) {
            return Sound.BLOCK_ROOTS_HIT;
        } else if (m == Material.GRAVEL) {
            return Sound.BLOCK_GRAVEL_HIT;
        } else {
            return Sound.BLOCK_SAND_HIT;
        }
    }
}