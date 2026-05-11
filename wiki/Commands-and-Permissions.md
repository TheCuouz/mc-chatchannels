# Commands & Permissions

All ChatChannels commands and their permissions are summarised below. Permission defaults follow `plugin.yml` and are the same as what a fresh install ships with.

---

## Commands

| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/channel <id>` | `/ch` | Switch your active chat channel (or show current if no arg) | `chatchannels.use` |
| `/channels` | — | List all channels you have permission to use | `chatchannels.use` |
| `/mute <player> [channel] [duration]` | — | Mute a player from a channel (or `*` for all) | `chatchannels.mute` |
| `/chatspy` | — | Toggle spy mode — see messages from channels you cannot read | `chatchannels.spy` |
| `/cc reload` | — | Reload `channels.yml` and `messages.yml` | `chatchannels.admin` |

> All plugin-voice feedback (success / error / usage strings) is prefixed by the TTS-Studio `◈ Channels:` tag. Player chat content uses each channel's own `format:` and is **not** tagged with the plugin prefix.

---

## Command Details

### `/channel <id>`

Switches your active channel. With no argument, it reports the channel you're currently posting to.

```text
> /channel
◈ Channels: Canal activo: Trade

> /channel staff
◈ Channels: Cambiaste al canal Staff.

> /channel unknown
◈ Channels: Canal 'unknown' no encontrado.
```

Tab-complete suggests only channels you have permission for.

### `/channels`

Lists every channel visible to you with its quick-prefix.

```text
> /channels
◈ Channels: Canales disponibles:
◈ Channels:   - Global (!)
◈ Channels:   - Local (sin prefijo)
◈ Channels:   - Trade (#)
◈ Channels:   - Staff (@)
```

### `/mute <player> [channel] [duration]`

Mutes a player from a single channel, or from every channel when `channel` is omitted or set to `*`.

**Duration syntax** (parsed by `DurationParser`):

| Token | Meaning | Example |
|-------|---------|---------|
| `Nm` | Minutes | `10m` |
| `Nh` | Hours | `2h` |
| `Nd` | Days | `7d` |
| *(omitted)* | Permanent | — |

```text
# Permanent server-wide mute
/mute Steve

# 10-minute mute in the trade channel only
/mute Steve trade 10m

# 1-hour mute server-wide
/mute Steve * 1h
```

Muted players see:

```text
◈ Channels: Estás silenciado en ese canal.
```

### `/chatspy`

Toggles spy mode for the issuing player. Spies receive a copy of every channel message they would otherwise miss (e.g. messages in `staff` while in survival, or messages outside the `local` radius). Spy copies are prefixed with `<dark_gray>[SPY]` so they are visually distinct from regular chat.

> Spies who already see a message normally (because they have permission and are in range) do **not** get a duplicate `[SPY]` copy.

### `/cc reload`

Re-reads `channels.yml` (channel definitions, spam filter, word filter) and `messages.yml`. Player state (active channel, mutes) is preserved.

```text
> /cc reload
◈ Channels: ChatChannels recargado correctamente.
```

---

## Quick Prefixes (no command needed)

Each channel can define a `quick-prefix:` in `channels.yml`. Starting a message with that prefix routes it to that channel **for that message only**, without changing your active channel.

| Default Prefix | Channel |
|----------------|---------|
| `!` | Global |
| *(none)* | Local |
| `#` | Trade |
| `@` | Staff |

```text
> !sup everyone           # → Global, even if your active channel is Local
> #wts 64 diamonds        # → Trade
> @meeting in 5            # → Staff
```

Quick-prefix routing still respects permissions, mutes, cooldowns, and filters.

---

## Permission Tree

### Channel Access

| Permission | Default | Effect |
|------------|---------|--------|
| `chatchannels.use` | `true` | Use `/channel` and `/channels` |
| `chatchannels.use.global` | `true` | Read + write the Global channel |
| `chatchannels.use.local` | `true` | Read + write the Local channel |
| `chatchannels.use.trade` | `true` | Read + write the Trade channel |
| `chatchannels.use.staff` | `op` | Read + write the Staff channel |

> The `permission:` of a channel governs **both** sending and receiving. A player without `chatchannels.use.staff` will neither see nor send staff messages (unless they have `chatchannels.spy`).

### Admin / Moderation

| Permission | Default | Effect |
|------------|---------|--------|
| `chatchannels.mute` | `op` | Use `/mute` |
| `chatchannels.spy` | `op` | Use `/chatspy` |
| `chatchannels.admin` | `op` | Use `/cc reload` |
| `chatchannels.bypass.cooldown` | `op` | Ignore per-channel cooldown |
| `chatchannels.bypass.filter` | `op` | Ignore spam and word filters |

---

## LuckPerms Examples

```bash
# Grant the trade-builders group access to the staff channel
/lp group helper permission set chatchannels.use.staff true

# Make moderators able to mute and spy
/lp group moderator permission set chatchannels.mute true
/lp group moderator permission set chatchannels.spy true

# Let VIPs ignore the cooldown
/lp group vip permission set chatchannels.bypass.cooldown true

# Revoke the trade channel from new accounts
/lp group default permission set chatchannels.use.trade false
```

---

## Permission Summary Table

| Permission | Player | VIP | Helper | Moderator | OP |
|------------|--------|-----|--------|-----------|----|
| `chatchannels.use` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `chatchannels.use.global` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `chatchannels.use.local` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `chatchannels.use.trade` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `chatchannels.use.staff` | ✗ | ✗ | ✓ | ✓ | ✓ |
| `chatchannels.bypass.cooldown` | ✗ | ✓ | ✗ | ✓ | ✓ |
| `chatchannels.bypass.filter` | ✗ | ✗ | ✗ | ✓ | ✓ |
| `chatchannels.mute` | ✗ | ✗ | ✗ | ✓ | ✓ |
| `chatchannels.spy` | ✗ | ✗ | ✗ | ✓ | ✓ |
| `chatchannels.admin` | ✗ | ✗ | ✗ | ✗ | ✓ |

---

[Home](Home) · [Installation](Installation) · [Configuration](Configuration) · [Channel Formats](Channel-Formats) · [PlaceholderAPI](PlaceholderAPI)
