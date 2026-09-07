package com.pedro.sharedfate.listeners;

import com.pedro.sharedfate.SharedFatePlugin;
import org.bukkit.entity.Piglin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

/**
 * Cuando un piglin completa un trueque (bartering), la API de Bukkit no
 * indica qué jugador lanzó el lingote de oro -- el resultado del trueque
 * simplemente se suelta en el mundo junto al piglin. Como aproximación
 * (razonable en un servidor de 3 jugadores), asumimos que el jugador
 * en línea más cercano al piglin fue quien inició el trueque, y le
 * damos una copia del resultado a los demás jugadores en línea.
 */
public class SharedBarterListener implements Listener {

    private static final double NEARBY_RADIUS = 8.0;

    private final SharedFatePlugin plugin;

    public SharedBarterListener(SharedFatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBarter(PiglinBarterEvent event) {
        if (!plugin.isBarteringEnabled()) return;

        Piglin piglin = event.getEntity();
        Player nearest = findNearestPlayer(piglin);
        if (nearest == null) return;

        List<ItemStack> outcome = event.getOutcome();
        if (outcome.isEmpty()) return;

        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (other.getUniqueId().equals(nearest.getUniqueId())) continue;

            for (ItemStack item : outcome) {
                ItemStack copy = item.clone();
                HashMap<Integer, ItemStack> leftover = other.getInventory().addItem(copy);
                for (ItemStack extra : leftover.values()) {
                    other.getWorld().dropItemNaturally(other.getLocation(), extra);
                }
            }

            if (plugin.isBarteringBroadcast()) {
                other.sendMessage("§b" + nearest.getName() + " §7hizo trueque con un piglin"
                        + " §7- te tocó una copia.");
            }
        }
    }

    private Player findNearestPlayer(Piglin piglin) {
        Player nearest = null;
        double closestDistanceSquared = NEARBY_RADIUS * NEARBY_RADIUS;
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!player.getWorld().equals(piglin.getWorld())) continue;
            double distanceSquared = player.getLocation().distanceSquared(piglin.getLocation());
            if (distanceSquared <= closestDistanceSquared) {
                closestDistanceSquared = distanceSquared;
                nearest = player;
            }
        }
        return nearest;
    }
}
