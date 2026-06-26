package me.detraismc.spcontentquests.commands;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Category;
import me.detraismc.spcontentquests.models.PlayerQuestData;
import me.detraismc.spcontentquests.models.Quest;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpContentQuestsCommand implements CommandExecutor, TabCompleter {
    private final SpContentQuests plugin;

    public SpContentQuestsCommand(SpContentQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (!sender.hasPermission("SpContentQuests.admin")) {
            return true;
        }

        if (args.length == 0) {
            return handleHelp(sender);
        }

        switch (args[0].toLowerCase()) {
            case "help":
                return handleHelp(sender);
            case "reload":
                return handleReload(sender);
            case "objective":
                return handleObjective(sender, args);
            case "quest":
                return handleQuest(sender, args);
            case "open":
                return handleOpen(sender, args);
            case "resetall":
                return handleResetAll(sender, args);
            case "resetcategory":
                return handleResetCategory(sender, args);
            default:
                sender.sendMessage(plugin.msg("usage"));
                return true;
        }
    }

    private boolean handleHelp(CommandSender sender) {
        sender.sendMessage(plugin.msg("help-header"));
        sender.sendMessage(plugin.msg("help-command"));
        sender.sendMessage(plugin.msg("help-reload"));
        if (sender.hasPermission("SpContentQuests.admin")) {
            sender.sendMessage(plugin.msg("help-objective-add"));
            sender.sendMessage(plugin.msg("help-objective-subtract"));
            sender.sendMessage(plugin.msg("help-objective-set"));
            sender.sendMessage(plugin.msg("help-quest-reset"));
            sender.sendMessage(plugin.msg("help-quest-completed"));
            sender.sendMessage(plugin.msg("help-quest-claimed"));
            sender.sendMessage(plugin.msg("help-resetall"));
            sender.sendMessage(plugin.msg("help-resetcategory"));
        }
        sender.sendMessage(plugin.msg("help-open"));
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("SpContentQuests.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }
        plugin.reloadConfig();
        plugin.getQuestManager().loadAll();
        plugin.getMenuManager().reloadObjectiveConfig();
        sender.sendMessage(plugin.msg("config-reloaded"));
        return true;
    }

    private boolean handleObjective(CommandSender sender, String[] args) {
        if (!sender.hasPermission("SpContentQuests.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length < 5) {
            sender.sendMessage(plugin.msg("usage-objective"));
            return true;
        }

        String action = args[1].toLowerCase();
        if (!action.equals("add") && !action.equals("subtract") && !action.equals("set")) {
            sender.sendMessage(plugin.msg("usage-objective"));
            return true;
        }

        String questId = args[2];
        Quest quest = plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            sender.sendMessage(plugin.msg("quest-not-found"));
            return true;
        }

        int objectiveId;
        try {
            objectiveId = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.msg("invalid-amount"));
            return true;
        }

        if (objectiveId < 1 || objectiveId > quest.getObjectives().size()) {
            sender.sendMessage(plugin.msg("objective-not-found"));
            return true;
        }

        int index = objectiveId - 1;

        Player target = Bukkit.getPlayer(args[4]);
        if (target == null) {
            sender.sendMessage(plugin.msg("player-not-found"));
            return true;
        }

        int amount;
        try {
            amount = args.length >= 6 ? Integer.parseInt(args[5]) : 1;
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.msg("invalid-amount"));
            return true;
        }

        if (amount < 0) {
            sender.sendMessage(plugin.msg("invalid-amount"));
            return true;
        }

        PlayerQuestData data = plugin.getPlayerDataManager().getOrCreateQuestData(target.getUniqueId(), questId);

        switch (action) {
            case "add": {
                if (!data.isCompleted()) {
                    int current = data.getObjectiveProgress(index);
                    int max = quest.getObjectives().get(index).getAmount();
                    int newPoints = Math.min(current + amount, max);
                    data.setObjectiveProgress(index, newPoints);
                    if (quest.isCompleted(data.getObjectivesProgress())) {
                        data.setCompleted(true);
                        plugin.playQuestComplete(target, quest);
                    }
                }
                sender.sendMessage(plugin.msg("points-added", "{amount}", String.valueOf(amount),
                        "{player}", target.getName(), "{quest}", questId));
                break;
            }
            case "subtract": {
                int current = data.getObjectiveProgress(index);
                int newPoints = Math.max(current - amount, 0);
                data.setObjectiveProgress(index, newPoints);
                if (data.isCompleted() && !quest.isCompleted(data.getObjectivesProgress())) {
                    data.setCompleted(false);
                }
                sender.sendMessage(plugin.msg("points-subtracted", "{amount}", String.valueOf(amount),
                        "{player}", target.getName(), "{quest}", questId));
                break;
            }
            case "set": {
                int max = quest.getObjectives().get(index).getAmount();
                if (amount > max) {
                    amount = max;
                }
                data.setObjectiveProgress(index, amount);
                if (quest.isCompleted(data.getObjectivesProgress())) {
                    data.setCompleted(true);
                    plugin.playQuestComplete(target, quest);
                } else {
                    data.setCompleted(false);
                }
                sender.sendMessage(plugin.msg("points-set", "{amount}", String.valueOf(amount),
                        "{player}", target.getName(), "{quest}", questId));
                break;
            }
        }

        return true;
    }

    private boolean handleQuest(CommandSender sender, String[] args) {
        if (!sender.hasPermission("SpContentQuests.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(plugin.msg("usage-quest"));
            return true;
        }

        String action = args[1].toLowerCase();
        if (!action.equals("reset") && !action.equals("completed") && !action.equals("claimed")) {
            sender.sendMessage(plugin.msg("usage-quest"));
            return true;
        }

        String questId = args[2];
        Quest quest = plugin.getQuestManager().getQuest(questId);
        if (quest == null) {
            sender.sendMessage(plugin.msg("quest-not-found"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[3]);
        if (target == null) {
            sender.sendMessage(plugin.msg("player-not-found"));
            return true;
        }

        PlayerQuestData data = plugin.getPlayerDataManager().getOrCreateQuestData(target.getUniqueId(), questId);

        switch (action) {
            case "reset": {
                data.setPoints(0);
                data.setCompleted(false);
                data.setClaimed(false);
                sender.sendMessage(plugin.msg("quest-reset", "{player}", target.getName(), "{quest}", questId));
                break;
            }
            case "completed": {
                for (int i = 0; i < quest.getObjectives().size(); i++) {
                    int max = quest.getObjectives().get(i).getAmount();
                    if (data.getObjectiveProgress(i) < max) {
                        data.setObjectiveProgress(i, max);
                    }
                }
                data.setCompleted(true);
                sender.sendMessage(plugin.msg("quest-set-completed", "{player}", target.getName(), "{quest}", questId));
                break;
            }
            case "claimed": {
                data.setClaimed(true);
                sender.sendMessage(plugin.msg("quest-set-claimed", "{player}", target.getName(), "{quest}", questId));
                break;
            }
        }

        return true;
    }

    private boolean handleOpen(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(plugin.msg("usage-open"));
            return true;
        }

        String categoryId = args[1];
        Category category = plugin.getQuestManager().getCategory(categoryId);
        if (category == null) {
            sender.sendMessage(plugin.msg("category-not-found", "{category}", categoryId));
            return true;
        }

        Player target = null;
        if (args.length >= 3) {
            target = Bukkit.getPlayer(args[2]);
        } else if (sender instanceof Player) {
            target = (Player) sender;
        }

        if (target == null) {
            sender.sendMessage(plugin.msg("player-not-found"));
            return true;
        }

        plugin.getMenuManager().openCategory(target, category, 1);
        return true;
    }

    private boolean handleResetAll(CommandSender sender, String[] args) {
        if (!sender.hasPermission("SpContentQuests.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(plugin.msg("usage-resetall"));
            return true;
        }

        String target = args[1];

        if (target.equals("*") || target.equalsIgnoreCase("all")) {
            if (sender instanceof Player) {
                sender.sendMessage(plugin.msg("console-only"));
                return true;
            }
            plugin.getPlayerDataManager().purgeAll();
            sender.sendMessage(plugin.msg("resetall-all"));
            return true;
        }

        Player online = Bukkit.getPlayer(target);
        if (online != null) {
            plugin.getPlayerDataManager().purgePlayer(online.getUniqueId());
            sender.sendMessage(plugin.msg("resetall-player", "{player}", online.getName()));
            return true;
        }

        UUID uuid = Bukkit.getOfflinePlayer(target).getUniqueId();
        plugin.getPlayerDataManager().purgePlayer(uuid);
        sender.sendMessage(plugin.msg("resetall-player", "{player}", target));
        return true;
    }

    private boolean handleResetCategory(CommandSender sender, String[] args) {
        if (!sender.hasPermission("SpContentQuests.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(plugin.msg("usage-resetcategory"));
            return true;
        }

        String categoryId = args[1];
        Category category = plugin.getQuestManager().getCategory(categoryId);
        if (category == null) {
            sender.sendMessage(plugin.msg("category-not-found", "{category}", categoryId));
            return true;
        }

        List<String> questIds = plugin.getQuestManager().getAllQuests().stream()
            .filter(q -> q.getCategoryId().equals(categoryId))
            .map(Quest::getId)
            .collect(Collectors.toList());

        if (questIds.isEmpty()) {
            sender.sendMessage(plugin.msg("category-no-quests", "{category}", categoryId));
            return true;
        }

        String target = args[2];

        if (target.equals("*") || target.equalsIgnoreCase("all")) {
            if (sender instanceof Player) {
                sender.sendMessage(plugin.msg("console-only"));
                return true;
            }
            plugin.getPlayerDataManager().resetQuestsAll(questIds);
            sender.sendMessage(plugin.msg("resetcategory-all", "{category}", categoryId));
            return true;
        }

        Player online = Bukkit.getPlayer(target);
        if (online != null) {
            plugin.getPlayerDataManager().resetQuests(online.getUniqueId(), questIds);
            sender.sendMessage(plugin.msg("resetcategory-player", "{category}", categoryId, "{player}", online.getName()));
            return true;
        }

        UUID uuid = Bukkit.getOfflinePlayer(target).getUniqueId();
        plugin.getPlayerDataManager().resetQuests(uuid, questIds);
        sender.sendMessage(plugin.msg("resetcategory-player", "{category}", categoryId, "{player}", target));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("SpContentQuests.admin")) {
            return new ArrayList<>();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            completions.add("open");
            completions.add("reload");
            completions.add("objective");
            completions.add("quest");
            completions.add("resetall");
            completions.add("resetcategory");
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("resetall")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
                completions.add("all");
            } else if (sub.equals("resetcategory")) {
                completions.addAll(plugin.getQuestManager().getAllCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toList()));
            } else if (sub.equals("objective")) {
                completions.add("add");
                completions.add("subtract");
                completions.add("set");
            } else if (sub.equals("quest")) {
                completions.add("reset");
                completions.add("completed");
                completions.add("claimed");
            } else if (sub.equals("open")) {
                completions.addAll(plugin.getQuestManager().getAllCategories().stream()
                        .map(Category::getId)
                        .collect(Collectors.toList()));
            }
        } else if (args.length == 3) {
            String sub = args[0].toLowerCase();
            if (sub.equals("objective") || sub.equals("quest")) {
                completions.addAll(plugin.getQuestManager().getAllQuests().stream()
                        .map(Quest::getId)
                        .collect(Collectors.toList()));
            } else if (sub.equals("resetcategory")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
                completions.add("all");
            } else if (sub.equals("open")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 4) {
            String sub = args[0].toLowerCase();
            if (sub.equals("objective")) {
                Quest quest = plugin.getQuestManager().getQuest(args[2]);
                if (quest != null) {
                    for (int i = 1; i <= quest.getObjectives().size(); i++) {
                        completions.add(String.valueOf(i));
                    }
                }
            } else if (sub.equals("quest")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 5) {
            String sub = args[0].toLowerCase();
            if (sub.equals("objective")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 6) {
            String sub = args[0].toLowerCase();
            if (sub.equals("objective")) {
                completions.add("1");
            }
        }

        return completions;
    }
}
