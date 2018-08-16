# Plugin Controller
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Version: 1.0.1-BETA](https://img.shields.io/badge/Version-1.0.1--BETA-brightgreen.svg)](https://gitlab.com/kovansky/PluginController)
[![Build Status](https://travis-ci.org/kovansky/PluginController.svg?branch=master)](https://travis-ci.org/kovansky/PluginController)

This plugin is rewrite of [Plugin Manager Reloaded](https://www.spigotmc.org/resources/plugin-manager-reloaded.7144/)
with new features - downloading plugins from Spigot. Made by [F4 Developer](http://f4dev.me).

## Found a bug?
[Report it here](https://github.com/kovansky/PluginController/issues/new?template=bug_report.md)

## Help to translate
Want to help with translation? Go [here](https://www.transifex.com/f4-developer/plugincontroller/)

## Changelog

### 1.0.1-BETA
##### Languages
- Created Polish (pl_PL) language file

### 1.0.0-BETA
#### First beta release
Everything is new :smiley: 

##### Commands
- Create /plugincommander command [/pc, /plc]
  - /plugincontroller enable,e - enables plugin
  - /plugincontroller disable,d - disables plugin
  - /plugincontroller load,l - loads plugin from file
  - /plugincontroller unload,u - unloads plugin from server
  - /plugincontroller reload,r,rl - unloads and loads plugin
  - /plugincontroller sreload,softreload,sr,srl - disables and enables plugin
  - /plugincontroller details,show,info,i - informations about loaded plugin
  - /plugincontroller list,ls - lists enabled and disabled plugins
  - /plugincontroller configreload,cr - reloads config of plugin
  - /plugincontroller search - search for plugins on Spigot plugins repository
  - /plugincontroller more - displays information about plugin from Spigot repository
  - /plugincontroller download - downloads plugin from Spigot repository
##### Listeners
- Create CommandPreprocessListener
- Create JoinNotifyListener - notify about updates on player join
##### Tab Completer
- Create PluginControllerTabCompleter
##### Utils
- Create Controller class - to manage plugins (disable, enable, unload, load etc)
- Create PluginListManager class - to manage disabled plugins in list.yml file
- Create SelfUpdateChecker class - check for updates when server starts
- Create SpigetClient class - to connect with Spiget.org API
##### Others
- On reload disable plugins disabled earlier