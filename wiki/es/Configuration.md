> 🌐 [English](../Configuration.md) · **Español**

# Configuración

Toda la configuración de ChatChannels se encuentra en dos archivos:

```
plugins/ChatChannels/
├── channels.yml    # Definiciones de canal + configuración del filtro de spam/palabras
└── messages.yml    # Cadenas de cara al jugador (voz del plugin con prefijo TTS)
```

Tras editar cualquiera de los dos, ejecuta `/cc reload` — no es necesario reiniciar.

> Las cadenas de voz del plugin (`messages.yml`) se etiquetan con el prefijo TTS `◈ Channels:` al entregarse. El contenido del chat del canal **no**: el `format:` de cada canal es la única fuente de verdad sobre cómo aparecen los mensajes de los jugadores.

---

## `channels.yml` completo

```yaml
# ChatChannels — configuración de canales
# range: -1  → global (todos los mundos, todos los jugadores en línea)
# range: N   → solo jugadores dentro de N bloques en el mismo mundo
# quick-prefix: escribe esto al inicio de tu mensaje para enviarlo a ese
#               canal sin cambiar el canal activo

channels:
  global:
    display-name: "<gold>Global"
    quick-prefix: "!"
    range: -1
    permission: "chatchannels.use.global"
    format: "<gray>[<gold>G</gold>]</gray> %luckperms_prefix%<player><reset>: <message>"
    cooldown-seconds: 2

  local:
    display-name: "<green>Local"
    quick-prefix: ""
    range: 100
    permission: "chatchannels.use.local"
    format: "<gray>[<green>L</green>]</gray> <player>: <message>"
    cooldown-seconds: 1

  trade:
    display-name: "<aqua>Trade"
    quick-prefix: "#"
    range: -1
    permission: "chatchannels.use.trade"
    format: "<gray>[<aqua>$</aqua>]</gray> <player>: <message>"
    cooldown-seconds: 3

  staff:
    display-name: "<dark_red>Staff"
    quick-prefix: "@"
    range: -1
    permission: "chatchannels.use.staff"
    format: "<dark_red>[STAFF]</dark_red> <player>: <message>"
    cooldown-seconds: 0

default-channel: "local"

filters:
  spam:
    enabled: true
    duplicate-window-seconds: 5
    caps-threshold-pct: 70
    caps-min-length: 10
    flood-window-seconds: 5
    flood-max-messages: 4
  words:
    mode: REPLACE
    list:
      - "badword1"
      - "badword2"
    replacement: "****"
```

---

## Anatomía de un canal

| Clave | Tipo | Propósito |
|-------|------|-----------|
| `display-name` | Cadena MiniMessage | Se muestra en `/channels`, `/channel` y mensajes de voz del plugin |
| `quick-prefix` | cadena | Escribe esto al inicio de una línea de chat para enrutar el mensaje a este canal (cadena vacía = sin prefijo rápido) |
| `range` | int | `-1` para todo el servidor; N positivo para "solo jugadores dentro de N bloques en el mismo mundo" |
| `permission` | cadena | Permiso de Bukkit necesario para **leer y escribir** en este canal |
| `format` | Cadena MiniMessage | Plantilla de cómo se renderiza el contenido del chat. Usa `<player>`, `<message>` y cualquier placeholder de PAPI |
| `cooldown-seconds` | int | Cooldown anti-spam por jugador específico de este canal. `0` = sin cooldown. Los jugadores con `chatchannels.bypass.cooldown` lo ignoran |

La clave `default-channel:` en el nivel superior establece el canal en que los nuevos jugadores publican antes de ejecutar `/channel`.

---

## Añadir un nuevo canal

Supongamos que quieres un canal `recruit` para los nuevos jugadores que sea global pero con límite de velocidad y su propio formato MiniMessage. Añade esto bajo `channels:` en `channels.yml`:

```yaml
recruit:
  display-name: "<light_purple>Recruit"
  quick-prefix: "?"
  range: -1
  permission: "chatchannels.use.recruit"
  format: "<gray>[<light_purple>?</light_purple>]</gray> <player>: <message>"
  cooldown-seconds: 5
```

Luego otorga el permiso a tu grupo por defecto:

```bash
/lp group default permission set chatchannels.use.recruit true
```

Finalmente, recarga:

```text
/cc reload
```

> Los permisos nuevos añadidos mediante `channels.yml` **no** se registran automáticamente en `plugin.yml`. Los jugadores siguen necesitando que se les otorgue el permiso mediante LuckPerms (o establecerlo como `default: true` en el nodo de permiso de tu plugin de permisos). Bukkit tratará los nodos de permiso no registrados como `default: false` para los no-ops.

---

## Filtro de spam

Se encuentra bajo `filters.spam` en `channels.yml`. Tres comprobaciones independientes se ejecutan en cada mensaje; cualquiera de ellas puede bloquearlo.

