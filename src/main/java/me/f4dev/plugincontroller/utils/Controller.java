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
import org.bukkit.event.Event;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
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
  
    if(!pluginFile.exists()) {
      File unloadedFile = new File(pluginFile.getPath() + ".unloaded");
    
      if(unloadedFile.exists()) {
        unloadedFile.renameTo(pluginFile);
      }
    }
    
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
  
  public boolean unloadPlugin(Plugin pluginInstance) {
    String plName = pluginInstance.getName();
    PluginManager pluginManager = Bukkit.getPluginManager();
    SimpleCommandMap commandMap = null;
    List<Plugin> plugins = null;
    List<File> reload = null;
    Map<String, Plugin> names = null;
    Map<String, Command> commands = null;
    Map<Event, SortedSet<RegisteredListener>> listeners = null;
    boolean reloadlisteners = true;
    
    for(final Plugin pl : pluginManager.getPlugins()) {
      final List<String> dependList = pl.getDescription().getDepend();
      if(dependList != null) {
        for(final String depend : dependList) {
          if(depend.equals(plName)) {
            disablePlugin(pl);
          }
        }
      }
      
      final List<String> softDependList = pl.getDescription().getSoftDepend();
      if(softDependList != null) {
        for(final String softDepend : softDependList) {
          if(softDepend.equals(plName)) {
            if(!reload.contains(pl)) {
              reload.add(getFile((JavaPlugin) pl));
              unloadPlugin(pl);
            }
          }
        }
      }
    }
    
    disablePlugin(pluginInstance);
    
    if(pluginManager != null) {
      try {
        Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
        Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
        Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
        Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
        
        pluginsField.setAccessible(true);
        lookupNamesField.setAccessible(true);
        commandMapField.setAccessible(true);
        knownCommandsField.setAccessible(true);
        
        plugins = (List<Plugin>) pluginsField.get(pluginManager);
        names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);
        commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
        commands = (Map<String, Command>) knownCommandsField.get(commandMap);
        
        try {
          Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
          listenersField.setAccessible(true);
          listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
        } catch(Exception e) {
          reloadlisteners = false;
        }
      } catch(NoSuchFieldException | IllegalAccessException e) {
        e.printStackTrace();
        return false;
      }
    }
    
    if(plugins != null && plugins.contains(pluginInstance)) {
      plugins.remove(pluginInstance);
    }
    
    if(names != null && names.containsKey(plName)) {
      names.remove(plName);
    }
    
    if(listeners != null && reloadlisteners) {
      for(SortedSet<RegisteredListener> set : listeners.values()) {
        set.removeIf(value -> value.getPlugin() == pluginInstance);
      }
    }
    
    if(commandMap != null) {
      for(Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
        Map.Entry<String, Command> entry = it.next();
        if(entry.getValue() instanceof PluginCommand) {
          PluginCommand c = (PluginCommand) entry.getValue();
          if(c.getPlugin() == pluginInstance) {
            c.unregister(commandMap);
            it.remove();
          }
        }
      }
    }
  
    if(reload != null) {
      for(File pl : reload) {
        loadPlugin(pl);
      }
    }
    
    final JavaPluginLoader javaPluginLoader = (JavaPluginLoader) pluginInstance.getPluginLoader();
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
    
    ClassLoader cl = pluginInstance.getClass().getClassLoader();
    
    if(cl instanceof URLClassLoader) {
      try {
        Field pluginField = cl.getClass().getDeclaredField("plugin");
        Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
        
        pluginField.setAccessible(true);
        pluginInitField.setAccessible(true);
        
        pluginField.set(cl, null);
        pluginInitField.set(cl, null);
      } catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
      }
      
      try {
        ((URLClassLoader) cl).close();
      } catch(IOException e) {
        Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);
      }
    }
    
    System.gc();
    
    File loaded = getFile((JavaPlugin) pluginInstance);
    File unloaded = new File(getFile((JavaPlugin) pluginInstance) + ".unloaded");
    
    return loaded.renameTo(unloaded);
  }
}
