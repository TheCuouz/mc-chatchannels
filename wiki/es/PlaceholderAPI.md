> 🌐 [English](../PlaceholderAPI.md) · **Español**

# Integración con PlaceholderAPI

ChattyChannels registra una expansión PAPI `chatchannels` cuando [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) está instalado. La expansión expone el estado del canal de chat del jugador actual para uso en marcadores, listas de TAB, formateadores de chat y plugins de HUD.

---

## Configuración

### 1. Instala PlaceholderAPI

```bash
# Coloca PAPI en plugins/ y reinicia
# O descárgalo mediante el eCloud:
/papi ecloud download Player
/papi reload
```

> La expansión propia de ChattyChannels se registra **internamente** cuando el plugin se habilita — no está en el eCloud de PAPI. No necesitas ejecutar `/papi ecloud download ChattyChannels`.

### 2. Verifica el hook

Al habilitarse, el banner de consola informa del estado de PAPI:

```
  ◈ Channels: 4 channels · PAPI ✓ · ready in 42ms
```

También puedes verificarlo en el juego:

```bash
/papi test <tunombre> %chatchannels_active%
# Resultado esperado: trade  (o el canal que tengas activo)
```

---

## Referencia de placeholders

| Placeholder | Devuelve | Ejemplo de salida |
|-------------|----------|-------------------|
| `%chatchannels_active%` | El ID del canal activo actual del jugador | `trade` |
| `%chatchannels_muted%` | `true` si el jugador está silenciado en su canal activo, `false` en caso contrario | `false` |

> Si PlaceholderAPI no está instalado, estos placeholders se renderizarán literalmente (`%chatchannels_active%`) en lugar de expandirse. Protégete siempre ante eso en los formatos que distribuyas públicamente.

### `%chatchannels_active%`

Devuelve el **ID interno del canal** (la clave YAML en `channels.yml`) al que el jugador está publicando actualmente. Para la configuración por defecto eso es uno de `global`, `local`, `trade` o `staff`. Los nuevos canales personalizados que añadas devolverán su ID personalizado.

Útil para:
- Mostrar el canal actual en un marcador o HUD
- Formato de chat condicional en `DeluxeChat` / `EssentialsChat`
- Sufijos del plugin TAB que cambian con el canal activo

### `%chatchannels_muted%`

Devuelve la cadena literal `true` o `false` dependiendo de si el jugador está actualmente silenciado **en su canal activo**. Ten en cuenta que esto comprueba el canal activo específicamente — un jugador silenciado en `trade` pero actualmente en `local` devolverá `false`.

Si necesitas detección de silenciamiento en todo el servidor, silencia al jugador con el canal `*`:

```bash
/mute Steve * 1h
```

Eso hará que `%chatchannels_muted%` devuelva `true` independientemente del canal al que cambien.

---

## Ejemplos de uso

### Marcador (FeatherBoard / AnimatedScoreboard)

```yaml
lines:
  - "&7Canal: &b%chatchannels_active%"
  - "&7Silenciado: &c%chatchannels_muted%"
```

### Sufijo del plugin TAB

```yaml
tablist-name: "%player_name% &8| &7%chatchannels_active%"
```

### Formato condicional

Usando una expansión de placeholder condicional (por ejemplo, `PlaceholderAPI-Expansion-Conditions`):

```yaml
# Mostrar un marcador rojo junto a los jugadores silenciados
name: "{condition: %chatchannels_muted%=true}&c[silenciado] &7%player_name%|%player_name%}"
```

### Dentro de un `format:` de canal (auto-referencia)

Aunque el formato del canal ya implica el canal, puedes incluir el ID para mayor claridad en los canales de administración:

```yaml
staff:
  format: "<dark_red>[STAFF/%chatchannels_active%]</dark_red> <player>: <message>"
```

---

## Uso de placeholders de otros plugins en los formatos de canal

Lo inverso también está soportado: ChattyChannels ejecuta cada `format:` de canal a través de PAPI antes del análisis de MiniMessage, para poder incrustar cualquier expansión instalada. El formato Global por defecto hace exactamente esto con LuckPerms:

```yaml
format: "<gray>[<gold>G</gold>]</gray> %luckperms_prefix%<player><reset>: <message>"
```

Otras incrustaciones populares:

| Placeholder | Fuente | Caso de uso |
|-------------|--------|-------------|
| `%luckperms_prefix%` | LuckPerms | Inyectar prefijo de grupo en el chat |
| `%player_world%` | PAPI integrado | Mostrar el nombre del mundo del remitente |
| `%vault_eco_balance_formatted%` | Vault | Mostrar la riqueza del jugador en el canal trade |
| `%essentials_nickname%` | Essentials | Usar el apodo establecido del jugador |

---

## Matriz de compatibilidad

| Plugin | Funciona con los placeholders de ChattyChannels |
|--------|----------------------------------------------|
| TAB by NEZNAMY | ✓ |
| FeatherBoard | ✓ |
| AnimatedScoreboard | ✓ |
| CMI | ✓ |
| DeluxeChat | ✓ |
| EssentialsChat | ✓ |
| HolographicDisplays | ✓ (mediante puente PAPI) |

---

## Solución de problemas

| Síntoma | Causa probable |
|---------|---------------|
| El placeholder se muestra literalmente como `%chatchannels_active%` | PlaceholderAPI no está instalado, o ChattyChannels se cargó **antes** que PAPI |
| Devuelve una cadena vacía | El jugador está desconectado, o se consulta como `console` (los placeholders requieren un contexto de `Player`) |
| Siempre devuelve `local` | El jugador nunca ha ejecutado `/channel`, así que sigue en el `default-channel:` configurado |
| `%chatchannels_muted%` devuelve `false` aunque el jugador esté silenciado | El silenciamiento se aplica a un canal diferente al canal activo del jugador — comprueba con `/mute` en el canal correcto o usa `*` |

---

[Inicio](Home.md) · [Instalación](Installation.md) · [Comandos y permisos](Commands-and-Permissions.md) · [Configuración](Configuration.md) · [Formatos de canal](Channel-Formats.md)
