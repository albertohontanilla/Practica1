# Práctica 2: Uso de Bases de Datos y Gestión de Contenido Multimedia (Quiz Cervecero)
**Asignatura:** Laboratorio de Dispositivos Móviles
**Universidad:** Universidad Rey Juan Carlos (URJC)
**Profesora:** Liliana Patricia Santacruz Valencia

---

## 1. Resumen

La Práctica 2 evoluciona el **Quiz Cervecero** de la Práctica 1. Las preguntas dejan de estar
fijas en `strings.xml` y pasan a una **base de datos SQLite**, se añade **contenido multimedia**
(efectos de sonido, música de fondo y sonidos de fin de partida) y se completan los
**requisitos de diseño** (nombre, icono, Toolbar con color propio y manual de usuario).

## 2. Cumplimiento del enunciado

| Requisito | Estado | Implementación |
| :--- | :---: | :--- |
| **1. Manejo de Bases de Datos** | Completo | `bd/QuizDbHelper.java` (`SQLiteOpenHelper`) con dos tablas: `preguntas` (banco de 20 preguntas precargadas, 4 por tipo de control) y `partidas` (ranking). Consultas con `query`, `insert`, `delete`, `ORDER BY RANDOM()` y `Cursor`. |
| **2. Gestión de Contenido Multimedia** | Completo | `audio/GestorAudio.java`: `SoundPool` para los sonidos de acierto y fallo, `MediaPlayer` para la música de fondo en bucle (`res/raw/musica_fondo.mp3`) y para la fanfarria de victoria o el sonido de derrota en la pantalla de resultados. |
| **Diseño 1. Nombre representativo** | Completo | `app_name` = «Quiz Cervecero». |
| **Diseño 2. Icono de la app** | Completo | Icono propio (jarras de cerveza) en el lanzador: `mipmap-*/ic_launcher*.webp` e icono adaptativo `mipmap-anydpi-v26`. El mismo icono aparece junto al nombre en la Toolbar (`drawable-*/ic_logo_toolbar.webp`). |
| **Diseño 3. Color de fondo del ToolBar** | Completo | `MaterialToolbar` común (`layout/toolbar.xml`) con estilo `Widget.Practica1.Toolbar` y fondo `@color/toolbar_bg` (#8C1D18). |
| **Diseño 4. Manual de usuario** | Completo | `ManualActivity` accesible desde el icono del libro de la Toolbar. |
| **Sin warnings (ContentDescription, etc.)** | Completo | Todas las imágenes tienen `contentDescription`, ya no hay textos escritos a mano en el código ni en los layouts y se han eliminado los recursos sin usar. Lint solo avisa de versiones nuevas de las dependencias y del SDK. |

### Funcionalidades adicionales (Sobresaliente: al menos dos)

1. **Ranking de partidas persistente:** al terminar, el jugador escribe su nombre y guarda la partida
   en la tabla `partidas`. `RankingActivity` muestra las 20 mejores con un adaptador propio y
   permite borrar el historial (con confirmación).
2. **Banco de preguntas aleatorio:** cada partida elige al azar una pregunta de cada tipo de control
   (`ORDER BY RANDOM()`), por lo que dos partidas seguidas casi nunca son iguales.
3. **Ajustes de audio persistentes:** desde el menú ⋮ de la Toolbar se activan o desactivan por
   separado la música y los efectos. La elección se guarda en `SharedPreferences`.
4. **Conservación del estado al girar la pantalla:** `onSaveInstanceState` guarda los ids de las
   preguntas, la puntuación y si la pregunta ya estaba respondida, y la partida se recupera desde la BD.

## 3. Pantallas y navegación

```
MainActivity (quiz) ──Toolbar: trofeo──▶ RankingActivity ──▶ (Borrar historial)
      │          └──Toolbar: libro───▶ ManualActivity
      │          └──Toolbar: ⋮ ──────▶ Música de fondo / Efectos de sonido
      └──5ª pregunta──▶ ResultadoActivity ──▶ Guardar en el ranking (BD)
                              ├──▶ Ver ranking (RankingActivity)
                              ├──▶ Jugar de nuevo (MainActivity, preguntas nuevas)
                              └──▶ Salir
```

## 4. Principales clases

| Clase | Función |
| :--- | :--- |
| `MainActivity` | Lógica del quiz: carga las preguntas de la BD, gestiona los 5 tipos de control, la puntuación, el audio y el estado al girar. |
| `ResultadoActivity` | Resumen final, sonido de victoria o derrota y guardado de la partida en el ranking. |
| `RankingActivity` | Lista de mejores partidas (`ListView` + `ArrayAdapter` propio con `ViewHolder`) y borrado del historial. |
| `ManualActivity` | Manual de usuario; muestra cuántas preguntas hay en la BD. |
| `bd.QuizDbHelper` | `SQLiteOpenHelper`: creación de tablas, datos iniciales y consultas. |
| `bd.Pregunta`, `bd.Partida` | Modelos de datos de cada tabla. |
| `bd.ImagenesQuiz` | Traduce las claves de imagen guardadas en la BD a recursos `drawable`. |
| `audio.GestorAudio` | `SoundPool`, `MediaPlayer` y preferencias de audio. |

## 5. Estructura de la base de datos

```sql
CREATE TABLE preguntas (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo INTEGER NOT NULL,          -- 1 Radio, 2 Imagen, 3 Lista, 4 Spinner, 5 Logos
    enunciado TEXT NOT NULL,
    opcion_a TEXT NOT NULL, opcion_b TEXT NOT NULL,
    opcion_c TEXT NOT NULL, opcion_d TEXT NOT NULL,
    correcta INTEGER NOT NULL,      -- índice 0..3
    imagen TEXT                     -- clave de imagen (solo tipo 2)
);

CREATE TABLE partidas (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    jugador TEXT NOT NULL,
    puntos INTEGER NOT NULL,
    aciertos INTEGER NOT NULL,
    fallos INTEGER NOT NULL,
    fecha INTEGER NOT NULL          -- milisegundos
);
```

## 6. Recursos multimedia

| Recurso | Uso |
| :--- | :--- |
| `res/raw/sonido_acierto.wav` | Arpegio ascendente al acertar (`SoundPool`). |
| `res/raw/sonido_fallo.wav` | Dos notas graves al fallar (`SoundPool`). |
| `res/raw/sonido_victoria.wav` | Fanfarria al terminar con más aciertos que fallos (`MediaPlayer`). |
| `res/raw/sonido_derrota.wav` | Escala descendente al terminar con más fallos (`MediaPlayer`). |
| `res/raw/musica_fondo.mp3` | Música en bucle durante la partida (`MediaPlayer`). Procede de los recursos de la asignatura (`DOCS/Recursos/ReproductorMusical/raw/rules.mp3`). |

## 7. Pendiente para la entrega

- Memoria en el formato del enunciado (portada, índice, capturas numeradas, bibliografía en APA 7).
- Vídeo de 3 minutos como máximo, con la app en ejecución, el código y la cara de los integrantes.
- Comprimir el proyecto como `NombreApellidosAlumno_P2.zip`.
