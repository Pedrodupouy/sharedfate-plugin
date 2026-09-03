package com.pedro.sharedfate.listeners;

import com.pedro.sharedfate.SharedFatePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class SharedMiningListener implements Listener {

    private final SharedFatePlugin plugin;

    public SharedMiningListener(SharedFatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        int total = plugin.incrementAndGetSharedBlocksMined();
        int interval = plugin.getConfig().getInt("shared-mining.milestone-interval", 100);

        if (interval > 0 && total % interval == 0) {
            String message = ChatColor.GOLD + "[SharedFate] " + ChatColor.YELLOW
                    + "El equipo ha minado " + total + " bloques en total!";
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                player.sendMessage(message);
            }
        }
    }
}
