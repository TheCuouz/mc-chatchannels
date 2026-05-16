# 💬 ChattyChannels

```
  ██████╗██╗  ██╗ █████╗ ████████╗ ██████╗██╗  ██╗ █████╗ ███╗   ██╗███╗   ██╗███████╗██╗     ███████╗
 ██╔════╝██║  ██║██╔══██╗╚══██╔══╝██╔════╝██║  ██║██╔══██╗████╗  ██║████╗  ██║██╔════╝██║     ██╔════╝
 ██║     ███████║███████║   ██║   ██║     ███████║███████║██╔██╗ ██║██╔██╗ ██║█████╗  ██║     ███████╗
 ██║     ██╔══██║██╔══██║   ██║   ██║     ██╔══██║██╔══██║██║╚██╗██║██║╚██╗██║██╔══╝  ██║     ╚════██║
 ╚██████╗██║  ██║██║  ██║   ██║   ╚██████╗██║  ██║██║  ██║██║ ╚████║██║ ╚████║███████╗███████╗███████║
  ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝   ╚═╝    ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═══╝╚══════╝╚══════╝╚══════╝
```

> 🌐 [English](README.md) · **Español**

> **Plugin de canales de chat ligero y con criterio para Paper 1.21.x** — Múltiples canales con permisos, atajos de prefijo rápido, rango por canal, modo spy para staff, filtros anti-spam y de palabras, y ocultación de canal por jugador para que cada uno cuide su propio feed de chat.

