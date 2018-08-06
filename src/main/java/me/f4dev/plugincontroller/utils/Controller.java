/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */

package me.f4dev.plugincontroller.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Controller {
//  public void enablePlugin( final Plugin plugin )
//  {
//    Bukkit.getPluginManager( ).enablePlugin( plugin );
//    File file = new File( this.plugin.getDataFolder( ), "List.yml");
//    if( file.exists( ) )
//    {
//      FileConfiguration disabled = YamlConfiguration.loadConfiguration( file );
//      List<String> list = disabled.getStringList( "Disabled" );
//      if( list.contains( plugin.getName( ) ) )
//        list.remove( plugin.getName( ) );
//      disabled.set( "Disabled", list );
//
//      try {
//        disabled.save( file );
//      } catch( Exception e ) {
//        e.printStackTrace( );
//      }
//    }
//  }
  
  public static void enablePlugin(final Plugin plugin) {
    Bukkit.getPluginManager().enablePlugin(plugin);
    
  }
}
