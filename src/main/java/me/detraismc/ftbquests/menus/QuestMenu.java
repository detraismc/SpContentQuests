package me.detraismc.ftbquests.menus;

import me.detraismc.ftbquests.models.Category;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class QuestMenu implements InventoryHolder {
    private final Category category;
    private final int page;
    private final Map<String, me.detraismc.ftbquests.models.PlayerQuestData> questData;
    private Inventory inventory;

    public QuestMenu(Category category, int page, Map<String, me.detraismc.ftbquests.models.PlayerQuestData> questData) {
        this.category = category;
        this.page = page;
        this.questData = questData;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public Category getCategory() {
        return category;
    }

    public int getPage() {
        return page;
    }

    public Map<String, me.detraismc.ftbquests.models.PlayerQuestData> getQuestData() {
        return questData;
    }
}
