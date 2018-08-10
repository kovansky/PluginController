/*
 * This file is part of PluginController project by F4 Developer which is released under GNU General Public License v3.0.
 * See file LICENSE for full license details.
 */
package me.f4dev.plugincontroller.utils;

import me.f4dev.plugincontroller.PluginController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

public class SelfUpdateChecker {
  private PluginController plugin;
  private String currentVersion;
  private String readUrl = "https://gitlab.com/kovansky/PluginController/raw/feature/plc-4/version.txt";
  
  public SelfUpdateChecker(PluginController plugin) {
    this.plugin = plugin;
    currentVersion = plugin.getDescription().getVersion();
  }
  
  public void startUpdateCheck() {
    if(plugin.getConfig().getBoolean("updater.startCheck")) {
      Logger logger = plugin.getLogger();
      
      try {
        logger.info("Checking for a new version...");
        URL url = new URL(readUrl);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
        String str;
        
        while((str = bufferedReader.readLine()) != null) {
          String[] split = str.split("\\.");
          String[] cVersion = currentVersion.split("\\.");
          
          int uMajor = Integer.parseInt(split[0]);
          int uMinor = Integer.parseInt(split[1]);
          int uRev;
          
          String uChannel = "stable";
          
          if(split[2].contains("-")) {
            String uRevSplit[] = split[2].split("-");
            uChannel = uRevSplit[1];
            uRev = Integer.parseInt(uRevSplit[0]);
          } else {
            uRev = Integer.parseInt(split[2]);
          }
  
          int cMajor = Integer.parseInt(cVersion[0]);
          int cMinor = Integer.parseInt(cVersion[1]);
          int cRev;
          
          if(cVersion[2].contains("-")) {
            String[] cRevSplit = cVersion[2].split("-");
            cRev = Integer.parseInt(cRevSplit[0]);
          } else {
            cRev = Integer.parseInt(cVersion[2]);
          }
          
          if(plugin.getConfig().getString("updater.channel").equalsIgnoreCase(uChannel)) {
            if(updateAvailable(uMajor, uMinor, uRev, cMajor, cMinor, cRev)) {
              logger.info(String.format(plugin.language.getString("response.action" +
                      ".updateAvailable"), uChannel, uMajor, uMinor, uRev, cMajor, cMinor, cRev));
              plugin.updateMessage = String.format("&a" + plugin.language.getString("response.action" +
                      ".updateAvailable"), "&c" + uChannel + "&a", "&6" + uMajor, uMinor, uRev +
                      "&a", "&6" + cMajor, cMinor, cRev + "&a");
            } else {
              logger.info("No updates.");
            }
          } else {
            logger.info("No updates.");
          }
        }
        bufferedReader.close();
      } catch(IOException e) {
        logger.severe("The SelfUpdateCheck URL is invalid! Please inform developer " +
                "(plugincontroller@f4dev.me)");
      }
    }
  }
  
  private boolean updateAvailable(int uMajor, int uMinor, int uRev, int cMajor, int cMinor,
                                 int cRev) {
    return uMajor > cMajor || uMinor > cMinor || uRev > cRev;
  }
}
