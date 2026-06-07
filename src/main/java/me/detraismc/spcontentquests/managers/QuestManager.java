package me.detraismc.spcontentquests.managers;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Category;
import me.detraismc.spcontentquests.models.Quest;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestManager {
    private final SpContentQuests plugin;
    private final Map<String, Category> categories = new HashMap<>();
    private final Map<String, Quest> quests = new HashMap<>();

    public QuestManager(SpContentQuests plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        categories.clear();
        quests.clear();

        loadCategories();
        loadQuests();
        
        plugin.getLogger().info("Loaded " + categories.size() + " categories and " + quests.size() + " quests.");
    }

    private void loadCategories() {
        File folder = new File(plugin.getDataFolder(), "category");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            String id = file.getName().replace(".yml", "");
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            categories.put(id, new Category(id, config));
        }
    }

    private void loadQuests() {
        File folder = new File(plugin.getDataFolder(), "quests");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (String key : config.getKeys(false)) {
                quests.put(key, new Quest(key, config.getConfigurationSection(key)));
            }
        }
    }

    public Category getCategory(String id) {
        return categories.get(id);
    }

    public Quest getQuest(String id) {
        return quests.get(id);
    }

    public Collection<Quest> getAllQuests() {
        return quests.values();
    }

    public Collection<Category> getAllCategories() {
        return categories.values();
    }

    public Collection<Quest> getQuestsInCategory(String categoryId) {
        return quests.values().stream()
                .filter(q -> q.getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }
}
