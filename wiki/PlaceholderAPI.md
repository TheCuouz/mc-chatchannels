> 🌐 **English** · [Español](es/PlaceholderAPI.md)

# PlaceholderAPI Integration

ChatChannels registers a `chatchannels` PAPI expansion when [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) is installed. The expansion exposes the current player's chat-channel state for use in scoreboards, TAB lists, chat formatters, and HUD plugins.

---

## Setup

### 1. Install PlaceholderAPI

```bash
# Drop PAPI in plugins/ and restart
# Or download via the eCloud:
/papi ecloud download Player
/papi reload
```

> ChatChannels' own expansion is registered **internally** when the plugin enables — it is not on the PAPI eCloud. You do not need to run `/papi ecloud download ChatChannels`.

### 2. Verify the hook

On enable, the console banner reports the PAPI status:

```
  ◈ Channels: 4 channels · PAPI ✓ · ready in 42ms
```

You can also verify in-game:

```bash
/papi test <yourname> %chatchannels_active%
# Expected output: trade  (or whichever channel is your active)
```

---

## Placeholder Reference

| Placeholder | Returns | Example Output |
|-------------|---------|----------------|
| `%chatchannels_active%` | The ID of the player's current active channel | `trade` |
| `%chatchannels_muted%` | `true` if the player is muted in their active channel, `false` otherwise | `false` |

> If PlaceholderAPI is not installed, these placeholders will render literally (`%chatchannels_active%`) rather than expand. Always guard against that in formats you ship publicly.

### `%chatchannels_active%`

Returns the **internal channel ID** (the YAML key in `channels.yml`) that the player is currently posting to. For the default config that's one of `global`, `local`, `trade`, or `staff`. New custom channels you add will return their custom ID.

Useful for:
- Showing the current channel on a scoreboard or HUD
- Conditional chat formatting in `DeluxeChat` / `EssentialsChat`
- TAB plugin suffixes that change with the active channel

### `%chatchannels_muted%`

Returns the literal string `true` or `false` depending on whether the player is currently muted **in their active channel**. Note this checks the active channel specifically — a player muted in `trade` but currently in `local` will return `false`.

If you need server-wide mute detection, mute the player with channel `*`:

```bash
/mute Steve * 1h
```

That will make `%chatchannels_muted%` return `true` regardless of which channel they switch to.

---

## Usage Examples

### Scoreboard (FeatherBoard / AnimatedScoreboard)

```yaml
lines:
  - "&7Channel: &b%chatchannels_active%"
  - "&7Muted: &c%chatchannels_muted%"
```

### TAB plugin suffix

```yaml
tablist-name: "%player_name% &8| &7%chatchannels_active%"
```

### Conditional formatting

Using a conditional placeholder expansion (e.g. `PlaceholderAPI-Expansion-Conditions`):

```yaml
# Show a red marker next to muted players
name: "{condition: %chatchannels_muted%=true}&c[muted] &7%player_name%|%player_name%}"
```

### Inside a channel `format:` (self-referencing)

Although the channel format already implies the channel, you can include the ID for clarity in admin-facing channels:

```yaml
staff:
  format: "<dark_red>[STAFF/%chatchannels_active%]</dark_red> <player>: <message>"
```

---

## Using Other Plugins' Placeholders in Channel Formats

The reverse is also supported: ChatChannels runs every channel `format:` through PAPI before MiniMessage parsing, so you can embed any installed expansion. The default Global format does exactly this with LuckPerms:

```yaml
format: "<gray>[<gold>G</gold>]</gray> %luckperms_prefix%<player><reset>: <message>"
```

Other popular embeds:

| Placeholder | Source | Use case |
|-------------|--------|----------|
| `%luckperms_prefix%` | LuckPerms | Inject group prefix into chat |
| `%player_world%` | PAPI built-in | Show sender's world name |
| `%vault_eco_balance_formatted%` | Vault | Show player wealth in trade channel |
| `%essentials_nickname%` | Essentials | Use the player's set nickname |

---

## Compatibility Matrix

| Plugin | Works with ChatChannels Placeholders |
|--------|--------------------------------------|
| TAB by NEZNAMY | ✓ |
| FeatherBoard | ✓ |
| AnimatedScoreboard | ✓ |
| CMI | ✓ |
| DeluxeChat | ✓ |
| EssentialsChat | ✓ |
| HolographicDisplays | ✓ (via PAPI bridge) |

---

## Troubleshooting

| Symptom | Likely cause |
|---------|-------------|
| Placeholder shows literally as `%chatchannels_active%` | PlaceholderAPI not installed, or ChatChannels was loaded **before** PAPI |
| Returns empty string | Player is offline, or being queried as `console` (placeholders require a `Player` context) |
| Always returns `local` | The player has never run `/channel`, so they're still on the configured `default-channel:` |
| `%chatchannels_muted%` returns `false` even though the player is muted | The mute applies to a different channel than the player's active one — check with `/mute` on the right channel or `*` |

---

[Home](Home) · [Installation](Installation) · [Commands & Permissions](Commands-and-Permissions) · [Configuration](Configuration) · [Channel Formats](Channel-Formats)
