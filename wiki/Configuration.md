> 🌐 **English** · [Español](es/Configuration.md)

# Configuration

All ChattyChannels configuration lives in two files:

```
plugins/ChattyChannels/
├── channels.yml    # Channel definitions + spam/word filter settings
└── messages.yml    # Player-facing strings (TTS-prefixed plugin voice)
```

After editing either file, run `/cc reload` — no restart required.

> Plugin-voice strings (`messages.yml`) are tagged with the TTS `◈ Channels:` prefix when delivered. Channel chat content is **not**: each channel's `format:` is the single source of truth for how players' messages appear.

---

## Full `channels.yml`

```yaml
# ChattyChannels — channel configuration
# range: -1  → global (all worlds, all players online)
# range: N   → only players within N blocks in the same world
# quick-prefix: type this at the start of your message to send to that
#               channel without switching active channel

channels:
  global:
    display-name: "<gold>Global"
    quick-prefix: "!"
    range: -1
    permission: "chatchannels.use.global"
    format: "<gray>[<gold>G</gold>]</gray> %luckperms_prefix%<player><reset>: <message>"
    cooldown-seconds: 2

  local:
    display-name: "<green>Local"
    quick-prefix: ""
    range: 100
    permission: "chatchannels.use.local"
    format: "<gray>[<green>L</green>]</gray> <player>: <message>"
    cooldown-seconds: 1

  trade:
    display-name: "<aqua>Trade"
    quick-prefix: "#"
    range: -1
    permission: "chatchannels.use.trade"
    format: "<gray>[<aqua>$</aqua>]</gray> <player>: <message>"
    cooldown-seconds: 3

  staff:
    display-name: "<dark_red>Staff"
    quick-prefix: "@"
    range: -1
    permission: "chatchannels.use.staff"
    format: "<dark_red>[STAFF]</dark_red> <player>: <message>"
    cooldown-seconds: 0

default-channel: "local"

filters:
  spam:
    enabled: true
    duplicate-window-seconds: 5
    caps-threshold-pct: 70
    caps-min-length: 10
    flood-window-seconds: 5
    flood-max-messages: 4
  words:
    mode: REPLACE
    list:
      - "badword1"
      - "badword2"
    replacement: "****"
```

---

## Anatomy of a Channel

| Key | Type | Purpose |
|-----|------|---------|
| `display-name` | MiniMessage string | Shown in `/channels`, `/channel`, and plugin-voice messages |
| `quick-prefix` | string | Type this at the start of a chat line to route the message to this channel (empty string = no quick prefix) |
| `range` | int | `-1` for server-wide; positive N for "only players within N blocks in the same world" |
| `permission` | string | Bukkit permission required to **read and write** this channel |
| `format` | MiniMessage string | Template for how chat content is rendered. Uses `<player>`, `<message>`, and any PAPI placeholder |
| `cooldown-seconds` | int | Per-player anti-spam cooldown specific to this channel. `0` = no cooldown. Players with `chatchannels.bypass.cooldown` ignore it |

The `default-channel:` key at the top level sets which channel new players post to before they run `/channel`.

---

## Adding a New Channel

Suppose you want a `recruit` channel for new players that is global but rate-limited and has its own MiniMessage format. Drop this under `channels:` in `channels.yml`:

```yaml
recruit:
  display-name: "<light_purple>Recruit"
  quick-prefix: "?"
  range: -1
  permission: "chatchannels.use.recruit"
  format: "<gray>[<light_purple>?</light_purple>]</gray> <player>: <message>"
  cooldown-seconds: 5
```

Then grant the permission to your default group:

```bash
/lp group default permission set chatchannels.use.recruit true
```

Finally, reload:

```text
/cc reload
```

> New permissions added via `channels.yml` are **not** auto-registered in `plugin.yml`. Players still need to be granted the permission via LuckPerms (or set it as `default: true` in the permission node of your permissions plugin). Bukkit will treat unregistered permission nodes as `default: false` for non-ops.

---

## Spam Filter

Located under `filters.spam` in `channels.yml`. Three independent checks run on every message; any one of them can block.

