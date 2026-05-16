> 🌐 [English](../Channel-Formats.md) · **Español**

# Formatos de canal

La cadena `format:` de cada canal en `channels.yml` controla exactamente cómo se renderiza un mensaje para los receptores. ChattyChannels usa el formato [MiniMessage](https://docs.advntr.dev/minimessage/format.html) de Adventure — el reemplazo moderno basado en etiquetas para los códigos `&` heredados — y procesa cada mensaje a través de PlaceholderAPI (cuando está presente) antes de la deserialización de etiquetas.

> Recordatorio: `format:` es el **contenido del chat del jugador**. Intencionalmente **no** está envuelto en el prefijo TTS `◈ Channels:`. Ese prefijo está reservado para mensajes de voz del plugin provenientes de `messages.yml` (por ejemplo, "te uniste al canal #trade", "canal no encontrado", "silenciamiento aplicado").

---

## El pipeline

Dado un input de chat sin procesar como `WTS 64 diamantes`, esto es lo que ChattyChannels hace para producir el componente final enviado a los espectadores:

```
1. input sin procesar   →  "WTS 64 diamantes"
2. filtro de palabras   →  "WTS 64 diamantes"           (REPLACE/BLOCK aplicado)
3. plantilla format     →  "<gray>[<aqua>$</aqua>]</gray> <player>: <message>"
4. paso de PAPI         →  "<gray>[<aqua>$</aqua>]</gray> <player>: <message>"
                           (cualquier %placeholder% expandido contra el *remitente*)
5. <player>             →  reemplazado con el nombre del remitente
6. <message>            →  reemplazado con el contenido del chat filtrado
                           (con `<` escapado como `\<` para que el chat no pueda inyectar etiquetas)
7. MiniMessage          →  deserializado en un Adventure Component
8. entregado a todos los espectadores en rango con el permiso correcto
```

Este orden importa: **los placeholders de PAPI se ejecutan antes del análisis de MiniMessage**, para poder incrustar prefijos con color de LuckPerms dentro del formato. La sustitución de `<message>` se ejecuta **después** del filtro de palabras, lo que significa que las máscaras del filtro (`****`) terminan en el chat — los jugadores no pueden eludirlas con etiquetas de MiniMessage porque `<` en el mensaje sin procesar se escapa a `\<` antes de la deserialización.

---

## Placeholders integrados

Estos son sustituidos por ChattyChannels mismo (no por PAPI):

| Placeholder | Reemplazado por |
|-------------|----------------|
| `<player>` | El nombre del remitente (`player.getName()`) |
| `<message>` | El contenido del chat filtrado, con `<` escapado |

> `<message>` es el único lugar donde aterriza el input del jugador. Debido a que `<` se escapa en la entrada, los jugadores no pueden inyectar etiquetas de MiniMessage a través del contenido de su chat aunque conozcan la sintaxis. Esto mantiene `<rainbow>` etc. como un privilegio de administrador/autor de formatos.

---

## Placeholders de PlaceholderAPI

Cuando PlaceholderAPI está instalado y activo, **cualquier** placeholder `%expansion_valor%` se expande contra el remitente justo antes del análisis de MiniMessage. Opciones populares:

| Placeholder | Expansión fuente | Valor típico |
|-------------|-----------------|--------------|
| `%player_name%` | `player` (integrado) | `Steve` |
| `%player_world%` | `player` | `world_nether` |
| `%luckperms_prefix%` | LuckPerms | `<gold>[VIP]<reset> ` |
| `%vault_prefix%` | Vault | `&6[Mod]&r ` |
| `%essentials_nickname%` | Essentials | `Steve_the_Brave` |
| `%chatchannels_active%` | ChattyChannels (este plugin) | `trade` |

El formato Global por defecto usa `%luckperms_prefix%`:

```yaml
format: "<gray>[<gold>G</gold>]</gray> %luckperms_prefix%<player><reset>: <message>"
```

Si LuckPerms está presente, esto se renderiza como por ejemplo `[G] [VIP] Steve: hola`. Si no lo está, el placeholder permanece como texto literal — tenlo en cuenta al diseñar formatos para un servidor que puede funcionar sin PAPI.

---

## Chuleta de etiquetas MiniMessage

Una lista no exhaustiva de las etiquetas más útiles dentro de un `format:` de canal.

### Colores

```text
<red>, <green>, <blue>, <yellow>, <aqua>, <gold>, <gray>, <dark_gray>,
<dark_red>, <dark_green>, <dark_blue>, <dark_purple>, <dark_aqua>,
<black>, <white>, <light_purple>
```

Hex: `<color:#5DADE2>Channels</color>` (color de marca de TTS-Studio).

### Decoraciones

```text
<bold>, <italic>, <underlined>, <strikethrough>, <obfuscated>
```

Cierra un span con la variante de cierre: `<gold>Trade</gold>`, o usa `<reset>` para eliminar todo el estilo actual.

### Gradientes

```text
<gradient:#5DADE2:#3498DB>Channels</gradient>
<gradient:gold:red:gold>WARNING</gradient>
```

Excelentes para encabezados del canal staff.

### Hover / Click (para el prefijo del bracket)

```text
<hover:show_text:'<gray>Canal Trade'>[$]</hover>
<click:run_command:'/channel trade'>[$]</click>
```

Envuelve la etiqueta del canal en tu `format:` para hacerlo clicable en el chat — los jugadores pueden cambiar de canal haciendo clic en el bracket.

---

## Recetas de formato

### Global minimalista

```yaml
format: "<gold>[G] <player></gold>: <message>"
```

### Trade con bracket clicable

```yaml
format: "<click:run_command:'/channel trade'><hover:show_text:'<gray>Clic para cambiar'><aqua>[$]</aqua></hover></click> <player>: <message>"
```

### Staff con encabezado degradado

```yaml
format: "<gradient:dark_red:red:dark_red>[STAFF]</gradient> <gray><player>:</gray> <white><message>"
```

### Prefijo de rango desde LuckPerms

```yaml
format: "%luckperms_prefix%<player><reset> <dark_gray>»<reset> <message>"
```

### Local con información de mundo

```yaml
format: "<gray>[L]<dark_gray>(%player_world%)<reset> <player>: <message>"
```

---

## Escape y seguridad

| Preocupación | Cómo lo gestiona ChattyChannels |
|-------------|-------------------------------|
| Jugadores inyectando etiquetas MiniMessage | `<` en `<message>` se escapa a `\<` antes del análisis |
| Jugadores inyectando placeholders de PAPI | PAPI se ejecuta en el **formato**, no en `<message>` — los jugadores no pueden desencadenar la expansión de placeholders a través del chat |
| Jugadores eludiendo filtros de palabras con códigos de color | El filtro de palabras se ejecuta en el texto plano *antes* de que se aplique el formato, así que `b<red>a</red>dword` solo coincidiría con `badword` si el usuario lo escribió literalmente — pero como los jugadores no pueden inyectar etiquetas, esto no es una preocupación práctica |

---

## Marca del banner de consola

El banner de TTS-Studio emitido al habilitarse usa el alias de marca **Channels** (azul cielo `#5DADE2`) porque el nombre completo `ChattyChannels` tiene 12 caracteres y supera el presupuesto de 10 caracteres del SDK. El prefijo que envuelve los mensajes de voz del plugin es `◈ Channels:`. **No** necesitas (y no deberías) reproducir este prefijo dentro de las cadenas `format:` del canal — eso etiquetaría cada mensaje del jugador como output del plugin y atribuiría erróneamente el discurso.

---

[Inicio](Home.md) · [Instalación](Installation.md) · [Comandos y permisos](Commands-and-Permissions.md) · [Configuración](Configuration.md) · [PlaceholderAPI](PlaceholderAPI.md)
