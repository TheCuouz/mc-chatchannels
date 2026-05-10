# ChatChannels

Lightweight but feature-complete chat channel plugin for Paper 1.21.4. Players switch between channels, use quick-prefix shortcuts for one-off messages, and staff get a global spy mode — all backed by per-channel spam and word filters.

## Features

- **Multiple channels** — define any number of channels in `channels.yml` (name, prefix, format, range, permissions, cooldown)
- **Quick-prefix** — type `!message` to send to a channel once without switching your active channel
- **Timed mutes** — `/mute <player> 10m` with persistence across restarts; automatic expiry
- **Staff spy mode** — `/chatspy` lets staff see every channel simultaneously with a `[SPY]` prefix
- **Spam filter** — blocks duplicate messages, excessive caps, and flood; all thresholds configurable
- **Word filter** — REPLACE (`****`) or BLOCK mode with word-boundary, case-insensitive matching
- **Per-channel cooldowns** — configured per channel in `channels.yml`
- **PlaceholderAPI** support
- **bStats** metrics

## Requirements

| Dependency | Version | Scope |
|---|---|---|
| Paper | 1.21.4+ | Required |
| Java | 21+ | Required |
| PlaceholderAPI | 2.11+ | Optional |

## Installation

1. Drop `chatchannels-1.0.0.jar` into your `plugins/` folder
2. Restart the server — default channels are generated automatically
3. Edit `plugins/ChatChannels/channels.yml` to customise channels
4. `/cc reload` applies changes without restart

## Commands & Permissions

| Command | Description | Permission | Default |
|---|---|---|---|
| `/channel <id>` | Switch active channel | `chatchannels.use` | true |
| `/ch <id>` | Alias for `/channel` | `chatchannels.use` | true |
| `/channels` | List all available channels | `chatchannels.use` | true |
| `/mute <player> [duration]` | Mute a player | `chatchannels.mute` | op |
| `/chatspy` | Toggle staff spy mode | `chatchannels.spy` | op |
| `/cc reload` | Reload config + channels | `chatchannels.admin` | op |

**Duration format:** `10m` · `2h` · `1d` — omit for permanent.

## Configuration

```yaml
# channels.yml
global:
  display-name: "Global"
  prefix: "!"             # quick-prefix — send once without switching
  format: "<gray>[Global]</gray> {player}: {message}"
  range: -1               # -1 = server-wide, positive = block radius
  permission: ""          # empty = all players
  cooldown: 0             # seconds between messages

staff:
  display-name: "Staff"
  prefix: "@"
  format: "<red>[Staff]</red> {player}: {message}"
  range: -1
  permission: "chatchannels.staff"
  cooldown: 0
```

```yaml
# config.yml
filters:
  spam:
    duplicate-block: true
    caps-threshold: 0.7       # 0–1; fraction of caps before blocking
    flood-window-ms: 3000
    flood-limit: 4
  words:
    mode: REPLACE             # REPLACE | BLOCK
    replacement: "****"
    list: ["badword1", "badword2"]
```

## PlaceholderAPI

| Placeholder | Value |
|---|---|
| `%chatchannels_active%` | Current channel id |
| `%chatchannels_muted%` | `true` / `false` |

## Documentation

Full reference in [docs/](docs/):
[CONFIG.md](docs/CONFIG.md) · [PERMISSIONS.md](docs/PERMISSIONS.md) · [PLACEHOLDERS.md](docs/PLACEHOLDERS.md) · [CHANGELOG.md](docs/CHANGELOG.md)
