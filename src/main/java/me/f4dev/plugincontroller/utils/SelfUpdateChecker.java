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

public class SelfUpdateChecker implements  Runnable {
  private PluginController plugin;
  private String currentVersion;
  private String readUrl = "https://raw.githubusercontent.com/kovansky/PluginController/master/version.txt";
  
  /**
   * Class constructor
   *
   * @param plugin PluginController instance
   */
  public SelfUpdateChecker(PluginController plugin) {
    this.plugin = plugin;
    currentVersion = plugin.getDescription().getVersion();
  }
  
  /**
   * Checks if plugin has new version
   */
  public void run() {
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
          int uPatch;
          
          String uChannel = "stable";
          
          if(split[2].contains("-")) {
            String uPatchSplit[] = split[2].split("-");
            uChannel = uPatchSplit[1];
            uPatch = Integer.parseInt(uPatchSplit[0]);
          } else {
            uPatch = Integer.parseInt(split[2]);
          }
          
          int cMajor = Integer.parseInt(cVersion[0]);
          int cMinor = Integer.parseInt(cVersion[1]);
          int cPatch;
          
          if(cVersion[2].contains("-")) {
            String[] cPatchSplit = cVersion[2].split("-");
            cPatch = Integer.parseInt(cPatchSplit[0]);
          } else {
            cPatch = Integer.parseInt(cVersion[2]);
          }
          
          if(plugin.getConfig().getString("updater.channel").equalsIgnoreCase(uChannel)) {
            if(updateAvailable(uMajor, uMinor, uPatch, cMajor, cMinor, cPatch)) {
              logger.info(String.format(plugin.language.getString("response.action" +
                      ".updateAvailable"), uChannel, uMajor, uMinor, uPatch, cMajor, cMinor, cPatch));
              
              plugin.updateMessage = String.format("&a" + plugin.language.getString("response.action" +
                      ".updateAvailable"), "&c" + uChannel + "&a", "&6" + uMajor, uMinor, uPatch +
                      "&a", "&6" + cMajor, cMinor, cPatch + "&a");
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
  
  /**
   * Checks if remote version is newer than local
   *
   * @param uMajor latest major version
   * @param uMinor latest minor version
   * @param uPatch latest patch
   * @param cMajor local major version
   * @param cMinor local minor version
   * @param cPatch local patch
   * @return true, if latest version is newer than local
   */
  private boolean updateAvailable(int uMajor, int uMinor, int uPatch, int cMajor, int cMinor,
                                  int cPatch) {
    return uMajor > cMajor || uMinor > cMinor || uPatch > cPatch;
  }
}