| Clave | Por defecto | Efecto |
|-------|-------------|--------|
| `enabled` | `true` | Interruptor principal. Ponlo en `false` para desactivar las tres comprobaciones de spam |
| `duplicate-window-seconds` | `5` | Bloquea mensajes idénticos enviados por el mismo jugador dentro de esta ventana |
| `caps-threshold-pct` | `70` | Si ≥ este % de letras son mayúsculas, el mensaje se bloquea |
| `caps-min-length` | `10` | La comprobación de mayúsculas solo se ejecuta en mensajes de al menos esta longitud (evita bloquear `OK` o `LOL`) |
| `flood-window-seconds` | `5` | La comprobación de flood cuenta los mensajes enviados en esta ventana deslizante |
| `flood-max-messages` | `4` | Si un jugador envía más que esta cantidad en `flood-window-seconds`, los mensajes adicionales se bloquean |

Los jugadores con `chatchannels.bypass.filter` saltan las tres comprobaciones.

Los motivos de bloqueo se mapean a diferentes mensajes de cara al jugador en `messages.yml`:

| Motivo | Clave del mensaje |
|--------|------------------|
| Duplicado | `filter-spam-duplicate` |
| Abuso de mayúsculas | `filter-spam-caps` |
| Flood | `filter-spam-flood` |

---

## Filtro de palabras

Se encuentra bajo `filters.words`.

| Clave | Tipo | Efecto |
|-------|------|--------|
| `mode` | `REPLACE` o `BLOCK` | `REPLACE` enmascara cada palabra coincidente con `replacement`; `BLOCK` cancela el mensaje completo |
| `list` | lista de cadenas | Palabras a filtrar (sin distinción de mayúsculas/minúsculas, coincidencia de palabra completa) |
| `replacement` | cadena | La máscara que se muestra en modo `REPLACE` (por defecto `****`) |

Ejemplos:

```yaml
# Moderación suave — enmascarar palabras ofensivas pero dejar pasar el mensaje
filters:
  words:
    mode: REPLACE
    list: ["slur1", "slur2"]
    replacement: "***"

# Moderación estricta — eliminar cualquier mensaje que contenga una palabra prohibida
filters:
  words:
    mode: BLOCK
    list: ["scamlink.example", "ddosthreat"]
```

Los mensajes bloqueados activan el mensaje `filter-word-block`. Los jugadores con `chatchannels.bypass.filter` saltan el filtro de palabras.

---

## `messages.yml`

Cada línea de voz del plugin. Edítalo para traducir o cambiar la marca. Los placeholders entre corchetes angulares se sustituyen en el momento del envío.

```yaml
prefix: "<gray>[<gold>ChatChannels</gold>]</gray> "

channel-switched:        "<green>Cambiaste al canal <channel>."
channel-not-found:       "<red>Canal '<id>' no encontrado."
channel-no-permission:   "<red>No tienes permiso para usar ese canal."
channel-list-header:     "<gold>Canales disponibles:</gold>"
channel-list-entry:      "  <gray>- <channel> <dark_gray>(<prefix>)</dark_gray>"
channel-cooldown:        "<red>Espera <seconds>s antes de enviar otro mensaje."
channel-muted:           "<red>Estás silenciado en ese canal."

mute-applied:            "<green><target> silenciado en <channel> por <duration>."
mute-permanent:          "<green><target> silenciado en <channel> permanentemente."
mute-not-found:          "<red>Jugador '<target>' no encontrado."
mute-usage:              "<red>Uso: /mute <jugador> [canal] [duración: 10m, 1h, 2d]"

spy-enabled:             "<gold>Modo espía <green>activado</green>."
spy-disabled:            "<gold>Modo espía <red>desactivado</red>."
spy-prefix:              "<dark_gray>[SPY] "

reload-success:          "<green>ChatChannels recargado correctamente."

filter-spam-duplicate:   "<red>No envíes el mismo mensaje repetidamente."
filter-spam-caps:        "<red>Por favor escribe en minúsculas."
filter-spam-flood:       "<red>Estás enviando mensajes muy rápido."
filter-word-block:       "<red>Tu mensaje contiene palabras no permitidas."
```

> Todas estas cadenas se entregan con el prefijo TTS-Studio `◈ Channels:` automáticamente — no lo dupliques dentro de la propia cadena.

---

## `config.yml`

Actualmente es un stub usado solo para rastrear la versión del esquema de configuración:

```yaml
config-version: 1
```

Este archivo existe para que las futuras migraciones tengan un ancla de versión limpia. Raramente necesitarás modificarlo.

---

[Inicio](Home.md) · [Instalación](Installation.md) · [Comandos y permisos](Commands-and-Permissions.md) · [Formatos de canal](Channel-Formats.md) · [PlaceholderAPI](PlaceholderAPI.md)
