package me.detraismc.ftbquests.models;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class Quest {
    private final String id;
    private final String categoryId;
    private final ConfigurationSection config;

    public Quest(String id, ConfigurationSection config) {
        this.id = id;
        this.config = config;
        this.categoryId = config.getString("category", "introduction");
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

    public int getObjectiveAmount() {
        return config.getInt("objective.amount", 1);
    }

    public String getObjectiveType() {
        return config.getString("objective.type", "none");
    }

    public List<String> getObjectiveRequired() {
        return config.getStringList("objective.required");
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
