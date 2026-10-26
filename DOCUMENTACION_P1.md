# Práctica 1: Juego con Interfaces de Usuario (Quiz Cervecero)
**Asignatura:** Laboratorio de Dispositivos Móviles  
**Universidad:** Universidad Rey Juan Carlos (URJC)  
**Profesora:** Liliana Patricia Santacruz Valencia  

---

## 1. Resumen de la Aplicación

La aplicación desarrollada es un juego interactivo de tipo **Quiz sobre marcas de cerveza**, con preguntas sobre marcas españolas y europeas y una ronda visual de reconocimiento de logotipos.

La aplicación consta de **5 preguntas** con diferentes componentes visuales y controles de usuario, un sistema de puntuación en vivo con penalizaciones y bonificaciones, control de errores con bifurcación de partida (continuar o reiniciar), y una segunda Activity para visualizar los resultados finales con animaciones y opción de jugar nuevas tandas.

---

## 2. Cumplimiento de la Rúbrica de Evaluación

| Criterio de la Rúbrica | Puntuación | Estado | Implementación |
| :--- | :---: | :---: | :--- |
| **1.1 Activities y Puntuaciones** | **1,50 pts** | **100% Completo** | El juego principal se ejecuta en `MainActivity` y la puntuación se envía mediante un `Intent` explícito con extras a `ResultadoActivity`, donde se visualiza el resumen y se ofrece el botón de comenzar una nueva tanda. Además, cuenta con transiciones animadas entre Activities (`ActivityOptions.makeSceneTransitionAnimation`). |
| **1.2 Preguntas de texto con opciones de texto** | **1,25 pts** | **100% Completo** | **Pregunta 1:** identifica Cruzcampo por su origen sevillano, mediante un `RadioGroup`. |
| **1.3 Pregunta sobre imagen con opciones de texto** | **1,25 pts** | **100% Completo** | **Pregunta 2:** reconoce Heineken por su estrella roja, con una imagen de dos jarras de cerveza y opciones en un `RadioGroup`. |
| **2. Varios controles (RadioButton, ListView, Spinner, etc.)** | **1,50 pts** | **100% Completo** | Se integran `RadioGroup` + `RadioButton` (P1 y P2), `ListView` (P3, grupo Damm), `Spinner` (P4, origen de Pilsner Urquell), `ImageButton` (P5), `MaterialButton`, `CardView`, `TextView` y `LottieAnimationView`. |
| **Preguntas con opciones de imágenes** | **Criterio 10/10** | **100% Completo** | **Pregunta 5:** identifica Estrella Galicia entre los logotipos PNG de Mahou, Estrella Galicia, Cruzcampo y San Miguel. |
| **Calidad del código y diseño** | **1,50 pts** | **100% Completo** | Arquitectura Java limpia, recursos separados en XML y diseño temático en tonos ámbar y cobre. |

---

## 3. Modo de Juego y Reglas Implementadas

1. **Sistema de Puntuación:**
   - **Puntuación inicial:** `0 puntos`.
   - **Tras un acierto:**
     - Se suman **3 puntos** (`+3`).
     - Se incrementa el contador de aciertos.
     - Se notifica mediante un **Toast** informativo (`¡Respuesta correcta! (+3 puntos)`) y un banner visual en verde con icono de check.
   - **Tras un fallo:**
     - Se restan **2 puntos** (`-2`).
     - Se incrementa el contador de fallos.
     - Se notifica mediante un **Toast** (`¡Respuesta incorrecta! (-2 puntos)`) y un banner en rojo con icono de cruz.
     - **Opciones tras fallo:** Se despliega un diálogo emergente (`AlertDialog`) que permite al usuario elegir:
       - **"Continuar"**: Seguir adelante con la partida asumiendo la penalización.
       - **"Empezar de nuevo"**: Reiniciar el juego desde la Pregunta 1 con 0 puntos.

2. **Navegación y Fin del Juego:**
   - Barra superior con contador en tiempo real (`Pregunta X / 5`) y marcador acumulado (`Puntos: X`).
   - Botón de **"Reiniciar"** disponible en todo momento con confirmación.
   - Al finalizar la 5ª pregunta, se abre mediante un `Intent` la pantalla `ResultadoActivity` con animación de transición (`ActivityOptions.makeSceneTransitionAnimation`).

3. **Pantalla de Resultados (`ResultadoActivity`):**
   - Animación de celebración gráfica usando **Lottie** (`celebration.json`).
   - Tarjeta con puntuación final destacada en grande (ej: `+15 pts`).
   - Desglose detallado: número de aciertos (+pts ganados) y número de fallos (-pts restados).
   - Mensaje de veredicto según el nivel alcanzado (Excelente, Notable, Aprobado o Ánimo).
   - Botón **"Jugar de nuevo"**: Inicia una nueva tanda limpia devolviendo al usuario a la `MainActivity`.
   - Botón **"Salir"**: Cierra la aplicación ordenadamente.

---

## 4. Estructura de Archivos del Proyecto

```
app/src/main/
├── AndroidManifest.xml              # Declaración de MainActivity y ResultadoActivity con temas
├── assets/
│   ├── celebration.json             # Animación vectorial Lottie para la pantalla final
│   └── loading_animation.json       # Fichero Lottie (siguiendo Diapositivas Lottie)
├── java/com/example/practica1/
│   ├── MainActivity.java            # Controlador del Quiz (lógica, 5 preguntas, puntuación, controles)
│   └── ResultadoActivity.java       # Controlador de Resultados (recepción de extras, reinicio, veredicto)
└── res/
    ├── drawable/
    │   ├── bg_brewery_theme.xml     # Fondo vectorial en tonos cálidos
    │   ├── bg_parchment_card.xml    # Tarjeta redondeada para las preguntas
    │   ├── bg_badge_score.xml       # Badge verde para puntuación
    │   ├── bg_badge_progress.xml    # Badge naranja para progreso
    │   ├── bg_option_card.xml       # Estado normal de las opciones
    │   ├── bg_option_selected.xml   # Estado seleccionado / acierto
    │   ├── ic_arrow_forward.xml     # Icono de avance
    │   ├── ic_arrow_back.xml        # Icono de retroceso
    │   ├── ic_check.xml             # Icono de acierto
    │   ├── ic_cross.xml             # Icono de fallo
    │   ├── ic_refresh.xml           # Icono de reinicio
    │   ├── ic_trophy.xml            # Icono de trofeo
    │   └── logo_android.png         # Logo Android proporcionado en la asignatura
    ├── drawable-nodpi/
    │   ├── jarra_cerveza.png        # Jarras de cerveza (P2)
    │   ├── logo_mahou.png           # Logotipo para P5, opción A
    │   ├── logo_estrella_galicia.png # Logotipo para P5, opción B
    │   ├── logo_cruzcampo.png       # Logotipo para P5, opción C
    │   └── logo_san_miguel.png      # Logotipo para P5, opción D
    ├── layout/
    │   ├── activity_main.xml        # Layout principal con los 5 contenedores de preguntas
    │   ├── activity_resultado.xml   # Layout de pantalla de fin de juego y puntuaciones
    │   └── item_quiz_option.xml     # Layout personalizado para elementos del ListView
    └── values/
        ├── colors.xml               # Paleta de colores temáticos
        ├── strings.xml              # Textos externalizados para preguntas, opciones y mensajes
        └── themes.xml               # Tema con animaciones de transición entre actividades
```

---


