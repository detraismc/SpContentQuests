package me.detraismc.spcontentquests.integration;

import io.lumine.mythic.lib.api.crafting.event.MythicCraftItemEvent;
import io.lumine.mythic.lib.api.item.NBTItem;
import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Objective;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent;
import net.Indyuce.mmoitems.api.event.item.ApplyGemStoneEvent;
import net.Indyuce.mmoitems.api.event.item.ApplySoulboundEvent;
import net.Indyuce.mmoitems.api.event.item.ConsumableConsumedEvent;
import net.Indyuce.mmoitems.api.event.item.RepairItemEvent;
import net.Indyuce.mmoitems.api.event.item.UnsocketGemStoneEvent;
import net.Indyuce.mmoitems.api.event.item.UpgradeItemEvent;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MMOItemsHook {

    public static void register(SpContentQuests plugin) {
        try {
            plugin.getServer().getPluginManager().registerEvents(new MMOItemsListener(plugin), plugin);
            plugin.getLogger().info("MMOItems integration enabled!");
        } catch (Throwable e) {
            plugin.getLogger().warning("Failed to enable MMOItems integration: " + e.getMessage());
        }
    }

    private static class MMOItemsListener implements Listener {
        private final SpContentQuests plugin;

        MMOItemsListener(SpContentQuests plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onCraftingStationUse(PlayerUseCraftingStationEvent event) {
            String actionName = event.getInteraction().toString();
            if (!"INSTANT_RECIPE".equals(actionName) && !"CRAFTING_QUEUE".equals(actionName)) return;

            if (!event.hasResult()) return;

            ItemStack result = event.getResult();
            if (result == null || result.getType().isAir()) return;

            PlayerData playerData = event.getPlayerData();
            if (playerData == null) return;
            Player player = playerData.getPlayer();
            if (player == null) return;

            String mmoId = extractMMOItemId(result);
            if (mmoId == null) return;

            processObjective(player, "MMOITEMS_STATIONS_TRADE", mmoId, result.getAmount());
        }

        @EventHandler
        public void onGemStoneApply(ApplyGemStoneEvent event) {
            PlayerData playerData = event.getPlayerData();
            if (playerData == null) return;
            Player player = playerData.getPlayer();
            if (player == null) return;

            MMOItem gemStone = event.getGemStone();
            if (gemStone == null) return;

            String mmoId = extractMMOItemTypeId(gemStone);
            if (mmoId == null) return;

            processObjective(player, "MMOITEMS_APPLY_GEMSTONE", mmoId, 1);
        }

        @EventHandler
        public void onConsumableConsumed(ConsumableConsumedEvent event) {
            PlayerData playerData = event.getPlayerData();
            if (playerData == null) return;
            Player player = playerData.getPlayer();
            if (player == null) return;

            MMOItem mmoItem = event.getMMOItem();
            if (mmoItem == null) return;

            String mmoId = extractMMOItemTypeId(mmoItem);
            if (mmoId == null) return;

            processObjective(player, "MMOITEMS_CONSUME", mmoId, 1);
        }

        @EventHandler
        public void onUpgradeItem(UpgradeItemEvent event) {
            PlayerData playerData = event.getPlayerData();
            if (playerData == null) return;
            Player player = playerData.getPlayer();
            if (player == null) return;

            MMOItem consumable = event.getConsumable();
            if (consumable == null) return;

            String mmoId = extractMMOItemTypeId(consumable);
            if (mmoId == null) return;

            processObjective(player, "MMOITEMS_APPLY_UPGRADE", mmoId, 1);
        }

        @EventHandler
        public void onApplySoulbound(ApplySoulboundEvent event) {
            PlayerData playerData = event.getPlayerData();
            if (playerData == null) return;
            Player player = playerData.getPlayer();
            if (player == null) return;

            MMOItem consumable = event.getConsumable();
            if (consumable == null) return;

            String mmoId = extractMMOItemTypeId(consumable);
            if (mmoId == null) return;

            processObjective(player, "MMOITEMS_APPLY_SOULBOUND", mmoId, 1);
        }

        @EventHandler
        public void onRepairItem(RepairItemEvent event) {
            PlayerData playerData = event.getPlayerData();
            if (playerData == null) return;
            Player player = playerData.getPlayer();
            if (player == null) return;

            MMOItem consumable = event.getConsumable();
            if (consumable == null) return;

            String mmoId = extractMMOItemTypeId(consumable);
            if (mmoId == null) return;

            processObjective(player, "MMOITEMS_APPLY_REPAIR", mmoId, 1);
        }

        @EventHandler
        public void onUnsocketGemStone(UnsocketGemStoneEvent event) {
            PlayerData playerData = event.getPlayerData();
            if (playerData == null) return;
            Player player = playerData.getPlayer();
            if (player == null) return;

            MMOItem consumable = event.getConsumable();
            if (consumable == null) return;

            String mmoId = extractMMOItemTypeId(consumable);
            if (mmoId == null) return;

            processObjective(player, "MMOITEMS_UNSOCKET_GEMSTONE", mmoId, 1);
        }

        @EventHandler
        public void onMythicCraftItem(MythicCraftItemEvent event) {
            InventoryClickEvent trigger = event.getTrigger();
            if (!(trigger.getWhoClicked() instanceof Player player)) return;

            if (trigger.isShiftClick()) {
                Map<Integer, Integer> beforeAmounts = new HashMap<>();
                for (int i = 0; i < 36; i++) {
                    ItemStack item = player.getInventory().getItem(i);
                    beforeAmounts.put(i, (item == null || item.getType().isAir()) ? 0 : item.getAmount());
                }
                final Player finalPlayer = player;
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    processShiftCraft(finalPlayer, beforeAmounts), 1L);
            } else {
                final Player finalPlayer = player;
                plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                    processNormalCraft(finalPlayer), 1L);
            }
        }

        private void processNormalCraft(Player player) {
            try {
                ItemStack cursorItem = player.getItemOnCursor();
                if (cursorItem == null || cursorItem.getType().isAir()) return;

                String mmoId = extractMMOItemId(cursorItem);
                if (mmoId == null) return;

                processObjective(player, "MMOITEMS_CRAFT", mmoId, cursorItem.getAmount());
            } catch (Exception ignored) {}
        }

        private void processShiftCraft(Player player, Map<Integer, Integer> beforeAmounts) {
            try {
                ItemStack craftedSample = null;
                int totalYield = 0;

                for (int i = 0; i < 36; i++) {
                    ItemStack itemAfter = player.getInventory().getItem(i);
                    int amountAfter = (itemAfter == null || itemAfter.getType().isAir()) ? 0 : itemAfter.getAmount();
                    int beforeAmount = beforeAmounts.getOrDefault(i, 0);

                    if (amountAfter > beforeAmount) {
                        int diff = amountAfter - beforeAmount;
                        totalYield += diff;
                        if (craftedSample == null) {
                            craftedSample = itemAfter.clone();
                            craftedSample.setAmount(1);
                        }
                    }
                }

                if (totalYield > 0 && craftedSample != null) {
                    String mmoId = extractMMOItemId(craftedSample);
                    if (mmoId == null) return;

                    processObjective(player, "MMOITEMS_CRAFT", mmoId, totalYield);
                }
            } catch (Exception ignored) {}
        }

        private void processObjective(Player player, String objectiveType, String mmoId, int amount) {
            plugin.getQuestManager().getAllQuests().forEach(q -> {
                PlayerQuestData data = plugin.getPlayerDataManager()
                    .getOrCreateQuestData(player.getUniqueId(), q.getId());
                if (data.isCompleted()) return;

                List<Objective> objectives = q.getObjectives();
                boolean progressed = false;

                for (int i = 0; i < objectives.size(); i++) {
                    Objective obj = objectives.get(i);
                    if (!objectiveType.equalsIgnoreCase(obj.getType())) continue;
                    if (obj.getRequired() != null && !obj.getRequired().isEmpty()
                            && obj.getRequired().stream().noneMatch(r -> matchesRequired(r, mmoId)))
                        continue;

                    int current = data.getObjectiveProgress(i);
                    int max = obj.getAmount();
                    data.setObjectiveProgress(i, Math.min(current + amount, max));
                    progressed = true;
                }

                if (progressed && q.isCompleted(data.getObjectivesProgress())) {
                    data.setCompleted(true);
                    plugin.playQuestComplete(player, q);
                }
            });
        }

        private static String extractMMOItemId(ItemStack item) {
            try {
                NBTItem nbtItem = NBTItem.get(item);
                if (nbtItem != null) {
                    String type = nbtItem.getString("MMOITEMS_ITEM_TYPE");
                    String id = nbtItem.getString("MMOITEMS_ITEM_ID");
                    if (type != null && !type.isEmpty() && id != null && !id.isEmpty()) {
                        return type + "|" + id;
                    }
                }
            } catch (Exception ignored) {}

            try {
                String type = String.valueOf(MMOItems.getType(item));
                String id = MMOItems.getID(item);
                if (type != null && id != null) {
                    return type + "|" + id;
                }
            } catch (Exception ignored) {}

            return null;
        }

        private static String extractMMOItemTypeId(MMOItem mmoItem) {
            try {
                String type = mmoItem.getType().getId();
                String id = mmoItem.getId();
                if (type != null && id != null) {
                    return type + "|" + id;
                }
            } catch (Exception ignored) {}
            return null;
        }

        private static boolean matchesRequired(String required, String value) {
            if (required.regionMatches(true, 0, "CONTAINS:", 0, 9)) {
                return value.toLowerCase().contains(required.substring(9).toLowerCase());
            }
            return required.equalsIgnoreCase(value);
        }
    }
}
