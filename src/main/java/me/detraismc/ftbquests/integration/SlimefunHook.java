package me.detraismc.ftbquests.integration;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class SlimefunHook {

    private static final String PKG = "io.github.thebusybiscuit.slimefun4.api.events";

    public static void register(FTBQuests plugin) {
        Class<?> eventClass;
        try {
            eventClass = Class.forName(PKG + ".MultiBlockCraftEvent");
        } catch (ClassNotFoundException e) {
            return;
        }

        Method getPlayer = findMethod(eventClass, "getPlayer");
        if (getPlayer == null) return;

        Method getOutput = findReturningMethod(eventClass, ItemStack.class, "getOutput", "getItem", "getResult");
        if (getOutput == null) return;

        Method getMachine = findMethod(eventClass, "getMachine", "getSlimefunItem");

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedPlayer = getPlayer;
        final Method resolvedOutput = getOutput;
        final Method resolvedMachine = getMachine;

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleCraft(plugin, resolvedEvent, resolvedPlayer, resolvedOutput, resolvedMachine, rawEvent),
            plugin
        );

        registerAncientAltar(plugin);
        plugin.getLogger().info("Slimefun integration enabled!");
    }

    private static void handleCraft(FTBQuests plugin, Class<?> eventClass, Method getPlayer,
                                     Method getOutput, Method getMachine, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerObj = getPlayer.invoke(event);
            if (!(playerObj instanceof Player player)) return;

            String machineId = extractMachineId(event, getMachine);
            if (machineId == null) return;

            String objectiveType = "SLIMEFUN_MULTIBLOCK_" + machineId;

            Object itemObj = getOutput.invoke(event);
            ItemStack result;
            if (itemObj instanceof ItemStack is && !is.getType().isAir()) {
                result = is;
            } else if (itemObj instanceof ItemStack[] arr && arr.length > 0 && !arr[0].getType().isAir()) {
                result = arr[0];
            } else {
                return;
            }

            String slimefunId = extractSlimefunId(result);
            final String resolvedType = objectiveType;
            final String resolvedRequired = slimefunId != null ? slimefunId : result.getType().name();

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> resolvedType.equalsIgnoreCase(q.getObjectiveType()))
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

    private static void registerAncientAltar(FTBQuests plugin) {
        Class<?> eventClass;
        try {
            eventClass = Class.forName(PKG + ".AncientAltarCraftEvent");
        } catch (ClassNotFoundException e) {
            return;
        }

        Method getPlayer = findMethod(eventClass, "getPlayer");
        if (getPlayer == null) return;

        Method getOutput = findReturningMethod(eventClass, ItemStack.class, "getOutput", "getItem", "getResult");
        if (getOutput == null) return;

        final Class<?> resolvedEvent = eventClass;
        final Method resolvedPlayer = getPlayer;
        final Method resolvedOutput = getOutput;
        final String objectiveType = "SLIMEFUN_ANCIENT_ALTAR";

        Listener listener = new Listener() {};
        plugin.getServer().getPluginManager().registerEvent(
            (Class) resolvedEvent, listener, EventPriority.NORMAL,
            (l, rawEvent) -> handleAncientAltar(plugin, resolvedEvent, resolvedPlayer, resolvedOutput, objectiveType, rawEvent),
            plugin
        );
    }

    private static void handleAncientAltar(FTBQuests plugin, Class<?> eventClass, Method getPlayer,
                                            Method getOutput, String objectiveType, Object rawEvent) {
        try {
            if (!eventClass.isInstance(rawEvent)) return;
            Object event = eventClass.cast(rawEvent);

            Object playerObj = getPlayer.invoke(event);
            if (!(playerObj instanceof Player player)) return;

            Object itemObj = getOutput.invoke(event);
            if (!(itemObj instanceof ItemStack result)) return;
            if (result.getType().isAir()) return;

            String slimefunId = extractSlimefunId(result);
            final String resolvedRequired = slimefunId != null ? slimefunId : result.getType().name();

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> objectiveType.equalsIgnoreCase(q.getObjectiveType()))
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

    private static String extractMachineId(Object event, Method getMachine) throws Exception {
        if (getMachine == null) return null;
        Object machine = getMachine.invoke(event);
        if (machine == null) return null;
        try {
            Method getId = machine.getClass().getMethod("getId");
            Object id = getId.invoke(machine);
            if (id instanceof String s) return s;
        } catch (NoSuchMethodException ignored) {}
        return null;
    }

    private static String extractSlimefunId(ItemStack item) {
        try {
            Class<?> slimefunItemClass = Class.forName("io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem");
            Method getByItem = slimefunItemClass.getMethod("getByItem", ItemStack.class);
            Object sfItem = getByItem.invoke(null, item);
            if (sfItem == null) return null;
            Method getId = sfItem.getClass().getMethod("getId");
            Object id = getId.invoke(sfItem);
            return id instanceof String s ? s : null;
        } catch (Exception ignored) {
            return null;
        }
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

    private static Method findReturningMethod(Class<?> clazz, Class<?> returnType, String... names) {
        for (String name : names) {
            try {
                Method m = clazz.getMethod(name);
                if (m.getReturnType() == returnType || m.getReturnType().isArray() && m.getReturnType().getComponentType() == returnType) {
                    return m;
                }
            } catch (NoSuchMethodException ignored) {}
        }
        return null;
    }
}
