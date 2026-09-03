package com.pedro.sharedfate.listeners;
import com.pedro.sharedfate.SharedFatePlugin;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import java.util.Collection;
import java.util.HashMap;
public class SharedMiningListener implements Listener {
    private final SharedFatePlugin plugin;
    public SharedMiningListener(SharedFatePlugin plugin) {
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.isMiningEnabled()) return;
        Block block = event.getBlock();
        Material type = block.getType();
        if (!plugin.getSharedMaterials().contains(type)) return;
        Player breaker = event.getPlayer();
        Collection<ItemStack> drops = block.getDrops(breaker.getInventory().getItemInMainHand(), breaker);
        if (drops.isEmpty()) return;
        for (Player other : plugin.getServer().getOnlinePlayers()) {
            if (other.getUniqueId().equals(breaker.getUniqueId())) continue;
            for (ItemStack drop : drops) {
                ItemStack copy = drop.clone();
                HashMap<Integer, ItemStack> leftover = other.getInventory().addItem(copy);
                for (ItemStack extra : leftover.values()) {
                    other.getWorld().dropItemNaturally(other.getLocation(), extra);
                }
            }
            if (plugin.isMiningBroadcast()) {
                other.sendMessage("§b" + breaker.getName() + " §7minó " + formatDrops(drops)
                        + " §7- te tocó una copia.");
            }
        }
    }
    private String formatDrops(Collection<ItemStack> drops) {
        StringBuilder sb = new StringBuilder();
        for (ItemStack item : drops) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(item.getAmount()).append("x ").append(item.getType());
        }
        return sb.toString();
    }
}
