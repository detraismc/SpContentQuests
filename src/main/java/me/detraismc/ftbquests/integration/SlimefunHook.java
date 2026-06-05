package me.detraismc.ftbquests.integration;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.models.PlayerQuestData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SlimefunHook {

    private static final String PKG = "io.github.thebusybiscuit.slimefun4.api.events";
    private static final Map<String, String> MACHINE_MAP = new HashMap<>();

    static {
        MACHINE_MAP.put("ENHANCED_CRAFTING_TABLE", "SLIMEFUN_CRAFT_ENHANCED_WORKBENCH");
        MACHINE_MAP.put("GRIND_STONE", "SLIMEFUN_CRAFT_GRIND_STONE");
        MACHINE_MAP.put("ORE_CRUSHER", "SLIMEFUN_CRAFT_ORE_CRUSHER");
        MACHINE_MAP.put("COMPRESSOR", "SLIMEFUN_CRAFT_COMPRESSOR");
        MACHINE_MAP.put("SMELTERY", "SLIMEFUN_CRAFT_SMELTERY");
        MACHINE_MAP.put("PRESSURE_CHAMBER", "SLIMEFUN_CRAFT_PRESSURE_CHAMBER");
        MACHINE_MAP.put("MAGIC_WORKBENCH", "SLIMEFUN_CRAFT_MAGIC_WORKBENCH");
        MACHINE_MAP.put("ORE_WASHER", "SLIMEFUN_USE_ORE_WASHER");
        MACHINE_MAP.put("TABLE_SAW", "SLIMEFUN_USE_TABLE_SAW");
        MACHINE_MAP.put("COMPOSTER", "SLIMEFUN_USE_COMPOSTER");
        MACHINE_MAP.put("GOLD_PAN", "SLIMEFUN_USE_PANNING");
        MACHINE_MAP.put("AUTOMATED_PANNING_MACHINE", "SLIMEFUN_USE_PANNING");
        MACHINE_MAP.put("CRUCIBLE", "SLIMEFUN_USE_CRUCIBLE");
        MACHINE_MAP.put("JUICER", "SLIMEFUN_USE_JUICER");
    }

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

            String objectiveType = MACHINE_MAP.get(machineId);
            if (objectiveType == null) return;

            Object itemObj = getOutput.invoke(event);
            ItemStack result;
            if (itemObj instanceof ItemStack is && !is.getType().isAir()) {
                result = is;
            } else if (itemObj instanceof ItemStack[] arr && arr.length > 0 && !arr[0].getType().isAir()) {
                result = arr[0];
            } else {
                return;
            }

            final String resolvedType = objectiveType;

            plugin.getQuestManager().getAllQuests().stream()
                .filter(q -> resolvedType.equalsIgnoreCase(q.getObjectiveType()))
                .filter(q -> q.getObjectiveRequired() == null
                    || q.getObjectiveRequired().isEmpty()
                    || q.getObjectiveRequired().stream().anyMatch(r -> r.equalsIgnoreCase(result.getType().name())))
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
