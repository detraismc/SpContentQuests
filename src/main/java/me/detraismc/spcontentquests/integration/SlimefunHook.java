package me.detraismc.spcontentquests.integration;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Objective;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

public class SlimefunHook {

    private static final String PKG = "io.github.thebusybiscuit.slimefun4.api.events";

    public static void register(SpContentQuests plugin) {
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

    private static void handleCraft(SpContentQuests plugin, Class<?> eventClass, Method getPlayer,
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

            plugin.getQuestManager().getAllQuests().forEach(q -> {
                    PlayerQuestData data = plugin.getPlayerDataManager()
                        .getOrCreateQuestData(player.getUniqueId(), q.getId());
                    if (data.isCompleted()) return;
                    java.util.List<Objective> objectives = q.getObjectives();
                    boolean progressed = false;
                    for (int i = 0; i < objectives.size(); i++) {
                        Objective obj = objectives.get(i);
                        if (!resolvedType.equalsIgnoreCase(obj.getType())) continue;
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
        } catch (Exception ignored) {}
    }

    private static void registerAncientAltar(SpContentQuests plugin) {
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

    private static void handleAncientAltar(SpContentQuests plugin, Class<?> eventClass, Method getPlayer,
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

            plugin.getQuestManager().getAllQuests().forEach(q -> {
                    PlayerQuestData data = plugin.getPlayerDataManager()
                        .getOrCreateQuestData(player.getUniqueId(), q.getId());
                    if (data.isCompleted()) return;
                    java.util.List<Objective> objectives = q.getObjectives();
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
