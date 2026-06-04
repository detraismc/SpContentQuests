package me.detraismc.ftbquests.commands;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.models.Category;
import me.detraismc.ftbquests.models.PlayerQuestData;
import me.detraismc.ftbquests.models.Quest;
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
import java.util.stream.Collectors;

public class FTBQuestsCommand implements CommandExecutor, TabCompleter {
    private final FTBQuests plugin;

    public FTBQuestsCommand(FTBQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.msg("usage"));
            return true;
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
            default:
                sender.sendMessage(plugin.msg("usage"));
                return true;
        }
    }

    private boolean handleHelp(CommandSender sender) {
        sender.sendMessage(plugin.msg("help-header"));
        sender.sendMessage("§e/ftbquests help §7- §fShow this help");
        sender.sendMessage("§e/ftbquests reload §7- §fReload config and quests");
        if (sender.hasPermission("ftbquests.admin")) {
            sender.sendMessage("§e/ftbquests objective add <quest> <player> <amount> §7- §fAdd progress");
            sender.sendMessage("§e/ftbquests objective subtract <quest> <player> <amount> §7- §fSubtract progress");
            sender.sendMessage("§e/ftbquests objective set <quest> <player> <amount> §7- §fSet progress");
            sender.sendMessage("§e/ftbquests quest reset <quest> <player> §7- §fReset quest progress");
            sender.sendMessage("§e/ftbquests quest completed <quest> <player> §7- §fMark quest completed");
            sender.sendMessage("§e/ftbquests quest claimed <quest> <player> §7- §fMark quest claimed");
        }
        sender.sendMessage("§e/ftbquests open <category> [player] §7- §fOpen quest category");
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("ftbquests.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }
        plugin.reloadConfig();
        plugin.getQuestManager().loadAll();
        sender.sendMessage(plugin.msg("config-reloaded"));
        return true;
    }

    private boolean handleObjective(CommandSender sender, String[] args) {
        if (!sender.hasPermission("ftbquests.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(plugin.msg("usage"));
            return true;
        }

        String action = args[1].toLowerCase();
        if (!action.equals("add") && !action.equals("subtract") && !action.equals("set")) {
            sender.sendMessage(plugin.msg("usage"));
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

        int amount;
        try {
            amount = args.length >= 5 ? Integer.parseInt(args[4]) : 1;
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
                    int newPoints = Math.min(data.getPoints() + amount, quest.getObjectiveAmount());
                    data.setPoints(newPoints);
                    if (newPoints >= quest.getObjectiveAmount()) {
                        data.setCompleted(true);
                        target.sendMessage(plugin.msg("quest-completed", "{quest}",
                                quest.getConfig().getString("icon.display", quest.getId()).replace("&", "§")));
                    }
                }
                sender.sendMessage(plugin.msg("points-added", "{amount}", String.valueOf(amount),
                        "{player}", target.getName(), "{quest}", questId));
                break;
            }
            case "subtract": {
                int newPoints = Math.max(data.getPoints() - amount, 0);
                data.setPoints(newPoints);
                if (data.isCompleted() && newPoints < quest.getObjectiveAmount()) {
                    data.setCompleted(false);
                }
                sender.sendMessage(plugin.msg("points-subtracted", "{amount}", String.valueOf(amount),
                        "{player}", target.getName(), "{quest}", questId));
                break;
            }
            case "set": {
                if (amount > quest.getObjectiveAmount()) {
                    amount = quest.getObjectiveAmount();
                }
                data.setPoints(amount);
                if (amount >= quest.getObjectiveAmount()) {
                    data.setCompleted(true);
                    target.sendMessage(plugin.msg("quest-completed", "{quest}",
                            quest.getConfig().getString("icon.display", quest.getId()).replace("&", "§")));
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
        if (!sender.hasPermission("ftbquests.admin")) {
            sender.sendMessage(plugin.msg("no-permission"));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(plugin.msg("usage"));
            return true;
        }

        String action = args[1].toLowerCase();
        if (!action.equals("reset") && !action.equals("completed") && !action.equals("claimed")) {
            sender.sendMessage(plugin.msg("usage"));
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
                data.setCompleted(true);
                if (data.getPoints() < quest.getObjectiveAmount()) {
                    data.setPoints(quest.getObjectiveAmount());
                }
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
            sender.sendMessage(plugin.msg("usage"));
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("help");
            completions.add("open");
            if (sender.hasPermission("ftbquests.admin")) {
                completions.add("reload");
                completions.add("objective");
                completions.add("quest");
            }
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("objective")) {
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
            } else if (sub.equals("open")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 4) {
            String sub = args[0].toLowerCase();
            if (sub.equals("objective") || sub.equals("quest")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        } else if (args.length == 5) {
            String sub = args[0].toLowerCase();
            if (sub.equals("objective")) {
                completions.add("1");
            }
        }

        return completions;
    }
}
