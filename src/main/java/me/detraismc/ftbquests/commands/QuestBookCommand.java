package me.detraismc.ftbquests.commands;

import me.detraismc.ftbquests.FTBQuests;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QuestBookCommand implements CommandExecutor {
    private final FTBQuests plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public QuestBookCommand(FTBQuests plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!plugin.isQuestBookEnabled()) {
            sender.sendMessage(plugin.msg("quest-book-disabled"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg("player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("ftbquests.questbook")) {
            player.sendMessage(plugin.msg("no-permission"));
            return true;
        }
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        int cooldownSeconds = plugin.getQuestBookCooldown();

        if (cooldowns.containsKey(uuid)) {
            long elapsed = (now - cooldowns.get(uuid)) / 1000;
            if (elapsed < cooldownSeconds) {
                player.sendMessage(plugin.msg("quest-book-cooldown", "{time}", String.valueOf(cooldownSeconds - elapsed)));
                return true;
            }
        }

        cooldowns.put(uuid, now);
        player.getInventory().addItem(plugin.getQuestBookItem());
        player.sendMessage(plugin.msg("quest-book-given"));
        return true;
    }
}
