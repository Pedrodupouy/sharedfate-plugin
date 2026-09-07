package com.pedro.sharedfate.listeners;

import com.pedro.sharedfate.SharedFatePlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

/**
 * Cuando un jugador mata a un animal/mob configurado como "compartido",
 * cada uno de los demás jugadores en línea recibe una COPIA de lo que
 * soltó ese animal. El jugador que lo mató se queda con su drop normal
 * (vanilla, sin tocar). Al ser copias en inventarios separados, gastar
 * esos ítems solo afecta a quien los gasta.
 */
public class SharedHuntingListener implements Listener {

    private final SharedFatePlugin plugin;

    public SharedHuntingListener(SharedFatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin.isHuntingEnabled()) return;

        LivingEntity dead = event.getEntity();
        EntityType type = dead.getType();
        if (!plugin.getSharedEntities().contains(type)) return;

        Player killer = dead.getKiller();
        if (killer == null) return;

        List<ItemStack> drops = event.getDrops();
        if (drops.isEmpty()) return;

        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (other.getUniqueId().equals(killer.getUniqueId())) continue;

            for (ItemStack drop : drops) {
                ItemStack copy = drop.clone();
                HashMap<Integer, ItemStack> leftover = other.getInventory().addItem(copy);
                for (ItemStack extra : leftover.values()) {
                    other.getWorld().dropItemNaturally(other.getLocation(), extra);
                }
            }

            if (plugin.isHuntingBroadcast()) {
                other.sendMessage("§b" + killer.getName() + " §7cazó " + formatDrops(drops)
                        + " §7- te tocó una copia.");
            }
        }
    }

    private String formatDrops(List<ItemStack> drops) {
        StringBuilder sb = new StringBuilder();
        for (ItemStack item : drops) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(item.getAmount()).append("x ").append(item.getType());
        }
        return sb.toString();
    }
}
