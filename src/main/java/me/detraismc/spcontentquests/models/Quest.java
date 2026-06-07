package me.detraismc.spcontentquests.models;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Quest {
    private final String id;
    private final String categoryId;
    private final ConfigurationSection config;
    private final List<Objective> objectives;
    private final List<Requirement> requirements;

    public Quest(String id, ConfigurationSection config) {
        this.id = id;
        this.config = config;
        this.categoryId = config.getString("category", "introduction");
        this.objectives = parseObjectives(config);
        this.requirements = parseRequirements(config);
    }

    @SuppressWarnings("unchecked")
    private List<Objective> parseObjectives(ConfigurationSection config) {
        List<Objective> result = new ArrayList<>();
        List<Map<?, ?>> rawList = config.getMapList("objective");
        if (rawList != null) {
            for (Map<?, ?> entry : rawList) {
                String type = entry.containsKey("type") ? (String) entry.get("type") : "none";
                int amount = entry.containsKey("amount") ? ((Number) entry.get("amount")).intValue() : 1;
                List<String> required = (List<String>) entry.get("required");
                String display = entry.containsKey("display") ? (String) entry.get("display") : type + " x" + amount;
                List<String> desc = (List<String>) entry.get("desc");
                List<String> command = (List<String>) entry.get("command");
                Map<String, Object> icon = entry.containsKey("icon") ? (Map<String, Object>) entry.get("icon") : null;
                result.add(new Objective(type, amount, required, display, desc, command, icon));
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

    @SuppressWarnings("unchecked")
    private List<Requirement> parseRequirements(ConfigurationSection config) {
        List<Requirement> result = new ArrayList<>();
        List<Map<?, ?>> rawList = config.getMapList("requirements");
        if (rawList == null) return result;
        for (Map<?, ?> entry : rawList) {
            result.add(new Requirement((Map<String, Object>) entry));
        }
        return result;
    }

    public boolean checkRequirements(Player player) {
        if (requirements.isEmpty()) return true;
        for (Requirement req : requirements) {
            if (!req.check(player)) return false;
        }
        return true;
    }

    public List<String> getRequirementDisplays(Player player) {
        return requirements.stream()
                .map(req -> req.getDisplay(player))
                .collect(Collectors.toList());
    }

    public List<Requirement> getRequirements() {
        return requirements;
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
