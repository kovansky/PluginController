/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */
package me.f4dev.plugincontroller.listeners;

import me.f4dev.plugincontroller.PluginController;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import java.io.File;
import java.io.IOException;

public class CommandPreprocessListener implements Listener {
  private PluginController plugin;
  
  /**
   * Class constructor
   *
   * @param plugin PluginController instance
   */
  public CommandPreprocessListener(PluginController plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  @EventHandler
  public void onServerCommand(ServerCommandEvent e) {
    String command = e.getCommand();
    String[] args = command.split(" ");
    
    if(args.length == 1) {
      if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
        File listFile = new File(plugin.getDataFolder(), "list.yml");
        if(listFile.exists()) {
          FileConfiguration disabledPlugins = YamlConfiguration.loadConfiguration(listFile);
          disabledPlugins.set("reload", true);
          try {
            disabledPlugins.save(listFile);
          } catch(IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    }
  }
  
  @EventHandler
  public void onCommandPreprocess(PlayerCommandPreprocessEvent e) {
    String message = e.getMessage();
    String[] args = message.split(" ");
  
    if(args.length == 1) {
      if(args[0].equalsIgnoreCase("/reload") || args[0].equalsIgnoreCase("/rl")) {
        File listFile = new File(plugin.getDataFolder(), "list.yml");
        if(listFile.exists()) {
          FileConfiguration disabledPlugins = YamlConfiguration.loadConfiguration(listFile);
          disabledPlugins.set("reload", true);
          try {
            disabledPlugins.save(listFile);
          } catch(IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    }
  }
}
