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
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;

public class PluginControllerCommand implements CommandExecutor {
  
  private PluginController plugin;
  
  public PluginControllerCommand(PluginController plugin) {
    this.plugin = plugin;
    
    plugin.getCommand("plugincontroller").setExecutor(this);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(args.length == 0 || (args.length == 1 && (args[0].equals("help") || args[0].equals("h")))) {
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
  
        return true;
      } else {
        sender.sendMessage(PluginController.colorify(plugin.language.getString("response.error" +
                ".nopermission")));
        return true;
      }
    }
  
    switch(args[0].toLowerCase()) {
      case "enable":
      case "e":
        return enableSubcommand(sender, label, args);
      case "disable":
      case "d":
        return disableSubcommand(sender, label, args);
      case "load":
      case "l":
        return loadSubcommand(sender, label, args);
      case "unload":
      case "u":
        return unloadSubcommand(sender, label, args);
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
  
    return true;
  }
  
  private boolean disableSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.disable")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorify("&7/&6" + label + " " + plugin.language.getString(
                "command" +
                        ".description.disable")));
        return true;
      }
  
      final Plugin pluginInstance = Bukkit.getServer().getPluginManager().getPlugin(args[1]);
  
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.error.noPlugin"), args[1])));
      } else if(!pluginInstance.isEnabled()) {
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.error.alreadyDisabled"), args[1])));
      } else {
        plugin.controller.disablePlugin(pluginInstance);
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.action.pluginDisabled"), args[1])));
      }
    } else {
      sender.sendMessage(PluginController.colorify(plugin.language.getString("response.error" +
              ".noPermission")));
    }
  
    return true;
  }
  
  private boolean loadSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.load")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorify("&7/&6" + label + " " + plugin.language.getString(
                "command" +
                        ".description.load")));
        return true;
      }
      
      String fileName = args[1] + (args[1].endsWith(".jar") ? "" : ".jar");
      final File pluginToLoad =
              new File("plugins" + File.separator + fileName);
      
      if(!pluginToLoad.exists()) {
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.error.noSuchFile"), fileName)));
      }
  
      PluginDescriptionFile descriptionFile = plugin.controller.getDescription(pluginToLoad);
  
      if(descriptionFile == null) {
        sender.sendMessage(PluginController.colorify(plugin.language.getString("response.error" +
                ".noDescription")));
        return true;
      }
  
      if(Bukkit.getPluginManager().getPlugin(descriptionFile.getName()) != null) {
        sender.sendMessage(PluginController.colorify(plugin.language.getString("response.error" +
                ".alreadyLoaded")));
        return true;
      }
  
      Plugin pluginInstance;
      if((pluginInstance = plugin.controller.loadPlugin(pluginToLoad)) != null) {
        plugin.controller.enablePlugin(pluginInstance);
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.action.pluginLoaded"), pluginInstance.getName(),
                pluginInstance.getDescription().getVersion())));
      } else {
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.error.pluginNotLoaded"), args[1])));
      }
    } else {
      sender.sendMessage(PluginController.colorify(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  private boolean unloadSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.unload")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorify("&7/&6" + label + " " + plugin.language.getString(
                "command" +
                        ".description.unload")));
        return true;
      }
      
      final Plugin pluginInstance = Bukkit.getPluginManager().getPlugin(args[1]);
      
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                "response.error.noPlugin", args[1]))));
      } else {
        if(plugin.controller.unloadPlugin(pluginInstance, true)) {
          sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                  "response.action.pluginUnloaded"), pluginInstance.getName(),
                  pluginInstance.getDescription().getVersion())));
        } else {
          sender.sendMessage(PluginController.colorify(String.format(plugin.language.getString(
                  "response.error.pluginNotUnloaded"), args[1])));
        }
      }
    } else {
      sender.sendMessage(PluginController.colorify(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
}
