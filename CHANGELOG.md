# Changelog

All notable changes to ChatChannels are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.1.0] — 2026-05-14

### Added
- **Per-channel hide (self-mute receive-side).** Players can now hide a channel from their own view without affecting other players:
  - `/cc hide <#channel>` — stop receiving messages from that channel
  - `/cc show <#channel>` — restore the channel to your view
  - `/cc hidden` — list the channels you currently have hidden
- New permission `chatchannels.hide` (default `true`) gating the player-side commands above
- Persistent storage of hidden channels in `plugins/ChatChannels/hidden_channels.yml`, preserved across restarts
- New `messages.yml` keys: `channel-hidden`, `channel-shown`, `channel-not-hidden`, `channel-already-hidden`, `hidden-list-empty`, `hidden-list`

### Changed
- `/cc` is no longer admin-only at the command level. Subcommand-level permission checks now route reload to `chatchannels.admin` and the new hide/show/hidden to `chatchannels.hide`.
- `channel-not-found` message now uses the `{channel}` placeholder (legacy `<id>` still supported in `/channel <id>`)

### Notes
- The new feature is **receive-side**: a player who hides a channel still appears in every recipient list for everyone else, and other players continue to see normal traffic.
- This is intentionally distinct from `/mute <player> [channel]`, which is **send-side** and admin-only: it silences a specific player from sending into a channel. Both systems are now available and operate independently.

## [1.0.0] — 2026-05-10

### Added
- Multiple configurable chat channels (`global`, `local`, `trade`, `staff` out of the box)
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
