package me.detraismc.spcontentquests.integration;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Objective;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class MythicMobsHook {

    public static void register(SpContentQuests plugin) {
        try {
            plugin.getServer().getPluginManager().registerEvents(new MythicMobsListener(plugin), plugin);
            plugin.getLogger().info("MythicMobs integration enabled!");
        } catch (Throwable e) {
            plugin.getLogger().warning("Failed to enable MythicMobs integration: " + e.getMessage());
        }
    }

    private static class MythicMobsListener implements Listener {
        private final SpContentQuests plugin;

        MythicMobsListener(SpContentQuests plugin) {
            this.plugin = plugin;
        }

        @EventHandler
        public void onMythicMobDeath(MythicMobDeathEvent event) {
            Player player = (Player) event.getKiller();
            if (player == null) return;

            String mobName = event.getMob().getType().getInternalName();

            plugin.getQuestManager().getAllQuests().forEach(q -> {
                PlayerQuestData data = plugin.getPlayerDataManager()
                    .getOrCreateQuestData(player.getUniqueId(), q.getId());
                if (data.isCompleted()) return;
                List<Objective> objectives = q.getObjectives();
                boolean progressed = false;
                for (int i = 0; i < objectives.size(); i++) {
                    Objective obj = objectives.get(i);
                    if (!"MYTHICMOBS_KILL".equalsIgnoreCase(obj.getType())) continue;
                    if (obj.getRequired() != null && !obj.getRequired().isEmpty()
                            && obj.getRequired().stream().noneMatch(r -> matchesRequired(r, mobName)))
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
