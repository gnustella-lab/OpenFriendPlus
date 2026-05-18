# OpenFriend Plus

Summary:
Unofficial enhanced fork of OpenFriendMod with better UX, diagnostics, privacy options, translations, and modpack-friendly settings.

## Description

OpenFriend Plus is an unofficial enhanced fork of OpenFriendMod.

It keeps the core idea of bringing a Friends List experience to older Minecraft versions, while adding quality-of-life improvements for regular players, streamers, modpacks, and community servers.

## Features

- Friends List-style overlay
- Search players by gamertag
- Send, accept, decline, and manage friend requests
- Join friends through the in-game flow
- In-game Settings tab
- Diagnostics tab for Core, IPC, auth, config, and connection status
- Streamer/privacy mode
- pt-BR translation
- Configurable or randomized local join port
- Local auth/cache clearing
- Modpack-friendly config defaults
- Blocks tab for blocked users

## Privacy and local data

OpenFriend Plus uses a bundled local OpenFriend Core process for Microsoft/Xbox/Minecraft-related friend and join functionality. Authentication data may be stored locally on your device by the Core component.

The fork adds clearer diagnostics and local data controls so users can better understand what is running and where files are stored.

## Disclaimer

OpenFriend Plus is an independent community fork based on OpenFriendMod.

It is not affiliated with, endorsed by, sponsored by, or connected to ZSHARE, zerozshare, Microsoft, Mojang, Xbox, Minecraft, or Modrinth.

## Credits

Based on OpenFriendMod by ZSHARE, licensed under MIT.

## First Release Changelog

OpenFriend Plus 1.0.0

Initial public fork release.

Added:
- New OpenFriend Plus branding and isolated `openfriendplus` mod id
- In-game Settings tab
- Diagnostics tab
- Streamer/privacy mode
- pt-BR translation
- Configurable and randomized local join port
- Modpack-friendly config defaults
- Improved notices and error messages
- Local auth/cache clearing

Changed:
- Project metadata now points to OpenFriend Plus resources
- Data directory is separated from the original OpenFriendMod
- UI title and strings updated for the fork

Credits:
- Based on OpenFriendMod by ZSHARE, licensed under MIT.
