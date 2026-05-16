# ChattyChannels — Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `chatchannels.use` | `true` | Basic access to ChattyChannels commands |
| `chatchannels.use.global` | `true` | Access to the global channel |
| `chatchannels.use.local` | `true` | Access to the local channel |
| `chatchannels.use.trade` | `true` | Access to the trade channel |
| `chatchannels.use.staff` | `op` | Access to the staff channel |
| `chatchannels.spy` | `op` | Toggle spy mode to see all channels |
| `chatchannels.mute` | `op` | Mute players from channels |
| `chatchannels.bypass.cooldown` | `op` | Bypass channel cooldowns |
| `chatchannels.bypass.filter` | `op` | Bypass spam and word filters |
| `chatchannels.admin` | `op` | Admin commands (`/cc reload`) |

> For custom channels, the permission node is `chatchannels.use.<channel-id>` unless overridden in `channels.yml`.

## Recommended LuckPerms setup

```sh
# Default players
lp group default permission set chatchannels.use true
lp group default permission set chatchannels.use.global true
lp group default permission set chatchannels.use.local true
lp group default permission set chatchannels.use.trade true

# Staff / moderators
lp group staff permission set chatchannels.use.staff true
lp group staff permission set chatchannels.spy true
lp group staff permission set chatchannels.mute true
lp group staff permission set chatchannels.bypass.cooldown true
lp group staff permission set chatchannels.bypass.filter true
lp group staff permission set chatchannels.admin true
```
