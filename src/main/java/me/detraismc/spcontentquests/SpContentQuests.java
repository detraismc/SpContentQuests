package me.detraismc.spcontentquests;

import me.detraismc.spcontentquests.commands.SpContentQuestsCommand;
import me.detraismc.spcontentquests.commands.QuestBookCommand;
import me.detraismc.spcontentquests.database.DatabaseManager;
import me.detraismc.spcontentquests.database.MySQLManager;
import me.detraismc.spcontentquests.database.SQLiteManager;
import me.detraismc.spcontentquests.listener.MenuListener;
import me.detraismc.spcontentquests.listener.ObjectiveListener;
import me.detraismc.spcontentquests.listener.PlayerJoinQuitListener;
import me.detraismc.spcontentquests.listener.QuestBookListener;
import me.detraismc.spcontentquests.managers.MenuManager;
import me.detraismc.spcontentquests.managers.PlayerDataManager;
import me.detraismc.spcontentquests.managers.QuestManager;
import me.detraismc.spcontentquests.utils.GitHubUpdateChecker;

import me.detraismc.spcontentquests.models.Category;
import me.detraismc.spcontentquests.models.Quest;
import me.detraismc.spcontentquests.models.SoundData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SpContentQuests extends JavaPlugin {
   private static SpContentQuests instance;
   private DatabaseManager databaseManager;
   private QuestManager questManager;
   private MenuManager menuManager;
   private PlayerDataManager playerDataManager;

   public void onEnable() {
      instance = this;

       saveDefaultConfig();

       if (getConfig().getBoolean("update-checker", true)) {
           new GitHubUpdateChecker(this, "detraismc/SpContentQuests").check();
       }

       boolean useMySQL = getConfig().getBoolean("mysql-settings.enabled", false);
       if (useMySQL) {
           databaseManager = new MySQLManager(this);
       } else {
           databaseManager = new SQLiteManager(this);
       }

       try {
           databaseManager.connect();
           getLogger().info("Successfully connected to the database!");
       } catch (SQLException e) {
           getLogger().severe("Failed to connect to the database! " + e.getMessage());
           getServer().getPluginManager().disablePlugin(this);
           return;
       }

        // Save default resources (only if folder is empty/missing)
        saveDefaultFolder("category", "introduction.yml", "overworld.yml");
        saveDefaultFolder("quests", "example-quests.yml");

       // Initialize Managers
       questManager = new QuestManager(this);
       menuManager = new MenuManager(this);
       playerDataManager = new PlayerDataManager(this);
       
       questManager.loadAll();

        // Register Listeners
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ObjectiveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new QuestBookListener(this), this);

        // Soft-depend: MythicMobs
        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            me.detraismc.spcontentquests.integration.MythicMobsHook.register(this);
        }

        // Soft-depend: Slimefun
        if (Bukkit.getPluginManager().getPlugin("Slimefun") != null) {
            me.detraismc.spcontentquests.integration.SlimefunHook.register(this);
        }

        // Soft-depend: MMOItems
        if (Bukkit.getPluginManager().getPlugin("MMOItems") != null) {
            me.detraismc.spcontentquests.integration.MMOItemsHook.register(this);
        }

        // Soft-depend: ItemsAdder
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            me.detraismc.spcontentquests.integration.ItemsAdderHook.register(this);
        }

       // Auto-save task (config: auto-save-interval in minutes)
       int interval = getConfig().getInt("auto-save-interval", 5);
       Bukkit.getScheduler().runTaskTimer(this, () -> playerDataManager.saveAllAsync(), interval * 1200L, interval * 1200L);

        // Register Commands
        getCommand("spcontentquests").setExecutor(new SpContentQuestsCommand(this));
        getCommand("spcontentquests").setTabCompleter(new SpContentQuestsCommand(this));
        QuestBookCommand questBookCommand = new QuestBookCommand(this);
        getCommand("questbook").setExecutor(questBookCommand);
        getCommand("questbook").setTabCompleter(questBookCommand);

   }

    private void saveDefaultFolder(String folder, String... defaults) {
        java.io.File dir = new java.io.File(getDataFolder(), folder);
        if (dir.exists()) {
            java.io.File[] files = dir.listFiles((f, name) -> name.endsWith(".yml"));
            if (files != null && files.length > 0) return;
        }
        for (String name : defaults) {
            saveResource(folder + "/" + name, false);
        }
    }

   public void onDisable() {
       if (playerDataManager != null) {
           playerDataManager.saveAllSync();
       }
       if (databaseManager != null) {
           databaseManager.disconnect();
           getLogger().info("Disconnected from the database.");
       }
   }

   //public String getBugTrackerURL() {
   //   return null;
   //}

   public JavaPlugin getJavaPlugin() {
      return this;
   }

   public static SpContentQuests getInstance() {
      return instance;
   }

   public DatabaseManager getDatabaseManager() {
       return databaseManager;
   }

   public QuestManager getQuestManager() {
       return questManager;
   }

   public MenuManager getMenuManager() {
       return menuManager;
   }

   public PlayerDataManager getPlayerDataManager() {
       return playerDataManager;
   }

    public String msg(String key, String... replacements) {
        String prefix = getConfig().getString("messages.prefix", "");
        prefix = hexToLegacy(prefix.replace("&", "§"));
        String message = getConfig().getString("messages." + key, "&cMissing message: " + key);
        message = hexToLegacy(message.replace("&", "§"));
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return prefix + message;
    }

    private String hexToLegacy(String text) {
        var pattern = java.util.regex.Pattern.compile("<#([0-9a-fA-F]{6})>");
        return pattern.matcher(text).replaceAll(mr -> {
            String hex = mr.group(1);
            StringBuilder legacy = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                legacy.append('§').append(c);
            }
            return legacy.toString();
        });
    }

    public void playSound(Player player, String categoryId, String soundKey) {
        Category category = questManager.getCategory(categoryId);
        if (category == null) return;

        ConfigurationSection soundSection = category.getConfig().getConfigurationSection("sound");
        if (soundSection == null) return;

        ConfigurationSection soundDataSection = soundSection.getConfigurationSection(soundKey);
        SoundData soundData = SoundData.fromConfig(soundDataSection);
        if (soundData == null) return;

        try {
            Sound sound = Sound.valueOf(soundData.getId());
            player.playSound(player.getLocation(), sound, soundData.getVolume(), soundData.getPitch());
        } catch (IllegalArgumentException ignored) {}
    }

    public void playQuestComplete(Player player, Quest quest) {
        Category category = questManager.getCategory(quest.getCategoryId());
        if (category == null) return;

        ConfigurationSection catConfig = category.getConfig();

        // Message
        ConfigurationSection msgSection = catConfig.getConfigurationSection("message-complete");
        if (msgSection != null && msgSection.getBoolean("enable", true)) {
            String msg = msgSection.getString("message", "&aQuest Completed: &e{quest}")
                    .replace("{quest}", quest.getConfig().getString("icon.display", quest.getId()));
            player.sendMessage(msg.replace("&", "§"));
        }

        // Sound
        playSound(player, quest.getCategoryId(), "complete");

        // Title
        ConfigurationSection titleSection = catConfig.getConfigurationSection("title-complete");
        if (titleSection != null && titleSection.getBoolean("enable", false)) {
            String questDisplay = quest.getConfig().getString("icon.display", quest.getId());
            String title = titleSection.getString("title", "%quest%").replace("%quest%", questDisplay);
            String subtitle = titleSection.getString("subtitle", "").replace("%quest%", questDisplay);
            int fadein = titleSection.getInt("fadein", 10);
            int stay = titleSection.getInt("stay", 40);
            int fadeout = titleSection.getInt("fadeout", 10);
            player.sendTitle(
                    title.replace("&", "§"),
                    subtitle.replace("&", "§"),
                    fadein, stay, fadeout
            );
        }
    }

    public boolean isQuestBookEnabled() {
        return getConfig().getBoolean("quest-book.enable", false);
    }

    public boolean isQuestBookAutoGive() {
        return getConfig().getBoolean("quest-book.auto-give-newbies", false);
    }

    public int getQuestBookCooldown() {
        return getConfig().getInt("quest-book.give-cooldown", 10);
    }

    public int getGuiClickCooldown() {
        return getConfig().getInt("gui-click-cooldown", 200);
    }

    public String getQuestBookOpenCategory() {
        return getConfig().getString("quest-book.open-category", "introduction");
    }

    public SoundData getQuestBookOpenSound() {
        ConfigurationSection section = getConfig().getConfigurationSection("quest-book.open-sound");
        return SoundData.fromConfig(section);
    }

    public ItemStack getQuestBookItem() {
        ConfigurationSection itemSection = getConfig().getConfigurationSection("quest-book.item");
        if (itemSection == null) return null;

        String matStr = itemSection.getString("material", "KNOWLEDGE_BOOK");
        Material mat = Material.matchMaterial(matStr);
        if (mat == null) mat = Material.KNOWLEDGE_BOOK;

        ItemStack item = new ItemStack(mat, itemSection.getInt("amount", 1));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (itemSection.contains("name")) {
                String name = itemSection.getString("name").replace("&", "§");
                Component nameComponent = Component.text(name);
                if (!name.contains("§o")) {
                    nameComponent = nameComponent.decoration(TextDecoration.ITALIC, false);
                }
                meta.displayName(nameComponent);
            }
            if (itemSection.contains("lore")) {
                List<Component> lore = new ArrayList<>();
                for (String line : itemSection.getStringList("lore")) {
                    String raw = line.replace("&", "§");
                    Component lineComponent = Component.text(raw);
                    if (!raw.contains("§o")) {
                        lineComponent = lineComponent.decoration(TextDecoration.ITALIC, false);
                    }
                    lore.add(lineComponent);
                }
                meta.lore(lore);
            }
            if (itemSection.contains("modeldata")) {
                meta.setCustomModelData(itemSection.getInt("modeldata"));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
