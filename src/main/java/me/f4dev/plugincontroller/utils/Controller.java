/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller.utils;

import me.f4dev.plugincontroller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class Controller {
  
  PluginController plugin;
  
  public Controller(PluginController plugin) {
    this.plugin = plugin;
  }
  
  public PluginDescriptionFile getDescription(final File file) {
    try {
      final JarFile jar = new JarFile(file);
      final ZipEntry zip = jar.getEntry("plugin.yml");
      if(zip == null) {
        jar.close();
        return null;
      }
      final PluginDescriptionFile pdf = new PluginDescriptionFile(jar.getInputStream(zip));
      jar.close();
      return pdf;
    } catch(InvalidDescriptionException | IOException ioe) {
      ioe.printStackTrace();
    }
    return null;
  }
  
  public void enablePlugin(final Plugin pluginInstance) {
    Bukkit.getPluginManager().enablePlugin(pluginInstance);
    plugin.pluginListManager.removePlugin(pluginInstance.getName());
  }
  
  public void disablePlugin(final Plugin pluginInstance) {
    Bukkit.getPluginManager().disablePlugin(pluginInstance);
    plugin.pluginListManager.addPlugin(pluginInstance.getName());
  }
  
  public void loadPlugin(final File pluginFile) {
  
  }
}
