> 🌐 **English** · [Español](es/Home.md)

# Channels Wiki

```
   ██████╗██╗  ██╗ █████╗ ███╗   ██╗███╗   ██╗███████╗██╗     ███████╗
  ██╔════╝██║  ██║██╔══██╗████╗  ██║████╗  ██║██╔════╝██║     ██╔════╝
  ██║     ███████║███████║██╔██╗ ██║██╔██╗ ██║█████╗  ██║     ███████╗
  ██║     ██╔══██║██╔══██║██║╚██╗██║██║╚██╗██║██╔══╝  ██║     ╚════██║
  ╚██████╗██║  ██║██║  ██║██║ ╚████║██║ ╚████║███████╗███████╗███████║
   ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═══╝╚══════╝╚══════╝╚══════╝
```

**Multi-channel chat for Paper 1.21.x** — split Global, Local, Trade, and Staff into independent streams with permissions, radius, anti-spam, mutes, and admin spy mode. Part of the **TTS-Studio** plugin suite.

> Brand alias: **Channels** (the full name "ChatChannels" is 12 chars; TTS uses the shortened alias for prefixes and the console banner). Brand color: sky blue `#5DADE2`.

---

## Console Banner

On enable, ChatChannels emits a framed TTS-Studio banner:

```
╔═══════════════════════════════════════════════╗
║                                               ║
║                ChatChannels v1.0.0            ║
║                                               ║
╚═══════════════════════════════════════════════╝
  ◈ Channels: 4 channels · PAPI ✓ · ready in 42ms
```

The `◈ Channels:` prefix (TTS chat-prefix) is used for **plugin-voice messages only** — feedback like `you joined #trade`, `channel not found`, `mute applied`, `reload complete`. **Actual chat content** is rendered using each channel's own `format:` string from `channels.yml` and is **never** wrapped in the TTS prefix. That separation is intentional: branding plugin output is good UX; branding player speech would mis-attribute every message in the server to ChatChannels.

---

## Feature Highlights

| Feature | Description |
|---------|-------------|
| **Configurable Channels** | Define any number of channels in `channels.yml` — name, format, range, permission, cooldown, quick prefix |
| **Quick Prefixes** | Type `!hi` for Global, `#wts diamonds` for Trade, `@meeting` for Staff — no channel switch required |
| **Radius / Global** | `range: -1` = server-wide; `range: 100` = only players within 100 blocks in the same world |
| **MiniMessage Formats** | Full Adventure MiniMessage support per channel — colors, gradients, hover, click |
| **Spam Filter** | Duplicate, caps-lock, and flood detection with bypass permission |
| **Word Filter** | `REPLACE` (mask matched words) or `BLOCK` (drop entire message) modes |
| **Timed Mutes** | `/mute Steve trade 10m` — per-channel or server-wide, with duration parser (`10m`, `1h`, `2d`) |
| **Chat-Spy** | Admins see all channels, including those they can't normally read — prefixed with `[SPY]` |
| **PlaceholderAPI** | `%chatchannels_active%`, `%chatchannels_muted%` |
| **LuckPerms Aware** | `%luckperms_prefix%` flows through channel formats out of the box |
| **TTS-Studio Console Banner** | Framed enable/disable banner with status, hooks, and ready-time |

---

## Quick Start

```bash
# 1. Drop the jar into plugins/
cp ChatChannels-1.0.0.jar plugins/

# 2. Restart the server — channels.yml is generated automatically

# 3. List available channels
/channels

# 4. Switch active channel
/channel trade

# 5. Or just use quick prefixes inline
!hello everyone        # → Global
#WTS 64 diamonds       # → Trade
@staff meeting now     # → Staff
```

> Tip — run `/cc reload` after editing `channels.yml` or `messages.yml`. No restart needed.

---

## Wiki Navigation

| Page | Contents |
|------|----------|
| [Installation](Installation) | Requirements, JAR deployment, first-run checklist |
| [Commands & Permissions](Commands-and-Permissions) | Full command table, permission tree, examples |
| [Configuration](Configuration) | `channels.yml` anatomy, adding channels, spam + word filter settings |
| [Channel Formats](Channel-Formats) | MiniMessage deep-dive, placeholder list, format recipes |
| [PlaceholderAPI](PlaceholderAPI) | Exposed placeholders and integration examples |

---

## Requirements

| Requirement | Version | Notes |
|-------------|---------|-------|
| Paper | 1.21.x | Uses `AsyncChatEvent` — Spigot is **not** supported |
| Java | 21+ | Required |
| PlaceholderAPI | 2.11.6+ | Optional — enables `%chatchannels_*%` and `%luckperms_prefix%` in formats |
| LuckPerms | latest | Optional — used via PAPI to inject rank prefixes into chat |

---

*ChatChannels — part of the [TTS-Studio](https://ttsstudio.dev) plugin suite. Sky-blue branded, professionally maintained.*