| Key | Default | Effect |
|-----|---------|--------|
| `enabled` | `true` | Master switch. Set `false` to disable all three spam checks |
| `duplicate-window-seconds` | `5` | Block identical messages sent by the same player within this window |
| `caps-threshold-pct` | `70` | If ≥ this % of letters are uppercase, the message is blocked |
| `caps-min-length` | `10` | The caps check only runs on messages at least this long (avoids blocking `OK` or `LOL`) |
| `flood-window-seconds` | `5` | The flood check counts messages sent in this rolling window |
| `flood-max-messages` | `4` | If a player sends more than this many in `flood-window-seconds`, further messages are blocked |

Players with `chatchannels.bypass.filter` skip all three checks.

Block reasons map to different player-facing messages in `messages.yml`:

| Reason | Message key |
|--------|-------------|
| Duplicate | `filter-spam-duplicate` |
| Caps abuse | `filter-spam-caps` |
| Flood | `filter-spam-flood` |

---

## Word Filter

Located under `filters.words`.

| Key | Type | Effect |
|-----|------|--------|
| `mode` | `REPLACE` or `BLOCK` | `REPLACE` masks each matched word with `replacement`; `BLOCK` cancels the whole message |
| `list` | string list | Words to filter (case-insensitive, whole-word matching) |
| `replacement` | string | The mask shown in `REPLACE` mode (default `****`) |

Examples:

```yaml
# Soft moderation — mask slurs but let the message through
filters:
  words:
    mode: REPLACE
    list: ["slur1", "slur2"]
    replacement: "***"

# Strict moderation — drop any message containing a banned word
filters:
  words:
    mode: BLOCK
    list: ["scamlink.example", "ddosthreat"]
```

Blocked messages trigger the `filter-word-block` message. Players with `chatchannels.bypass.filter` skip the word filter.

---

## `messages.yml`

Every plugin-voice line. Edit to translate or rebrand. Placeholders in angle-brackets are substituted at send time.

```yaml
prefix: "<gray>[<gold>ChattyChannels</gold>]</gray> "

channel-switched:        "<green>Cambiaste al canal <channel>."
channel-not-found:       "<red>Canal '<id>' no encontrado."
channel-no-permission:   "<red>No tienes permiso para usar ese canal."
channel-list-header:     "<gold>Canales disponibles:</gold>"
channel-list-entry:      "  <gray>- <channel> <dark_gray>(<prefix>)</dark_gray>"
channel-cooldown:        "<red>Espera <seconds>s antes de enviar otro mensaje."
channel-muted:           "<red>Estás silenciado en ese canal."

mute-applied:            "<green><target> silenciado en <channel> por <duration>."
mute-permanent:          "<green><target> silenciado en <channel> permanentemente."
mute-not-found:          "<red>Jugador '<target>' no encontrado."
mute-usage:              "<red>Uso: /mute <jugador> [canal] [duración: 10m, 1h, 2d]"

spy-enabled:             "<gold>Modo espía <green>activado</green>."
spy-disabled:            "<gold>Modo espía <red>desactivado</red>."
spy-prefix:              "<dark_gray>[SPY] "

reload-success:          "<green>ChattyChannels recargado correctamente."

filter-spam-duplicate:   "<red>No envíes el mismo mensaje repetidamente."
filter-spam-caps:        "<red>Por favor escribe en minúsculas."
filter-spam-flood:       "<red>Estás enviando mensajes muy rápido."
filter-word-block:       "<red>Tu mensaje contiene palabras no permitidas."
```

> All these strings are delivered with the TTS-Studio `◈ Channels:` prefix automatically — do not duplicate it inside the string itself.

---

## `config.yml`

Currently a stub used only for tracking the config schema version:

```yaml
config-version: 1
```

This file exists so future migrations have a clean version anchor. You will rarely need to touch it.

---

[Home](Home) · [Installation](Installation) · [Commands & Permissions](Commands-and-Permissions) · [Channel Formats](Channel-Formats) · [PlaceholderAPI](PlaceholderAPI)
