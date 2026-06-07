package me.detraismc.spcontentquests.managers;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.menus.QuestMenu;
import me.detraismc.spcontentquests.models.Category;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import me.detraismc.spcontentquests.models.Quest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuManager {
    private final SpContentQuests plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    public MenuManager(SpContentQuests plugin) {
        this.plugin = plugin;
    }

    public Component format(String text) {
        if (text == null) return Component.empty();
        // Fallback for hex using MiniMessage or legacy &
        if (text.contains("<#") || text.contains("<")) {
            return miniMessage.deserialize("<!italic>" + text);
        } else {
            Component component = legacySerializer.deserialize(text);
            if (!text.contains("&o")) {
                component = component.decoration(TextDecoration.ITALIC, false);
            }
            return component;
        }
    }

    public void openCategory(Player player, Category category, int page) {
        Map<String, PlayerQuestData> questData = plugin.getPlayerDataManager().getQuestData(player.getUniqueId());

        QuestMenu holder = new QuestMenu(category, page, questData);
        Inventory inv = Bukkit.createInventory(holder, category.getGuiRows() * 9, format(category.getGuiName()));
        holder.setInventory(inv);

        // Load Custom Items
        ConfigurationSection customItems = category.getConfig().getConfigurationSection("item-custom");
        if (customItems != null) {
            for (String key : customItems.getKeys(false)) {
                ConfigurationSection itemCfg = customItems.getConfigurationSection(key);
                if (itemCfg != null) {
                    ItemStack item = buildItem(itemCfg);
                    List<Integer> slots = itemCfg.getIntegerList("slots");
                    if (slots.isEmpty() && itemCfg.contains("slot")) {
                        slots = new ArrayList<>();
                        slots.add(itemCfg.getInt("slot"));
                    }
                    for (int slot : slots) {
                        if (slot >= 0 && slot < inv.getSize()) {
                            inv.setItem(slot, item);
                        }
                    }
                }
            }
        }

        // Load Quests
        List<Quest> quests = new ArrayList<>(plugin.getQuestManager().getQuestsInCategory(category.getId()));

        // Load Pagination Items
        ConfigurationSection pageItems = category.getConfig().getConfigurationSection("item-page");
        boolean autoLayout = category.isQuestsAutomaticLayout();

        if (pageItems != null) {
            int maxPage = 1;
            if (autoLayout) {
                int questsPerPage = category.getQuestsSlots().size();
                maxPage = (int) Math.ceil((double) quests.size() / questsPerPage);
            } else {
                for (Quest q : quests) {
                    if (q.getPage() > maxPage) maxPage = q.getPage();
                }
            }

            if (page < maxPage) {
                ConfigurationSection next = pageItems.getConfigurationSection("next");
                if (next != null) inv.setItem(next.getInt("slot"), buildItem(next));
            }

            if (page > 1) {
                ConfigurationSection prev = pageItems.getConfigurationSection("prev");
                if (prev != null) inv.setItem(prev.getInt("slot"), buildItem(prev));
            }
        }

        if (autoLayout) {
            List<Integer> questSlots = category.getQuestsSlots();
            int questsPerPage = questSlots.size();
            int startIndex = (page - 1) * questsPerPage;

            for (int i = 0; i < questsPerPage; i++) {
                int questIndex = startIndex + i;
                if (questIndex >= quests.size()) break;

                Quest quest = quests.get(questIndex);
                PlayerQuestData data = questData.getOrDefault(quest.getId(), new PlayerQuestData(quest.getId(), 0, false, false));
                String state = data.isClaimed() ? "claimed" : (data.isCompleted() || data.getPoints() >= quest.getObjectiveAmount() ? "complete" : "ongoing");

                ConfigurationSection questDisplay = category.getConfig().getConfigurationSection("quests-item." + state);
                if (questDisplay != null) {
                    ConfigurationSection questIcon = quest.getConfig().getConfigurationSection("icon");
                    ItemStack icon = buildItem(questIcon != null ? questIcon : questDisplay);

                    if (questDisplay.contains("item")) {
                        icon.setType(Material.matchMaterial(questDisplay.getString("item", "STONE")));
                    }

                    ItemMeta meta = icon.getItemMeta();
                    if (meta != null) {
                        String displayName = quest.getConfig().getString("icon.display", "&cUnknown");
                        if (questDisplay.contains("name")) {
                            displayName = questDisplay.getString("name").replace("<display>", displayName);
                        }
                        meta.displayName(format(displayName));

                        List<Component> lore = new ArrayList<>();
                        if (questDisplay.contains("lore")) {
                            for (String line : questDisplay.getStringList("lore")) {
                                if (line.contains("<desc>")) {
                                    if (quest.getConfig().contains("icon.desc")) {
                                        for (String descLine : quest.getConfig().getStringList("icon.desc")) {
                                            lore.add(format(descLine));
                                        }
                                    }
                                } else {
                                    line = line.replace("<objective-value>", String.valueOf(data.getPoints()));
                                    line = line.replace("<objective-max-value>", String.valueOf(quest.getObjectiveAmount()));
                                    line = line.replace("<reward>", quest.getConfig().getStringList("reward-display").isEmpty() ? "" : String.join(", ", quest.getConfig().getStringList("reward-display")));
                                    lore.add(format(line));
                                }
                            }
                        }
                        meta.lore(lore);
                        icon.setItemMeta(meta);
                    }

                    inv.setItem(questSlots.get(i), icon);
                }
            }
        } else {
            for (Quest quest : quests) {
                if (quest.getPage() != page) continue;
                int slot = quest.getSlot();
                if (slot < 0 || slot >= inv.getSize()) continue;

                PlayerQuestData data = questData.getOrDefault(quest.getId(), new PlayerQuestData(quest.getId(), 0, false, false));
                String state = data.isClaimed() ? "claimed" : (data.isCompleted() || data.getPoints() >= quest.getObjectiveAmount() ? "complete" : "ongoing");

                ConfigurationSection questDisplay = category.getConfig().getConfigurationSection("quests-item." + state);
                if (questDisplay != null) {
                    ConfigurationSection questIcon = quest.getConfig().getConfigurationSection("icon");
                    ItemStack icon = buildItem(questIcon != null ? questIcon : questDisplay);

                    if (questDisplay.contains("item")) {
                        icon.setType(Material.matchMaterial(questDisplay.getString("item", "STONE")));
                    }

                    ItemMeta meta = icon.getItemMeta();
                    if (meta != null) {
                        String displayName = quest.getConfig().getString("icon.display", "&cUnknown");
                        if (questDisplay.contains("name")) {
                            displayName = questDisplay.getString("name").replace("<display>", displayName);
                        }
                        meta.displayName(format(displayName));

                        List<Component> lore = new ArrayList<>();
                        if (questDisplay.contains("lore")) {
                            for (String line : questDisplay.getStringList("lore")) {
                                if (line.contains("<desc>")) {
                                    if (quest.getConfig().contains("icon.desc")) {
                                        for (String descLine : quest.getConfig().getStringList("icon.desc")) {
                                            lore.add(format(descLine));
                                        }
                                    }
                                } else {
                                    line = line.replace("<objective-value>", String.valueOf(data.getPoints()));
                                    line = line.replace("<objective-max-value>", String.valueOf(quest.getObjectiveAmount()));
                                    line = line.replace("<reward>", quest.getConfig().getStringList("reward-display").isEmpty() ? "" : String.join(", ", quest.getConfig().getStringList("reward-display")));
                                    lore.add(format(line));
                                }
                            }
                        }
                        meta.lore(lore);
                        icon.setItemMeta(meta);
                    }

                    inv.setItem(slot, icon);
                }
            }
        }

        player.openInventory(inv);
    }

    private ItemStack buildItem(ConfigurationSection section) {
        String matStr = section.getString("item", "STONE");
        Material mat = Material.matchMaterial(matStr);
        if (mat == null) mat = Material.STONE;
        
        ItemStack item = new ItemStack(mat, section.getInt("amount", 1));
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            if (section.contains("name") || section.contains("display")) {
                meta.displayName(format(section.getString("name", section.getString("display", ""))));
            }
            if (section.contains("lore")) {
                List<Component> lore = new ArrayList<>();
                for (String line : section.getStringList("lore")) {
                    lore.add(format(line));
                }
                meta.lore(lore);
            }
            if (section.contains("modeldata") || section.contains("model")) {
                meta.setCustomModelData(section.getInt("modeldata", section.getInt("model", 0)));
            }
            if (section.getBoolean("enchanted", false)) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, false);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
        }
        
        return item;
    }
}
