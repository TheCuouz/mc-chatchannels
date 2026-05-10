# ChatChannels — Configuration Reference

## channels.yml

### Channel definition

Each channel is defined under `channels:` with a unique string ID.

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `display-name` | String (MiniMessage) | channel id | Name shown in `/channels` list |
| `quick-prefix` | String | `""` | Type this at message start to send to channel without switching |
| `range` | int | `-1` | Radius in blocks. `-1` = global (all worlds, all players online) |
| `permission` | String | `chatchannels.use.<id>` | Permission node required to use this channel |
| `format` | String (MiniMessage) | `<player>: <message>` | Message format. Tokens: `<player>`, `<message>`. PAPI placeholders supported |
| `cooldown-seconds` | int | `0` | Seconds between messages in this channel. `0` = no cooldown |

**Format tokens:**
- `<player>` — replaced with the sender's name
- `<message>` — replaced with the message content
- Any `%placeholder%` — resolved via PlaceholderAPI if installed

### `default-channel`

The channel players are assigned to when they first join. Must match a channel ID.

### Spam filter (`filters.spam`)

| Key | Default | Description |
|-----|---------|-------------|
| `enabled` | `true` | Enable/disable spam filter entirely |
| `duplicate-window-seconds` | `5` | Block if player sends the exact same message within this window |
| `caps-threshold-pct` | `70` | Block if more than 70% of letters are uppercase |
| `caps-min-length` | `10` | Minimum message length for caps check to apply |
| `flood-window-seconds` | `5` | Time window for flood detection |
| `flood-max-messages` | `4` | Maximum messages allowed within the flood window |

### Word filter (`filters.words`)

| Key | Default | Description |
|-----|---------|-------------|
| `mode` | `REPLACE` | `REPLACE` = replace matched words with the replacement string; `BLOCK` = cancel the message entirely |
| `list` | `[]` | List of blacklisted words. Matching is case-insensitive and word-boundary aware |
| `replacement` | `****` | String used to replace matched words (only for `REPLACE` mode) |

## messages.yml

All messages use MiniMessage format. Full reference: https://docs.advntr.dev/minimessage/format.html

| Key | Default description |
|-----|---------------------|
| `prefix` | Plugin prefix for system messages |
| `channel-switched` | Shown when player switches channel (`<channel>` token) |
| `channel-not-found` | Channel ID not found (`<id>` token) |
| `channel-no-permission` | No permission for channel |
| `channel-list-header` | Header for `/channels` output |
| `channel-list-entry` | Entry line for `/channels` (`<channel>` and `<prefix>` tokens) |
| `channel-cooldown` | Cooldown active (`<seconds>` token) |
| `channel-muted` | Player is muted in this channel |
| `mute-applied` | Mute applied with expiry (`<target>`, `<channel>`, `<duration>` tokens) |
| `mute-permanent` | Permanent mute applied (`<target>`, `<channel>` tokens) |
| `mute-not-found` | Target player not found (`<target>` token) |
| `mute-usage` | `/mute` usage hint |
| `spy-enabled` | Spy mode turned on |
| `spy-disabled` | Spy mode turned off |
| `spy-prefix` | Prefix added before spy messages |
| `reload-success` | Config reloaded successfully |
| `filter-spam-duplicate` | Blocked: duplicate message |
| `filter-spam-caps` | Blocked: too many caps |
| `filter-spam-flood` | Blocked: flooding |
| `filter-word-block` | Blocked: contains blacklisted word |
