/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller;

import me.f4dev.plugincontroller.listeners.CommandPreprocessListener;
import me.f4dev.plugincontroller.listeners.JoinNotifyListener;
import me.f4dev.plugincontroller.utils.Controller;
import me.f4dev.plugincontroller.utils.PluginListManager;
import me.f4dev.plugincontroller.utils.SelfUpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class PluginController extends JavaPlugin {
  public FileConfiguration language;
  public PluginListManager pluginListManager;
  public Controller controller;
  public String updateMessage = null;
  
  @Override
  public void onEnable() {
    initConfig();
    
    // Utils
    pluginListManager = new PluginListManager(this);
    controller = new Controller(this);
    
    // Commands, listeners & other related with game
    PluginControllerCommand pluginControllerCommand = new PluginControllerCommand(this);
    PluginControllerTabCompleter pluginControllerTabCompleter =
            new PluginControllerTabCompleter(this);
    
    JoinNotifyListener joinNotifyListener = new JoinNotifyListener(this);
    CommandPreprocessListener commandPreprocessListener = new CommandPreprocessListener(this);
    
    // Update checker
    Thread selfUpdateChecker = new Thread(new SelfUpdateChecker(this));
    selfUpdateChecker.start();
    
    // Disable plugins disabled before reload
    File listFile = new File(getDataFolder(), "list.yml");
    if(listFile.exists()) {
      FileConfiguration disabledPlugins = YamlConfiguration.loadConfiguration(listFile);
      boolean isReload = (disabledPlugins.contains("reload") && disabledPlugins.getBoolean(
              "reload"));
      if(isReload) {
        disabledPlugins.set("reload", null);
        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
          List<String> disabledList = disabledPlugins.getStringList("disabled");
          for(String pl : disabledList) {
            controller.disablePlugin(Bukkit.getPluginManager().getPlugin(pl));
          }
        });
      } else {
        disabledPlugins.set("disabled", null);
      }
      
      try {
        disabledPlugins.save(listFile);
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    
    getLogger().info("Plugin Controller has been enabled.");
  }
  
  @Override
  public void onDisable() {
    language = null;
    getLogger().info("Plugin Controller has been disabled.");
  }
  
  /**
   * Copies resources
   */
  private void initConfig() {
    if(!new File(getDataFolder(), "config.yml").exists()) {
      saveResource("config.yml", false);
    }
    
    String[] langArray = {"en_US"};
    for(String lang : langArray) {
      if(!new File(getDataFolder() + File.separator + "languages", lang + ".yml").exists()) {
        saveResource("languages" + File.separator + lang + ".yml", false);
      }
    }
    
    language = YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator +
            "languages", this.getConfig().getString("language") + ".yml"));
  }
  
  /**
   * Colorizes given string into Minecraft colors
   *
   * @param string string to colorize
   * @return colorized string
   */
  public static String colorize(String string) {
    if(string != null) {
      return ChatColor.translateAlternateColorCodes('&', string);
    }
    
    return null;
  }
}
