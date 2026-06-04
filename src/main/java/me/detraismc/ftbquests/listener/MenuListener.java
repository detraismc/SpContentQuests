package me.detraismc.ftbquests.listener;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.menus.QuestMenu;
import me.detraismc.ftbquests.models.Category;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {
    private final FTBQuests plugin;

    public MenuListener(FTBQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        
        if (holder instanceof QuestMenu) {
            event.setCancelled(true);
            
            if (event.getCurrentItem() == null) return;
            
            QuestMenu menu = (QuestMenu) holder;
            Category category = menu.getCategory();
            Player player = (Player) event.getWhoClicked();
            int slot = event.getRawSlot();

            // Check if it's a pagination button
            ConfigurationSection pageItems = category.getConfig().getConfigurationSection("item-page");
            if (pageItems != null) {
                if (pageItems.contains("next") && slot == pageItems.getInt("next.slot")) {
                    plugin.getMenuManager().openCategory(player, category, menu.getPage() + 1);
                    return;
                }
                if (pageItems.contains("prev") && slot == pageItems.getInt("prev.slot")) {
                    if (menu.getPage() > 1) {
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
                            for (String cmd : itemCfg.getStringList("commands")) {
                                cmd = cmd.replace("%player%", player.getName());
                                if (cmd.startsWith("[console] ")) {
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
            if (category.getQuestsSlots().contains(slot)) {
                // Determine which quest was clicked based on page and slot index
                int slotIndex = category.getQuestsSlots().indexOf(slot);
                int questIndex = ((menu.getPage() - 1) * category.getQuestsSlots().size()) + slotIndex;
                
                var quests = plugin.getQuestManager().getQuestsInCategory(category.getId());
                if (questIndex < quests.size()) {
                    var quest = new java.util.ArrayList<>(quests).get(questIndex);
                    me.detraismc.ftbquests.models.PlayerQuestData data = menu.getQuestData().getOrDefault(quest.getId(), new me.detraismc.ftbquests.models.PlayerQuestData(quest.getId(), 0, false, false));
                    
                    boolean isComplete = data.isCompleted() || data.getPoints() >= quest.getObjectiveAmount();

                    if (data.isClaimed()) {
                        player.sendMessage("§cYou have already claimed this reward!");
                    } else if (isComplete) {
                        // Claim reward
                        player.sendMessage("§aClaimed reward for " + quest.getId() + "!");
                        if (quest.getRewardCommand() != null) {
                            for (String cmd : quest.getRewardCommand()) {
                                cmd = cmd.replace("%player%", player.getName());
                                if (cmd.startsWith("[console] ")) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.substring(10));
                                } else if (cmd.startsWith("[player] ")) {
                                    player.performCommand(cmd.substring(9));
                                }
                            }
                        }
                        
                        // Update cache and refresh GUI
                        me.detraismc.ftbquests.models.PlayerQuestData cacheData = plugin.getPlayerDataManager().getOrCreateQuestData(player.getUniqueId(), quest.getId());
                        cacheData.setClaimed(true);
                        plugin.getMenuManager().openCategory(player, category, menu.getPage());
                        
                    } else {
                        // Ongoing interaction (e.g., guide)
                        if (quest.getObjectiveCommand() != null) {
                            for (String cmd : quest.getObjectiveCommand()) {
                                cmd = cmd.replace("%player%", player.getName());
                                if (cmd.startsWith("[console] ")) {
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
}
