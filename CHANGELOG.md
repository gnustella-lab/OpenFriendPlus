# Changelog

## OpenFriend Plus 1.0.0

Initial fork release preparation.

### Added

- OpenFriend Plus metadata and isolated `openfriendplus` mod id.
- Java package rename to `dev.gnustella.openfriendplus`.
- Renamed mixin configs, access widener, and asset namespace.
- Local config store with streamer/privacy, toast, refresh interval, diagnostics, and local join port settings.
- In-game Settings tab.
- Diagnostics tab for Core/IPC/auth/config/data status.
- Blocks tab in the overlay.
- Streamer/privacy formatter.
- Configurable local join port.
- English and pt-BR language resources.
- README, NOTICE, SECURITY, and Modrinth-oriented release notes.

### Changed

- UI title now uses OpenFriend Plus branding.
- Default overlay panel is larger and includes fork/version footer text.
- Data directory is separated from the original OpenFriendMod.
- Root buildAll targets included Fabric modules instead of absent loader modules.

### Credits

Based on OpenFriendMod by ZSHARE, licensed under MIT.
