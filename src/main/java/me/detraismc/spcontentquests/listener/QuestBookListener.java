package me.detraismc.spcontentquests.listener;

import me.detraismc.spcontentquests.SpContentQuests;
import me.detraismc.spcontentquests.models.Category;
import me.detraismc.spcontentquests.models.SoundData;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class QuestBookListener implements Listener {
    private final SpContentQuests plugin;

    public QuestBookListener(SpContentQuests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!plugin.isQuestBookEnabled()) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        ItemStack questBook = plugin.getQuestBookItem();
        if (questBook == null) return;

        if (!item.isSimilar(questBook)) return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        String categoryId = plugin.getQuestBookOpenCategory();
        Category category = plugin.getQuestManager().getCategory(categoryId);
        if (category == null) {
            player.sendMessage(plugin.msg("category-not-found", "{category}", categoryId));
            return;
        }

        SoundData soundData = plugin.getQuestBookOpenSound();
        if (soundData != null) {
            try {
                Sound sound = Sound.valueOf(soundData.getId());
                player.playSound(player.getLocation(), sound, soundData.getVolume(), soundData.getPitch());
            } catch (IllegalArgumentException ignored) {}
        }

        plugin.getMenuManager().openCategory(player, category, 1);
    }
}
