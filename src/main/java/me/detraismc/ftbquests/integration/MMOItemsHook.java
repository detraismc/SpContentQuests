package me.detraismc.ftbquests.integration;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class MMOItemsHook {

    private static final String[] MMO_PACKAGES = {
        "net.Indyuce.mmoitems",
        "io.lumine.mmoitems"
    };

    private static final String[] STATION_EVENT_PATHS = {
        "net.Indyuce.mmoitems.api.event.PlayerUseCraftingStationEvent",
        "io.lumine.mmoitems.api.event.PlayerUseCraftingStationEvent"
    };

    public static void register(FTBQuests plugin) {
        Class<?> eventClass = null;
        for (String path : STATION_EVENT_PATHS) {
            try {
                eventClass = Class.forName(path);
                break;
            } catch (ClassNotFoundException ignored) {}
        }
        if (eventClass == null) return;

        Method getPlayerData = findMethod(eventClass, "getPlayerData");
        if (getPlayerData == null) return;

        Method hasResult = findMethod(eventClass, "hasResult");
        if (hasResult == null) return;

        Method getResult = findMethod(eventClass, "getResult");
        if (getResult == null) return;

        Method getInteraction = findMethod(eventClass, "getInteraction");
        if (getInteraction == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedGetPlayerData = getPlayerData;
        final Method resolvedHasResult = hasResult;
        final Method resolvedGetResult = getResult;
        final Method resolvedGetInteraction = getInteraction;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleTrade(plugin, resolvedEvent, resolvedGetPlayerData, resolvedHasResult, resolvedGetResult, resolvedGetInteraction, rawEvent),
            plugin
        );
        registerGemStone(plugin);
        registerConsumable(plugin);
        registerUpgradeItem(plugin);
        registerApplySoulbound(plugin);
        registerRepairItem(plugin);
        registerUnsocketGemStone(plugin);
        plugin.getLogger().info("MMOItems integration enabled!");
    }

    private static void registerGemStone(FTBQuests plugin) {
        Class<?> eventClass = null;
        String[] gemPaths = {
            "net.Indyuce.mmoitems.api.event.item.ApplyGemStoneEvent",
            "io.lumine.mmoitems.api.event.item.ApplyGemStoneEvent"
        };
        for (String path : gemPaths) {
            try {
                eventClass = Class.forName(path);
                break;
            } catch (ClassNotFoundException ignored) {}
        }
        if (eventClass == null) return;

        Method getPlayerData = findMethod(eventClass, "getPlayerData");
        if (getPlayerData == null) return;

        Method getGemStone = findMethod(eventClass, "getGemStone");
        if (getGemStone == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedGetPlayerData = getPlayerData;
        final Method resolvedGetGemStone = getGemStone;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleGemStone(plugin, resolvedEvent, resolvedGetPlayerData, resolvedGetGemStone, rawEvent),
            plugin
        );
    }

    private static void handleGemStone(FTBQuests plugin, Class<?> eventClass, Method getPlayerData,
                                        Method getGemStone, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerDataObj = getPlayerData.invoke(event);
            Method getPlayerMethod = playerDataObj.getClass().getMethod("getPlayer");
            Object playerObj = getPlayerMethod.invoke(playerDataObj);
            if (!(playerObj instanceof Player player)) return;

            Object gemStone = getGemStone.invoke(event);
            if (gemStone == null) return;

            String mmoId = extractMMOItemTypeId(gemStone);
            if (mmoId == null) return;

            final String resolvedRequired = mmoId;

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> "MMOITEMS_APPLY_GEMSTONE".equalsIgnoreCase(q.getObjectiveType()))
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

    private static void registerConsumable(FTBQuests plugin) {
        Class<?> eventClass = null;
        String[] paths = {
            "net.Indyuce.mmoitems.api.event.item.ConsumableConsumedEvent",
            "io.lumine.mmoitems.api.event.item.ConsumableConsumedEvent"
        };
        for (String path : paths) {
            try {
                eventClass = Class.forName(path);
                break;
            } catch (ClassNotFoundException ignored) {}
        }
        if (eventClass == null) return;

        Method getPlayerData = findMethod(eventClass, "getPlayerData");
        if (getPlayerData == null) return;

        Method getMMOItem = findMethod(eventClass, "getMMOItem");
        if (getMMOItem == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedGetPlayerData = getPlayerData;
        final Method resolvedGetMMOItem = getMMOItem;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleConsumable(plugin, resolvedEvent, resolvedGetPlayerData, resolvedGetMMOItem, rawEvent),
            plugin
        );
    }

    private static void handleConsumable(FTBQuests plugin, Class<?> eventClass, Method getPlayerData,
                                          Method getMMOItem, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerDataObj = getPlayerData.invoke(event);
            Method getPlayerMethod = playerDataObj.getClass().getMethod("getPlayer");
            Object playerObj = getPlayerMethod.invoke(playerDataObj);
            if (!(playerObj instanceof Player player)) return;

            Object mmoItem = getMMOItem.invoke(event);
            if (mmoItem == null) return;

            String mmoId = extractMMOItemTypeId(mmoItem);
            if (mmoId == null) return;

            final String resolvedRequired = mmoId;

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> "MMOITEMS_CONSUME".equalsIgnoreCase(q.getObjectiveType()))
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

    private static void registerUpgradeItem(FTBQuests plugin) {
        Class<?> eventClass = null;
        String[] paths = {
            "net.Indyuce.mmoitems.api.event.item.UpgradeItemEvent",
            "io.lumine.mmoitems.api.event.item.UpgradeItemEvent"
        };
        for (String path : paths) {
            try {
                eventClass = Class.forName(path);
                break;
            } catch (ClassNotFoundException ignored) {}
        }
        if (eventClass == null) return;

        Method getPlayerData = findMethod(eventClass, "getPlayerData");
        if (getPlayerData == null) return;

        Method getConsumable = findMethod(eventClass, "getConsumable");
        if (getConsumable == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedGetPlayerData = getPlayerData;
        final Method resolvedGetConsumable = getConsumable;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleUpgradeItem(plugin, resolvedEvent, resolvedGetPlayerData, resolvedGetConsumable, rawEvent),
            plugin
        );
    }

    private static void handleUpgradeItem(FTBQuests plugin, Class<?> eventClass, Method getPlayerData,
                                           Method getConsumable, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerDataObj = getPlayerData.invoke(event);
            Method getPlayerMethod = playerDataObj.getClass().getMethod("getPlayer");
            Object playerObj = getPlayerMethod.invoke(playerDataObj);
            if (!(playerObj instanceof Player player)) return;

            Object consumable = getConsumable.invoke(event);
            if (consumable == null) return;

            String mmoId = extractMMOItemTypeId(consumable);
            if (mmoId == null) return;

            final String resolvedRequired = mmoId;

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> "MMOITEMS_APPLY_UPGRADE".equalsIgnoreCase(q.getObjectiveType()))
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

    private static void registerApplySoulbound(FTBQuests plugin) {
        Class<?> eventClass = null;
        String[] paths = {
            "net.Indyuce.mmoitems.api.event.item.ApplySoulboundEvent",
            "io.lumine.mmoitems.api.event.item.ApplySoulboundEvent"
        };
        for (String path : paths) {
            try {
                eventClass = Class.forName(path);
                break;
            } catch (ClassNotFoundException ignored) {}
        }
        if (eventClass == null) return;

        Method getPlayerData = findMethod(eventClass, "getPlayerData");
        if (getPlayerData == null) return;

        Method getConsumable = findMethod(eventClass, "getConsumable");
        if (getConsumable == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedGetPlayerData = getPlayerData;
        final Method resolvedGetConsumable = getConsumable;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleApplySoulbound(plugin, resolvedEvent, resolvedGetPlayerData, resolvedGetConsumable, rawEvent),
            plugin
        );
    }

    private static void handleApplySoulbound(FTBQuests plugin, Class<?> eventClass, Method getPlayerData,
                                              Method getConsumable, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerDataObj = getPlayerData.invoke(event);
            Method getPlayerMethod = playerDataObj.getClass().getMethod("getPlayer");
            Object playerObj = getPlayerMethod.invoke(playerDataObj);
            if (!(playerObj instanceof Player player)) return;

            Object consumable = getConsumable.invoke(event);
            if (consumable == null) return;

            String mmoId = extractMMOItemTypeId(consumable);
            if (mmoId == null) return;

            final String resolvedRequired = mmoId;

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> "MMOITEMS_APPLY_SOULBOUND".equalsIgnoreCase(q.getObjectiveType()))
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

    private static void registerRepairItem(FTBQuests plugin) {
        Class<?> eventClass = null;
        String[] paths = {
            "net.Indyuce.mmoitems.api.event.item.RepairItemEvent",
            "io.lumine.mmoitems.api.event.item.RepairItemEvent"
        };
        for (String path : paths) {
            try {
                eventClass = Class.forName(path);
                break;
            } catch (ClassNotFoundException ignored) {}
        }
        if (eventClass == null) return;

        Method getPlayerData = findMethod(eventClass, "getPlayerData");
        if (getPlayerData == null) return;

        Method getConsumable = findMethod(eventClass, "getConsumable");
        if (getConsumable == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedGetPlayerData = getPlayerData;
        final Method resolvedGetConsumable = getConsumable;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleRepairItem(plugin, resolvedEvent, resolvedGetPlayerData, resolvedGetConsumable, rawEvent),
            plugin
        );
    }

    private static void handleRepairItem(FTBQuests plugin, Class<?> eventClass, Method getPlayerData,
                                          Method getConsumable, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerDataObj = getPlayerData.invoke(event);
            Method getPlayerMethod = playerDataObj.getClass().getMethod("getPlayer");
            Object playerObj = getPlayerMethod.invoke(playerDataObj);
            if (!(playerObj instanceof Player player)) return;

            Object consumable = getConsumable.invoke(event);
            if (consumable == null) return;

            String mmoId = extractMMOItemTypeId(consumable);
            if (mmoId == null) return;

            final String resolvedRequired = mmoId;

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> "MMOITEMS_APPLY_REPAIR".equalsIgnoreCase(q.getObjectiveType()))
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

    private static void registerUnsocketGemStone(FTBQuests plugin) {
        Class<?> eventClass = null;
        String[] paths = {
            "net.Indyuce.mmoitems.api.event.item.UnsocketGemStoneEvent",
            "io.lumine.mmoitems.api.event.item.UnsocketGemStoneEvent"
        };
        for (String path : paths) {
            try {
                eventClass = Class.forName(path);
                break;
            } catch (ClassNotFoundException ignored) {}
        }
        if (eventClass == null) return;

        Method getPlayerData = findMethod(eventClass, "getPlayerData");
        if (getPlayerData == null) return;

        Method getConsumable = findMethod(eventClass, "getConsumable");
        if (getConsumable == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedGetPlayerData = getPlayerData;
        final Method resolvedGetConsumable = getConsumable;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleUnsocketGemStone(plugin, resolvedEvent, resolvedGetPlayerData, resolvedGetConsumable, rawEvent),
            plugin
        );
    }

    private static void handleUnsocketGemStone(FTBQuests plugin, Class<?> eventClass, Method getPlayerData,
                                                Method getConsumable, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerDataObj = getPlayerData.invoke(event);
            Method getPlayerMethod = playerDataObj.getClass().getMethod("getPlayer");
            Object playerObj = getPlayerMethod.invoke(playerDataObj);
            if (!(playerObj instanceof Player player)) return;

            Object consumable = getConsumable.invoke(event);
            if (consumable == null) return;

            String mmoId = extractMMOItemTypeId(consumable);
            if (mmoId == null) return;

            final String resolvedRequired = mmoId;

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> "MMOITEMS_UNSOCKET_GEMSTONE".equalsIgnoreCase(q.getObjectiveType()))
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

    private static String extractMMOItemTypeId(Object mmoItem) {
        try {
            Method getTypeMethod = mmoItem.getClass().getMethod("getType");
            Object itemType = getTypeMethod.invoke(mmoItem);

            String typeStr = null;
            try {
                Method getId = itemType.getClass().getMethod("getId");
                Object id = getId.invoke(itemType);
                if (id instanceof String s) typeStr = s;
            } catch (NoSuchMethodException ignored) {}
            if (typeStr == null) typeStr = itemType.toString();

            Method getIdMethod = mmoItem.getClass().getMethod("getId");
            Object idObj = getIdMethod.invoke(mmoItem);
            if (idObj instanceof String idStr) {
                return typeStr + "|" + idStr;
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static void handleTrade(FTBQuests plugin, Class<?> eventClass, Method getPlayerData,
                                     Method hasResult, Method getResult, Method getInteraction, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            // Only process instant crafts and queue claims (where a result is given)
            Object action = getInteraction.invoke(event);
            String actionName = action.toString();
            if (!"INSTANT_RECIPE".equals(actionName) && !"CRAFTING_QUEUE".equals(actionName)) return;

            if (!((Boolean) hasResult.invoke(event))) return;

            Object resultObj = getResult.invoke(event);
            if (!(resultObj instanceof ItemStack result)) return;
            if (result.getType().isAir()) return;

            // Get player from PlayerData
            Object playerDataObj = getPlayerData.invoke(event);
            Method getPlayerMethod = playerDataObj.getClass().getMethod("getPlayer");
            Object playerObj = getPlayerMethod.invoke(playerDataObj);
            if (!(playerObj instanceof Player player)) return;

            String mmoId = extractMMOItemId(result);
            if (mmoId == null) return;

            final String resolvedRequired = mmoId;

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> "MMOITEMS_STATIONS_TRADE".equalsIgnoreCase(q.getObjectiveType()))
                .filter(q -> q.getObjectiveRequired() == null
                    || q.getObjectiveRequired().isEmpty()
                    || q.getObjectiveRequired().stream().anyMatch(r -> matchesRequired(r, resolvedRequired)))
                .forEach(q -> {
                    PlayerQuestData data = plugin.getPlayerDataManager()
                        .getOrCreateQuestData(player.getUniqueId(), q.getId());
                    if (data.isCompleted()) return;
                    int newPoints = Math.min(data.getPoints() + result.getAmount(), q.getObjectiveAmount());
                    data.setPoints(newPoints);
                    if (newPoints >= q.getObjectiveAmount()) {
                        data.setCompleted(true);
                        plugin.playQuestComplete(player, q);
                    }
                });
        } catch (Exception ignored) {}
    }

    private static String extractMMOItemId(ItemStack item) {
        for (String pkg : MMO_PACKAGES) {
            try {
                Class<?> mmoClass = Class.forName(pkg + ".MMOItems");

                Method getTypeAndID = findMethod(mmoClass, "getTypeAndID");
                if (getTypeAndID != null) {
                    Object pair = getTypeAndID.invoke(null, item);
                    if (pair != null) {
                        String type = tryPairGet(pair, "getKey", "getLeft");
                        String id = tryPairGet(pair, "getValue", "getRight");
                        if (type != null && id != null) return type + "|" + id;
                    }
                }

                Method getType = findMethod(mmoClass, "getType");
                Method getID = findMethod(mmoClass, "getID");
                if (getType != null && getID != null) {
                    Object type = getType.invoke(null, item);
                    Object id = getID.invoke(null, item);
                    if (type instanceof String sType && id instanceof String sID) {
                        return sType + "|" + sID;
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static String tryPairGet(Object pair, String... methodNames) {
        for (String name : methodNames) {
            try {
                Method m = pair.getClass().getMethod(name);
                Object val = m.invoke(pair);
                if (val instanceof String s) return s;
            } catch (Exception ignored) {}
        }
        return null;
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
