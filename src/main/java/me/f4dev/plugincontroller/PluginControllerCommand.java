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
  
      Plugin pluginInstance = null;
    } else {
      sender.sendMessage(PluginController.colorify(plugin.language.getString("response.error" +
              ".noPermission")));
    }
//    if( !hasPermission( sender, "pluginmanager.load" ) )
//			return true;
//
//		if( split.length < 2 )
//		{
//			sender.sendMessage( ChatColor.GOLD + "/" + label + " " + ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Command.Description.Load" ) ) );
//			return true;
//		}
//
//		final File toLoad = new File( "plugins" + File.separator + split[1] + ( split[1].endsWith( ".jar" ) ? "" : ".jar" ) );
//
//		if( !toLoad.exists( ) )
//		{
//			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.NoSuchFile" ), split[1] + ".jar" ) ) );
//			return true;
//		}
//
//		PluginDescriptionFile desc = control.getDescription( toLoad );
//		if( desc == null )
//		{
//			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Response.Error.NoDescriptionFile" ) ) );
//			return true;
//		}
//
//		if( Bukkit.getPluginManager( ).getPlugin( desc.getName( ) ) != null )
//		{
//			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', plugin.language.getString( "Response.Error.AlreadyLoaded" ) ) );
//			return true;
//		}
//
//		Plugin p = null;
//		if( ( p = control.loadPlugin( toLoad ) ) != null)
//		{
//			control.enablePlugin( p );
//			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Action.PluginLoaded" ), p.getDescription( ).getName( ), p.getDescription( ).getVersion( ) ) ) );
//		}
//		else
//			sender.sendMessage( ChatColor.translateAlternateColorCodes( '&', String.format( plugin.language.getString( "Response.Error.PluginNotLoaded" ), split[1] ) ) );
//
//		return true;
    
    return true;
  }
}
