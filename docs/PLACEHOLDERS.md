# ChattyChannels — PlaceholderAPI Placeholders

Requires [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) installed on the server.

| Placeholder | Returns | Example |
|-------------|---------|---------|
| `%chatchannels_active%` | The player's current active channel ID | `global` |
| `%chatchannels_muted%` | Whether the player is muted in their active channel | `true` / `false` |

## Usage examples

In a scoreboard or tab-list plugin:
```
Active channel: %chatchannels_active%
```

In a condition check (e.g. DeluxeMenus):
```yaml
condition: '%chatchannels_muted% == false'
```
