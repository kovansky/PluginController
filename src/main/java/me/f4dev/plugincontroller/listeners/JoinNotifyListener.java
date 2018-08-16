/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */
package me.f4dev.plugincontroller.listeners;

import me.f4dev.plugincontroller.PluginController;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinNotifyListener implements Listener {
  PluginController plugin;
  
  /**
   * Class constructor
   *
   * @param plugin PluginController instance
   */
  public JoinNotifyListener(PluginController plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    if(!plugin.getConfig().getBoolean("updater.notify")) {
      return;
    }
    
    Player player = event.getPlayer();
    
    if(player.hasPermission("pluginmanager.notify") && plugin.updateMessage != null) {
      player.sendMessage(PluginController.colorize(plugin.updateMessage));
    }
  }
}
