# ChatChannels

> **Lightweight, opinionated chat-channels plugin for Paper 1.21.x** — Multiple channels with permissions, quick-prefix shortcuts, per-channel range, staff spy mode, anti-spam + word filters, and (NEW in v1.1.0) **per-channel hide** so every player curates their own chat feed.

> Brought to you by **TTS-Studio** — part of the unified TTS-Studio plugin suite.

---

## Features

| Feature | Description |
|---------|-------------|
| **Multiple channels** | Define any number of channels in `channels.yml` — name, prefix, format, range, permission, cooldown |
| **Quick-prefix** | Type `!message` or `#message` to send to a channel once without switching your active channel |
| **Per-channel range** | `range: -1` = server-wide, positive integer = block radius (great for `local` / RP servers) |
| **Per-channel cooldowns** | Optional anti-spam baseline configured per channel |
| **Admin mute** (`/mute`) | `/mute <player> [channel] [duration]` — silences a player **send-side**; persistent across restarts; auto-expiry |
| **Self-hide** (`/cc hide`) | **NEW in v1.1.0** — players hide a channel from their own view **receive-side**; persistent across restarts |
| **Staff spy mode** | `/chatspy` shows every channel with a `[SPY]` prefix, even if you're not in range |
| **Spam filter** | Duplicate detection, configurable caps threshold, flood limiter — bypass permission included |
| **Word filter** | REPLACE (`****`) or BLOCK mode with word-boundary, case-insensitive matching |
| **PlaceholderAPI** | `%chatchannels_active%`, `%chatchannels_muted%` |
| **bStats** | Anonymous usage metrics |
| **TTS-Studio house style** | Suite-wide chat prefix and framed boot banner |

---

## Requirements

| Dependency | Version | Scope |
|---|---|---|
| Paper | 1.21.4+ | Required |
| Java | 21+ | Required |
| PlaceholderAPI | 2.11+ | Optional |
| LuckPerms | 5.x | Optional |

---

## Quick Start

```bash
# 1. Drop the jar into your plugins folder
cp chatchannels-1.1.0.jar plugins/

# 2. Restart the server
#    Default channels (global, local, trade, staff) are written on first enable.

# 3. Customise channels
nano plugins/ChatChannels/channels.yml

# 4. Hot-reload after edits — no restart needed
/cc reload
```

---

## Curate your own chat feed (NEW in v1.1.0)

Tired of the trade channel? Hide it from your own view without bothering anyone else:

```
/cc hide #trade        # stop receiving the trade channel
/cc show #trade        # restore it
/cc hidden             # show which channels you have hidden
```

Selections survive restarts (stored in `plugins/ChatChannels/hidden_channels.yml`). The channel itself remains live for every other player — this is a **per-player, receive-side** toggle.

### How it differs from `/mute`

| Action | Direction | Who can run it | Effect |
|--------|-----------|---------------|--------|
| `/mute <player> [channel] [duration]` | **Send-side** | Staff (`chatchannels.mute`) | Stops the target player from posting into the channel |
| `/cc hide <#channel>` | **Receive-side** | Any player (`chatchannels.hide`, default `true`) | Stops the caller from receiving the channel; nobody else is affected |

Both systems are completely independent and can be combined freely.

---

## Commands

| Command | Description | Permission | Default |
|---|---|---|---|
| `/channel <id>` | Switch your active channel | `chatchannels.use` | true |
| `/ch <id>` | Alias for `/channel` | `chatchannels.use` | true |
| `/channels` | List available channels | `chatchannels.use` | true |
| `/mute <player> [channel] [duration]` | Mute a player **send-side** | `chatchannels.mute` | op |
| `/chatspy` | Toggle staff spy mode | `chatchannels.spy` | op |
| `/cc hide <#channel>` | Hide a channel from your view (**NEW in v1.1.0**) | `chatchannels.hide` | true |
| `/cc show <#channel>` | Restore a hidden channel (**NEW in v1.1.0**) | `chatchannels.hide` | true |
| `/cc hidden` | List your hidden channels (**NEW in v1.1.0**) | `chatchannels.hide` | true |
| `/cc reload` | Reload config + channels + filters | `chatchannels.admin` | op |

