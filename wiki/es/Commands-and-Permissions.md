> 🌐 [English](../Commands-and-Permissions.md) · **Español**

# Comandos y permisos

Todos los comandos de ChattyChannels y sus permisos se resumen a continuación. Los valores por defecto de los permisos siguen `plugin.yml` y son los mismos que los de una instalación nueva.

---

## Comandos

| Comando | Alias | Descripción | Permiso |
|---------|-------|-------------|---------|
| `/channel <id>` | `/ch` | Cambia tu canal de chat activo (o muestra el actual si no hay argumento) | `chatchannels.use` |
| `/channels` | — | Lista todos los canales para los que tienes permiso de uso | `chatchannels.use` |
| `/mute <jugador> [canal] [duración]` | — | Silencia a un jugador en un canal (o `*` para todos) | `chatchannels.mute` |
| `/chatspy` | — | Activa o desactiva el modo espía — ver mensajes de canales que normalmente no puedes leer | `chatchannels.spy` |
| `/cc reload` | — | Recarga `channels.yml` y `messages.yml` | `chatchannels.admin` |

> Todo el feedback del plugin (cadenas de éxito / error / uso) viene prefijado con la etiqueta TTS-Studio `◈ Channels:`. El contenido del chat de los jugadores usa el `format:` propio de cada canal y **no** lleva el prefijo del plugin.

---

## Detalles de los comandos

### `/channel <id>`

Cambia tu canal activo. Sin argumento, informa del canal en el que estás publicando actualmente.

```text
> /channel
◈ Channels: Canal activo: Trade

> /channel staff
◈ Channels: Cambiaste al canal Staff.

> /channel unknown
◈ Channels: Canal 'unknown' no encontrado.
```

El autocompletado con Tab sugiere solo los canales para los que tienes permiso.

### `/channels`

Lista todos los canales que son visibles para ti con su prefijo rápido.

```text
> /channels
◈ Channels: Canales disponibles:
◈ Channels:   - Global (!)
◈ Channels:   - Local (sin prefijo)
◈ Channels:   - Trade (#)
◈ Channels:   - Staff (@)
```

### `/mute <jugador> [canal] [duración]`

Silencia a un jugador en un canal específico, o en todos los canales cuando `canal` se omite o se establece en `*`.

**Sintaxis de duración** (analizada por `DurationParser`):

| Token | Significado | Ejemplo |
|-------|-------------|---------|
| `Nm` | Minutos | `10m` |
| `Nh` | Horas | `2h` |
| `Nd` | Días | `7d` |
| *(omitido)* | Permanente | — |

```text
# Silenciamiento permanente en todo el servidor
/mute Steve

# Silenciamiento de 10 minutos solo en el canal trade
/mute Steve trade 10m

# Silenciamiento de 1 hora en todo el servidor
/mute Steve * 1h
```

Los jugadores silenciados ven:

```text
◈ Channels: Estás silenciado en ese canal.
```

### `/chatspy`

Activa o desactiva el modo espía para el jugador que lo ejecuta. Los espías reciben una copia de cada mensaje de canal que de otro modo no verían (por ejemplo, mensajes en `staff` mientras están en supervivencia, o mensajes fuera del radio de `local`). Las copias de espía van prefijadas con `<dark_gray>[SPY]` para que sean visualmente distintas del chat normal.

> Los espías que ya ven un mensaje normalmente (porque tienen permiso y están dentro del rango) **no** reciben una copia `[SPY]` duplicada.

### `/cc reload`

Vuelve a leer `channels.yml` (definiciones de canal, filtro de spam, filtro de palabras) y `messages.yml`. El estado de los jugadores (canal activo, silenciamientos) se conserva.

```text
> /cc reload
◈ Channels: ChattyChannels recargado correctamente.
```

---

## Prefijos rápidos (sin necesidad de comando)

Cada canal puede definir un `quick-prefix:` en `channels.yml`. Comenzar un mensaje con ese prefijo lo enruta a ese canal **solo para ese mensaje**, sin cambiar tu canal activo.

| Prefijo por defecto | Canal |
|--------------------|-------|
| `!` | Global |
| *(ninguno)* | Local |
| `#` | Trade |
| `@` | Staff |

```text
> !qué tal a todos       # → Global, aunque tu canal activo sea Local
> #vendo 64 diamantes    # → Trade
> @reunión en 5          # → Staff
```

El enrutamiento por prefijo rápido sigue respetando permisos, silenciamientos, cooldowns y filtros.

---

## Árbol de permisos

### Acceso a canales

| Permiso | Por defecto | Efecto |
|---------|-------------|--------|
| `chatchannels.use` | `true` | Usar `/channel` y `/channels` |
| `chatchannels.use.global` | `true` | Leer + escribir en el canal Global |
| `chatchannels.use.local` | `true` | Leer + escribir en el canal Local |
| `chatchannels.use.trade` | `true` | Leer + escribir en el canal Trade |
| `chatchannels.use.staff` | `op` | Leer + escribir en el canal Staff |

> El `permission:` de un canal regula **tanto el envío como la recepción**. Un jugador sin `chatchannels.use.staff` no verá ni enviará mensajes de staff (a menos que tenga `chatchannels.spy`).

### Administración / Moderación

| Permiso | Por defecto | Efecto |
|---------|-------------|--------|
| `chatchannels.mute` | `op` | Usar `/mute` |
| `chatchannels.spy` | `op` | Usar `/chatspy` |
| `chatchannels.admin` | `op` | Usar `/cc reload` |
| `chatchannels.bypass.cooldown` | `op` | Ignorar el cooldown por canal |
| `chatchannels.bypass.filter` | `op` | Ignorar los filtros de spam y palabras |

---

## Ejemplos con LuckPerms

```bash
# Dar al grupo trade-builders acceso al canal staff
/lp group helper permission set chatchannels.use.staff true

# Hacer que los moderadores puedan silenciar y espiar
/lp group moderator permission set chatchannels.mute true
/lp group moderator permission set chatchannels.spy true

# Permitir a los VIP ignorar el cooldown
/lp group vip permission set chatchannels.bypass.cooldown true

# Revocar el canal trade a las cuentas nuevas
/lp group default permission set chatchannels.use.trade false
```

---

## Tabla resumen de permisos

| Permiso | Jugador | VIP | Helper | Moderador | OP |
|---------|---------|-----|--------|-----------|----|
| `chatchannels.use` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `chatchannels.use.global` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `chatchannels.use.local` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `chatchannels.use.trade` | ✓ | ✓ | ✓ | ✓ | ✓ |
| `chatchannels.use.staff` | ✗ | ✗ | ✓ | ✓ | ✓ |
| `chatchannels.bypass.cooldown` | ✗ | ✓ | ✗ | ✓ | ✓ |
| `chatchannels.bypass.filter` | ✗ | ✗ | ✗ | ✓ | ✓ |
| `chatchannels.mute` | ✗ | ✗ | ✗ | ✓ | ✓ |
| `chatchannels.spy` | ✗ | ✗ | ✗ | ✓ | ✓ |
| `chatchannels.admin` | ✗ | ✗ | ✗ | ✗ | ✓ |

---

[Inicio](Home.md) · [Instalación](Installation.md) · [Configuración](Configuration.md) · [Formatos de canal](Channel-Formats.md) · [PlaceholderAPI](PlaceholderAPI.md)
