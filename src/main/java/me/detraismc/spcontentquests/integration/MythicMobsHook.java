package me.detraismc.spcontentquests.integration;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Objective;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

public class MythicMobsHook {

    private static boolean matchesRequired(String required, String value) {
        if (required.regionMatches(true, 0, "CONTAINS:", 0, 9)) {
            return value.toLowerCase().contains(required.substring(9).toLowerCase());
        }
        return required.equalsIgnoreCase(value);
    }

    public static void register(SpContentQuests plugin) {
        try {
            Class<?> eventClass = Class.forName("io.lumine.mythic.bukkit.events.MythicMobDeathEvent");
            Method getMobMethod = eventClass.getMethod("getMob");
            Class<?> activeMobClass = getMobMethod.getReturnType();
            Method getTypeMethod = activeMobClass.getMethod("getType");
            Class<?> mythicMobClass = getTypeMethod.getReturnType();
            Method getInternalNameMethod = mythicMobClass.getMethod("getInternalName");

            Method getKillerMethod;
            try {
                getKillerMethod = eventClass.getMethod("getKiller");
            } catch (NoSuchMethodException e) {
                plugin.getLogger().warning("MythicMobs API missing getKiller(), integration disabled.");
                return;
            }

            Listener listener = new Listener() {};

            plugin.getServer().getPluginManager().registerEvent(
                (Class) eventClass,
                listener,
                EventPriority.NORMAL,
                (l, rawEvent) -> {
                    try {
                        if (!eventClass.isInstance(rawEvent)) return;
                        Object event = eventClass.cast(rawEvent);

                        Object killer = getKillerMethod.invoke(event);
                        if (!(killer instanceof Player player)) return;

                        Object mobType = getTypeMethod.invoke(getMobMethod.invoke(event));
                        String mobName = (String) getInternalNameMethod.invoke(mobType);

                        plugin.getQuestManager().getAllQuests().forEach(q -> {
                                PlayerQuestData data = plugin.getPlayerDataManager()
                                    .getOrCreateQuestData(player.getUniqueId(), q.getId());
                                if (data.isCompleted()) return;
                                java.util.List<Objective> objectives = q.getObjectives();
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
                    } catch (Exception ignored) {}
                },
                plugin
            );

            plugin.getLogger().info("MythicMobs integration enabled!");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to hook MythicMobs: " + e.getMessage());
        }
    }
}
