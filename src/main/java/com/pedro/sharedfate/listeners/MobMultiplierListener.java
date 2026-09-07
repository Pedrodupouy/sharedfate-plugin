package com.pedro.sharedfate.listeners;

import com.pedro.sharedfate.SharedFatePlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

/**
 * Cuando un mob configurado aparece de forma NATURAL, se generan copias
 * extra junto a él. Las copias se crean con SpawnReason.CUSTOM
 * explícitamente, así que este mismo listener las ignora (solo reacciona
 * a NATURAL) y no hay riesgo de que se multipliquen entre sí sin control.
 */
public class MobMultiplierListener implements Listener {

    private final SharedFatePlugin plugin;

    public MobMultiplierListener(SharedFatePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onNaturalSpawn(CreatureSpawnEvent event) {
        if (!plugin.isMobMultiplierEnabled()) return;
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;

        EntityType type = event.getEntityType();
        if (!plugin.getMultipliedEntities().contains(type)) return;

        int extraCopies = plugin.getMobMultiplier() - 1;
        if (extraCopies <= 0) return;

        LivingEntity original = event.getEntity();
        Location baseLocation = original.getLocation();
        boolean isBaby = (original instanceof Zombie zombie) && zombie.isBaby();

        for (int i = 0; i < extraCopies; i++) {
            Location spawnAt = baseLocation.clone().add(
                    (Math.random() * 2 - 1), 0, (Math.random() * 2 - 1));
            Entity spawned = baseLocation.getWorld().spawnEntity(
                    spawnAt, type, CreatureSpawnEvent.SpawnReason.CUSTOM);
            if (isBaby && spawned instanceof Zombie zombieCopy) {
                zombieCopy.setBaby(true);
            }
        }
    }
}
