/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller.utils;

import me.f4dev.plugincontroller.PluginController;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PluginListManager {
  
  private PluginController plugin;
  private File list;
  private FileConfiguration fileConfiguration = null;
  
  public PluginListManager(PluginController plugin) {
    this.plugin = plugin;
    this.list = new File(plugin.getDataFolder(), "list.yml");
    
    createFile();
  }
  
  private YamlConfiguration getYamlConfiguration() {
    return YamlConfiguration.loadConfiguration(list);
  }
  
  private void createFile() {
    if(!list.exists()) {
      try {
        list.createNewFile();
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    
    if(fileConfiguration == null) {
      fileConfiguration = getYamlConfiguration();
    }
  }
  
  public void removePlugin(String pluginName) {
    List<String> plugins = fileConfiguration.getStringList("disabled");
    
    if(plugins.contains(pluginName)) {
      plugins.remove(pluginName);
    }
    
    fileConfiguration.set("disabled", plugins);
    
    try {
      fileConfiguration.save(list);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
}
