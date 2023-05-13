# DataMigrator

<img src="https://slocraft.eu/slocraft-logo-512.png" width=124 height=124 align="right"/>

DataMigrator was developed for use on the [SloCraft](https://slocraft.eu) network.

Please keep in mind that the plugin has not been updated since May 2022.

### Description

DataMigrator was developed to help migrate offline mode player data from various plugins to online mode data. It provides functionality for migrating data from AutoRank, LuckPerms, GriefPrevention, and a Nickname plugin.

### Features

- Migration of player data (name based)
  - Migration of permissions (LuckPerms)
  - Migration of playtime (AutoRank)
  - Migration of claimblocks (GriefPrevention)
  - Migration of old rank names to new names
  - Migration of nickname data
- Discord embed notification on attempted migration
  <br/><img src="https://slocraft.eu/github-pictures/migrator-demo.png" width=400 />
- Manual migration command
- Automatic migration trigger on server join
- Logging to local sqlite database

### Dependencies

DataMigrator requires a PaperMc server version 1.18.2 or higher (not tested).

### Configuration

On startup a configuration file is loaded: `config.conf`. This file is generated automatically on first startup and can be found in the plugin's data folder.

- `config.conf` contains settings and language strings for the plugin.

### Usage

  - `/migration status [playerName]` - check your/other players migration status.
  - `/migration migrate <currentPlayerName> <oldPlayerName> <overridePrevious>` - trigger a manual migration from and to specified names.
  - `/migration reload` - reloads plugin configuration files.

### Permissions

  - `datamigrator.status` - Allows checking of own migration status.
  - `datamigrator.status.others` - Allows checking of other players migration status.
  - `datamigrator.admin` - Allows usage of admin commands.
  - `datamigrator.attempted` - Automatically assigned to any player where migration was attempted.
