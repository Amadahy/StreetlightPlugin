package de.chronicals.streetlights;

import de.chronicals.etc.Configuration;
import de.chronicals.etc.Functions;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class streetlights extends JavaPlugin implements org.bukkit.event.Listener
{
  public static File directory;
  private Logger log;
  public ArrayList<String[]> pendingBlocks = new ArrayList();
  public ArrayList<Material> onstate_mats = new ArrayList();
  public ArrayList<Material> offstate_mats = new ArrayList();
  public Map<World, Boolean> World_Light_Status = new HashMap();
  public Map<Player, String> status = new HashMap();
  
  public Functions func;
  public Configuration conf;
  public int config_on_time;
  public int config_off_time;
  public boolean config_use_rain;
  
  public void onEnable()
  {
    getServer().getPluginManager().registerEvents(this, this);
    new StreetLightsBlockListner(this).registerEvents();
    new StreetLightsPlayerListner(this).registerEvents();
    new StreetLightsWeatherListner(this).registerEvents();
    this.func = new Functions(this);
    this.conf = new Configuration(this);
    

    directory = getDataFolder();
    if (!directory.exists()) {
      directory.mkdir();
    }
    this.conf.loadConfig();
    
    for (World world : Bukkit.getWorlds()) {
      if (world.getTime() > this.config_on_time) {
        this.World_Light_Status.put(world, Boolean.valueOf(true));
      }
      if ((world.getTime() > this.config_off_time) && (world.getTime() < this.config_on_time)) {
        this.World_Light_Status.put(world, Boolean.valueOf(false));
      }
    }
    
    this.log = getLogger();
    this.log.info("[SLr] Streetlights is now enabled.");
    startTimeCheck();
  }
  
  public void onDisable() {
    saveConfig();
    this.log.info("[SLr] Streetlights is now disabled.");
  }
  
  public void startTimeCheck() {
    getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
      public void run() {
        for (World world : Bukkit.getWorlds()) { // was: getWorld(getName()), before that: world.getName()
          if (!streetlights.this.World_Light_Status.containsKey(world)) {
            if (world.getTime() > streetlights.this.config_on_time) {
              streetlights.this.World_Light_Status.put(world, Boolean.valueOf(true));
            }
            if ((world.getTime() > streetlights.this.config_off_time) && (world.getTime() < streetlights.this.config_on_time)) {
              streetlights.this.World_Light_Status.put(world, Boolean.valueOf(false));
            }
          }
          
          if ((world.getTime() > streetlights.this.config_on_time) && (!((Boolean)streetlights.this.World_Light_Status.get(world)).booleanValue())) {
        	streetlights.this.World_Light_Status.put(world, Boolean.valueOf(true));
        	streetlights.this.func.togglelights(false, world.getName());
          }
          if ((world.getTime() > streetlights.this.config_off_time) && (world.getTime() < streetlights.this.config_on_time) && (((Boolean)streetlights.this.World_Light_Status.get(world)).booleanValue())) {
        	streetlights.this.World_Light_Status.put(world, Boolean.valueOf(false));
        	streetlights.this.func.togglelights(false, world.getName());
          }
        }
      }
    }, 0L, 60L);
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    Player player = null;
    if ((sender instanceof Player)) {
      player = (Player)sender;
    }
    

    if (cmd.getName().equalsIgnoreCase("sl")) {
      if (player == null) {
        sender.sendMessage("[SLr] This command can only be run by a player");

      }
      else if (args.length > 0) {
        if (args[0].equalsIgnoreCase("info")) {
          if (player.hasPermission("streetlights.info")) {
            if (this.status.get(player) == null) {
              this.status.put(player, "");
            }
            if (!((String)this.status.get(player)).equalsIgnoreCase("info")) {
              this.status.put(player, "info");
              player.sendMessage("[SLr] Please click a streetlight to get info about it.");
            } else {
              this.status.put(player, "");
              player.sendMessage("[SLr] You're out info mode now");
            }
          } else {
            player.sendMessage("[SLr] " + ChatColor.RED + "You don't have the permission to do this.");
          }
          return true;
        }
        if (args[0].equalsIgnoreCase("reload")) {
          if (player.hasPermission("streetlights.reload")) {
            reloadConfig();
            
            this.onstate_mats.clear();
            this.offstate_mats.clear();
            this.pendingBlocks.clear();
            
            for (String string : getConfig().getStringList("materials")) {
              this.onstate_mats.add(Material.getMaterial(string.split(",")[0]));
            }
            for (String string : getConfig().getStringList("materials")) {
              this.offstate_mats.add(Material.getMaterial(string.split(",")[1]));
            }
            for (String string : getConfig().getStringList("lights")) {
              this.pendingBlocks.add(string.split(",", 6));
            }
            
            this.config_on_time = getConfig().getInt("Time_on");
            this.config_off_time = getConfig().getInt("Time_off");
            this.config_use_rain = getConfig().getBoolean("On_when_rain");
            
            player.sendMessage("[SLr] " + ChatColor.GREEN + "Config reloaded!");
          }
          return true;
        }
        if (args[0].equalsIgnoreCase("save")) {
          if (player.hasPermission("streetlights.save")) {
            String[] listOfStrings = new String[this.pendingBlocks.size()];
            Integer count = Integer.valueOf(0);
            for (String[] array : this.pendingBlocks) {
              listOfStrings[count.intValue()] = (array[0] + "," + array[1] + "," + array[2] + "," + array[3] + "," + array[4] + "," + array[5]);
              count = Integer.valueOf(count.intValue() + 1);
            }
            getConfig().set("lights", Arrays.asList(listOfStrings));
            saveConfig();
            player.sendMessage("[SLr] Saved!");
          } else {
            player.sendMessage("[SLr] " + ChatColor.RED + "You don't have the permission to do this.");
          }
          return true;
        }
        if (args[0].equalsIgnoreCase("list")) {
          if (player.hasPermission("streetlights.list")) {
            for (int i = 0; i < this.pendingBlocks.size(); i++) {
              String[] element = (String[])this.pendingBlocks.get(i);
              player.sendMessage("[SLr] Loc: " + element[1] + "," + element[2] + "," + element[3]);
            }
          } else {
            player.sendMessage("[SLr] " + ChatColor.RED + "You don't have the permission to do this.");
          }
          return true;
        }
        if (args[0].equalsIgnoreCase("clear")) {
          if (player.hasPermission("streetlights.clear")) {
            this.pendingBlocks.clear();
            player.sendMessage("[SLr] List is now cleared!");
          } else {
            player.sendMessage("[SLr] " + ChatColor.RED + "You don't have the permission to do this.");
          }
          return true;
        }
        
        if (args[0].equalsIgnoreCase("create")) {
          if (player.hasPermission("streetlights.create")) {
            if (this.status.get(player) == null) {
              this.status.put(player, "");
            }
            if (!((String)this.status.get(player)).equalsIgnoreCase("create")) {
              this.status.put(player, "create");
              player.sendMessage("[SLr] You can now add lights to the list!");
            } else {
              this.status.put(player, "");
              player.sendMessage("[SLr] You're done creating streetlights now!");
            }
          } else {
            player.sendMessage("[SLr] " + ChatColor.RED + "You don't have the permission to do this.");
          }
          return true;
        }
        if (args[0].equalsIgnoreCase("remove")) {
          if (player.hasPermission("streetlights.remove")) {
            if (this.status.get(player) == null) {
              this.status.put(player, "");
            }
            if (!((String)this.status.get(player)).equalsIgnoreCase("remove")) {
              this.status.put(player, "remove");
              player.sendMessage("[SLr] You can now remove streetlights");
            } else {
              this.status.put(player, "");
              player.sendMessage("[SLr] You're done removing streetlights now!");
            }
          } else {
            player.sendMessage("[SLr] " + ChatColor.RED + "You don't have the permission to do this.");
          }
          return true;
        }
        if (args[0].equalsIgnoreCase("help")) {
          player.sendMessage("[SLr] " + ChatColor.GOLD + ChatColor.BOLD + "=====StreetLightsReloaded Help=====");
          player.sendMessage("[SLr] " + ChatColor.YELLOW + ChatColor.BOLD + "COMMAND LIST:");
          player.sendMessage("[SLr] " + ChatColor.YELLOW + "/sl save|list|clear|create|info|reload|remove");
          player.sendMessage("[SLr] " + ChatColor.WHITE + ChatColor.BOLD + "COMMAND EXPLANATION:");
          player.sendMessage("[SLr] " + ChatColor.WHITE + ChatColor.BOLD + "save" + ChatColor.WHITE + " - Save lights you're currently editing");
          player.sendMessage("[SLr] " + ChatColor.WHITE + ChatColor.BOLD + "list" + ChatColor.WHITE + " - List created Streetlights");
          player.sendMessage("[SLr] " + ChatColor.WHITE + ChatColor.BOLD + "clear" + ChatColor.WHITE + " - Cancel active Streetlight creation ");
          player.sendMessage("[SLr] " + ChatColor.WHITE + ChatColor.BOLD + "info" + ChatColor.WHITE + " - Show information about targeted streetlight");
          player.sendMessage("[SLr] " + ChatColor.WHITE + ChatColor.BOLD + "reload" + ChatColor.WHITE + " - Reload configuration");
          player.sendMessage("[SLr] " + ChatColor.WHITE + ChatColor.BOLD + "remove" + ChatColor.WHITE + " - Remove existing streetlights");
          player.sendMessage("[SLr] " + ChatColor.GRAY + "(C) 2015 CHRONICALSde");
         
        }
        

        return true;
      }
    }
    
    return false;
  }
}
