# Commands

- `/plugincontroller` (`/pc`, `/plc`)
    - `enable <plugin>` (`e`) - enables a plugin
    - `disable <plugin>` (`d`) - disables a plugin
    - `load <jar>` (`l`) - loads plugin from given .jar file
    - `unload <plugin>` (`u`) - unloads a plugin from a server
    - `reload <plugin>` (`r`, `rl`) - unloads and loads a plugin
    - `sreload <plugin>` (`s`, `srl`, `softreload`) - disables and enables a plugin
    - `details <plugin>` (`info`, `show`, `i`) - shows detailed information about a plugin
    - `list [options]` (`ls`) - shows list of plugins with given options
        - `-v`, `-version` - shows plugins with versions
        - `-o`, `-options` - lists options
        - `-a`, `-alphabetical` - lists plugins in alphabetical order
        - `-s:name`, `-search:name` - lists plugins containig the given name
    - `configreload [plugin]` (`cr`) - reloads a plugins's config file
    - `search <query> [page]` - looks for a plugin in the Spigot repository
    - `more <id or name>` - shows information about a plugin from the Spigot repository
    - `download <id or name>` - downloads plugin from Spigot repository
    
`[argument]` is optional, `<argument>` is required argument. Terms in `()` are aliases. 