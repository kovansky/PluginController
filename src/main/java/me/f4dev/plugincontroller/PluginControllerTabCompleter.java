/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */
package me.f4dev.plugincontroller;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginControllerTabCompleter implements TabCompleter {
  private PluginController plugin;
  private List<String> params = Arrays.asList("disable", "enable", "load", "unload", "reload",
          "sreload", "details", "configreload", "list");
  
  public PluginControllerTabCompleter(PluginController plugin) {
    this.plugin = plugin;
    plugin.getCommand("plugincontroller").setTabCompleter(this);
  }
  
  private boolean checkParam(String arg) {
    for(String param : params) {
      if(arg.equalsIgnoreCase(param)) {
        return true;
      }
    }
    
    return false;
  }
  
  @Override
  public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
    List<String> list = new ArrayList<>();
    
    if(args.length == 1) {
      for(String param : params) {
        if(param.toLowerCase().startsWith(args[0].toLowerCase())) {
          list.add(param);
        }
      }
    } else if(args.length > 1 && args.length < 3) {
      if(checkParam(args[0])) {
        for(Plugin pluginInstance : Bukkit.getServer().getPluginManager().getPlugins()) {
          String pluginName = pluginInstance.getName();
          
          if(pluginName.toLowerCase().startsWith(args[1].toLowerCase())) {
            list.add(pluginName);
          }
        }
      }
    }
    
    return list;
  }
}