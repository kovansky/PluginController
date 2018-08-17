/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller;

import me.f4dev.plugincontroller.utils.SpigetClient;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class PluginControllerCommand implements CommandExecutor {
  private PluginController plugin;
  
  /**
   * Class constructor
   *
   * @param plugin PluginController instance
   */
  public PluginControllerCommand(PluginController plugin) {
    this.plugin = plugin;
    
    plugin.getCommand("plugincontroller").setExecutor(this);
  }
  
  /**
   * @param sender  command sender
   * @param command command
   * @param label   used command label
   * @param args    arguments used
   * @return <code>true</code>, if command was successful
   */
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if(args.length == 0 || (args.length == 1 && (args[0].equals("help") || args[0].equals("h")))) {
      if(sender.hasPermission("plugincontroller.help")) {
        String[] subcommands = {"enable", "disable", "load", "unload", "reload", "sreload",
                "details", "list", "configReload", "search", "more", "download"};
        
        sender.sendMessage(PluginController.colorize("&2|------------------ &6&lPluginController " +
                "Help &2------------------|"));
        
        for(String subcommand : subcommands) {
          sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                  "command.description." + subcommand)));
        }
        
        sender.sendMessage(PluginController.colorize("&7|------------------------------------|"));
        
        return true;
      } else {
        sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
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
      case "reload":
      case "r":
      case "rl":
        return reloadSubcommand(sender, label, args);
      case "softreload":
      case "sreload":
      case "s":
      case "srl":
        return softReloadSubcommand(sender, label, args);
      case "details":
      case "show":
      case "info":
      case "i":
        return detailsSubcommand(sender, label, args);
      case "list":
      case "ls":
        return listSubcommand(sender, label, args);
      case "configreload":
      case "cr":
        return configReloadSubcommand(sender, label, args);
      case "search":
        return searchSubcommand(sender, label, args);
      case "more":
        return moreSubcommand(sender, label, args);
      case "download":
        return downloadSubcommand(sender, label, args);
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean enableSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.enable")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command" +
                        ".description.enable")));
        return true;
      }
      
      final Plugin pluginInstance = Bukkit.getServer().getPluginManager().getPlugin(args[1]);
      
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noPlugin"), args[1])));
      } else if(pluginInstance.isEnabled()) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.alreadyEnabled"), args[1])));
      } else {
        plugin.controller.enablePlugin(pluginInstance);
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.pluginEnabled"), args[1])));
      }
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean disableSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.disable")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command" +
                        ".description.disable")));
        return true;
      }
      
      final Plugin pluginInstance = Bukkit.getServer().getPluginManager().getPlugin(args[1]);
      
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noPlugin"), args[1])));
      } else if(!pluginInstance.isEnabled()) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.alreadyDisabled"), args[1])));
      } else {
        plugin.controller.disablePlugin(pluginInstance);
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.pluginDisabled"), args[1])));
      }
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean loadSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.load")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command.description.load")));
        return true;
      }
      
      String fileName = args[1] + (args[1].endsWith(".jar") ? "" : ".jar");
      final File pluginToLoad =
              new File("plugins" + File.separator + fileName);
      
      if(!pluginToLoad.exists()) {
        final File unloadedFile = new File("plugins" + File.separator + fileName + ".unloaded");
        
        if(unloadedFile.exists()) {
          unloadedFile.renameTo(pluginToLoad);
        } else {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.error.noSuchFile"), fileName)));
          return true;
        }
      }
      
      PluginDescriptionFile descriptionFile = plugin.controller.getDescription(pluginToLoad);
      
      if(descriptionFile == null) {
        sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
                ".noDescription")));
        return true;
      }
      
      if(Bukkit.getPluginManager().getPlugin(descriptionFile.getName()) != null) {
        sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
                ".alreadyLoaded")));
        return true;
      }
      
      Plugin pluginInstance;
      if((pluginInstance = plugin.controller.loadPlugin(pluginToLoad)) != null) {
        plugin.controller.enablePlugin(pluginInstance);
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.pluginLoaded"), pluginInstance.getName(),
                pluginInstance.getDescription().getVersion())));
      } else {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.pluginNotLoaded"), args[1])));
      }
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean unloadSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.unload")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command" +
                        ".description.unload")));
        return true;
      }
      
      final Plugin pluginInstance = Bukkit.getPluginManager().getPlugin(args[1]);
      
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noPlugin"), args[1])));
      } else {
        if(plugin.controller.unloadPlugin(pluginInstance)) {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.action.pluginUnloaded"), pluginInstance.getName(),
                  pluginInstance.getDescription().getVersion())));
        } else {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.error.pluginNotUnloaded"), args[1])));
        }
      }
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean reloadSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.reload")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command.description.reload")));
        return true;
      }
      
      final Plugin pluginInstance = Bukkit.getPluginManager().getPlugin(args[1]);
      final File pluginFile = plugin.controller.getFile((JavaPlugin) pluginInstance);
      
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noPlugin"), args[1])));
      } else {
        if(plugin.controller.unloadPlugin(pluginInstance) && plugin.controller.loadPlugin(pluginFile) != null) {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.action.pluginReloaded"), pluginInstance.getName())));
        } else {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.error.reloadError"), args[1])));
        }
      }
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean softReloadSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.softreload")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command.description.sreload")));
        return true;
      }
      
      final Plugin pluginInstance = Bukkit.getPluginManager().getPlugin(args[1]);
      
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noPlugin"), args[1])));
      } else if(!pluginInstance.isEnabled()) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.alreadyDisabled"), args[1])));
      } else {
        plugin.controller.disablePlugin(pluginInstance);
        plugin.controller.enablePlugin(pluginInstance);
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.pluginSreloaded"), pluginInstance.getName(),
                pluginInstance.getDescription().getVersion())));
      }
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean detailsSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.details")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command.description.details")));
        return true;
      }
      
      final Plugin pluginInstance = Bukkit.getPluginManager().getPlugin(args[1]);
      
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noPlugin"), args[1])));
        return true;
      } else {
        File pluginFile = plugin.controller.getFile((JavaPlugin) pluginInstance);
        
        sender.sendMessage(PluginController.colorize("&a|--- " + String.format(plugin.language.getString("response.details.name"), args[1]) + " &a---|"));
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString("response.details.status.main"), (pluginInstance.isEnabled() ? plugin.language.getString("response.details.status.enabled") : plugin.language.getString("response.details.status.disabled")))));
        
        if(pluginInstance.getDescription().getDescription() != null) {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.details.description"), pluginInstance.getDescription().getDescription())));
        }
        
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.details.version"), pluginInstance.getDescription().getVersion())));
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.details.main"), pluginInstance.getDescription().getMain())));
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.details.file"), pluginFile.getName())));
        
        final StringBuffer authors = new StringBuffer();
        
        if(pluginInstance.getDescription().getAuthors() != null) {
          if(!pluginInstance.getDescription().getAuthors().isEmpty()) {
            for(final String author : pluginInstance.getDescription().getAuthors()) {
              if(authors.length() > 0) {
                authors.append(", ");
              }
              
              authors.append(author);
            }
          }
        }
        
        if(authors.length() > 0) {
          sender.sendMessage(PluginController.colorize(String.format((pluginInstance.getDescription().getAuthors().size() == 1 ? plugin.language.getString("response.details.author.single") : plugin.language.getString("response.details.author.multiple")), authors)));
        }
        
        if(pluginInstance.getDescription().getWebsite() != null) {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.details.website"), pluginInstance.getDescription().getWebsite())));
        }
      }
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean listSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.list")) {
      boolean versions = false;
      boolean options = false;
      boolean alphabetical = false;
      String search = "";
      
      for(String str : args) {
        if(str.equalsIgnoreCase("-o") || str.equalsIgnoreCase("-options")) {
          options = true;
        }
        if(str.equalsIgnoreCase("-v") || str.equalsIgnoreCase("-verison")) {
          versions = true;
        }
        if(str.equalsIgnoreCase("-a") || str.equalsIgnoreCase("-alphabetical")) {
          alphabetical = true;
        }
        if(str.startsWith("-s:") || str.startsWith("-search:")) {
          String[] strings = str.split("[:]", 2);
          
          if(strings.length != 2) {
            continue;
          }
          
          search = strings[1];
        }
      }
      
      if(options) {
        String[] optionsString =
                plugin.language.getString("command.description.listOptions").split("\n");
        
        for(String str : optionsString) {
          sender.sendMessage(PluginController.colorize(str));
        }
        
        return true;
      }
      
      Plugin[] pluginsArray = Bukkit.getPluginManager().getPlugins();
      StringBuilder enabled = new StringBuilder();
      StringBuilder disabled = new StringBuilder();
      
      ArrayList<Plugin> plugins = new ArrayList<>(Arrays.asList(pluginsArray));
      
      if(!search.isEmpty()) {
        String finalSearch = search;
        plugins.removeIf(pluginInstance -> !pluginInstance.getName().toLowerCase().contains(finalSearch.toLowerCase()));
      }
      
      if(alphabetical) {
        ArrayList<String> pluginsCollection = new ArrayList<>();
        
        for(Plugin pluginInstance : plugins) {
          pluginsCollection.add(pluginInstance.getName());
        }
        
        Collections.sort(pluginsCollection);
        
        plugins = new ArrayList<>();
        
        for(String pluginEntry : pluginsCollection) {
          plugins.add(Bukkit.getPluginManager().getPlugin(pluginEntry));
        }
      }
      
      for(Plugin pluginInstance : plugins) {
        String pluginName = pluginInstance.getName();
        
        if(!search.isEmpty()) {
          int index = pluginName.toLowerCase().indexOf(search.toLowerCase());
          
          if(index != 0) {
            pluginName =
                    pluginName.substring(0, index) + "&5" + pluginName.substring(index,
                            index + search.length()) + "&9&l" + pluginName.substring(index + search.length());
          } else {
            pluginName =
                    "&5" + pluginName.substring(index, index + search.length()) + "&9&l" + pluginName.substring(index + search.length());
          }
        }
        
        String entry = "&9&l" + pluginName + (versions ?
                " &6(" + pluginInstance.getDescription().getVersion() + ")&9&l" : "");
        
        if(pluginInstance.isEnabled()) {
          if(enabled.length() == 0) {
            enabled = new StringBuilder(entry);
          } else {
            enabled.append(", ").append(entry);
          }
        } else {
          if(disabled.length() == 0) {
            disabled = new StringBuilder(entry);
          } else {
            disabled.append(", ").append(entry);
          }
        }
      }
      
      if(enabled.length() > 0) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.list.enabled"), enabled)));
      }
      if(disabled.length() > 0) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.list.disabled"), disabled)));
      }
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean configReloadSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.configreload")) {
      Plugin pluginInstance;
      
      if(args.length < 2) {
        pluginInstance = plugin;
      } else {
        pluginInstance = Bukkit.getPluginManager().getPlugin(args[1]);
      }
      
      if(pluginInstance == null) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noPlugin"), args[1])));
        return true;
      }
      
      pluginInstance.reloadConfig();
      sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
              "response.action.configReload"), pluginInstance.getName())));
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean searchSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.search")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command.description.search")));
        return true;
      }
      
      String paidSign = plugin.language.getString("response.action.search.entry.paid");
      String externalSign = plugin.language.getString("response.action.search.entry.external");
      
      LinkedHashMap<Integer, SpigetClient.ListItem> foundPlugins = new LinkedHashMap<>();
      
      int size = 10;
      int page = (args.length == 3 ? Integer.parseInt(args[2]) : 1);
      
      ArrayList<SpigetClient.ListItem> spigetSearch = SpigetClient.search(args[1], size, page,
              true);
      
      if(spigetSearch != null && spigetSearch.size() > 0) {
        for(SpigetClient.ListItem spigetPlugin : spigetSearch) {
          if(!foundPlugins.containsKey(spigetPlugin.id)) {
            foundPlugins.put(spigetPlugin.id, spigetPlugin);
          }
        }
      } else {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noResults"), args[1])));
        return true;
      }
      
      sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
              "response.action.search.header"), args[1])));
      
      for(Map.Entry<Integer, SpigetClient.ListItem> entry : foundPlugins.entrySet()) {
        SpigetClient.ListItem pl = entry.getValue();
        StringBuilder pluginMessage = new StringBuilder(plugin.language.getString("response" +
                ".action.search.entry.main"));
        boolean canDownload = true;
        
        if(pl.premium) {
          pluginMessage.append(" ").append(paidSign);
          canDownload = false;
        }
        
        if(pl.external) {
          pluginMessage.append(" ").append(externalSign);
          canDownload = false;
        }
        
        sender.sendMessage(PluginController.colorize(String.format(pluginMessage.toString(),
                pl.id, (canDownload ? "&a" : "&c") + "&l" + pl.name)));
      }
      
      sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
              "response.action.search.page.actual"), page)));
      
      if(page > 1) {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.search.page.previous"), label, args[1], (page - 1))));
      }
      
      sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
              "response.action.search.page.next"), label, args[1], (page + 1))));
      
      sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
              "response.action.search.more"), label)));
      
      sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
              "response.action.search.definitions"), paidSign, externalSign)));
      
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.action" +
              ".search.footer")));
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return false;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean moreSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.more")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command.description.more")));
        return true;
      }
      
      int pluginId;
      
      try {
        pluginId = Integer.parseInt(args[1]);
      } catch(NumberFormatException e) {
        ArrayList<SpigetClient.ListItem> searchPlugin = SpigetClient.search(args[1], 1, 1);
        
        if(searchPlugin != null) {
          pluginId = searchPlugin.get(0).id;
        } else {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.error.noResults"), args[1])));
          return true;
        }
      }
      SpigetClient.ListItem spigetPlugin = SpigetClient.get(pluginId);
      
      if(spigetPlugin != null) {
        StringBuilder versions = new StringBuilder();
        
        for(String version : spigetPlugin.testedVersions) {
          if(versions.length() > 0) {
            versions.append(", ");
          }
          
          versions.append(version);
        }
        
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.more.header"), spigetPlugin.id, spigetPlugin.name)));
        
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.more.versions"), versions)));
        
        if(spigetPlugin.premium) {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.action.more.premium"), spigetPlugin.price, spigetPlugin.currency)));
        }
        
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.more.contributors"), spigetPlugin.contributors)));
        
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.more.tag"), spigetPlugin.tag)));
        
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.more.likesRating"), spigetPlugin.likes, spigetPlugin.rating.average)));
        
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.more.downloads"), spigetPlugin.downloads)));
        
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.more.url"), "https://spigotmc.org/resources/" + spigetPlugin.id)));
        
        if(!spigetPlugin.premium && !spigetPlugin.external) {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.action.more.download"), label, spigetPlugin.id)));
        }
        
        sender.sendMessage(PluginController.colorize(plugin.language.getString(
                "response.action.more.footer")));
      } else {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noResults"), args[1])));
        return true;
      }
    } else {
      sender.sendMessage(PluginController.colorize(plugin.language.getString("response.error" +
              ".noPermission")));
    }
    
    return true;
  }
  
  /**
   * @param sender command sender
   * @param label  used command label
   * @param args   arguments used
   * @return <code>true</code>, if command was successful
   */
  private boolean downloadSubcommand(CommandSender sender, String label, String[] args) {
    if(sender.hasPermission("plugincontroller.download")) {
      if(args.length < 2) {
        sender.sendMessage(PluginController.colorize("&7/&6" + label + " " + plugin.language.getString(
                "command.description.download")));
        return true;
      }
      
      int pluginId;
      
      try {
        pluginId = Integer.parseInt(args[1]);
      } catch(NumberFormatException e) {
        ArrayList<SpigetClient.ListItem> searchPlugin = SpigetClient.search(args[1], 1, 1);
        
        if(searchPlugin != null) {
          pluginId = searchPlugin.get(0).id;
        } else {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.error.noResults"), args[1])));
          return true;
        }
      }
      SpigetClient.ListItem spigetPlugin = SpigetClient.get(pluginId);
      
      if(spigetPlugin != null) {
        if(spigetPlugin.premium) {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.error.download.premium"), spigetPlugin.price, spigetPlugin.currency,
                  "https://spigotmc.org/resources/" + spigetPlugin.id)));
          return true;
        }
        if(spigetPlugin.external) {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.error.download.external"),
                  "https://spigotmc.org/" + spigetPlugin.file.url)));
          return true;
        }
        
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.action.download.downloading"), spigetPlugin.name)));
        
        if(SpigetClient.download(SpigetClient.spigetURL + "resources/" + spigetPlugin.id +
                "/download", "plugins/" + spigetPlugin.name + ".jar")) {
          sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                  "response.action.download.downloaded"), spigetPlugin.name,
                  spigetPlugin.name + ".jar")));
          
          File pluginFile = new File("plugins" + File.separator + spigetPlugin.name + ".jar");
          
          Plugin pluginInstance;
          if((pluginInstance = plugin.controller.loadPlugin(pluginFile)) != null) {
            plugin.controller.enablePlugin(pluginInstance);
            sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                    "response.action.pluginLoaded"), pluginInstance.getName(),
                    pluginInstance.getDescription().getVersion())));
          } else {
            sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                    "response.error.pluginNotLoaded"), spigetPlugin.name)));
          }
        }
      } else {
        sender.sendMessage(PluginController.colorize(String.format(plugin.language.getString(
                "response.error.noResults"), args[1])));
        return true;
      }
    }
    
    return true;
  }
}
