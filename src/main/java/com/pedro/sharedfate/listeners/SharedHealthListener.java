package com.pedro.sharedfate.listeners;

import com.pedro.sharedfate.SharedFatePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SharedHealthListener implements Listener {

    private final SharedFatePlugin plugin;
    // Evita que el dano replicado a los demas jugadores vuelva a dispararse a si mismo
    private final Set<UUID> applyingSharedDamage = new HashSet<>();

    public SharedHealthListener(SharedFatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player damagedPlayer)) {
            return;
        }

        if (applyingSharedDamage.contains(damagedPlayer.getUniqueId())) {
            return;
        }

        double percentage = plugin.getConfig().getDouble("share-health.percentage", 100) / 100.0;
        double minFloor = plugin.getConfig().getDouble("share-health.min-health-floor", 1.0);
        double sharedDamage = event.getFinalDamage() * percentage;

        if (sharedDamage <= 0) {
            return;
        }

        for (Player teammate : plugin.getServer().getOnlinePlayers()) {
            if (teammate.getUniqueId().equals(damagedPlayer.getUniqueId())) {
                continue;
            }

            double newHealth = teammate.getHealth() - sharedDamage;
            double floor = Math.min(minFloor, teammate.getHealth());
            double clampedHealth = Math.max(newHealth, floor);

            applyingSharedDamage.add(teammate.getUniqueId());
            try {
                teammate.setHealth(Math.max(0.0, clampedHealth));
            } finally {
                applyingSharedDamage.remove(teammate.getUniqueId());
            }
        }
    }
}
