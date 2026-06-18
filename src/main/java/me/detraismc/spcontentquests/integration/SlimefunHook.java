package me.detraismc.spcontentquests.integration;

import io.github.thebusybiscuit.slimefun4.api.events.AncientAltarCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.events.MultiBlockCraftEvent;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Objective;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SlimefunHook {

    public static void register(SpContentQuests plugin) {
        try {
            plugin.getServer().getPluginManager().registerEvents(new SlimefunListener(plugin), plugin);
            plugin.getLogger().info("Slimefun integration enabled!");
        } catch (Throwable e) {
            plugin.getLogger().warning("Failed to enable Slimefun integration: " + e.getMessage());
        }
    }

    private static class SlimefunListener implements Listener {
        private final SpContentQuests plugin;

        SlimefunListener(SpContentQuests plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onMultiBlockCraft(MultiBlockCraftEvent event) {
            Player player = event.getPlayer();
            if (player == null) return;

            String machineId = null;
            SlimefunItem machine = event.getMachine();
            if (machine != null) {
                machineId = machine.getId();
            }
            if (machineId == null) return;

            String objectiveType = "SLIMEFUN_MULTIBLOCK_" + machineId;

            ItemStack result = event.getOutput();
            if (result == null || result.getType().isAir()) return;

            String slimefunId = extractSlimefunId(result);
            final String resolvedRequired = slimefunId != null ? slimefunId : result.getType().name();
            final String resolvedType = objectiveType;

            processCraft(player, result, resolvedType, resolvedRequired);
        }

        @EventHandler
        public void onAncientAltarCraft(AncientAltarCraftEvent event) {
            Player player = event.getPlayer();
            if (player == null) return;

            ItemStack result = event.getItem();
            if (result == null || result.getType().isAir()) return;

            String slimefunId = extractSlimefunId(result);
            final String resolvedRequired = slimefunId != null ? slimefunId : result.getType().name();

            processCraft(player, result, "SLIMEFUN_ANCIENT_ALTAR", resolvedRequired);
        }

        private void processCraft(Player player, ItemStack result, String objectiveType, String resolvedRequired) {
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
                            && obj.getRequired().stream().noneMatch(r -> matchesRequired(r, resolvedRequired)))
                        continue;
                    int current = data.getObjectiveProgress(i);
                    int max = obj.getAmount();
                    data.setObjectiveProgress(i, Math.min(current + result.getAmount(), max));
                    progressed = true;
                }
                if (progressed && q.isCompleted(data.getObjectivesProgress())) {
                    data.setCompleted(true);
                    plugin.playQuestComplete(player, q);
                }
            });
        }

        private static String extractSlimefunId(ItemStack item) {
            try {
                SlimefunItem sfItem = SlimefunItem.getByItem(item);
                if (sfItem != null) {
                    return sfItem.getId();
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
