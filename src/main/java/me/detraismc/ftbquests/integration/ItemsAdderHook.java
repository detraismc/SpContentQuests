package me.detraismc.ftbquests.integration;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

public class ItemsAdderHook {

    private static final String PKG = "dev.lone.itemsadder.api.Events";

    public static void register(FTBQuests plugin) {
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

    private static void registerEvent(FTBQuests plugin, String eventName, String objectiveType) {
        Class<?> eventClass;
        try {
            eventClass = Class.forName(PKG + "." + eventName);
        } catch (ClassNotFoundException e) {
            return;
        }

        Method getPlayer = findMethod(eventClass, "getPlayer", "getBukkitPlayer");
        if (getPlayer == null) return;

        Method getCustomBlock = findMethod(eventClass, "getCustomBlock");
        if (getCustomBlock == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedPlayer = getPlayer;
        final Method resolvedGetBlock = getCustomBlock;
        final String resolvedType = objectiveType;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleBlockEvent(plugin, resolvedEvent, resolvedPlayer, resolvedGetBlock, resolvedType, rawEvent),
            plugin
        );
    }

    private static void handleBlockEvent(FTBQuests plugin, Class<?> eventClass, Method getPlayer,
                                          Method getCustomBlock, String objectiveType, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerObj = getPlayer.invoke(event);
            if (!(playerObj instanceof Player player)) return;

            Object customBlock = getCustomBlock.invoke(event);
            if (customBlock == null) return;

            Method getNamespacedID = customBlock.getClass().getMethod("getNamespacedID");
            Object idObj = getNamespacedID.invoke(customBlock);
            if (!(idObj instanceof String blockId)) return;

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