**Duration format:** `10m` · `2h` · `1d` — omit for permanent.
**Channel reference:** `/cc hide #trade` or `/cc hide trade` both work.

---

## Permissions

| Permission | Description | Default |
|---|---|---|
| `chatchannels.use` | Basic access — `/channel`, `/channels`, `/cc` | `true` |
| `chatchannels.use.<id>` | Per-channel access (auto-derived if `permission:` omitted in `channels.yml`) | varies |
| `chatchannels.hide` | Use `/cc hide`, `/cc show`, `/cc hidden` (**NEW in v1.1.0**) | `true` |
| `chatchannels.spy` | Toggle spy mode | `op` |
| `chatchannels.mute` | Mute players from channels | `op` |
| `chatchannels.bypass.cooldown` | Bypass channel cooldowns | `op` |
| `chatchannels.bypass.filter` | Bypass spam and word filters | `op` |
| `chatchannels.admin` | Reload command | `op` |

---

## Configuration

```yaml
# plugins/ChatChannels/channels.yml
default-channel: local

channels:
  global:
    display-name: "<gradient:gold:yellow>Global</gradient>"
    quick-prefix: "!"
    format: "<gray>[G] <player>:</gray> <message>"
    range: -1                  # -1 = server-wide
    permission: "chatchannels.use.global"
    cooldown-seconds: 0

  local:
    display-name: "<aqua>Local</aqua>"
    quick-prefix: ""
    format: "<gray>[L] <player>:</gray> <message>"
    range: 64                  # blocks
    permission: "chatchannels.use.local"
    cooldown-seconds: 0

  staff:
    display-name: "<red>Staff</red>"
    quick-prefix: "@"
    format: "<red>[Staff] <player>:</red> <message>"
    range: -1
    permission: "chatchannels.use.staff"
    cooldown-seconds: 0

filters:
  spam:
    enabled: true
    duplicate-window-seconds: 5
    caps-threshold-pct: 70
    caps-min-length: 10
    flood-window-seconds: 5
    flood-max-messages: 4
  words:
    mode: REPLACE              # REPLACE | BLOCK
    replacement: "****"
    list: ["badword1", "badword2"]
```

---

## Persistence

| File | What's stored |
|------|---------------|
| `players.yml` | Each player's active channel + last-message-time + spy state |
| `mutes.yml` | Admin-applied mutes (`uuid:channelId` → expiry epoch ms) |
| `hidden_channels.yml` | Per-player self-hidden channels (NEW in v1.1.0) |

All three are written on mutation and again on `onDisable` as a belt-and-braces flush.

---

## PlaceholderAPI

| Placeholder | Value |
|---|---|
| `%chatchannels_active%` | The player's current channel id |
| `%chatchannels_muted%` | `true` / `false` — whether the player is admin-muted in their active channel |

PlaceholderAPI is auto-detected on enable; placeholders silently no-op when the plugin is absent.

---

## Documentation

Full reference in [docs/](docs/):
[CONFIG.md](docs/CONFIG.md) · [PERMISSIONS.md](docs/PERMISSIONS.md) · [PLACEHOLDERS.md](docs/PLACEHOLDERS.md) · [CHANGELOG.md](CHANGELOG.md)

---

## Reporting bugs

Open an issue on the GitHub repository with:

- ChatChannels version (`/version ChatChannels`)
- Server type and version (Paper build, Java version)
- A minimal `channels.yml` that reproduces the issue
- Server log excerpt — especially the stack trace if there is one

---

## License

ChatChannels is distributed under the **TTS-Studio open-source license** as part of the SpigotMC funnel suite. See `LICENSE` for the full terms.

---

<sub>ChatChannels is a TTS-Studio plugin · (c) TTS-Studio · giving every player their own chat since 2024.</sub>
