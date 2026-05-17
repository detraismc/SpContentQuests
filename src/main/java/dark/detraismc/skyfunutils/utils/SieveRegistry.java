package dark.detraismc.skyfunutils.utils;

import dark.detraismc.skyfunutils.SkyfunItems;
import dark.detraismc.skyfunutils.SkyfunUtils;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SieveRegistry {

    public static class SiftReward {
        private final ItemStack itemStack;
        private final int weight;
        private final String displayName;

        public SiftReward(ItemStack itemStack, int weight, String displayName) {
            this.itemStack = itemStack;
            this.weight = weight;
            this.displayName = displayName;
        }

        public ItemStack getItemStack() {
            return itemStack.clone();
        }

        public int getWeight() {
            return weight;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private static final Map<Material, List<SiftReward>> lootTables = new HashMap<>();
    private static final Map<Material, Integer> totalWeights = new HashMap<>();

    public static void load(SkyfunUtils plugin) {
        lootTables.clear();
        totalWeights.clear();

        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("sifting");

        if (section == null) return;

        for (String key : section.getKeys(false)) {
            Material inputMat;
            try {
                inputMat = Material.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid sifting material type in config: " + key);
                continue;
            }

            ConfigurationSection matSection = section.getConfigurationSection(key);
            if (matSection == null) continue;

            List<SiftReward> rewards = new ArrayList<>();
            int totalWeight = 0;

            for (String itemKey : matSection.getKeys(false)) {
                int weight = matSection.getInt(itemKey);
                ItemStack itemStack = parseItem(itemKey);
                String name = getFriendlyName(itemKey);

                if (itemStack != null && weight > 0) {
                    rewards.add(new SiftReward(itemStack, weight, name));
                    totalWeight += weight;
                }
            }

            if (!rewards.isEmpty()) {
                lootTables.put(inputMat, rewards);
                totalWeights.put(inputMat, totalWeight);
            }
        }
    }

    private static ItemStack parseItem(String key) {
        if (key.equalsIgnoreCase("SIFTING_RESIDUE")) {
            return new CustomItemStack(Material.SAND, "&7Sifting Residue", "&8Discarded fine stone dust.");
        }
        if (key.equalsIgnoreCase("SIFTED_ORE")) {
            return SlimefunItems.SIFTED_ORE.clone();
        }
        if (key.equalsIgnoreCase("SKYFUN_GRASS_SEEDS")) {
            return SkyfunItems.SKYFUN_GRASS_SEEDS.clone();
        }
        try {
            return new ItemStack(Material.valueOf(key.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String getFriendlyName(String key) {
        if (key.equalsIgnoreCase("SIFTING_RESIDUE")) return "&7Sifting Residue";
        if (key.equalsIgnoreCase("SIFTED_ORE")) return "&eSifted Ore";
        if (key.equalsIgnoreCase("SKYFUN_GRASS_SEEDS")) return "&aGrass Seeds";

        String[] split = key.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String s : split) {
            sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return "&f" + sb.toString().trim();
    }

    public static List<SiftReward> getRewards(Material material) {
        return lootTables.getOrDefault(material, Collections.emptyList());
    }

    public static int getTotalWeight(Material material) {
        return totalWeights.getOrDefault(material, 0);
    }

    public static ItemStack rollDrop(Material material) {
        int index = rollIndex(material);
        List<SiftReward> rewards = lootTables.get(material);
        if (index >= 0 && rewards != null) {
            return rewards.get(index).getItemStack();
        }
        return null;
    }

    public static int rollIndex(Material material) {
        List<SiftReward> rewards = lootTables.get(material);
        Integer totalWeight = totalWeights.get(material);

        if (rewards == null || totalWeight == null || totalWeight <= 0) return -1;

        int roll = ThreadLocalRandom.current().nextInt(totalWeight);
        int count = 0;

        for (int i = 0; i < rewards.size(); i++) {
            count += rewards.get(i).getWeight();
            if (roll < count) {
                return i;
            }
        }
        return -1;
    }
}
