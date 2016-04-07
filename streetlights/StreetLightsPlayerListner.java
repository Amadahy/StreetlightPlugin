package de.chronicals.streetlights;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

@SuppressWarnings("unused")
public class StreetLightsPlayerListner implements org.bukkit.event.Listener
{
  private streetlights plugin;
  
  public StreetLightsPlayerListner(streetlights plugin)
  {
    this.plugin = plugin;
  }
  
  public void registerEvents() {
    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
  }
  
  @org.bukkit.event.EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if ((event.getAction() == org.bukkit.event.block.Action.LEFT_CLICK_BLOCK) || (event.getAction() == org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)) {
      if (this.plugin.status.get(event.getPlayer()) == null) {
        this.plugin.status.put(event.getPlayer(), "");
      }
      if (((String)this.plugin.status.get(event.getPlayer())).equalsIgnoreCase("create")) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if ((this.plugin.onstate_mats.contains(block.getType())) || (this.plugin.offstate_mats.contains(block.getType()))) {
          String[] coords = { player.getName(), Integer.toString(block.getX()), Integer.toString(block.getY()), Integer.toString(block.getZ()), block.getWorld().getName(), Integer.toString(this.plugin.func.getmaterialset(block.getType()).intValue()) };
          

          Boolean exists = Boolean.valueOf(false);
          for (int b = 0; b < this.plugin.pendingBlocks.size(); b++) {
            if (this.plugin.func.ComparePendingBlockLoc(Integer.valueOf(b), block, event.getPlayer().getWorld().getName()).booleanValue()) {
              exists = Boolean.valueOf(true);
            }
          }
          
          if (!exists.booleanValue())
          {
            if (((Boolean)this.plugin.World_Light_Status.get(block.getWorld())).booleanValue()) {
              block.setType(this.plugin.func.getmatchingmaterial(this.plugin.func.getmaterialset(block.getType()), false));
            } else {
              block.setType(this.plugin.func.getmatchingmaterial(this.plugin.func.getmaterialset(block.getType()), true));
            }
            if (this.plugin.pendingBlocks != null) {
              this.plugin.pendingBlocks.add(coords);
              this.plugin.conf.save();
              player.sendMessage("[SLr] Lamp added.");
            }
          } else {
            player.sendMessage("[SLr] " + ChatColor.YELLOW + "This streetlight is already registered.");
            player.sendMessage("[SLr] " + ChatColor.RED + "Unable to create dublicate record!");
          }
        }
      }
      if (((String)this.plugin.status.get(event.getPlayer())).equalsIgnoreCase("remove")) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        for (int b = 0; b < this.plugin.pendingBlocks.size(); b++) {
          if (this.plugin.func.ComparePendingBlockLoc(Integer.valueOf(b), block, event.getPlayer().getWorld().getName()).booleanValue()) {
            if ((((String[])this.plugin.pendingBlocks.get(b))[0].equalsIgnoreCase(event.getPlayer().getName())) || (event.getPlayer().isOp()) || (event.getPlayer().hasPermission("streetlights.admin"))) {
              this.plugin.pendingBlocks.remove(b);
              this.plugin.conf.save();
              player.sendMessage("[SLr] " + ChatColor.RED + "Streetlight Removed.");
            } else {
              player.sendMessage("[SLr] " + ChatColor.RED + "This StreetLight belongs to " + ((String[])this.plugin.pendingBlocks.get(b))[0] + ".");
            }
          }
        }
      }
      if (((String)this.plugin.status.get(event.getPlayer())).equalsIgnoreCase("info")) {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        Integer issl = Integer.valueOf(-1);
        for (int b = 0; b < this.plugin.pendingBlocks.size(); b++) {
          if (this.plugin.func.ComparePendingBlockLoc(Integer.valueOf(b), block, event.getPlayer().getWorld().getName()).booleanValue()) {
            issl = Integer.valueOf(b);
          }
        }
        if (issl.intValue() != -1) {
          player.sendMessage("[SLr] " + ChatColor.DARK_GREEN + "This streetlight belongs to: " + ChatColor.GREEN + ((String[])this.plugin.pendingBlocks.get(issl.intValue()))[0]);
          player.sendMessage("[SLr] " + ChatColor.DARK_GREEN + "This streetlight is using: " + ChatColor.GREEN + this.plugin.onstate_mats.get(Integer.parseInt(((String[])this.plugin.pendingBlocks.get(issl.intValue()))[5])) + ChatColor.DARK_GREEN + " during the day");
          player.sendMessage("[SLr] " + ChatColor.DARK_GREEN + "This streetlight is using: " + ChatColor.GREEN + this.plugin.offstate_mats.get(Integer.parseInt(((String[])this.plugin.pendingBlocks.get(issl.intValue()))[5])) + ChatColor.DARK_GREEN + " during the night");
        } else {
          player.sendMessage("[SLr] " + ChatColor.RED + "This block is not a registered streetlight.");
        }
      }
    }
  }
}
