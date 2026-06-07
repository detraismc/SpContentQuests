package me.detraismc.spcontentquests.models;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Quest {
    private final String id;
    private final String categoryId;
    private final ConfigurationSection config;
    private final List<Objective> objectives;

    public Quest(String id, ConfigurationSection config) {
        this.id = id;
        this.config = config;
        this.categoryId = config.getString("category", "introduction");
        this.objectives = parseObjectives(config);
    }

    private List<Objective> parseObjectives(ConfigurationSection config) {
        List<Objective> result = new ArrayList<>();
        List<Map<?, ?>> rawList = config.getMapList("objective");
        if (rawList != null) {
            for (Map<?, ?> entry : rawList) {
                String type = entry.containsKey("type") ? (String) entry.get("type") : "none";
                int amount = entry.containsKey("amount") ? ((Number) entry.get("amount")).intValue() : 1;
                @SuppressWarnings("unchecked")
                List<String> required = (List<String>) entry.get("required");
                String display = entry.containsKey("display") ? (String) entry.get("display") : type + " x" + amount;
                @SuppressWarnings("unchecked")
                List<String> desc = (List<String>) entry.get("desc");
                @SuppressWarnings("unchecked")
                List<String> command = (List<String>) entry.get("command");
                result.add(new Objective(type, amount, required, display, desc, command));
            }
        }
        if (result.isEmpty()) {
            result.add(new Objective("none", 1, null, "none x1", null, null));
        }
        return result;
    }

    public String getId() {
        return id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public ConfigurationSection getConfig() {
        return config;
    }

    public List<Objective> getObjectives() {
        return objectives;
    }

    public boolean isCompleted(Map<Integer, Integer> objectiveProgress) {
        for (int i = 0; i < objectives.size(); i++) {
            int progress = objectiveProgress.getOrDefault(i, 0);
            if (progress < objectives.get(i).getAmount()) {
                return false;
            }
        }
        return true;
    }

    public int getSlot() {
        return config.getInt("slot", -1);
    }

    public int getPage() {
        return config.getInt("page", 1);
    }

    public List<String> getObjectiveCommand() {
        return config.getStringList("objective-command");
    }

    public List<String> getRewardCommand() {
        return config.getStringList("reward-command");
    }
}
