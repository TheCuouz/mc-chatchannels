# ChattyChannels

Configurable chat channels for Paper 1.21.x servers. Create unlimited channels with custom permissions, quick-type prefixes, range limits, cooldowns, mute system, staff spy mode, and anti-spam filters.

## Features

- **Multiple channels** — global, local, trade, staff (fully configurable in `channels.yml`)
- **Quick prefixes** — type `!hello` to send to global without switching your active channel
- **Range-based channels** — local chat only reaches players within N blocks in the same world
- **Cooldowns** — per-channel message cooldowns to reduce spam
- **Mute system** — mute players from specific channels or all channels, with optional expiry
- **Spy mode** — staff can see all channels simultaneously with `[SPY]` prefix
- **Anti-spam** — duplicate message detection, caps filter, flood protection
- **Word filter** — replace or block blacklisted words (word-boundary aware, case-insensitive)
- **PlaceholderAPI** — expose active channel and mute status as placeholders
- **bStats** — anonymous usage statistics

## Requirements

- Paper 1.21.x (or any Paper-based fork)
- Java 21+

## Optional integrations

- **PlaceholderAPI** — enables `%chatchannels_active%` and `%chatchannels_muted%` placeholders
- **LuckPerms** — use `%luckperms_prefix%` in channel format strings (via PAPI)

## Installation

1. Drop `ChattyChannels-1.0.0.jar` into your `plugins/` folder.
2. Restart your server.
3. Edit `plugins/ChattyChannels/channels.yml` to configure your channels and filters.
4. Edit `plugins/ChattyChannels/messages.yml` to customize player-facing messages.
5. Run `/cc reload` to apply changes without restarting.

## Quick start

Default channels out of the box:

| Channel | Quick prefix | Range | Permission |
|---------|-------------|-------|-----------|
| local   | (none)       | 100 blocks | `chatchannels.use.local` |
| global  | `!`          | Global | `chatchannels.use.global` |
| trade   | `#`          | Global | `chatchannels.use.trade` |
| staff   | `@`          | Global | `chatchannels.use.staff` |

Type `!hello` to send to global. Type `/channel global` to switch permanently.

## Commands

| Command | Description |
|---------|-------------|
| `/channel <id>` | Switch active channel |
| `/ch <id>` | Alias for `/channel` |
| `/channels` | List available channels |
| `/mute <player> [channel] [duration]` | Mute a player (duration: `10m`, `1h`, `2d`) |
| `/chatspy` | Toggle spy mode |
| `/cc reload` | Reload configuration |

## Documentation

- [CONFIG.md](CONFIG.md) — Full configuration reference
- [PERMISSIONS.md](PERMISSIONS.md) — All permissions
- [PLACEHOLDERS.md](PLACEHOLDERS.md) — PlaceholderAPI placeholders
- [CHANGELOG.md](CHANGELOG.md) — Version history

## Support

Open an issue on GitHub or join the Discord support server.
