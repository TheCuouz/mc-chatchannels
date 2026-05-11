# Channel Formats

Every channel's `format:` string in `channels.yml` controls exactly how a message renders for the recipients. ChatChannels uses Adventure's [MiniMessage](https://docs.advntr.dev/minimessage/format.html) format — the modern, tag-based replacement for legacy `&`-codes — and runs each message through PlaceholderAPI (when present) before tag deserialisation.

> Reminder: `format:` is **player chat content**. It is intentionally **not** wrapped in the TTS `◈ Channels:` prefix. That prefix is reserved for plugin-voice messages from `messages.yml` (e.g. "you joined #trade", "channel not found", "mute applied").

---

## The Pipeline

Given a raw chat input like `WTS 64 diamonds`, this is what ChatChannels does to produce the final component sent to viewers:

```
1. raw input        →  "WTS 64 diamonds"
2. word filter      →  "WTS 64 diamonds"           (REPLACE/BLOCK applied)
3. format template  →  "<gray>[<aqua>$</aqua>]</gray> <player>: <message>"
4. PAPI pass        →  "<gray>[<aqua>$</aqua>]</gray> <player>: <message>"
                       (any %placeholder% expanded against the *sender*)
5. <player>         →  replaced with sender's name
6. <message>        →  replaced with filtered chat content
                       (with `<` escaped as `\<` so chat can't inject tags)
7. MiniMessage      →  deserialised into an Adventure Component
8. delivered to all viewers in range with the right permission
```

This ordering matters: **PAPI placeholders run before MiniMessage parsing**, so you can embed coloured prefixes from LuckPerms inside the format. The `<message>` substitution runs **after** the word filter, which means filter masks (`****`) end up in chat — players cannot route around them with MiniMessage tags because `<` in the raw message is escaped to `\<` before deserialisation.

---

## Built-in Placeholders

These are substituted by ChatChannels itself (not PAPI):

| Placeholder | Replaced with |
|-------------|---------------|
| `<player>` | The sender's name (`player.getName()`) |
| `<message>` | The filtered chat content, with `<` escaped |

> `<message>` is the only place player input lands. Because `<` is escaped on the way in, players cannot inject MiniMessage tags via their chat content even if they know the syntax. This keeps `<rainbow>` etc. as an admin/format-author privilege.

---

## PlaceholderAPI Placeholders

When PlaceholderAPI is installed and enabled, **any** `%expansion_value%` placeholder is expanded against the sender just before MiniMessage parsing. Popular choices:

| Placeholder | Source expansion | Typical value |
|-------------|------------------|---------------|
| `%player_name%` | `player` (built-in) | `Steve` |
| `%player_world%` | `player` | `world_nether` |
| `%luckperms_prefix%` | LuckPerms | `<gold>[VIP]<reset> ` |
| `%vault_prefix%` | Vault | `&6[Mod]&r ` |
| `%essentials_nickname%` | Essentials | `Steve_the_Brave` |
| `%chatchannels_active%` | ChatChannels (this plugin) | `trade` |

The default Global format uses `%luckperms_prefix%`:

```yaml
format: "<gray>[<gold>G</gold>]</gray> %luckperms_prefix%<player><reset>: <message>"
```

If LuckPerms is present, this renders as e.g. `[G] [VIP] Steve: hi`. If it isn't, the placeholder remains as literal text — keep that in mind when designing formats for a server that may run without PAPI.

---

## MiniMessage Tag Cheat-sheet

A non-exhaustive list of tags that are most useful inside a channel `format:`.

### Colours

```text
<red>, <green>, <blue>, <yellow>, <aqua>, <gold>, <gray>, <dark_gray>,
<dark_red>, <dark_green>, <dark_blue>, <dark_purple>, <dark_aqua>,
<black>, <white>, <light_purple>
```

Hex: `<color:#5DADE2>Channels</color>` (TTS-Studio brand colour).

### Decorations

```text
<bold>, <italic>, <underlined>, <strikethrough>, <obfuscated>
```

End a span with the closing variant: `<gold>Trade</gold>`, or `<reset>` to drop all current styling.

### Gradients

```text
<gradient:#5DADE2:#3498DB>Channels</gradient>
<gradient:gold:red:gold>WARNING</gradient>
```

Great for staff-channel headers.

### Hover / Click (for the bracket prefix)

```text
<hover:show_text:'<gray>Trade channel'>[$]</hover>
<click:run_command:'/channel trade'>[$]</click>
```

Wrap the channel tag in your `format:` to make it clickable in chat — players can switch by clicking the bracket.

---

## Format Recipes

### Minimalist Global

```yaml
format: "<gold>[G] <player></gold>: <message>"
```

### Trade with clickable bracket

```yaml
format: "<click:run_command:'/channel trade'><hover:show_text:'<gray>Click to switch'><aqua>[$]</aqua></hover></click> <player>: <message>"
```

### Staff with gradient header

```yaml
format: "<gradient:dark_red:red:dark_red>[STAFF]</gradient> <gray><player>:</gray> <white><message>"
```

### Rank prefix from LuckPerms

```yaml
format: "%luckperms_prefix%<player><reset> <dark_gray>»<reset> <message>"
```

### World-aware Local

```yaml
format: "<gray>[L]<dark_gray>(%player_world%)<reset> <player>: <message>"
```

---

## Escaping & Safety

| Concern | How ChatChannels handles it |
|---------|------------------------------|
| Players injecting MiniMessage tags | `<` in `<message>` is escaped to `\<` before parsing |
| Players injecting PAPI placeholders | PAPI runs on the **format**, not on `<message>` — players cannot trigger placeholder expansion via chat |
| Players bypassing word filters with colour codes | The word filter runs on the plain text *before* the format is applied, so `b<red>a</red>dword` would still match `badword` only if the user typed it literally — but since players can't inject tags, this is not a practical concern |

---

## Console Banner Branding

The TTS-Studio banner emitted on enable uses the brand alias **Channels** (sky-blue `#5DADE2`) because the full name `ChatChannels` is 12 chars and exceeds the 10-char SDK budget. The prefix that wraps plugin-voice messages is `◈ Channels:`. You do **not** need to (and should not) reproduce this prefix inside channel `format:` strings — that would brand every player message as plugin output and mis-attribute speech.

---

[Home](Home) · [Installation](Installation) · [Commands & Permissions](Commands-and-Permissions) · [Configuration](Configuration) · [PlaceholderAPI](PlaceholderAPI)
