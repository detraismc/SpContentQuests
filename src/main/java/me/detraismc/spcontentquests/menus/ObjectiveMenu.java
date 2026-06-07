package me.detraismc.spcontentquests.menus;

import me.detraismc.spcontentquests.models.Category;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import me.detraismc.spcontentquests.models.Quest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ObjectiveMenu implements InventoryHolder {
    private final Quest quest;
    private final Category category;
    private final int questPage;
    private final Map<String, PlayerQuestData> questData;
    private Inventory inventory;

    public ObjectiveMenu(Quest quest, Category category, int questPage, Map<String, PlayerQuestData> questData) {
        this.quest = quest;
        this.category = category;
        this.questPage = questPage;
        this.questData = questData;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Quest getQuest() {
        return quest;
    }

    public Category getCategory() {
        return category;
    }

    public int getQuestPage() {
        return questPage;
    }

    public Map<String, PlayerQuestData> getQuestData() {
        return questData;
    }
}