> 🏷️ Hecho por **[TTS-Studio](https://github.com/TheCuouz)** — parte de la suite unificada de plugins TTS-Studio.

---

## ✨ Funcionalidades

| Funcionalidad | Descripción |
|---------------|-------------|
| 📡 **Múltiples canales** | Define cualquier número de canales en `channels.yml` — nombre, prefijo, formato, rango, permiso, cooldown. |
| ⚡ **Prefijo rápido** | Escribe `!mensaje` o `#mensaje` para enviar a un canal una sola vez sin cambiar tu canal activo. |
| 📏 **Rango por canal** | `range: -1` = todo el servidor, entero positivo = radio en bloques (ideal para servidores `local` / RP). |
| ⏱️ **Cooldowns por canal** | Línea base anti-spam opcional configurada por canal. |
| 🔇 **Silencio de admin** (`/mute`) | `/mute <jugador> [canal] [duración]` — silencia a un jugador en el lado de envío; persistente entre reinicios; expiración automática. |
| 👁️ **Ocultación propia** (`/cc hide`) | Los jugadores ocultan un canal de su propia vista en el lado de recepción; persistente entre reinicios. |
| 🕵️ **Modo spy para staff** | `/chatspy` muestra cada canal con un prefijo `[SPY]`, aunque no estés en rango. |
| 🚫 **Filtro de spam** | Detección de duplicados, umbral de mayúsculas configurable, limitador de flood — permiso de bypass incluido. |
| 🔤 **Filtro de palabras** | Modo REPLACE (`****`) o BLOCK con coincidencia de límite de palabra, sin distinción de mayúsculas/minúsculas. |
| 📊 **PlaceholderAPI** | `%chatchannels_active%`, `%chatchannels_muted%`. |
| 📈 **bStats** | Métricas de uso anónimas. |
| 🎨 **Estilo TTS-Studio** | Prefijo de chat de la suite y banner de consola enmarcado. |

---

## 🚀 Inicio rápido

```bash
# 1. Coloca el jar en tu carpeta de plugins
cp chatchannels-1.1.0.jar plugins/

# 2. Reinicia el servidor
#    Los canales por defecto (global, local, trade, staff) se generan al primer arranque.

# 3. Personaliza los canales
nano plugins/ChattyChannels/channels.yml

# 4. Recarga en caliente tras los cambios — sin reinicio
/cc reload
```

---

## 📦 Cuida tu propio feed de chat

¿Cansado del canal de comercio? Ocúltalo de tu propia vista sin molestar a nadie más:

```
/cc hide #trade        # dejar de recibir el canal de comercio
/cc show #trade        # restaurarlo
/cc hidden             # mostrar qué canales tienes ocultos
```

Las selecciones sobreviven a los reinicios (almacenadas en `plugins/ChattyChannels/hidden_channels.yml`). El canal sigue activo para el resto de jugadores — esto es un toggle **por jugador, en el lado de recepción**.

### Diferencia con `/mute`

| Acción | Dirección | Quién puede ejecutarlo | Efecto |
|--------|-----------|----------------------|--------|
| `/mute <jugador> [canal] [duración]` | **Lado de envío** | Staff (`chatchannels.mute`) | Impide que el jugador objetivo publique en el canal |
| `/cc hide <#canal>` | **Lado de recepción** | Cualquier jugador (`chatchannels.hide`, por defecto `true`) | Impide que el ejecutante reciba el canal; nadie más se ve afectado |

Ambos sistemas son completamente independientes y pueden combinarse libremente.

---

## 🎮 Comandos

| Comando | Descripción | Permiso | Por defecto |
|---------|-------------|---------|-------------|
| `/channel <id>` | Cambia tu canal activo | `chatchannels.use` | `true` |
| `/ch <id>` | Alias de `/channel` | `chatchannels.use` | `true` |
| `/channels` | Lista los canales disponibles | `chatchannels.use` | `true` |
| `/mute <jugador> [canal] [duración]` | Silencia a un jugador en el lado de envío | `chatchannels.mute` | `op` |
| `/chatspy` | Activa/desactiva el modo spy de staff | `chatchannels.spy` | `op` |
| `/cc hide <#canal>` | Oculta un canal de tu vista | `chatchannels.hide` | `true` |
| `/cc show <#canal>` | Restaura un canal oculto | `chatchannels.hide` | `true` |
| `/cc hidden` | Lista tus canales ocultos | `chatchannels.hide` | `true` |
| `/cc reload` | Recarga config + canales + filtros | `chatchannels.admin` | `op` |

**Formato de duración:** `10m` · `2h` · `1d` — omite para permanente.
**Referencia de canal:** `/cc hide #trade` o `/cc hide trade` funcionan igual.

---

## 🛠️ Permisos

| Permiso | Descripción | Por defecto |
|---------|-------------|-------------|
| `chatchannels.use` | Acceso básico — `/channel`, `/channels`, `/cc` | `true` |
| `chatchannels.use.<id>` | Acceso por canal (derivado automáticamente si `permission:` se omite en `channels.yml`) | varía |
| `chatchannels.hide` | Usar `/cc hide`, `/cc show`, `/cc hidden` | `true` |
| `chatchannels.spy` | Activar el modo spy | `op` |
| `chatchannels.mute` | Silenciar jugadores en canales | `op` |
| `chatchannels.bypass.cooldown` | Saltarse los cooldowns de canal | `op` |
| `chatchannels.bypass.filter` | Saltarse los filtros de spam y palabras | `op` |
| `chatchannels.admin` | Comando de recarga | `op` |

---

## 🔗 Integraciones

| Plugin | ¿Requerido? | Qué hace |
|--------|-------------|----------|
| **PlaceholderAPI** | Opcional | Expone placeholders `%chatchannels_*%` para scoreboards, TAB, chat |
| **LuckPerms** | Opcional | Nodos de permiso por canal más detallados |

---

## ⚙️ Resumen de configuración

```yaml
# plugins/ChattyChannels/channels.yml
default-channel: local

channels:
  global:
    display-name: "<gradient:gold:yellow>Global</gradient>"
    quick-prefix: "!"
    format: "<gray>[G] <player>:</gray> <message>"
    range: -1                  # -1 = todo el servidor
    permission: "chatchannels.use.global"
    cooldown-seconds: 0

  local:
    display-name: "<aqua>Local</aqua>"
    quick-prefix: ""
    format: "<gray>[L] <player>:</gray> <message>"
    range: 64                  # bloques
    permission: "chatchannels.use.local"
    cooldown-seconds: 0

  staff:
    display-name: "<red>Staff</red>"
    quick-prefix: "@"
    format: "<red>[Staff] <player>:</red> <message>"
    range: -1
    permission: "chatchannels.use.staff"
    cooldown-seconds: 0

filters:
  spam:
    enabled: true
    duplicate-window-seconds: 5
    caps-threshold-pct: 70
    caps-min-length: 10
    flood-window-seconds: 5
    flood-max-messages: 4
  words:
    mode: REPLACE              # REPLACE | BLOCK
    replacement: "****"
    list: ["badword1", "badword2"]
```

### Persistencia

| Archivo | Qué almacena |
|---------|--------------|
| `players.yml` | Canal activo de cada jugador + tiempo del último mensaje + estado spy |
| `mutes.yml` | Silencias aplicados por admin (`uuid:channelId` → expiración epoch ms) |
| `hidden_channels.yml` | Canales ocultados por cada jugador |

Los tres se escriben en cada mutación y de nuevo en `onDisable` como vaciado de seguridad.

---

## 📊 PlaceholderAPI

| Placeholder | Devuelve |
|-------------|----------|
| `%chatchannels_active%` | El id del canal activo del jugador |
| `%chatchannels_muted%` | `true` / `false` — si el jugador está silenciado por admin en su canal activo |

PlaceholderAPI se detecta automáticamente al arrancar; los placeholders no hacen nada silenciosamente cuando el plugin está ausente.

---

## 🐛 Reportar bugs

Abre un issue en el repositorio de GitHub con:

- Versión de ChattyChannels (`/version ChattyChannels`)
- Tipo y versión del servidor (build de Paper, versión de Java)
- Un `channels.yml` mínimo que reproduzca el problema
- Extracto del log del servidor — especialmente el stack trace si hay uno

> Nota interna: el soporte de producción se gestiona en el tablero de issues de TTS-Studio.

---

## 📜 Licencia

ChattyChannels se distribuye bajo la **licencia open-source de TTS-Studio** como parte de la suite gratuita de SpigotMC. Consulta `LICENSE` para los términos completos.

---

<sub>ChattyChannels es un plugin de TTS-Studio · © TTS-Studio · dando a cada jugador su propio chat desde 2024.</sub>
