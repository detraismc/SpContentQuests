package me.detraismc.spcontentquests.commands;

import me.detraismc.spcontentquests.SpContentQuests;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestBookCommand implements CommandExecutor, TabCompleter {
    private final SpContentQuests plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public QuestBookCommand(SpContentQuests plugin) {
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

        if (!player.hasPermission("SpContentQuests.questbook")) {
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

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
            @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("SpContentQuests.questbook")) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }
}
