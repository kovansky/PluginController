/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller.utils;

import me.f4dev.plugincontroller.PluginController;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class Controller {
  
  PluginController plugin;
  
  public Controller(PluginController plugin) {
    this.plugin = plugin;
  }
  
  public File getFile(final JavaPlugin javaPlugin) {
    Field file;
    
    try {
      file = JavaPlugin.class.getDeclaredField("file");
      file.setAccessible(true);
      return (File) file.get(javaPlugin);
    } catch(Exception e) {
      e.printStackTrace();
    }
    
    return null;
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
  
  public Plugin loadPlugin(final File pluginFile) {
    Plugin pluginInstance;
    
    try {
      pluginInstance = Bukkit.getPluginManager().loadPlugin(pluginFile);
      
      try {
        pluginInstance.onLoad();
      } catch(final Exception e) {
        plugin.getLogger().info(String.format(plugin.language.getString("response.error" +
                ".failedOnLoad"), plugin.getName()));
        e.printStackTrace();
      }
      
      return pluginInstance;
    } catch(InvalidPluginException | InvalidDescriptionException | UnknownDependencyException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  public boolean unloadPlugin(Plugin pluginInstance, boolean reloadDependents) {
    String pluginName = pluginInstance.getName();
    PluginManager pluginManager = Bukkit.getPluginManager();
    List<Plugin> plugins;
    Map<String, Plugin> names;
    Map<String, Command> commands;
    SimpleCommandMap commandMap;
    ArrayList<Plugin> reload = new ArrayList<>();
    
    if(reloadDependents) {
      for(final Plugin pl : pluginManager.getPlugins()) {
        final List<String> dependList = pl.getDescription().getDepend();
        if(dependList != null) {
          for(final String depend : dependList) {
            if(depend.equals(pluginName)) {
              if(!reload.contains(pl)) {
                reload.add(pl);
                unloadPlugin(pl, false);
              }
            }
          }
        }
        
        final List<String> softDependList = pl.getDescription().getSoftDepend();
        if(softDependList != null) {
          for(final String softDepend : softDependList) {
            if(softDepend.equals(pluginName)) {
              if(!reload.contains(pl)) {
                reload.add(pl);
                unloadPlugin(pl, false);
              }
            }
          }
        }
      }
    }
    
    for(Plugin pl : reload) {
      Bukkit.getServer().broadcastMessage(pl.getName() + "\n");
    }
    
    try {
      Field pluginsField;
      Field lookupNamesField;
      Field commandMapField;
      Field knownCommandsField;
      
      pluginsField = pluginManager.getClass().getDeclaredField("plugins");
      lookupNamesField = pluginManager.getClass().getDeclaredField("lookupNames");
      commandMapField = pluginManager.getClass().getDeclaredField("commandMap");
      knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
      
      pluginsField.setAccessible(true);
      lookupNamesField.setAccessible(true);
      commandMapField.setAccessible(true);
      knownCommandsField.setAccessible(true);
      
      plugins = (List<Plugin>) pluginsField.get(pluginManager);
      names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);
      commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
      commands = (Map<String, Command>) knownCommandsField.get(commandMap);
    } catch(IllegalAccessException | NoSuchFieldException e) {
      e.printStackTrace();
      return false;
    }
    
    if(commandMap != null) {
      synchronized(commandMap) {
        final Iterator<Map.Entry<String, Command>> iterator = commands.entrySet().iterator();
        while(iterator.hasNext()) {
          Map.Entry<String, Command> entry = iterator.next();
          if(entry.getValue() instanceof PluginCommand) {
            PluginCommand command = (PluginCommand) entry.getValue();
            if(command.getPlugin() == pluginInstance) {
              command.unregister(commandMap);
              iterator.remove();
            }
          }
        }
      }
    }
    
    synchronized(pluginManager) {
      if(plugins != null && plugins.contains(pluginInstance)) {
        plugins.remove(pluginInstance);
      }
      
      if(names != null && names.containsKey(pluginName)) {
        names.remove(pluginName);
      }
    }
    
    final JavaPluginLoader javaPluginLoader = (JavaPluginLoader) plugin.getPluginLoader();
    Field loaders = null;
    
    try {
      loaders = javaPluginLoader.getClass().getDeclaredField("loaders");
      loaders.setAccessible(true);
    } catch(final Exception e) {
      e.printStackTrace();
    }
    
    try {
      final Map<String, ?> loaderMap = (Map<String, ?>) loaders.get(javaPluginLoader);
      loaderMap.remove(pluginInstance.getDescription().getName());
    } catch(final Exception e) {
      e.printStackTrace();
    }
    
    ClassLoader cl = plugin.getClass().getClassLoader();
    
    try {
      ((URLClassLoader) cl).close();
    } catch(IOException e) {
      e.printStackTrace();
    }
    
    System.gc();
    
    if(reloadDependents) {
      for(int i = 0; i < reload.size(); i++) {
        enablePlugin(loadPlugin(getFile((JavaPlugin) reload.get(i))));
      }
    }
    
    File loaded = getFile((JavaPlugin) pluginInstance);
    File unloaded = new File(getFile((JavaPlugin) pluginInstance) + ".unloaded");
    
    return loaded.renameTo(unloaded);
  }
}
