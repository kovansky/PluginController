# Plugin Controller

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Version: 1.0.0](https://img.shields.io/badge/Version-1.0.0-brightgreen.svg)](https://github.com/kovansky/PluginController)
[![Spigot versions: 1.7.2-1.13](https://img.shields.io/badge/Spigot-1.7.2--1.13-green.svg)](https://spigotmc.org/resources/59957)
[![Spigot downloads](https://img.shields.io/badge/dynamic/json.svg?label=Spigot downloads&url=https%3A%2F%2Fapi.spiget.org%2Fv2%2Fresources%2F59957&query=%24.downloads&colorB=orange)](https://spigotmc.org/resources/59957)
[![Build Status](https://travis-ci.org/kovansky/PluginController.svg?branch=master)](https://travis-ci.org/kovansky/PluginController)

This plugin is rewrite of [Plugin Manager Reloaded](https://www.spigotmc.org/resources/plugin-manager-reloaded.7144/) with new features - downloading plugins from Spigot. Made by [F4 Developer](http://f4dev.me).

## Full documentation, usage of plugin

Go to [docs](http://plugincontroller.f4dev.me).

## Tested Spigot versions

- 1.13
- 1.12.2
- 1.12.1
- 1.12
- 1.11.2
- 1.11.1
- 1.11
- 1.10.2
- 1.10
- 1.9
- 1.8.8
- 1.8
- 1.7.9
- 1.7.2

## Found a bug?

[Report it here](https://github.com/kovansky/PluginController/issues/new?template=bug_report.md)

## Help to translate

Want to help with translation? Go [here](https://www.transifex.com/f4-developer/plugincontroller/)

## Changelog

### 1.0.0
First stable release :smiley:

**Docs**
* Created docs files

**plugin.yml**
* Added permissions to plugin.yml file

### 1.0.1-BETA

**Languages**

* Created Polish \(pl\_PL\) language file

### 1.0.0-BETA

#### First beta release

Everything is new :smiley:

**Commands**

* Create /plugincommander command \[/pc, /plc\]
  * /plugincontroller enable,e - enables plugin
  * /plugincontroller disable,d - disables plugin
  * /plugincontroller load,l - loads plugin from file
  * /plugincontroller unload,u - unloads plugin from server
  * /plugincontroller reload,r,rl - unloads and loads plugin
  * /plugincontroller sreload,softreload,sr,srl - disables and enables plugin
  * /plugincontroller details,show,info,i - informations about loaded plugin
  * /plugincontroller list,ls - lists enabled and disabled plugins
  * /plugincontroller configreload,cr - reloads config of plugin
  * /plugincontroller search - search for plugins on Spigot plugins repository
  * /plugincontroller more - displays information about plugin from Spigot repository
  * /plugincontroller download - downloads plugin from Spigot repository

    **Listeners**
* Create CommandPreprocessListener
* Create JoinNotifyListener - notify about updates on player join

  **Tab Completer**

* Create PluginControllerTabCompleter

  **Utils**

* Create Controller class - to manage plugins \(disable, enable, unload, load etc\)
* Create PluginListManager class - to manage disabled plugins in list.yml file
* Create SelfUpdateChecker class - check for updates when server starts
* Create SpigetClient class - to connect with Spiget.org API

  **Others**

* On reload disable plugins disabled earlier

