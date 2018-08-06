/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PluginController extends JavaPlugin {
  
  public FileConfiguration language;
  
  @Override
  public void onEnable() {
    initConfig();
  
    getLogger().info("Plugin Controller has been enabled.");
  }
  
  @Override
  public void onDisable() {
    language = null;
    getLogger().info("Plugin Controller hes been disabled");
  }
  
  private void initConfig() {
    if(!new File(getDataFolder(), "config.yml").exists()) {
      saveResource("config.yml", false);
    }
    
    String[] langArray = {"en_US"};
    for(String lang : langArray) {
      if(!new File(getDataFolder() + File.separator + "languages", lang + ".yml").exists()) {
        saveResource("languages" + File.separator + lang + ".yml", false);
      }
    }
    
    language = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator +
            "languages", this.getConfig().getString("language") + ".yml"));
  }
  
  public static String colorify(String s) {
    if(s != null) {
      return ChatColor.translateAlternateColorCodes('&', s);
    }
    
    return null;
  }
}
