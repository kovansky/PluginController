/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class PluginControllerCommand implements CommandExecutor {
  
  private PluginController plugin;
  
  public PluginControllerCommand(PluginController plugin) {
    this.plugin = plugin;
    
    plugin.getCommand("plugincontroller").setExecutor(this);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(args.length == 0) {
      if(sender.hasPermission("plugincontroller.help")) {
        String[] subcommands = {"enable", "disable", "load", "unload", "reload", "sreload", "show",
                "list", "listOptions", "configReload"};
      
        sender.sendMessage(PluginController.colorify("&2|------------------ &6&lPluginController " +
                "Help &2------------------|"));
      
        for(String subcommand : subcommands) {
          sender.sendMessage(PluginController.colorify("&7/&6" + label + " " + plugin.language.getString(
                  "command" +
                          ".description." + subcommand)));
        }
      
        sender.sendMessage(PluginController.colorify("&7|------------------------------------|"));
      }
    }
  
    switch(args[0].toLowerCase()) {
      case "enable":
      case "e":
        return enableSubcommand(sender, label, args);
    }
    
    return true;
  }
  
  private boolean enableSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.enable")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorify("&7/&6" + label + " " + plugin.language.getString(
                "command" +
                        ".description.enable")));
        return true;
      }
      
      final Plugin pluginInstance = Bukkit.getServer().getPluginManager().getPlugin(args[1]);
      
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.error.noPlugin"), args[1])));
      } else if(pluginInstance.isEnabled()) {
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.error.alreadyEnabled"), args[1])));
      } else {
        plugin.controller.enablePlugin(pluginInstance);
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.action.pluginEnabled"), args[1])));
      }
    } else {
      sender.sendMessage(PluginController.colorify(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    // ToDo: tests
    return true;
  }
}
