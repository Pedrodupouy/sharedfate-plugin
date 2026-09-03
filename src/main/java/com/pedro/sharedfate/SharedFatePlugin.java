package com.pedro.sharedfate;
import com.pedro.sharedfate.listeners.SharedHealthListener;
import com.pedro.sharedfate.listeners.SharedHungerListener;
import com.pedro.sharedfate.listeners.SharedMiningListener;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;
public final class SharedFatePlugin extends JavaPlugin {
    private boolean healthEnabled;
    private boolean hungerEnabled;
    private boolean miningEnabled;
    private boolean miningBroadcast;
    private final Set<Material> sharedMaterials = EnumSet.noneOf(Material.class);
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();
        getServer().getPluginManager().registerEvents(new SharedHealthListener(this), this);
        getServer().getPluginManager().registerEvents(new SharedHungerListener(this), this);
        getServer().getPluginManager().registerEvents(new SharedMiningListener(this), this);
        getLogger().info("SharedFate activo. Vida=" + healthEnabled
                + " Hambre=" + hungerEnabled
                + " Minado=" + miningEnabled
                + " (" + sharedMaterials.size() + " bloques configurados)");
    }
    public void loadSettings() {
        reloadConfig();
        healthEnabled = getConfig().getBoolean("shared-health.enabled", true);
        hungerEnabled = getConfig().getBoolean("shared-hunger.enabled", true);
        miningEnabled = getConfig().getBoolean("shared-mining.enabled", true);
        miningBroadcast = getConfig().getBoolean("shared-mining.broadcast", true);
        sharedMaterials.clear();
        for (String name : getConfig().getStringList("shared-mining.materials")) {
            try {
                sharedMaterials.add(Material.valueOf(name.trim().toUpperCase()));
            } catch (IllegalArgumentException ex) {
                getLogger().log(Level.WARNING, "Material desconocido en config.yml: " + name);
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
    public Set<Material> getSharedMaterials() {
        return sharedMaterials;
    }
}
