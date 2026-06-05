package me.detraismc.ftbquests.listener;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.models.Category;
import me.detraismc.ftbquests.models.PlayerQuestData;
import me.detraismc.ftbquests.models.Quest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ObjectiveListener implements Listener {
    private final FTBQuests plugin;

    public ObjectiveListener(FTBQuests plugin) {
        this.plugin = plugin;
    }

    private void addProgress(Player player, String type, String requiredMatch, int amount) {
        for (Quest quest : plugin.getQuestManager().getAllQuests()) {
            if (!quest.getObjectiveType().equalsIgnoreCase(type))
                continue;

            boolean match = true;
            if (quest.getObjectiveRequired() != null && !quest.getObjectiveRequired().isEmpty()) {
                match = quest.getObjectiveRequired().stream().anyMatch(req -> req.equalsIgnoreCase(requiredMatch));
            }

            if (match) {
                incrementPoints(player, quest, amount);
            }
        }
    }

    private void incrementPoints(Player player, Quest quest, int amount) {
        PlayerQuestData data = plugin.getPlayerDataManager()
                .getOrCreateQuestData(player.getUniqueId(), quest.getId());

        if (data.isCompleted())
            return;

        int newPoints = Math.min(data.getPoints() + amount, quest.getObjectiveAmount());
        data.setPoints(newPoints);

        if (newPoints >= quest.getObjectiveAmount()) {
            data.setCompleted(true);
            plugin.playQuestComplete(player, quest);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        addProgress(event.getPlayer(), "BREAK_BLOCK", event.getBlock().getType().name(), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        addProgress(event.getPlayer(), "PLACE_BLOCK", event.getBlock().getType().name(), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            ItemStack result = event.getRecipe().getResult();
            int amount = result.getAmount();

            if (event.isShiftClick()) {
                int count = 0;
                for (ItemStack item : player.getInventory().getStorageContents()) {
                    if (item != null && item.isSimilar(result)) {
                        count += item.getAmount();
                    }
                }
                ItemStack resultSlot = event.getInventory().getResult();
                if (resultSlot != null && !resultSlot.getType().isAir() && resultSlot.isSimilar(result)) {
                    count += resultSlot.getAmount();
                }
                amount = Math.max(count, result.getAmount());
            }

            addProgress(player, "CRAFT", result.getType().name(), amount);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            addProgress((Player) event.getEntity(), "PICKUP_ITEM", event.getItem().getItemStack().getType().name(),
                    event.getItem().getItemStack().getAmount());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        addProgress(event.getPlayer(), "CONSUME", event.getItem().getType().name(), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmeltExtract(FurnaceExtractEvent event) {
        addProgress(event.getPlayer(), "SMELT", event.getItemType().name(), event.getItemAmount());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEnchant(EnchantItemEvent event) {
        addProgress(event.getEnchanter(), "ENCHANT", event.getItem().getType().name(), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFish(PlayerFishEvent event) {
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item) {
            Item caught = (Item) event.getCaught();
            addProgress(event.getPlayer(), "FISHING", caught.getItemStack().getType().name(), 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent event) {
        addProgress(event.getPlayer(), "THROW", event.getItemDrop().getItemStack().getType().name(),
                event.getItemDrop().getItemStack().getAmount());
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            addProgress(killer, "KILL", event.getEntity().getType().name(), 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreed(EntityBreedEvent event) {
        if (event.getBreeder() instanceof Player) {
            addProgress((Player) event.getBreeder(), "BREED", event.getEntity().getType().name(), 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTame(EntityTameEvent event) {
        if (event.getOwner() instanceof Player) {
            addProgress((Player) event.getOwner(), "TAME", event.getEntity().getType().name(), 1);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        addProgress(event.getPlayer(), "SHEAR", event.getEntity().getType().name(), 1);
    }
}
