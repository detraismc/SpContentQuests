package me.detraismc.spcontentquests.models;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.managers.QuestManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Map;

public class Requirement {
    private static Method papiSetPlaceholders;
    private static boolean papiChecked = false;

    private final String type;
    private final String quest;
    private final String permission;
    private final String dataType;
    private final String operator;
    private final String input;
    private final String output;
    private final String display;

    public Requirement(Map<String, Object> map) {
        this.type = map.containsKey("type") ? ((String) map.get("type")).toUpperCase() : "QUEST";
        this.quest = (String) map.get("quest");
        this.permission = (String) map.get("permission");
        this.dataType = map.containsKey("data-type") ? (String) map.get("data-type") : "string";
        this.operator = map.containsKey("operator") ? (String) map.get("operator") : "==";
        this.input = (String) map.get("input");
        this.output = (String) map.get("output");
        this.display = map.containsKey("display") ? (String) map.get("display") : "";
    }

    public String getType() {
        return type;
    }

    public String getDisplay(Player player) {
        if (display == null || display.isEmpty()) return "";
        String result = display;
        if (result.contains("<quest>") && quest != null) {
            Quest q = SpContentQuests.getInstance().getQuestManager().getQuest(quest);
            String questName = q != null ? q.getConfig().getString("icon.display", quest) : quest;
            result = result.replace("<quest>", questName);
        }
        if (result.contains("<permission>") && permission != null) {
            result = result.replace("<permission>", permission);
        }
        return result;
    }

    public boolean check(Player player) {
        switch (type) {
            case "QUEST":
                return checkQuest(player);
            case "PERMISSION":
                return checkPermission(player);
            case "PLACEHOLDER":
                return checkPlaceholder(player);
            default:
                return true;
        }
    }

    private boolean checkQuest(Player player) {
        if (quest == null) return true;
        QuestManager questManager = SpContentQuests.getInstance().getQuestManager();
        Quest q = questManager.getQuest(quest);
        if (q == null) return false;

        PlayerQuestData questData = SpContentQuests.getInstance()
                .getPlayerDataManager().getQuestData(player.getUniqueId())
                .get(quest);
        if (questData == null) return false;
        return questData.isCompleted() || q.isCompleted(questData.getObjectivesProgress());
    }

    private boolean checkPermission(Player player) {
        if (permission == null) return true;
        return player.hasPermission(permission);
    }

    private boolean checkPlaceholder(Player player) {
        if (input == null || output == null) return true;
        org.bukkit.plugin.Plugin papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (papi == null || !papi.isEnabled()) return false;
        try {
            if (!papiChecked) {
                Class<?> papiClass = Class.forName("me.clip.placeholderapi.PlaceholderAPI");
                papiSetPlaceholders = papiClass.getMethod("setPlaceholders", org.bukkit.entity.Player.class, String.class);
                papiChecked = true;
            }
            if (papiSetPlaceholders == null) return false;
            String value = (String) papiSetPlaceholders.invoke(null, player, input);
            return compare(value, output);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean compare(String value, String expected) {
        switch (dataType.toLowerCase()) {
            case "integer":
                try {
                    int val = Integer.parseInt(value);
                    int exp = Integer.parseInt(expected);
                    switch (operator) {
                        case "==": return val == exp;
                        case "!=": return val != exp;
                        case ">":  return val > exp;
                        case "<":  return val < exp;
                        case ">=": return val >= exp;
                        case "<=": return val <= exp;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                break;
            case "double":
                try {
                    double val = Double.parseDouble(value);
                    double exp = Double.parseDouble(expected);
                    switch (operator) {
                        case "==": return val == exp;
                        case "!=": return val != exp;
                        case ">":  return val > exp;
                        case "<":  return val < exp;
                        case ">=": return val >= exp;
                        case "<=": return val <= exp;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
                break;
            default: // string
                switch (operator) {
                    case "==": return value.equals(expected);
                    case "!=": return !value.equals(expected);
                }
                break;
        }
        return false;
    }
}
