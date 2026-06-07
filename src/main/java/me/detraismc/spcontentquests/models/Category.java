package me.detraismc.spcontentquests.models;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class Category {
    private final String id;
    private final String guiName;
    private final int guiRows;
    private final List<Integer> questsSlots;
    private final ConfigurationSection config;

    public Category(String id, ConfigurationSection config) {
        this.id = id;
        this.config = config;
        this.guiName = config.getString("gui-name", "Category");
        this.guiRows = config.getInt("gui-rows", 6);
        this.questsSlots = config.getIntegerList("quests-slots");
    }

    public String getId() {
        return id;
    }

    public String getGuiName() {
        return guiName;
    }

    public int getGuiRows() {
        return guiRows;
    }

    public List<Integer> getQuestsSlots() {
        return questsSlots;
    }

    public ConfigurationSection getConfig() {
        return config;
    }

    public boolean isQuestsAutomaticLayout() {
        return config.getBoolean("quests-automatic-layout", true);
    }
}
