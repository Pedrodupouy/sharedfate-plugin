package com.pedro.sharedfate;

import com.pedro.sharedfate.listeners.SharedHealthListener;
import com.pedro.sharedfate.listeners.SharedHungerListener;
import com.pedro.sharedfate.listeners.SharedMiningListener;
import org.bukkit.plugin.java.JavaPlugin;

public class SharedFatePlugin extends JavaPlugin {

    private int sharedBlocksMined = 0;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        boolean healthEnabled = getConfig().getBoolean("share-health.enabled", true);
        boolean hungerEnabled = getConfig().getBoolean("share-hunger.enabled", true);
        boolean miningEnabled = getConfig().getBoolean("shared-mining.enabled", true);

        if (healthEnabled) {
            getServer().getPluginManager().registerEvents(new SharedHealthListener(this), this);
        }
        if (hungerEnabled) {
            getServer().getPluginManager().registerEvents(new SharedHungerListener(this), this);
        }
        if (miningEnabled) {
            getServer().getPluginManager().registerEvents(new SharedMiningListener(this), this);
        }

        String vidaEstado = healthEnabled
                ? "ON(" + getConfig().getInt("share-health.percentage", 100) + "%)"
                : "OFF";
        String hambreEstado = hungerEnabled
                ? "ON(" + getConfig().getInt("share-hunger.percentage", 100) + "%)"
                : "OFF";
        String minadoEstado = miningEnabled
                ? "ON(cada " + getConfig().getInt("shared-mining.milestone-interval", 100) + " bloques)"
                : "OFF";

        getLogger().info("SharedFate activo. Vida=" + vidaEstado
                + " Hambre=" + hambreEstado
                + " Minado=" + minadoEstado);
    }

    @Override
    public void onDisable() {
        getLogger().info("SharedFate desactivado. Bloques minados por el equipo en esta sesion: " + sharedBlocksMined);
    }

    public int incrementAndGetSharedBlocksMined() {
        return ++sharedBlocksMined;
    }

    public int getSharedBlocksMined() {
        return sharedBlocksMined;
    }
}
