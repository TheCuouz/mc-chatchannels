> 🌐 **English** · [Español](es/Installation.md)

# Installation

ChattyChannels installs in under a minute: drop the JAR, restart the server, done. The default `channels.yml` ships with four production-ready channels so you can verify the plugin works before customising anything.

---

## Requirements

| Requirement | Version | Mandatory | Notes |
|-------------|---------|-----------|-------|
| **Paper** | 1.21.x | Yes | Uses `io.papermc.paper.event.player.AsyncChatEvent` — Spigot and CraftBukkit are not supported |
| **Java** | 21+ | Yes | Paper 1.21 requires Java 21 |
| **PlaceholderAPI** | 2.11.6+ | No | Enables `%chatchannels_*%` placeholders and lets channel formats embed other plugins' placeholders (e.g. `%luckperms_prefix%`) |
| **LuckPerms** | latest | No | When combined with PAPI, rank prefixes flow into channel formats |

> Folia is not supported in this release (`folia-supported: false` in `plugin.yml`).

---

## Step-by-step

### 1. Drop the JAR

```bash
cp ChattyChannels-1.0.0.jar /path/to/server/plugins/
```

### 2. Restart the server

A full restart (or first start) is required so the plugin can generate its configuration files. Use `stop` from the console — do not use `/reload confirm`, which can leave plugins in an inconsistent state.

### 3. Verify the console banner

When ChattyChannels enables you will see the framed TTS-Studio banner:

```
╔═══════════════════════════════════════════════╗
║                                               ║
║                ChattyChannels v1.0.0            ║
║                                               ║
╚═══════════════════════════════════════════════╝
  ◈ Channels: 4 channels · PAPI ✓ · ready in 42ms
```

The status line reports:
- The number of channels successfully loaded from `channels.yml`
- Whether PlaceholderAPI was detected (`PAPI ✓` / `no PAPI`)
- The cold-start time in milliseconds

If you see `no PAPI` and want placeholder support, install PlaceholderAPI and restart.

### 4. Confirm the default channels

The plugin ships with four channels in `plugins/ChattyChannels/channels.yml`:

| ID | Display | Quick Prefix | Range | Permission |
|----|---------|--------------|-------|------------|
| `global` | Global | `!` | `-1` (server-wide) | `chatchannels.use.global` |
| `local` | Local | *(none)* | `100` blocks | `chatchannels.use.local` |
| `trade` | Trade | `#` | `-1` (server-wide) | `chatchannels.use.trade` |
| `staff` | Staff | `@` | `-1` (server-wide) | `chatchannels.use.staff` |

Default channel on join: **Local**.

Run `/channels` in-game to see the active list filtered by your permissions.

### 5. First message

```
> /channel trade
◈ Channels: Cambiaste al canal Trade.

> WTS 64 diamonds for 8 emeralds each
[$] Steve: WTS 64 diamonds for 8 emeralds each
```

Or inline, without switching:

```
> !hello world
[G] Steve: hello world
```

---

## File Layout After First Run

```
plugins/
└── ChattyChannels/
    ├── channels.yml      # All channel definitions + filter settings
    ├── config.yml        # Plugin-wide settings (config-version)
    └── messages.yml      # All player-facing strings (MiniMessage)
```

---

## Reload vs Restart

| Action | Command | When to use |
|--------|---------|-------------|
| Hot reload | `/cc reload` | After editing `channels.yml` or `messages.yml` |
| Server restart | `stop` then start | After replacing the JAR or upgrading Paper/Java |

> `/cc reload` re-reads `channels.yml` (channels + filters) and `messages.yml`. It does **not** drop player mute state or active-channel assignments — those persist across reloads.

---

## Upgrading

1. Stop the server.
2. Replace `ChattyChannels-X.X.X.jar` in `plugins/`.
3. Start the server. The plugin will not overwrite your edited `channels.yml`.
4. Check the changelog for any new keys you may want to add manually.

---

[Home](Home) · [Commands & Permissions](Commands-and-Permissions) · [Configuration](Configuration) · [Channel Formats](Channel-Formats) · [PlaceholderAPI](PlaceholderAPI)
