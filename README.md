# OpenFriend Plus

OpenFriend Plus is an **unofficial enhanced fork of OpenFriendMod**. It is not affiliated with, endorsed by, sponsored by, or connected to ZSHARE, zerozshare, Microsoft, Mojang, Xbox, Minecraft, or Modrinth.

The fork keeps the original idea of bringing a Friends List-style flow to Minecraft versions that do not have it, while adding quality-of-life features for players, streamers, modpacks, and pack maintainers.

## Features

- Friends List-style overlay for Minecraft Java.
- Search players by gamertag.
- Send, accept, decline, remove, and block friends.
- Join friends through the in-game flow.
- Open a single-player world to friends when supported by the loader/version.
- OpenFriend Plus branding with isolated technical identifiers:
  - mod id: `openfriendplus`
  - Java package: `dev.gnustella.openfriendplus`
  - assets namespace: `openfriendplus`
  - data directory: `openfriendplus`
- In-game Settings tab for local preferences.
- Diagnostics tab for Core, IPC, auth, counts, data directory, config file, and join address.
- Streamer/privacy helpers for masking names, profile IDs, addresses, and device codes.
- Configurable local join port.
- Blocks tab so blocked players are visible in the UI.
- English and pt-BR language resources.
- Modpack-friendly local config defaults.

## Privacy and local data

OpenFriend Plus uses the bundled OpenFriend Core process inherited from the original MIT-licensed base. The mod communicates with that local Core process over JSON-RPC stdio. Authentication data may be stored locally by the Core component in the OpenFriend Plus data directory.

The fork separates its data from the original OpenFriendMod by using the `openfriendplus` mod id and data directory. It also includes diagnostics and local settings so users can better understand what is running and where files are stored.

Use this software only with accounts you own and servers/worlds you operate or have permission to use.

## Installation

1. Download the jar for your Minecraft loader and version.
2. Put it in your `mods/` directory.
3. Start Minecraft.
4. Open the Friends/OpenFriend Plus button.
5. Complete Microsoft/device-code sign-in if prompted.

## For modpack authors

- OpenFriend Plus is client-side.
- Do not install it as a required server mod unless a loader-specific build explicitly documents server support.
- Fabric builds do not require Fabric API unless a future optional integration explicitly documents it.
- The default config is created under the OpenFriend Plus data directory as `config.json`.
- Pack defaults can be shipped as `config/openfriendplus-defaults.json` or `config/openfriendplus.json`; the mod imports those only when its local config does not already exist.
- The default local join address is `127.0.0.1:25577` and can be changed in the Settings tab.
- `randomizeJoinPort`, toast toggles, `quietFirstBoot`, and language settings are available for pack defaults.
- The fork is designed to avoid conflicts with the original mod by using a separate mod id, package, assets namespace, mixin names, access widener name, and data directory.

## Build notes

The repository contains Fabric-oriented root modules and legacy/version-specific source trees. The root `buildAll` task currently targets the included Fabric module to avoid depending on Forge/NeoForge modules that are not included in root settings.

The bundled Core binary keeps the original OpenFriend Core resource path and binary names (`openfriend-*`). This is intentional for the first OpenFriend Plus release because the Core itself has not been rebranded or forked here.

## License and credits

OpenFriend Plus is licensed under MIT and is based on OpenFriendMod by ZSHARE.

- Original project: <https://github.com/zerozshare/OpenFriendMod>
- Fork source: <https://github.com/gnustella-lab/OpenFriendPlus>
- Modrinth slug: `openfriend-plus`
