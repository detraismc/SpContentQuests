package me.detraismc.spcontentquests.integration;

import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Objective;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class ItemsAdderHook {

    public static void register(SpContentQuests plugin) {
        try {
            plugin.getServer().getPluginManager().registerEvents(new ItemsAdderListener(plugin), plugin);
            plugin.getLogger().info("ItemsAdder integration enabled!");
        } catch (Throwable e) {
            plugin.getLogger().warning("Failed to enable ItemsAdder integration: " + e.getMessage());
        }
    }

    private static class ItemsAdderListener implements Listener {
        private final SpContentQuests plugin;

        ItemsAdderListener(SpContentQuests plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onCustomBlockBreak(CustomBlockBreakEvent event) {
            handleBlockEvent(event.getPlayer(), event.getNamespacedID(), "ITEMSADDER_BREAK_BLOCK");
        }

        @EventHandler
        public void onCustomBlockPlace(CustomBlockPlaceEvent event) {
            handleBlockEvent(event.getPlayer(), event.getNamespacedID(), "ITEMSADDER_PLACE_BLOCK");
        }

        private void handleBlockEvent(Player player, String blockId, String objectiveType) {
            if (blockId == null || blockId.isEmpty()) return;

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
                            && obj.getRequired().stream().noneMatch(r -> matchesRequired(r, blockId)))
                        continue;
                    int current = data.getObjectiveProgress(i);
                    int max = obj.getAmount();
                    data.setObjectiveProgress(i, Math.min(current + 1, max));
                    progressed = true;
                }
                if (progressed && q.isCompleted(data.getObjectivesProgress())) {
                    data.setCompleted(true);
                    plugin.playQuestComplete(player, q);
                }
            });
        }

        private static boolean matchesRequired(String required, String value) {
            if (required.regionMatches(true, 0, "CONTAINS:", 0, 9)) {
                return value.toLowerCase().contains(required.substring(9).toLowerCase());
            }
            return required.equalsIgnoreCase(value);
        }
    }
}
