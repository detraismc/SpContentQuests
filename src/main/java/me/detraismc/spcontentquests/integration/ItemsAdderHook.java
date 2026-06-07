package me.detraismc.spcontentquests.integration;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

public class ItemsAdderHook {

    private static final String PKG = "dev.lone.itemsadder.api.Events";

    public static void register(SpContentQuests plugin) {
        if (!isAvailable()) return;

        registerEvent(plugin, "CustomBlockBreakEvent", "ITEMSADDER_BREAK_BLOCK");
        registerEvent(plugin, "CustomBlockPlaceEvent", "ITEMSADDER_PLACE_BLOCK");
        plugin.getLogger().info("ItemsAdder integration enabled!");
    }

    private static boolean isAvailable() {
        try {
            Class.forName("dev.lone.itemsadder.api.ItemsAdder");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static void registerEvent(SpContentQuests plugin, String eventName, String objectiveType) {
        Class<?> eventClass;
        try {
            eventClass = Class.forName(PKG + "." + eventName);
        } catch (ClassNotFoundException e) {
            return;
        }

        Method getPlayer = findMethod(eventClass, "getPlayer", "getBukkitPlayer");
        if (getPlayer == null) return;

        Method getNamespacedID = findMethod(eventClass, "getNamespacedID");
        Method getCustomBlock = getNamespacedID != null ? null : findMethod(eventClass, "getCustomBlock");
        if (getNamespacedID == null && getCustomBlock == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedPlayer = getPlayer;
        final Method resolvedGetNamespacedID = getNamespacedID;
        final Method resolvedGetCustomBlock = getCustomBlock;
        final String resolvedType = objectiveType;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleBlockEvent(plugin, resolvedEvent, resolvedPlayer, resolvedGetNamespacedID, resolvedGetCustomBlock, resolvedType, rawEvent),
            plugin
        );
    }

    private static void handleBlockEvent(SpContentQuests plugin, Class<?> eventClass, Method getPlayer,
                                          Method getNamespacedID, Method getCustomBlock, String objectiveType, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerObj = getPlayer.invoke(event);
            if (!(playerObj instanceof Player player)) return;

            String blockId = null;

            if (getNamespacedID != null) {
                Object idObj = getNamespacedID.invoke(event);
                if (idObj != null) {
                    blockId = idObj instanceof String s ? s : idObj.toString();
                }
            }

            if (blockId == null && getCustomBlock != null) {
                Object customBlock = getCustomBlock.invoke(event);
                if (customBlock != null) {
                    Method getNS = customBlock.getClass().getMethod("getNamespacedID");
                    Object idObj = getNS.invoke(customBlock);
                    if (idObj != null) {
                        blockId = idObj instanceof String s ? s : idObj.toString();
                    }
                }
            }

            if (blockId == null || blockId.isEmpty()) return;

            final String resolvedRequired = blockId;

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> objectiveType.equalsIgnoreCase(q.getObjectiveType()))
                .filter(q -> q.getObjectiveRequired() == null
                    || q.getObjectiveRequired().isEmpty()
                    || q.getObjectiveRequired().stream().anyMatch(r -> matchesRequired(r, resolvedRequired)))
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
    }

    private static boolean matchesRequired(String required, String value) {
        if (required.regionMatches(true, 0, "CONTAINS:", 0, 9)) {
            return value.toLowerCase().contains(required.substring(9).toLowerCase());
        }
        return required.equalsIgnoreCase(value);
    }

    private static Method findMethod(Class<?> clazz, String... names) {
        for (String name : names) {
            try {
                return clazz.getMethod(name);
            } catch (NoSuchMethodException ignored) {}
        }
        return null;
    }
}
