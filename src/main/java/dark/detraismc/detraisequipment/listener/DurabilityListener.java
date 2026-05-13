package dark.detraismc.detraisequipment.listener;

import dark.detraismc.detraisequipment.DEItems;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class DurabilityListener implements Listener {

    @EventHandler
    public void onVanillaDamage(PlayerItemDamageEvent event) {
        ItemStack item = event.getItem();
        if (item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(DEItems.DURABILITY_KEY, PersistentDataType.INTEGER)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        handleDurability(player, player.getInventory().getItemInMainHand());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        handleDurability(event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
    }

    private void handleDurability(Player player, ItemStack item) {
        if (item == null || !item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (pdc.has(DEItems.DURABILITY_KEY, PersistentDataType.INTEGER)) {

            // --- NEW: UNBREAKING COMPATIBILITY ---
            int unbreakingLevel = item.getEnchantmentLevel(Enchantment.UNBREAKING);
            if (unbreakingLevel > 0) {
                // Formula: 100 / (level + 1) % chance to LOSE durability.
                // We roll a number between 0 and level. Only if we roll 0 do we lose durability.
                int roll = ThreadLocalRandom.current().nextInt(unbreakingLevel + 1);
                if (roll != 0) {
                    return; // Skip durability loss!
                }
            }
            // -------------------------------------

            int current = pdc.get(DEItems.DURABILITY_KEY, PersistentDataType.INTEGER);
            int max = pdc.get(DEItems.MAX_DURABILITY_KEY, PersistentDataType.INTEGER);

            current--;

            if (current <= 0) {
                player.getInventory().setItemInMainHand(null);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            } else {
                pdc.set(DEItems.DURABILITY_KEY, PersistentDataType.INTEGER, current);

                List<String> lore = meta.getLore();
                if (lore != null && !lore.isEmpty()) {
                    lore.set(lore.size() - 1, "§8Durability: " + current + " / " + max);
                    meta.setLore(lore);
                }

                if (meta instanceof Damageable damageable) {
                    int maxVanilla = item.getType().getMaxDurability();
                    double percentageLeft = (double) current / max;
                    int damageToShow = (int) (maxVanilla - (maxVanilla * percentageLeft));
                    damageable.setDamage(damageToShow);
                }

                item.setItemMeta(meta);
            }
        }
    }
    // Triggers when the player TAKES damage (for Armor durability)
    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(org.bukkit.event.entity.EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // Damage all 4 armor slots
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor != null && armor.getType() != Material.AIR) {
                handleDurability(player, armor);
            }
        }
    }
}