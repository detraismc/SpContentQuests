package me.detraismc.spcontentquests.listener;

import me.detraismc.spcontentquests.SpContentQuests;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {
    private final SpContentQuests plugin;

    public PlayerJoinQuitListener(SpContentQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        plugin.getPlayerDataManager().loadPlayer(player.getUniqueId());

        if (plugin.isQuestBookEnabled() && plugin.isQuestBookAutoGive() && !player.hasPlayedBefore()) {
            player.getInventory().addItem(plugin.getQuestBookItem());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().savePlayer(event.getPlayer().getUniqueId(), true);
    }
}
