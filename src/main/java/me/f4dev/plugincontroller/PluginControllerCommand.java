/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PluginControllerCommand implements CommandExecutor {
  
  private PluginController plugin;
  
  public PluginControllerCommand(PluginController plugin) {
    this.plugin = plugin;
    
    plugin.getCommand("plugincontroller").setExecutor(this);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    
    
    return true;
  }
}
