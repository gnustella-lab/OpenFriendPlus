# Security and privacy

OpenFriend Plus uses a bundled local Core process for Microsoft/Xbox/Minecraft friend and join functionality. The mod communicates with that local process over JSON-RPC stdio.

## Local data

Authentication data may be stored locally by the Core component inside the OpenFriend Plus data directory. The fork uses the `openfriendplus` mod id to keep this data separate from the original OpenFriendMod data directory.

## Sensitive data

Do not share:

- Microsoft device-code login codes.
- Access tokens or refresh tokens.
- Full profile IDs when you do not intend to disclose them.
- IP addresses or join ports during streams unless you understand the impact.

The fork adds streamer/privacy formatting and diagnostics that avoid intentionally exposing tokens.

The Settings tab includes local data controls. Clearing local auth/cache stops the Core bridge and removes OpenFriend Plus data-directory contents while preserving the mod config file.

## Reporting issues

Please report security or privacy issues through the fork issue tracker:

https://github.com/gnustella-lab/OpenFriendPlus/issues
