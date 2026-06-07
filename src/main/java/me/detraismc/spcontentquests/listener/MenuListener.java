package me.detraismc.spcontentquests.listener;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.menus.QuestMenu;
import me.detraismc.spcontentquests.models.Category;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import me.detraismc.spcontentquests.models.Quest;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuListener implements Listener {
    private final SpContentQuests plugin;
    private final Map<UUID, Long> lastClickTime = new HashMap<>();

    public MenuListener(SpContentQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        
        if (holder instanceof QuestMenu) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            
            long now = System.currentTimeMillis();
            Player player = (Player) event.getWhoClicked();
            UUID uuid = player.getUniqueId();
            long cooldown = plugin.getGuiClickCooldown();
            Long lastClick = lastClickTime.get(uuid);
            if (lastClick != null && (now - lastClick) < cooldown) {
                player.sendMessage(plugin.msg("gui-cooldown"));
                return;
            }
            lastClickTime.put(uuid, now);
            
            QuestMenu menu = (QuestMenu) holder;
            Category category = menu.getCategory();
            int slot = event.getRawSlot();

            // Check if it's a pagination button
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

            // Check if it's a custom item with commands
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
                            for (String cmd : itemCfg.getStringList("commands")) {
                                cmd = cmd.replace("%player%", player.getName());
                                if (cmd.equalsIgnoreCase("[close]")) {
                                    player.closeInventory();
                                } else if (cmd.startsWith("[console] ")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(10));
                                } else if (cmd.startsWith("[player] ")) {
                                    player.performCommand(cmd.substring(9));
                                }
                            }
                            return;
                        }
                    }
                }
            }

            // Check if it's a quest slot
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
                var quest = clickedQuest;
                PlayerQuestData data = menu.getQuestData().getOrDefault(quest.getId(), new PlayerQuestData(quest.getId(), 0, false, false));
                
                boolean isComplete = data.isCompleted() || data.getPoints() >= quest.getObjectiveAmount();

                if (data.isClaimed()) {
                    plugin.playSound(player, category.getId(), "no");
                    player.sendMessage(plugin.msg("already-claimed"));
                } else if (isComplete) {
                    // Claim reward
                    plugin.playSound(player, category.getId(), "claim");
                    player.sendMessage(plugin.msg("reward-claimed", "{quest}", quest.getConfig().getString("icon.display", quest.getId())));
                    if (quest.getRewardCommand() != null) {
                        for (String cmd : quest.getRewardCommand()) {
                            cmd = cmd.replace("%player%", player.getName());
                            if (cmd.equalsIgnoreCase("[close]")) {
                                player.closeInventory();
                            } else if (cmd.startsWith("[console] ")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(10));
                            } else if (cmd.startsWith("[player] ")) {
                                player.performCommand(cmd.substring(9));
                            }
                        }
                    }
                    
                    // Update cache and refresh GUI
                     PlayerQuestData cacheData = plugin.getPlayerDataManager().getOrCreateQuestData(player.getUniqueId(), quest.getId());
                    cacheData.setClaimed(true);
                    plugin.getMenuManager().openCategory(player, category, menu.getPage());
                    
                } else {
                    // Ongoing interaction (e.g., guide)
                    plugin.playSound(player, category.getId(), "click");
                    if (quest.getObjectiveCommand() != null) {
                        for (String cmd : quest.getObjectiveCommand()) {
                            cmd = cmd.replace("%player%", player.getName());
                            if (cmd.equalsIgnoreCase("[close]")) {
                                player.closeInventory();
                            } else if (cmd.startsWith("[console] ")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(10));
                            } else if (cmd.startsWith("[player] ")) {
                                player.performCommand(cmd.substring(9));
                            }
                        }
                    }
                }
            }
        }
    }
}
