/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller.utils;

import me.f4dev.plugincontroller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Controller {
  
  PluginController plugin;
  
  public Controller(PluginController plugin) {
    this.plugin = plugin;
  }
  
  public void enablePlugin(final Plugin pluginInstance) {
    Bukkit.getPluginManager().enablePlugin(pluginInstance);
    plugin.pluginListManager.removePlugin(pluginInstance.getName());
  }
  
  public void disablePlugin(final Plugin pluginInstance) {
    Bukkit.getPluginManager().disablePlugin(pluginInstance);
  }
}
