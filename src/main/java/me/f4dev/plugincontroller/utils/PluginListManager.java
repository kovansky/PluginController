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
  
  /**
   * Class constructor
   *
   * @param plugin PluginController instance
   */
  public PluginListManager(PluginController plugin) {
    this.plugin = plugin;
    this.list = new File(plugin.getDataFolder(), "list.yml");
    
    createFile();
  }
  
  /**
   * @return YamlConfiguration instance of list.yml file
   */
  private YamlConfiguration getYamlConfiguration() {
    return YamlConfiguration.loadConfiguration(list);
  }
  
  /**
   * Creates list.yml file if not exists
   */
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
  
  /**
   * Saves list.yml file
   */
  private void save() {
    try {
      fileConfiguration.save(list);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Removes given plugin from list
   *
   * @param pluginName name of plugin to remove
   */
  public void removePlugin(String pluginName) {
    List<String> plugins = fileConfiguration.getStringList("disabled");
    
    if(plugins.contains(pluginName)) {
      plugins.remove(pluginName);
      
      fileConfiguration.set("disabled", plugins);
      
      save();
    }
  }
  
  /**
   * Adds given plugin to list
   *
   * @param pluginName name of plugin to add
   */
  public void addPlugin(String pluginName) {
    List<String> plugins = fileConfiguration.getStringList("disabled");
    
    if(!plugins.contains(pluginName)) {
      plugins.add(pluginName);
      
      fileConfiguration.set("disabled", plugins);
      
      save();
    }
  }
}
