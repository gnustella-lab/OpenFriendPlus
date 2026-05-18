# Changelog

## OpenFriend Plus 1.0.0

Initial fork release preparation.

### Added

- OpenFriend Plus metadata and isolated `openfriendplus` mod id.
- Java package rename to `dev.gnustella.openfriendplus`.
- Renamed mixin configs, access widener, and asset namespace.
- Local config store with streamer/privacy, toast, refresh interval, diagnostics, and local join port settings.
- In-game Settings tab with save/defaults, data-folder access, local auth/cache clearing, toast toggles, language, click-outside behavior, random join port, and join-port settings.
- Diagnostics tab for Core/IPC/auth/config/data status, loader/version details, last refresh time, Core binary path, local join address, and clipboard copy.
- Blocks tab in the overlay.
- Streamer/privacy formatter.
- Configurable local join port.
- Random local join port support with fallback when the configured port is already in use.
- English and pt-BR language resources.
- Runtime translation helper for the overlay tabs/settings/empty states.
- README, NOTICE, SECURITY, and Modrinth-oriented release notes.

### Changed

- UI title now uses OpenFriend Plus branding.
- Default overlay panel is larger and includes fork/version footer text.
- Data directory is separated from the original OpenFriendMod.
- Root buildAll targets included Fabric modules instead of absent loader modules.
- Device-code login screen warns users not to share the code on stream and masks it when streamer mode is active.
- Friends, requests, blocks, diagnostics, logs, and toasts respect streamer/privacy masking.

### Credits

Based on OpenFriendMod by ZSHARE, licensed under MIT.
