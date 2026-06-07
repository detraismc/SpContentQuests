package me.detraismc.spcontentquests.listener;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.menus.ObjectiveMenu;
import me.detraismc.spcontentquests.menus.QuestMenu;
import me.detraismc.spcontentquests.models.Category;
import me.detraismc.spcontentquests.models.Objective;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import me.detraismc.spcontentquests.models.Quest;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuListener implements Listener {
    private final SpContentQuests plugin;
    private final Map<UUID, Long> lastClickTime = new HashMap<>();
    private final Map<UUID, ObjectiveRedirect> pendingRedirects = new HashMap<>();

    private static class ObjectiveRedirect {
        final Quest quest;
        final Category category;
        final int questPage;

        ObjectiveRedirect(Quest quest, Category category, int questPage) {
            this.quest = quest;
            this.category = category;
            this.questPage = questPage;
        }
    }

    public MenuListener(SpContentQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        ObjectiveRedirect redirect = pendingRedirects.remove(player.getUniqueId());
        if (redirect != null) {
            int questPage = redirect.questPage;
            Quest quest = redirect.quest;
            Category category = redirect.category;
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                plugin.getMenuManager().openObjective(player, quest, category, questPage), 1L);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();

        if (holder instanceof ObjectiveMenu) {
            handleObjectiveMenuClick(event);
        } else if (holder instanceof QuestMenu) {
            handleQuestMenuClick(event);
        }
    }

    private void handleObjectiveMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        ObjectiveMenu menu = (ObjectiveMenu) event.getInventory().getHolder();
        Quest quest = menu.getQuest();
        Category category = menu.getCategory();
        int questPage = menu.getQuestPage();

        // Cooldown check
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long cooldown = plugin.getGuiClickCooldown();
        Long lastClick = lastClickTime.get(uuid);
        if (lastClick != null && (now - lastClick) < cooldown) {
            return;
        }
        lastClickTime.put(uuid, now);

        // Custom items
        YamlConfiguration objCfg = getObjectiveConfig();
        if (objCfg != null) {
            ConfigurationSection customItems = objCfg.getConfigurationSection("item-custom");
            if (customItems != null) {
                for (String key : customItems.getKeys(false)) {
                    ConfigurationSection itemCfg = customItems.getConfigurationSection(key);
                    if (itemCfg != null) {
                        boolean match = false;
                        if (itemCfg.contains("slots") && itemCfg.getIntegerList("slots").contains(slot)) {
                            match = true;
                        } else if (itemCfg.contains("slot") && itemCfg.getInt("slot") == slot) {
                            match = true;
                        }
                        if (match && itemCfg.contains("commands")) {
                            runCommands(player, itemCfg.getStringList("commands"), null, null, null);
                            return;
                        }
                    }
                }
            }
        }

        // Go back item
        if (objCfg != null) {
            ConfigurationSection gobackSection = objCfg.getConfigurationSection("goback-item");
            if (gobackSection != null && slot == gobackSection.getInt("slot", 8)) {
                plugin.playSound(player, category.getId(), "click");
                plugin.getMenuManager().openCategory(player, category, questPage);
                return;
            }
        }

        // Objective click
        List<Integer> targetSlots = getObjectiveSlots(objCfg, quest.getObjectives().size());
        if (targetSlots != null) {
            int objIndex = targetSlots.indexOf(slot);
            if (objIndex >= 0 && objIndex < quest.getObjectives().size()) {
                playObjectiveClickSound(player);
                Objective objective = quest.getObjectives().get(objIndex);
                List<String> commands = objective.getCommand();
                if (commands != null && !commands.isEmpty()) {
                    runCommands(player, commands, quest, category, questPage);
                }
            }
        }
    }

    private void handleQuestMenuClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long cooldown = plugin.getGuiClickCooldown();
        Long lastClick = lastClickTime.get(uuid);
        if (lastClick != null && (now - lastClick) < cooldown) {
            return;
        }
        lastClickTime.put(uuid, now);

        QuestMenu menu = (QuestMenu) event.getInventory().getHolder();
        Category category = menu.getCategory();
        int slot = event.getRawSlot();

        // Pagination
        ConfigurationSection pageItems = category.getConfig().getConfigurationSection("item-page");
        if (pageItems != null) {
            if (pageItems.contains("next") && slot == pageItems.getInt("next.slot")) {
                int maxPage;
                if (category.isQuestsAutomaticLayout()) {
                    int questsPerPage = category.getQuestsSlots().size();
                    var allQuests = plugin.getQuestManager().getQuestsInCategory(category.getId());
                    maxPage = (int) Math.ceil((double) allQuests.size() / questsPerPage);
                } else {
                    maxPage = 1;
                    for (Quest q : plugin.getQuestManager().getQuestsInCategory(category.getId())) {
                        if (q.getPage() > maxPage) maxPage = q.getPage();
                    }
                }
                if (menu.getPage() < maxPage) {
                    plugin.playSound(player, category.getId(), "click");
                    plugin.getMenuManager().openCategory(player, category, menu.getPage() + 1);
                }
                return;
            }
            if (pageItems.contains("prev") && slot == pageItems.getInt("prev.slot")) {
                if (menu.getPage() > 1) {
                    plugin.playSound(player, category.getId(), "click");
                    plugin.getMenuManager().openCategory(player, category, menu.getPage() - 1);
                }
                return;
            }
        }

        // Custom items
        ConfigurationSection customItems = category.getConfig().getConfigurationSection("item-custom");
        if (customItems != null) {
            for (String key : customItems.getKeys(false)) {
                ConfigurationSection itemCfg = customItems.getConfigurationSection(key);
                if (itemCfg != null) {
                    boolean match = false;
                    if (itemCfg.contains("slots") && itemCfg.getIntegerList("slots").contains(slot)) {
                        match = true;
                    } else if (itemCfg.contains("slot") && itemCfg.getInt("slot") == slot) {
                        match = true;
                    }
                    if (match && itemCfg.contains("commands")) {
                        plugin.playSound(player, category.getId(), "click");
                        runCommands(player, itemCfg.getStringList("commands"), null, null, null);
                        return;
                    }
                }
            }
        }

        // Quest slot
        Quest clickedQuest = null;
        if (category.isQuestsAutomaticLayout()) {
            if (category.getQuestsSlots().contains(slot)) {
                int slotIndex = category.getQuestsSlots().indexOf(slot);
                int questIndex = ((menu.getPage() - 1) * category.getQuestsSlots().size()) + slotIndex;
                var quests = plugin.getQuestManager().getQuestsInCategory(category.getId());
                if (questIndex < quests.size()) {
                    clickedQuest = new java.util.ArrayList<>(quests).get(questIndex);
                }
            }
        } else {
            for (Quest q : plugin.getQuestManager().getQuestsInCategory(category.getId())) {
                if (q.getPage() == menu.getPage() && q.getSlot() == slot) {
                    clickedQuest = q;
                    break;
                }
            }
        }

        if (clickedQuest != null) {
            Quest quest = clickedQuest;
            PlayerQuestData data = menu.getQuestData().getOrDefault(quest.getId(), new PlayerQuestData(quest.getId(), 0, false, false));
            boolean isComplete = data.isCompleted() || quest.isCompleted(data.getObjectivesProgress());

            if (data.isClaimed()) {
                plugin.playSound(player, category.getId(), "no");
                player.sendMessage(plugin.msg("already-claimed"));
            } else if (!isComplete && !quest.checkRequirements(player)) {
                plugin.playSound(player, category.getId(), "no");
                player.sendMessage(plugin.msg("quest-locked"));
            } else if (isComplete) {
                plugin.playSound(player, category.getId(), "claim");
                player.sendMessage(plugin.msg("reward-claimed", "{quest}", quest.getConfig().getString("icon.display", quest.getId())));
                if (quest.getRewardCommand() != null) {
                    runCommands(player, quest.getRewardCommand(), null, null, null);
                }
                PlayerQuestData cacheData = plugin.getPlayerDataManager().getOrCreateQuestData(player.getUniqueId(), quest.getId());
                cacheData.setClaimed(true);
                plugin.getMenuManager().openCategory(player, category, menu.getPage());
            } else {
                plugin.playSound(player, category.getId(), "click");
                plugin.getMenuManager().openObjective(player, quest, category, menu.getPage());
            }
        }
    }

    private void runCommands(Player player, List<String> commands, Quest quest, Category category, Integer questPage) {
        if (commands == null) return;
        for (String cmd : commands) {
            cmd = cmd.replace("%player%", player.getName());
            if (cmd.equalsIgnoreCase("[close]")) {
                player.closeInventory();
            } else if (cmd.startsWith("[console] ")) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(10));
            } else if (cmd.startsWith("[player] ")) {
                player.performCommand(cmd.substring(9));
            } else if (cmd.equalsIgnoreCase("[redirect]") && quest != null && category != null && questPage != null) {
                pendingRedirects.put(player.getUniqueId(), new ObjectiveRedirect(quest, category, questPage));
            }
        }
    }

    private YamlConfiguration objectiveConfig;

    private YamlConfiguration getObjectiveConfig() {
        if (objectiveConfig == null) {
            File file = new File(plugin.getDataFolder(), "gui-objective.yml");
            if (file.exists()) {
                objectiveConfig = YamlConfiguration.loadConfiguration(file);
            }
        }
        return objectiveConfig;
    }

    private void playObjectiveClickSound(Player player) {
        YamlConfiguration objCfg = getObjectiveConfig();
        if (objCfg == null) return;
        String soundId = objCfg.getString("sound.click.id");
        if (soundId == null || soundId.isEmpty()) return;
        float volume = (float) objCfg.getDouble("sound.click.volume", 1.0);
        float pitch = (float) objCfg.getDouble("sound.click.pitch", 1.0);
        try {
            player.playSound(player.getLocation(), org.bukkit.Sound.valueOf(soundId), volume, pitch);
        } catch (IllegalArgumentException ignored) {}
    }

    private List<Integer> getObjectiveSlots(YamlConfiguration objCfg, int objCount) {
        if (objCfg == null) return null;
        ConfigurationSection objSlotsCfg = objCfg.getConfigurationSection("objective-slots");
        if (objSlotsCfg == null) return null;
        String key = String.valueOf(objCount);
        if (objSlotsCfg.contains(key)) {
            return objSlotsCfg.getIntegerList(key);
        }
        return objSlotsCfg.getIntegerList("7");
    }
}
