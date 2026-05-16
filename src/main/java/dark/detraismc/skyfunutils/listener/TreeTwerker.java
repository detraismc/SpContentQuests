package dark.detraismc.skyfunutils.listener;

import dark.detraismc.skyfunutils.SkyfunUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.concurrent.ThreadLocalRandom;

public class TreeTwerker implements Listener {

    private final boolean enabled;
    private final int radius;
    private final int growChance;

    public TreeTwerker() {
        SkyfunUtils plugin = SkyfunUtils.getInstance();
        FileConfiguration config = plugin.getConfig();

        // Load values from config.yml with safe fallback defaults
        this.enabled = config.getBoolean("mechanics.twerk-for-trees.enabled", true);
        this.radius = config.getInt("mechanics.twerk-for-trees.radius", 3);
        this.growChance = config.getInt("mechanics.twerk-for-trees.grow-chance", 15);

        // Only register the listener if the feature is enabled
        if (this.enabled) {
            Bukkit.getPluginManager().registerEvents(this, plugin);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onSneak(PlayerToggleSneakEvent e) {
        // Double check safety safeguard (though it won't fire if unregistered anyway)
        if (!enabled) return;

        // We only care when they PRESS shift (going down), not when they release it
        if (!e.isSneaking()) return;

        Player p = e.getPlayer();
        World w = p.getWorld();

        // Get the player's exact block coordinates
        int px = p.getLocation().getBlockX();
        int py = p.getLocation().getBlockY();
        int pz = p.getLocation().getBlockZ();

        // Loop through nearby blocks using our config-driven radius
        for (int x = px - radius; x <= px + radius; x++) {
            for (int y = py - 1; y <= py + 2; y++) {
                for (int z = pz - radius; z <= pz + radius; z++) {

                    Block b = w.getBlockAt(x, y, z);

                    // Check if the block is a vanilla sapling
                    if (Tag.SAPLINGS.isTagged(b.getType())) {

                        // Spawn the green "happy villager" particle
                        w.spawnParticle(Particle.HAPPY_VILLAGER, b.getLocation().add(0.5, 0.5, 0.5), 2, 0.3, 0.3, 0.3);

                        // Roll the chance based on our config variable
                        if (ThreadLocalRandom.current().nextInt(100) < growChance) {
                            // Simulate applying bonemeal to the sapling natively
                            b.applyBoneMeal(BlockFace.UP);
                        }
                    }
                }
            }
        }
    }
}