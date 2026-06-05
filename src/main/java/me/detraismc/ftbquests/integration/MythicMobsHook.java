package me.detraismc.ftbquests.integration;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

public class MythicMobsHook {

    public static void register(FTBQuests plugin) {
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

                        plugin.getQuestManager().getAllQuests().stream()
                            .filter(q -> "MYTHICMOBS_KILL".equalsIgnoreCase(q.getObjectiveType()))
                            .filter(q -> q.getObjectiveRequired() == null
                                || q.getObjectiveRequired().isEmpty()
                                || q.getObjectiveRequired().stream().anyMatch(r -> r.equalsIgnoreCase(mobName)))
                            .forEach(q -> {
                                PlayerQuestData data = plugin.getPlayerDataManager()
                                    .getOrCreateQuestData(player.getUniqueId(), q.getId());
                                if (data.isCompleted()) return;
                                int newPoints = Math.min(data.getPoints() + 1, q.getObjectiveAmount());
                                data.setPoints(newPoints);
                                if (newPoints >= q.getObjectiveAmount()) {
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
