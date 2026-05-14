> 🌐 [English](../Installation.md) · **Español**

# Instalación

ChatChannels se instala en menos de un minuto: coloca el JAR, reinicia el servidor, listo. El `channels.yml` por defecto incluye cuatro canales listos para producción para que puedas verificar que el plugin funciona antes de personalizar nada.

---

## Requisitos

| Requisito | Versión | Obligatorio | Notas |
|-----------|---------|-------------|-------|
| **Paper** | 1.21.x | Sí | Usa `io.papermc.paper.event.player.AsyncChatEvent` — Spigot y CraftBukkit no están soportados |
| **Java** | 21+ | Sí | Paper 1.21 requiere Java 21 |
| **PlaceholderAPI** | 2.11.6+ | No | Activa los placeholders `%chatchannels_*%` y permite que los formatos de canal incluyan placeholders de otros plugins (por ejemplo, `%luckperms_prefix%`) |
| **LuckPerms** | latest | No | Combinado con PAPI, los prefijos de rango fluyen hacia los formatos de canal |

> Folia no está soportado en esta versión (`folia-supported: false` en `plugin.yml`).

---

## Paso a paso

### 1. Coloca el JAR

```bash
cp ChatChannels-1.0.0.jar /ruta/a/tu/servidor/plugins/
```

### 2. Reinicia el servidor

Se requiere un reinicio completo (o primer inicio) para que el plugin pueda generar sus archivos de configuración. Usa `stop` desde la consola — no uses `/reload confirm`, que puede dejar los plugins en un estado inconsistente.

### 3. Verifica el banner de consola

Al habilitarse ChatChannels verás el banner de TTS-Studio enmarcado:

```
╔═══════════════════════════════════════════════╗
║                                               ║
║                ChatChannels v1.0.0            ║
║                                               ║
╚═══════════════════════════════════════════════╝
  ◈ Channels: 4 channels · PAPI ✓ · ready in 42ms
```

La línea de estado indica:
- El número de canales cargados correctamente desde `channels.yml`
- Si PlaceholderAPI fue detectado (`PAPI ✓` / `no PAPI`)
- El tiempo de inicio en frío en milisegundos

Si ves `no PAPI` y quieres soporte de placeholders, instala PlaceholderAPI y reinicia.

### 4. Confirma los canales por defecto

El plugin incluye cuatro canales en `plugins/ChatChannels/channels.yml`:

| ID | Nombre | Prefijo rápido | Rango | Permiso |
|----|--------|----------------|-------|---------|
| `global` | Global | `!` | `-1` (todo el servidor) | `chatchannels.use.global` |
| `local` | Local | *(ninguno)* | `100` bloques | `chatchannels.use.local` |
| `trade` | Trade | `#` | `-1` (todo el servidor) | `chatchannels.use.trade` |
| `staff` | Staff | `@` | `-1` (todo el servidor) | `chatchannels.use.staff` |

Canal por defecto al unirse: **Local**.

Ejecuta `/channels` en el juego para ver la lista activa filtrada por tus permisos.

### 5. Primer mensaje

```
> /channel trade
◈ Channels: Cambiaste al canal Trade.

> WTS 64 diamonds for 8 emeralds each
[$] Steve: WTS 64 diamonds for 8 emeralds each
```

O directamente, sin cambiar de canal:

```
> !hello world
[G] Steve: hello world
```

---

## Estructura de archivos tras el primer inicio

```
plugins/
└── ChatChannels/
    ├── channels.yml      # Todas las definiciones de canal + configuración del filtro
    ├── config.yml        # Ajustes de todo el plugin (config-version)
    └── messages.yml      # Todas las cadenas de cara al jugador (MiniMessage)
```

---

## Recarga vs. reinicio

| Acción | Comando | Cuándo usarlo |
|--------|---------|---------------|
| Recarga en caliente | `/cc reload` | Tras editar `channels.yml` o `messages.yml` |
| Reinicio del servidor | `stop` y luego iniciar | Tras reemplazar el JAR o actualizar Paper/Java |

> `/cc reload` vuelve a leer `channels.yml` (canales + filtros) y `messages.yml`. **No** descarta el estado de silenciamiento de los jugadores ni las asignaciones de canal activo — estos persisten entre recargas.

---

## Actualización

1. Detén el servidor.
2. Reemplaza `ChatChannels-X.X.X.jar` en `plugins/`.
3. Inicia el servidor. El plugin no sobrescribirá tu `channels.yml` editado.
4. Consulta el registro de cambios para ver si hay claves nuevas que quieras añadir manualmente.

---

[Inicio](Home.md) · [Comandos y permisos](Commands-and-Permissions.md) · [Configuración](Configuration.md) · [Formatos de canal](Channel-Formats.md) · [PlaceholderAPI](PlaceholderAPI.md)
