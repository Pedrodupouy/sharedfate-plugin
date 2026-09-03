package com.pedro.sharedfate.listeners;
import com.pedro.sharedfate.SharedFatePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
public class SharedHealthListener implements Listener {
    private final SharedFatePlugin plugin;
    public SharedHealthListener(SharedFatePlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!plugin.isHealthEnabled()) return;
        if (!(event.getEntity() instanceof Player damaged)) return;
        double damageDealt = event.getFinalDamage();
        if (damageDealt <= 0) return;
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (other.getUniqueId().equals(damaged.getUniqueId())) continue;
            if (other.isDead() || other.getHealth() <= 0) continue;
            double newHealth = Math.max(0.0, other.getHealth() - damageDealt);
            other.setHealth(newHealth);
        }
    }
}
