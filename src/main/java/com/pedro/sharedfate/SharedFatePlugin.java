package com.pedro.sharedfate;

import com.pedro.sharedfate.listeners.MobMultiplierListener;
import com.pedro.sharedfate.listeners.SharedBarterListener;
import com.pedro.sharedfate.listeners.SharedHealthListener;
import com.pedro.sharedfate.listeners.SharedHungerListener;
import com.pedro.sharedfate.listeners.SharedHuntingListener;
import com.pedro.sharedfate.listeners.SharedMiningListener;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

public final class SharedFatePlugin extends JavaPlugin {

    private boolean healthEnabled;
    private boolean hungerEnabled;
    private boolean miningEnabled;
    private boolean miningBroadcast;
    private boolean huntingEnabled;
    private boolean huntingBroadcast;
    private boolean barteringEnabled;
    private boolean barteringBroadcast;
    private boolean mobMultiplierEnabled;
    private int mobMultiplier;
    private final Set<Material> sharedMaterials = EnumSet.noneOf(Material.class);
    private final Set<EntityType> sharedEntities = EnumSet.noneOf(EntityType.class);
    private final Set<EntityType> multipliedEntities = EnumSet.noneOf(EntityType.class);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();

        getServer().getPluginManager().registerEvents(new SharedHealthListener(this), this);
        getServer().getPluginManager().registerEvents(new SharedHungerListener(this), this);
        getServer().getPluginManager().registerEvents(new SharedMiningListener(this), this);
        getServer().getPluginManager().registerEvents(new SharedHuntingListener(this), this);
        getServer().getPluginManager().registerEvents(new SharedBarterListener(this), this);
        getServer().getPluginManager().registerEvents(new MobMultiplierListener(this), this);

        getLogger().info("SharedFate activo. Vida=" + healthEnabled
                + " Hambre=" + hungerEnabled
                + " Minado=" + miningEnabled
                + " Caza=" + huntingEnabled
                + " Trueque=" + barteringEnabled
                + " MultiplicadorMobs=" + mobMultiplierEnabled + "(x" + mobMultiplier + ")"
                + " (" + sharedMaterials.size() + " bloques, " + sharedEntities.size() + " animales configurados)");
    }

    public void loadSettings() {
        reloadConfig();
        healthEnabled = getConfig().getBoolean("shared-health.enabled", true);
        hungerEnabled = getConfig().getBoolean("shared-hunger.enabled", true);
        miningEnabled = getConfig().getBoolean("shared-mining.enabled", true);
        miningBroadcast = getConfig().getBoolean("shared-mining.broadcast", true);
        huntingEnabled = getConfig().getBoolean("shared-hunting.enabled", true);
        huntingBroadcast = getConfig().getBoolean("shared-hunting.broadcast", true);
        barteringEnabled = getConfig().getBoolean("shared-bartering.enabled", true);
        barteringBroadcast = getConfig().getBoolean("shared-bartering.broadcast", true);
        mobMultiplierEnabled = getConfig().getBoolean("shared-mob-multiplier.enabled", true);
        mobMultiplier = getConfig().getInt("shared-mob-multiplier.multiplier", 3);

        sharedMaterials.clear();
        for (String name : getConfig().getStringList("shared-mining.materials")) {
            try {
                sharedMaterials.add(Material.valueOf(name.trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                getLogger().log(Level.WARNING, "Material desconocido en config.yml: " + name);
            }
        }

        sharedEntities.clear();
        for (String name : getConfig().getStringList("shared-hunting.entities")) {
            try {
                sharedEntities.add(EntityType.valueOf(name.trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                getLogger().log(Level.WARNING, "Entidad desconocida en config.yml: " + name);
            }
        }

        multipliedEntities.clear();
        for (String name : getConfig().getStringList("shared-mob-multiplier.entities")) {
            try {
                multipliedEntities.add(EntityType.valueOf(name.trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                getLogger().log(Level.WARNING, "Entidad desconocida en config.yml: " + name);
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("sharedfate")) {
            return false;
        }
        if (!sender.hasPermission("sharedfate.admin")) {
            sender.sendMessage("§cNo tienes permiso para usar este comando.");
            return true;
        }
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            loadSettings();
            sender.sendMessage("§aConfiguración de SharedFate recargada.");
            return true;
        }
        sender.sendMessage("§eUso: /sharedfate reload");
        return true;
    }

    public boolean isHealthEnabled() {
        return healthEnabled;
    }

    public boolean isHungerEnabled() {
        return hungerEnabled;
    }

    public boolean isMiningEnabled() {
        return miningEnabled;
    }

    public boolean isMiningBroadcast() {
        return miningBroadcast;
    }

    public boolean isHuntingEnabled() {
        return huntingEnabled;
    }

    public boolean isHuntingBroadcast() {
        return huntingBroadcast;
    }

    public boolean isBarteringEnabled() {
        return barteringEnabled;
    }

    public boolean isBarteringBroadcast() {
        return barteringBroadcast;
    }

    public boolean isMobMultiplierEnabled() {
        return mobMultiplierEnabled;
    }

    public int getMobMultiplier() {
        return mobMultiplier;
    }

    public Set<Material> getSharedMaterials() {
        return sharedMaterials;
    }

    public Set<EntityType> getSharedEntities() {
        return sharedEntities;
    }

    public Set<EntityType> getMultipliedEntities() {
        return multipliedEntities;
    }
}
