package de.chronicals.etc;

import de.chronicals.streetlights.streetlights;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

@SuppressWarnings("unused")
public class Functions
{
  private streetlights plugin;
  
  public Functions(streetlights plugin)
  {
    this.plugin = plugin;
  }
  
  public org.bukkit.Material getmatchingmaterial(Integer blockset, boolean onoff) {
    if (onoff) {
      return (org.bukkit.Material)this.plugin.offstate_mats.get(blockset.intValue());
    }
    return (org.bukkit.Material)this.plugin.onstate_mats.get(blockset.intValue());
  }
  
  public Integer getmaterialset(org.bukkit.Material mat)
  {
    for (int i = 0; i < this.plugin.offstate_mats.size(); i++) {
      if (this.plugin.offstate_mats.get(i) == mat) {
        return Integer.valueOf(i);
      }
    }
    for (int i = 0; i < this.plugin.onstate_mats.size(); i++) {
      if (this.plugin.onstate_mats.get(i) == mat) {
        return Integer.valueOf(i);
      }
    }
    return Integer.valueOf(-1);
  }
  
  public String LocationToString(Location locatie) {
    return 
      locatie.getWorld().getName() + "," + locatie.getX() + "," + locatie.getY() + "," + locatie.getZ();
  }
  
  public Boolean ComparePendingBlockLoc(Integer index, Block block, String world) {
    if ((Integer.parseInt(((String[])this.plugin.pendingBlocks.get(index.intValue()))[1]) == block.getX()) && 
      (Integer.parseInt(((String[])this.plugin.pendingBlocks.get(index.intValue()))[2]) == block
      .getY())) {
      if (Integer.parseInt(((String[])this.plugin.pendingBlocks.get(index.intValue()))[3]) == block
        .getZ())
        if (((String[])this.plugin.pendingBlocks.get(index.intValue()))[4].equalsIgnoreCase(world))
          return Boolean.valueOf(true);
    }
    return Boolean.valueOf(false);
  }
  
  public void setOn(Block block) {
	  org.bukkit.Material neighbor = block.getRelative(org.bukkit.block.BlockFace.WEST).getType();
	  block.getRelative(org.bukkit.block.BlockFace.WEST).setType(org.bukkit.Material.REDSTONE_BLOCK);
	  block.getRelative(org.bukkit.block.BlockFace.WEST).setType(neighbor);
  }
  
  public void togglelights(boolean aanuit, String wereld) {
    if ((this.plugin.pendingBlocks != null) && 
      (this.plugin.pendingBlocks.size() >= 0)) {
      for (int i = 0; i < this.plugin.pendingBlocks.size(); i++) {
        String[] element = (String[])this.plugin.pendingBlocks.get(i);
        if (element[4].equalsIgnoreCase(wereld)) {
          Block blokje = org.bukkit.Bukkit.getWorld(element[4]).getBlockAt(
            Integer.parseInt(element[1]), 
            Integer.parseInt(element[2]), 
            Integer.parseInt(element[3]));
          if ((((Boolean)this.plugin.World_Light_Status.get(blokje.getWorld())).booleanValue()) || 
            (aanuit)) {
        	 blokje.setType(getmatchingmaterial(
              Integer.valueOf(Integer.parseInt(element[5])), false));
        	 setOn(blokje);
          } else {
            blokje.setType(getmatchingmaterial(
              Integer.valueOf(Integer.parseInt(element[5])), true));
          }
        }
      }
    }
  }
}
