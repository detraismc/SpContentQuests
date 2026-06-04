package me.detraismc.ftbquests.commands;

import me.detraismc.ftbquests.FTBQuests;
import me.detraismc.ftbquests.models.Category;
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

public class FTBQuestsCommand implements CommandExecutor, TabCompleter {
    private final FTBQuests plugin;

    public FTBQuestsCommand(FTBQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label,
            @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§cUsage: /ftbquests open <category> [player]");
            return true;
        }

        if (args[0].equalsIgnoreCase("open")) {
            if (args.length < 2) {
                sender.sendMessage("§cUsage: /ftbquests open <category> [player]");
                return true;
            }

            String categoryId = args[1];
            Category category = plugin.getQuestManager().getCategory(categoryId);
            if (category == null) {
                sender.sendMessage("§cCategory '" + categoryId + "' not found.");
                return true;
            }

            Player target = null;
            if (args.length >= 3) {
                target = Bukkit.getPlayer(args[2]);
            } else if (sender instanceof Player) {
                target = (Player) sender;
            }

            if (target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }

            plugin.getMenuManager().openCategory(target, category, 1);
            return true;
        }

        if (args[0].equalsIgnoreCase("objective") && args.length >= 4) {
            // /ftbquests objective add <quest> [player] <amount>
            if (!sender.hasPermission("ftbquests.admin")) {
                sender.sendMessage("§cNo permission.");
                return true;
            }

            if (args[1].equalsIgnoreCase("add")) {
                String questId = args[2];
                Player target = Bukkit.getPlayer(args[3]);
                int amount;
                try {
                    amount = Integer.parseInt(args.length >= 5 ? args[4] : "1");
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cInvalid amount.");
                    return true;
                }

                if (target == null) {
                    sender.sendMessage("§cPlayer not found.");
                    return true;
                }

                me.detraismc.ftbquests.models.Quest quest = plugin.getQuestManager().getQuest(questId);
                if (quest == null) {
                    sender.sendMessage("§cQuest not found.");
                    return true;
                }

                me.detraismc.ftbquests.models.PlayerQuestData data = plugin.getPlayerDataManager().getOrCreateQuestData(target.getUniqueId(), questId);
                
                if (!data.isCompleted()) {
                    int newPoints = Math.min(data.getPoints() + amount, quest.getObjectiveAmount());
                    data.setPoints(newPoints);
                    
                    if (newPoints >= quest.getObjectiveAmount()) {
                        data.setCompleted(true);
                        target.sendMessage("§aQuest Completed: §e" + quest.getConfig().getString("icon.display", quest.getId()).replace("&", "§"));
                    }
                }

                sender.sendMessage("§aAdded " + amount + " points to " + target.getName() + " for quest " + questId);
                return true;
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("open");
            if (sender.hasPermission("ftbquests.admin")) {
                completions.add("objective");
            }
        }
        return completions;
    }
}
