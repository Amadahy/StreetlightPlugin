package de.chronicals.streetlights;

import java.util.Map;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;

@SuppressWarnings("unused")
public class StreetLightsBlockListner implements org.bukkit.event.Listener
{
  private streetlights plugin;
  
  public StreetLightsBlockListner(streetlights plugin)
  {
    this.plugin = plugin;
  }
  
  public void registerEvents() {
    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
  }
  
  @org.bukkit.event.EventHandler
  public void onBlockRedstone(BlockRedstoneEvent event) {
    if (this.plugin.pendingBlocks != null) {
      Block block = event.getBlock();
      if (block.getType() == org.bukkit.Material.REDSTONE_LAMP_ON) {
        for (int b = 0; b < this.plugin.pendingBlocks.size(); b++) {
          if (this.plugin.func.ComparePendingBlockLoc(Integer.valueOf(b), block, event.getBlock().getWorld().getName()).booleanValue()) {
            event.setNewCurrent(100);
          }
        }
      }
    }
  }
  
  @org.bukkit.event.EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Block block = event.getBlock();
    if (this.plugin.status.get(event.getPlayer()) == null) {
      this.plugin.status.put(event.getPlayer(), "");
    }
    if ((!((String)this.plugin.status.get(event.getPlayer())).equalsIgnoreCase("create")) && (!((String)this.plugin.status.get(event.getPlayer())).equalsIgnoreCase("info")) && (!((String)this.plugin.status.get(event.getPlayer())).equalsIgnoreCase("remove"))) {
      for (int b = 0; b < this.plugin.pendingBlocks.size(); b++) {
        if (this.plugin.func.ComparePendingBlockLoc(Integer.valueOf(b), block, event.getPlayer().getWorld().getName()).booleanValue()) {
          if ((((String[])this.plugin.pendingBlocks.get(b))[0].equalsIgnoreCase(event.getPlayer().getName())) || (event.getPlayer().isOp()) || (event.getPlayer().hasPermission("streetlights.admin"))) {
            this.plugin.pendingBlocks.remove(b);
            event.getPlayer().sendMessage(org.bukkit.ChatColor.RED + "StreetLight Removed");
            this.plugin.conf.save();
          } else {
            event.getPlayer().sendMessage(org.bukkit.ChatColor.RED + "This StreetLight belongs to " + ((String[])this.plugin.pendingBlocks.get(b))[0] + ".");
            event.setCancelled(true);
          }
        }
      }
    } else {
      event.setCancelled(true);
    }
  }
}
