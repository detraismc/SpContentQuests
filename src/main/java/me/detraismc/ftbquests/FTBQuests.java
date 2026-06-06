package me.detraismc.ftbquests;

import me.detraismc.ftbquests.commands.FTBQuestsCommand;
import me.detraismc.ftbquests.commands.QuestBookCommand;
import me.detraismc.ftbquests.database.DatabaseManager;
import me.detraismc.ftbquests.database.MySQLManager;
import me.detraismc.ftbquests.database.SQLiteManager;
import me.detraismc.ftbquests.listener.MenuListener;
import me.detraismc.ftbquests.listener.ObjectiveListener;
import me.detraismc.ftbquests.listener.PlayerJoinQuitListener;
import me.detraismc.ftbquests.listener.QuestBookListener;
import me.detraismc.ftbquests.managers.MenuManager;
import me.detraismc.ftbquests.managers.PlayerDataManager;
import me.detraismc.ftbquests.managers.QuestManager;
import me.detraismc.ftbquests.utils.GitHubUpdateChecker;

import me.detraismc.ftbquests.models.Category;
import me.detraismc.ftbquests.models.Quest;
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

public class FTBQuests extends JavaPlugin {
   private static FTBQuests instance;
   private DatabaseManager databaseManager;
   private QuestManager questManager;
   private MenuManager menuManager;
   private PlayerDataManager playerDataManager;

   public void onEnable() {
      instance = this;

       saveDefaultConfig();

       if (getConfig().getBoolean("update-checker", true)) {
           new GitHubUpdateChecker(this, "detraismc/FTBQuests").check();
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

       // Save default resources
       saveResourceIfNotExists("category/introduction.yml");
       saveResourceIfNotExists("category/overworld.yml");
       saveResourceIfNotExists("quests/example-quests.yml");

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
            me.detraismc.ftbquests.integration.MythicMobsHook.register(this);
        }

        // Soft-depend: Slimefun
        if (Bukkit.getPluginManager().getPlugin("Slimefun") != null) {
            me.detraismc.ftbquests.integration.SlimefunHook.register(this);
        }

        // Soft-depend: MMOItems
        if (Bukkit.getPluginManager().getPlugin("MMOItems") != null) {
            me.detraismc.ftbquests.integration.MMOItemsHook.register(this);
        }

        // Soft-depend: ItemsAdder
        if (Bukkit.getPluginManager().getPlugin("ItemsAdder") != null) {
            me.detraismc.ftbquests.integration.ItemsAdderHook.register(this);
        }

       // Auto-save task (config: auto-save-interval in minutes)
       int interval = getConfig().getInt("auto-save-interval", 5);
       Bukkit.getScheduler().runTaskTimer(this, () -> playerDataManager.saveAllAsync(), interval * 1200L, interval * 1200L);

        // Register Commands
        getCommand("ftbquests").setExecutor(new FTBQuestsCommand(this));
        getCommand("ftbquests").setTabCompleter(new FTBQuestsCommand(this));
        getCommand("questbook").setExecutor(new QuestBookCommand(this));

   }

   private void saveResourceIfNotExists(String path) {
       java.io.File file = new java.io.File(getDataFolder(), path);
       if (!file.exists()) {
           saveResource(path, false);
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

   public static FTBQuests getInstance() {
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
        prefix = prefix.replace("&", "§");
        String message = getConfig().getString("messages." + key, "&cMissing message: " + key);
        message = message.replace("&", "§");
        for (int i = 0; i < replacements.length - 1; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return prefix + message;
    }

    public void playSound(Player player, String categoryId, String soundKey) {
        Category category = questManager.getCategory(categoryId);
        if (category == null) return;

        ConfigurationSection soundSection = category.getConfig().getConfigurationSection("sound");
        if (soundSection == null || !soundSection.contains(soundKey)) return;

        try {
            Sound sound = Sound.valueOf(soundSection.getString(soundKey));
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
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

    public String getQuestBookOpenSound() {
        return getConfig().getString("quest-book.open-sound", "");
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
