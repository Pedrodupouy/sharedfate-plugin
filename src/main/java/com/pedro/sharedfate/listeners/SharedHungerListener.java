package com.pedro.sharedfate.listeners;
import com.pedro.sharedfate.SharedFatePlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
public class SharedHungerListener implements Listener {
    private final SharedFatePlugin plugin;
    private volatile boolean syncing = false;
    public SharedHungerListener(SharedFatePlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!plugin.isHungerEnabled()) return;
        if (syncing) return;
        if (!(event.getEntity() instanceof Player changed)) return;
        int newFoodLevel = event.getFoodLevel();
        float newSaturation = changed.getSaturation();
        syncing = true;
        try {
            for (Player other : plugin.getServer().getOnlinePlayers()) {
                if (other.getUniqueId().equals(changed.getUniqueId())) continue;
                other.setFoodLevel(newFoodLevel);
                other.setSaturation(newSaturation);
            }
        } finally {
            syncing = false;
        }
    }
}
