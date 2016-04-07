package de.chronicals.etc;

import de.chronicals.streetlights.streetlights;
import java.util.ArrayList;
import org.bukkit.configuration.file.FileConfiguration;

@SuppressWarnings("unused")
public class Configuration
{
  private streetlights plugin;
  
  public Configuration(streetlights plugin)
  {
    this.plugin = plugin;
  }
  
  public void loadConfig() {
    this.plugin.getConfig().options().copyDefaults(true);
    this.plugin.saveConfig();
    
    this.plugin.config_on_time = this.plugin.getConfig().getInt("Time_on");
    this.plugin.config_off_time = this.plugin.getConfig().getInt("Time_off");
    this.plugin.config_use_rain = this.plugin.getConfig().getBoolean("On_when_rain");
    
    for (String string : this.plugin.getConfig().getStringList("materials")) {
      this.plugin.onstate_mats.add(org.bukkit.Material.getMaterial(string.split(",")[0]));
    }
    for (String string : this.plugin.getConfig().getStringList("materials")) {
      this.plugin.offstate_mats.add(org.bukkit.Material.getMaterial(string.split(",")[1]));
    }
    for (String string : this.plugin.getConfig().getStringList("lights")) {
      this.plugin.pendingBlocks.add(string.split(",", 6));
    }
  }
  
  public void save() {
    String[] listOfStrings = new String[this.plugin.pendingBlocks.size()];
    Integer count = Integer.valueOf(0);
    for (String[] array : this.plugin.pendingBlocks) {
      listOfStrings[count.intValue()] = (array[0] + "," + array[1] + "," + array[2] + "," + array[3] + "," + array[4] + "," + array[5]);
      count = Integer.valueOf(count.intValue() + 1);
    }
    this.plugin.getConfig().set("lights", java.util.Arrays.asList(listOfStrings));
    this.plugin.saveConfig();
  }
}

