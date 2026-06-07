package me.detraismc.spcontentquests.managers;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.menus.ObjectiveMenu;
import me.detraismc.spcontentquests.menus.QuestMenu;
import me.detraismc.spcontentquests.models.Category;
import me.detraismc.spcontentquests.models.Objective;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import me.detraismc.spcontentquests.models.Quest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

import java.io.File;
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

        List<Quest> quests = new ArrayList<>(plugin.getQuestManager().getQuestsInCategory(category.getId()));

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

        String ongoingFormat = category.getConfig().getString("quests-item.objective-ongoing",
                "&8- &f<objective-display> &7(<objective-value>/<objective-max-value>) &4X");
        String completeFormat = category.getConfig().getString("quests-item.objective-complete",
                "&8- &f<objective-display> &7(<objective-value>/<objective-max-value>) &2\u2713");

        if (autoLayout) {
            List<Integer> questSlots = category.getQuestsSlots();
            int questsPerPage = questSlots.size();
            int startIndex = (page - 1) * questsPerPage;

            for (int i = 0; i < questsPerPage; i++) {
                int questIndex = startIndex + i;
                if (questIndex >= quests.size()) break;

                Quest quest = quests.get(questIndex);
                PlayerQuestData data = questData.getOrDefault(quest.getId(), new PlayerQuestData(quest.getId()));
                String state = data.isClaimed() ? "claimed" : (data.isCompleted() || quest.isCompleted(data.getObjectivesProgress()) ? "complete" : "ongoing");

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
                                } else if (line.contains("<objective>")) {
                                    for (int oi = 0; oi < quest.getObjectives().size(); oi++) {
                                        Objective obj = quest.getObjectives().get(oi);
                                        boolean objComplete = data.getObjectiveProgress(oi) >= obj.getAmount();
                                        String fmt = objComplete ? completeFormat : ongoingFormat;
                                        fmt = fmt.replace("<objective-display>", obj.getDisplay());
                                        fmt = fmt.replace("<objective-value>", String.valueOf(data.getObjectiveProgress(oi)));
                                        fmt = fmt.replace("<objective-max-value>", String.valueOf(obj.getAmount()));
                                        lore.add(format(fmt));
                                    }
                                } else {
                                    int totalMax = quest.getObjectives().stream().mapToInt(Objective::getAmount).sum();
                                    line = line.replace("<objective-value>", String.valueOf(data.getPoints()));
                                    line = line.replace("<objective-max-value>", String.valueOf(totalMax));
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

                PlayerQuestData data = questData.getOrDefault(quest.getId(), new PlayerQuestData(quest.getId()));
                String state = data.isClaimed() ? "claimed" : (data.isCompleted() || quest.isCompleted(data.getObjectivesProgress()) ? "complete" : "ongoing");

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
                                } else if (line.contains("<objective>")) {
                                    for (int oi = 0; oi < quest.getObjectives().size(); oi++) {
                                        Objective obj = quest.getObjectives().get(oi);
                                        boolean objComplete = data.getObjectiveProgress(oi) >= obj.getAmount();
                                        String fmt = objComplete ? completeFormat : ongoingFormat;
                                        fmt = fmt.replace("<objective-display>", obj.getDisplay());
                                        fmt = fmt.replace("<objective-value>", String.valueOf(data.getObjectiveProgress(oi)));
                                        fmt = fmt.replace("<objective-max-value>", String.valueOf(obj.getAmount()));
                                        lore.add(format(fmt));
                                    }
                                } else {
                                    int totalMax = quest.getObjectives().stream().mapToInt(Objective::getAmount).sum();
                                    line = line.replace("<objective-value>", String.valueOf(data.getPoints()));
                                    line = line.replace("<objective-max-value>", String.valueOf(totalMax));
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

    public void reloadObjectiveConfig() {
        objectiveConfig = null;
    }

    public void openObjective(Player player, Quest quest, Category category, int questPage) {
        YamlConfiguration objCfg = getObjectiveConfig();
        if (objCfg == null) return;

        Map<String, PlayerQuestData> questData = plugin.getPlayerDataManager().getQuestData(player.getUniqueId());
        PlayerQuestData data = questData.getOrDefault(quest.getId(), new PlayerQuestData(quest.getId()));

        String guiName = objCfg.getString("gui-name", "Objective").replace("{quest}", quest.getConfig().getString("icon.display", quest.getId()));
        int rows = objCfg.getInt("gui-rows", 4);
        int size = Math.min(Math.max(rows, 1), 6) * 9;

        ObjectiveMenu holder = new ObjectiveMenu(quest, category, questPage, questData);
        Inventory inv = Bukkit.createInventory(holder, size, format(guiName));
        holder.setInventory(inv);

        // Custom items
        ConfigurationSection customItems = objCfg.getConfigurationSection("item-custom");
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

        // Go back item
        ConfigurationSection gobackSection = objCfg.getConfigurationSection("goback-item");
        if (gobackSection != null) {
            int gobackSlot = gobackSection.getInt("slot", 8);
            if (gobackSlot >= 0 && gobackSlot < inv.getSize()) {
                inv.setItem(gobackSlot, buildItem(gobackSection));
            }
        }

        // Objective slots config
        ConfigurationSection objSlotsCfg = objCfg.getConfigurationSection("objective-slots");
        List<Integer> targetSlots = null;
        int objCount = quest.getObjectives().size();
        if (objSlotsCfg != null) {
            String key = String.valueOf(objCount);
            if (objSlotsCfg.contains(key)) {
                targetSlots = objSlotsCfg.getIntegerList(key);
            } else if (objSlotsCfg.contains("7")) {
                targetSlots = objSlotsCfg.getIntegerList("7");
            }
        }
        if (targetSlots == null) {
            targetSlots = new ArrayList<>();
            int start = (size / 2) - objCount / 2;
            for (int i = 0; i < objCount; i++) {
                targetSlots.add(start + i);
            }
        }

        // Place objective items
        for (int i = 0; i < quest.getObjectives().size() && i < targetSlots.size(); i++) {
            Objective obj = quest.getObjectives().get(i);
            int slot = targetSlots.get(i);
            if (slot < 0 || slot >= inv.getSize()) continue;

            boolean isComplete = data.getObjectiveProgress(i) >= obj.getAmount();
            String stateKey = isComplete ? "complete" : "ongoing";
            ConfigurationSection itemSection = objCfg.getConfigurationSection("objective-item." + stateKey);
            if (itemSection == null) continue;

            ItemStack icon = buildItem(itemSection);
            ItemMeta meta = icon.getItemMeta();
            if (meta != null) {
                String displayName = itemSection.getString("name", "<objective-display>");
                displayName = displayName.replace("<objective-display>", obj.getDisplay());
                meta.displayName(format(displayName));

                List<Component> lore = new ArrayList<>();
                if (itemSection.contains("lore")) {
                    for (String line : itemSection.getStringList("lore")) {
                        line = line.replace("<objective-display>", obj.getDisplay());
                        line = line.replace("<objective-value>", String.valueOf(data.getObjectiveProgress(i)));
                        line = line.replace("<objective-max-value>", String.valueOf(obj.getAmount()));
                        if (line.contains("<objective-desc>") && obj.getDesc() != null) {
                            for (String descLine : obj.getDesc()) {
                                lore.add(format(descLine));
                            }
                        } else {
                            lore.add(format(line));
                        }
                    }
                }
                meta.lore(lore);
                icon.setItemMeta(meta);
            }
            inv.setItem(slot, icon);
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
