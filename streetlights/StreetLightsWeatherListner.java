package de.chronicals.streetlights;

import org.bukkit.event.weather.WeatherChangeEvent;

public class StreetLightsWeatherListner implements org.bukkit.event.Listener
{
  private streetlights plugin;
  
  public StreetLightsWeatherListner(streetlights plugin)
  {
    this.plugin = plugin;
  }
  
  public void registerEvents() {
    this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
  }
  
  @org.bukkit.event.EventHandler
  public void onWeatherChange(WeatherChangeEvent event) {
    if (this.plugin.config_use_rain) {
      if (event.toWeatherState()) {
        this.plugin.func.togglelights(true, event.getWorld().getName());
      } else {
        this.plugin.func.togglelights(false, event.getWorld().getName());
      }
    }
  }
}


/* Location:              C:\Users\Danir\Desktop\Spigot\Spigot\Spigot-Server\target\plugins\StreetLights-Dev.jar!\de\chronicals\streetlights\StreetLightsWeatherListner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */