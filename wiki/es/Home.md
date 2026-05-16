> 🌐 [English](../Home.md) · **Español**

# Wiki de Channels

```
   ██████╗██╗  ██╗ █████╗ ███╗   ██╗███╗   ██╗███████╗██╗     ███████╗
  ██╔════╝██║  ██║██╔══██╗████╗  ██║████╗  ██║██╔════╝██║     ██╔════╝
  ██║     ███████║███████║██╔██╗ ██║██╔██╗ ██║█████╗  ██║     ███████╗
  ██║     ██╔══██║██╔══██║██║╚██╗██║██║╚██╗██║██╔══╝  ██║     ╚════██║
  ╚██████╗██║  ██║██║  ██║██║ ╚████║██║ ╚████║███████╗███████╗███████║
   ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝  ╚═══╝╚══════╝╚══════╝╚══════╝
```

**Chat multicanal para Paper 1.21.x** — divide el chat en Global, Local, Trade y Staff como flujos independientes con permisos, radio, anti-spam, silenciamientos y modo espía de administrador. Parte de la suite de plugins **TTS-Studio**.

> Alias de marca: **Channels** (el nombre completo "ChattyChannels" tiene 12 caracteres; TTS usa el alias abreviado para los prefijos y el banner de consola). Color de marca: azul cielo `#5DADE2`.

---

## Banner de consola

Al habilitarse, ChattyChannels emite un banner de TTS-Studio enmarcado:

```
╔═══════════════════════════════════════════════╗
║                                               ║
║                ChattyChannels v1.0.0            ║
║                                               ║
╚═══════════════════════════════════════════════╝
  ◈ Channels: 4 channels · PAPI ✓ · ready in 42ms
```

El prefijo `◈ Channels:` (prefijo de chat de TTS) se usa **solo para mensajes del plugin** — feedback como `cambiaste al canal #trade`, `canal no encontrado`, `silenciamiento aplicado`, `recarga completada`. El **contenido real del chat** se renderiza usando la cadena `format:` propia de cada canal en `channels.yml` y **nunca** se envuelve en el prefijo de TTS. Esa separación es intencional: marcar el output del plugin es buena UX; marcar el discurso de los jugadores atribuiría erróneamente cada mensaje del servidor a ChattyChannels.

---

## Características principales

| Característica | Descripción |
|----------------|-------------|
| **Canales configurables** | Define cualquier número de canales en `channels.yml` — nombre, formato, rango, permiso, cooldown, prefijo rápido |
| **Prefijos rápidos** | Escribe `!hola` para Global, `#vendo diamantes` para Trade, `@reunión` para Staff — sin necesidad de cambiar de canal |
| **Radio / Global** | `range: -1` = todo el servidor; `range: 100` = solo jugadores dentro de 100 bloques en el mismo mundo |
| **Formatos MiniMessage** | Soporte completo de Adventure MiniMessage por canal — colores, gradientes, hover, click |
| **Filtro de spam** | Detección de duplicados, mayúsculas excesivas y flood con permiso de bypass |
| **Filtro de palabras** | Modos `REPLACE` (enmascarar palabras coincidentes) o `BLOCK` (descartar el mensaje completo) |
| **Silenciamientos temporales** | `/mute Steve trade 10m` — por canal o para todo el servidor, con parser de duración (`10m`, `1h`, `2d`) |
| **Chat-Spy** | Los administradores ven todos los canales, incluidos los que normalmente no pueden leer — prefijados con `[SPY]` |
| **PlaceholderAPI** | `%chatchannels_active%`, `%chatchannels_muted%` |
| **Compatible con LuckPerms** | `%luckperms_prefix%` fluye a través de los formatos de canal sin configuración adicional |
| **Banner de consola TTS-Studio** | Banner de habilitación/deshabilitación enmarcado con estado, hooks y tiempo de preparación |

---

## Inicio rápido

```bash
# 1. Coloca el jar en plugins/
cp ChattyChannels-1.0.0.jar plugins/

# 2. Reinicia el servidor — channels.yml se genera automáticamente

# 3. Lista los canales disponibles
/channels

# 4. Cambia el canal activo
/channel trade

# 5. O usa prefijos rápidos directamente
!hola a todos        # → Global
#VENDO 64 diamantes  # → Trade
@reunión del staff   # → Staff
```

> Consejo — ejecuta `/cc reload` después de editar `channels.yml` o `messages.yml`. No es necesario reiniciar.

---

## Navegación de la wiki

| Página | Contenido |
|--------|-----------|
| [Instalación](Installation.md) | Requisitos, despliegue del JAR, lista de comprobación inicial |
| [Comandos y permisos](Commands-and-Permissions.md) | Tabla completa de comandos, árbol de permisos, ejemplos |
| [Configuración](Configuration.md) | Anatomía de `channels.yml`, añadir canales, configuración del filtro de spam y palabras |
| [Formatos de canal](Channel-Formats.md) | Análisis detallado de MiniMessage, lista de placeholders, recetas de formato |
| [PlaceholderAPI](PlaceholderAPI.md) | Placeholders expuestos y ejemplos de integración |

---

## Requisitos

| Requisito | Versión | Notas |
|-----------|---------|-------|
| Paper | 1.21.x | Usa `AsyncChatEvent` — Spigot **no** está soportado |
| Java | 21+ | Obligatorio |
| PlaceholderAPI | 2.11.6+ | Opcional — activa `%chatchannels_*%` y `%luckperms_prefix%` en los formatos |
| LuckPerms | latest | Opcional — se usa mediante PAPI para inyectar prefijos de rango en el chat |

---

*ChattyChannels — parte de la suite de plugins [TTS-Studio](https://ttsstudio.dev). Marca azul cielo, mantenido profesionalmente.*
