# Changelog

All notable changes to ChattyChannels are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.0.0] — 2026-05-10

### Added
- Multiple configurable chat channels (global, local, trade, staff out of the box)
- Quick-prefix system: type `!`, `#`, `@` at message start to target a channel instantly
- Range-based local channels (configurable block radius per channel)
- Per-channel message cooldowns with bypass permission
- Mute system: `/mute <player> [channel] [duration]` with channel scope and expiry time
- Staff spy mode: `/chatspy` to see all channels simultaneously with `[SPY]` prefix
- Spam filter: duplicate message detection, caps threshold (70% configurable), flood protection
- Word filter: REPLACE and BLOCK modes, word-boundary matching, case-insensitive
- PlaceholderAPI integration: `%chatchannels_active%` and `%chatchannels_muted%`
- bStats anonymous usage metrics
- Commands: `/channel`, `/ch`, `/channels`, `/mute`, `/chatspy`, `/cc reload`
- Full persistence: active channel saved across restarts, mutes saved with expiry
- MiniMessage formatting throughout — no legacy color codes
