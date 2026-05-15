# Changelog

All notable changes to ChatChannels are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [1.2.0] — 2026-05-15

### Added
- **Private messaging system** — `/msg` (aliases: `/tell`, `/whisper`, `/w`), `/reply` (alias: `/r`), and `/ignore` with persistent YAML ignore list
- **Social spy extended** — `/chatspy` now intercepts private messages in addition to channel messages
- **Chat log** — daily-rotating log at `plugins/ChatChannels/logs/chat-YYYY-MM-DD.log` covering both channel messages and PMs; toggled via `logging.enabled` in `config.yml`
- **Friends system** — `/friend add/accept/deny/remove/list/requests/notify` with clickable Accept/Reject buttons in chat; friend online/offline notifications with per-player toggle
- **Cross-server support (optional)** — configure MySQL in `config.yml` to enable: PM routing to players on other servers via BungeeCord plugin messaging channel `chatchannels:pm`, shared friend list and session tracking across instances
- New permissions: `chatchannels.pm.send`, `chatchannels.pm.receive`, `chatchannels.pm.bypass-ignore`, `chatchannels.ignore`, `chatchannels.friends`
- New config keys: `server-name`, `mysql.*`, `logging.enabled`, `friends.max-friends`, `friends.request-ttl-days`, `friends.notify-on-join`

### Notes
- MySQL is **optional**. Without it, all features work in single-server mode with YAML persistence.
- All servers in a BungeeCord network must have the same jar installed for cross-server messaging to work.
- The `bStats` plugin ID is still a placeholder (`12345`) — replace before publishing.

---

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
