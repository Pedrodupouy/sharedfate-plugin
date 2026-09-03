package com.pedro.sharedfate.listeners;

import com.pedro.sharedfate.SharedFatePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SharedHungerListener implements Listener {

    private final SharedFatePlugin plugin;
    private final Set<UUID> applyingSharedHunger = new HashSet<>();

    public SharedHungerListener(SharedFatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player changedPlayer)) {
            return;
        }

        if (applyingSharedHunger.contains(changedPlayer.getUniqueId())) {
            return;
        }

        double percentage = plugin.getConfig().getDouble("share-hunger.percentage", 100) / 100.0;
        int delta = event.getFoodLevel() - changedPlayer.getFoodLevel();
        int sharedDelta = (int) Math.round(delta * percentage);

        if (sharedDelta == 0) {
            return;
        }

        for (Player teammate : plugin.getServer().getOnlinePlayers()) {
            if (teammate.getUniqueId().equals(changedPlayer.getUniqueId())) {
                continue;
            }

            int newFoodLevel = teammate.getFoodLevel() + sharedDelta;
            newFoodLevel = Math.max(0, Math.min(20, newFoodLevel));

            applyingSharedHunger.add(teammate.getUniqueId());
            try {
                teammate.setFoodLevel(newFoodLevel);
            } finally {
                applyingSharedHunger.remove(teammate.getUniqueId());
            }
        }
    }
}
