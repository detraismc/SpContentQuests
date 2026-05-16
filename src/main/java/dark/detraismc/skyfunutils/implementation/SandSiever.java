package dark.detraismc.skyfunutils.implementation;

import dark.detraismc.skyfunutils.SkyfunUtils;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class SandSiever extends SlimefunItem implements Listener {

    public SandSiever(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    public void preRegister() {
        // We register the class as a Listener so Bukkit sees the EventHandler below
        Bukkit.getPluginManager().registerEvents(this, SkyfunUtils.getInstance());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onRightClick(PlayerInteractEvent e) {
        // 1. Only track right-clicks on blocks
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // 2. Prevent the event from running twice (once for main hand, once for off hand)
        if (e.getHand() != org.bukkit.inventory.EquipmentSlot.HAND) return;

        Block b = e.getClickedBlock();
        if (b == null) return;

        // 3. Check if this block is actually our Sand Siever
        if (!BlockStorage.check(b, getId())) return;

        // 4. CRITICAL FIX: Since it IS our machine, cancel the event IMMEDIATELY.
        // This stops the default vanilla Loom GUI from opening no matter what is in their hand!
        e.setCancelled(true);

        Player p = e.getPlayer();
        ItemStack itemInHand = e.getItem();

        // 5. Check if they are holding Sand
        if (itemInHand != null && itemInHand.getType() == Material.SAND) {

            // Consume the sand safely
            if (itemInHand.getAmount() > 1) {
                itemInHand.setAmount(itemInHand.getAmount() - 1);
            } else {
                p.getInventory().setItemInMainHand(null);
            }

            // Effects
            b.getWorld().playSound(b.getLocation(), Sound.BLOCK_SAND_BREAK, 1.0f, 0.8f);
            b.getWorld().playEffect(b.getLocation().add(0.5, 0.5, 0.5), org.bukkit.Effect.STEP_SOUND, Material.SAND);

            // Generate and pop the drop
            ItemStack drop = getRandomDrop();
            if (drop != null) {
                b.getWorld().dropItemNaturally(b.getLocation().add(0.5, 1.2, 0.5), drop);
            }
        } else {
            // 6. Optional: Send a action bar or chat message telling them what they need
            p.sendMessage("§cYou must hold Sand to use the Sand Siever!");
        }
    }

    private ItemStack getRandomDrop() {
        int roll = ThreadLocalRandom.current().nextInt(100);

        if (roll < 1) return new ItemStack(Material.DIAMOND);
        if (roll < 10) return new ItemStack(Material.LAPIS_LAZULI);
        if (roll < 25) return new ItemStack(Material.REDSTONE);
        if (roll < 45) return new ItemStack(Material.IRON_NUGGET);
        if (roll < 65) return new ItemStack(Material.GUNPOWDER);
        if (roll < 90) return new ItemStack(Material.BONE_MEAL);

        return null;
    }
}