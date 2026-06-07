package me.detraismc.spcontentquests.listener;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Objective;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import me.detraismc.spcontentquests.models.Quest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObjectiveListener implements Listener {
    private final SpContentQuests plugin;
    private final Set<Location> playerPlacedBlocks = new HashSet<>();

    public ObjectiveListener(SpContentQuests plugin) {
        this.plugin = plugin;
    }

    private void addProgress(Player player, String type, String requiredMatch, int amount) {
        for (Quest quest : plugin.getQuestManager().getAllQuests()) {
            PlayerQuestData data = plugin.getPlayerDataManager()
                    .getOrCreateQuestData(player.getUniqueId(), quest.getId());
            if (data.isCompleted()) continue;

            List<Objective> objectives = quest.getObjectives();
            boolean progressed = false;

            for (int i = 0; i < objectives.size(); i++) {
                Objective obj = objectives.get(i);
                if (!obj.getType().equalsIgnoreCase(type)) continue;

                boolean match = true;
                if (obj.getRequired() != null && !obj.getRequired().isEmpty()) {
                    match = obj.getRequired().stream().anyMatch(req -> matchesRequired(req, requiredMatch));
                }

                if (match) {
                    int current = data.getObjectiveProgress(i);
                    int max = obj.getAmount();
                    int newPoints = Math.min(current + amount, max);
                    data.setObjectiveProgress(i, newPoints);
                    progressed = true;
                }
            }

            if (progressed && quest.isCompleted(data.getObjectivesProgress())) {
                data.setCompleted(true);
                plugin.playQuestComplete(player, quest);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (playerPlacedBlocks.remove(event.getBlock().getLocation())) return;
        addProgress(event.getPlayer(), "BREAK_BLOCK", event.getBlock().getType().name(), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        playerPlacedBlocks.add(event.getBlock().getLocation());
        addProgress(event.getPlayer(), "PLACE_BLOCK", event.getBlock().getType().name(), 1);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            ItemStack result = event.getRecipe().getResult();
            int amount = result.getAmount();

            if (event.isShiftClick()) {
                ItemStack[] matrix = event.getInventory().getMatrix();
                java.util.List<ItemStack> ingredientTypes = new java.util.ArrayList<>();
                java.util.List<Integer> neededAmounts = new java.util.ArrayList<>();

                for (ItemStack ingredient : matrix) {
                    if (ingredient == null || ingredient.getType().isAir()) continue;
                    boolean found = false;
                    for (int i = 0; i < ingredientTypes.size(); i++) {
                        if (ingredientTypes.get(i).isSimilar(ingredient)) {
                            neededAmounts.set(i, neededAmounts.get(i) + ingredient.getAmount());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ingredientTypes.add(ingredient.clone());
                        neededAmounts.add(ingredient.getAmount());
                    }
                }

                int maxCrafts = Integer.MAX_VALUE;
                for (int i = 0; i < ingredientTypes.size(); i++) {
                    ItemStack neededType = ingredientTypes.get(i);
                    int neededPerOp = neededAmounts.get(i);
                    int available = 0;

                    for (ItemStack item : player.getInventory().getStorageContents()) {
                        if (item != null && item.isSimilar(neededType)) {
                            available += item.getAmount();
                        }
                    }
                    for (ItemStack item : matrix) {
                        if (item != null && item.isSimilar(neededType)) {
                            available += item.getAmount();
                        }
                    }

                    maxCrafts = Math.min(maxCrafts, available / neededPerOp);
                }

                if (maxCrafts == Integer.MAX_VALUE || maxCrafts < 1) {
                    amount = result.getAmount();
                } else {
                    amount = maxCrafts * result.getAmount();
                }
            }

            if (isCustomItem(result)) {
                addProgress(player, "CRAFT_CUSTOM", getCustomItemMatch(result), amount);
            } else {
                addProgress(player, "CRAFT", result.getType().name(), amount);
            }
        }
    }

    private boolean isCustomItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        ItemMeta defaultMeta = new ItemStack(item.getType()).getItemMeta();
        return !meta.equals(defaultMeta);
    }

    private String getCustomItemMatch(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            return ChatColor.stripColor(meta.getDisplayName());
        }
        return item.getType().name();
    }

    private static boolean matchesRequired(String required, String value) {
        if (required.regionMatches(true, 0, "CONTAINS:", 0, 9)) {
            return value.toLowerCase().contains(required.substring(9).toLowerCase());
        }
        return required.equalsIgnoreCase(value);
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

    @EventHandler(ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (var block : event.getBlocks()) {
            Location loc = block.getLocation();
            if (playerPlacedBlocks.remove(loc)) {
                playerPlacedBlocks.add(loc.add(event.getDirection().getDirection()));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (var block : event.getBlocks()) {
            Location loc = block.getLocation();
            if (playerPlacedBlocks.remove(loc)) {
                playerPlacedBlocks.add(loc.add(event.getDirection().getDirection()));
            }
        }
    }
}
