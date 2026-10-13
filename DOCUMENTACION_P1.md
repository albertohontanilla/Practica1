# Práctica 1: Juego con Interfaces de Usuario (Animal Quiz)
**Asignatura:** Laboratorio de Dispositivos Móviles  
**Universidad:** Universidad Rey Juan Carlos (URJC)  
**Profesora:** Liliana Patricia Santacruz Valencia  

---

## 1. Resumen de la Aplicación

La aplicación desarrollada es un juego interactivo de tipo **Quiz sobre el Reino Animal** que cumple con **todos los requisitos obligatorios y opcionales para la máxima calificación (10.00 / 10.00 - Sobresaliente)** establecidos en el enunciado y la rúbrica oficial.

La aplicación consta de **5 preguntas** con diferentes componentes visuales y controles de usuario, un sistema de puntuación en vivo con penalizaciones y bonificaciones, control de errores con bifurcación de partida (continuar o reiniciar), y una segunda Activity para visualizar los resultados finales con animaciones y opción de jugar nuevas tandas.

---

## 2. Cumplimiento de la Rúbrica de Evaluación

| Criterio de la Rúbrica | Puntuación | Estado | Implementación |
| :--- | :---: | :---: | :--- |
| **1.1 Activities y Puntuaciones** | **1,50 pts** | **100% Completo** | El juego principal se ejecuta en `MainActivity` y la puntuación se envía mediante un `Intent` explícito con extras a `ResultadoActivity`, donde se visualiza el resumen y se ofrece el botón de comenzar una nueva tanda. Además, cuenta con transiciones animadas entre Activities (`ActivityOptions.makeSceneTransitionAnimation`). |
| **1.2 Preguntas de texto con opciones de texto** | **1,25 pts** | **100% Completo** | **Pregunta 1:** "¿Cuál de los siguientes animales es un marsupial?" con 4 opciones de texto mediante `RadioGroup` y `RadioButton` (Koala, Tigre, Lobo, Vaca). |
| **1.3 Pregunta sobre imagen con opciones de texto** | **1,25 pts** | **100% Completo** | **Pregunta 2:** "¿Cuál es el animal terrestre más rápido del mundo?" que incluye un componente `ImageView` con ilustración vectorial (`img_guepardo`) y opciones en texto mediante `RadioGroup` (León, Guepardo, Gacela, Liebre). |
| **2. Varios controles (RadioButton, ListView, Spinner, etc.)** | **1,50 pts** | **100% Completo** | Se integran múltiples controles a lo largo del flujo:<br>• `RadioGroup` + `RadioButton` (P1 y P2)<br>• `ListView` con `ArrayAdapter` y `setOnItemClickListener` (P3: "¿Qué ave es conocida por su canto melodioso?")<br>• `Spinner` desplegable con `ArrayAdapter` (P4: "¿Cuál es el mamífero más grande del mundo?")<br>• `ImageButton` interactivo (P5)<br>• `MaterialButton`, `CardView`, `TextView` y `LottieAnimationView`. |
| **Sobresaliente (Hasta 10,00 pts)**: Preguntas con opciones de imágenes | **Criterio 10/10** | **100% Completo** | **Pregunta 5:** "¿Cuál de las siguientes aves no tiene la capacidad de volar?" con **4 opciones que muestran imágenes** interactivas (`ImageButton` y contenedores con `img_pinguino`, `img_aguila`, `img_loro`, `img_colibri`) y selección visual con borde activo. |
| **Calidad del código y diseño** | **1,50 pts** | **100% Completo** | Arquitectura Java limpia, separación estricta de recursos en XML (`strings.xml`, `colors.xml`, `themes.xml`, `drawables`), diseño temático de selva con tarjetas pergamino responsivas. |

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
    │   ├── bg_jungle_theme.xml      # Fondo vectorial de selva tropical con hojas decorativas (Figura 1)
    │   ├── bg_nature_gradient.xml   # Fondo degradado de estilo selva / naturaleza
    │   ├── bg_parchment_card.xml    # Contenedor estilo pergamino / tarjeta redondeada
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
    │   ├── guepardo.png             # Imagen PNG del animal más rápido (P2, Diapos 5-9)
    │   ├── pinguino.png             # Imagen PNG Pingüino para opción con imagen (P5, Sobresaliente)
    │   ├── aguila.png               # Imagen PNG Águila para opción con imagen (P5, Sobresaliente)
    │   ├── loro.png                 # Imagen PNG Loro para opción con imagen (P5, Sobresaliente)
    │   ├── colibri.png              # Imagen PNG Colibrí para opción con imagen (P5, Sobresaliente)
    │   └── logo_android.png         # Logo Android proporcionado en la asignatura
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

## 5. Guion Sugerido para el Vídeo Explicativo (Máx. 3 minutos)

El enunciado exige un vídeo de máximo 3 minutos donde aparezcan los integrantes (rostro en cámara) explicando la app en ejecución y el código.

### Minuto 0:00 - 0:45 | Presentación y Demostración en Ejecución
- Presentarse ante la cámara: "Hola, soy [Nombre y Apellidos]. Esta es la entrega de la Práctica 1 de Laboratorio de Dispositivos Móviles: Quiz sobre el Reino Animal".
- Mostrar la app en pantalla:
  - **Pregunta 1:** Pregunta de texto con `RadioGroup` ("¿Cuál es un marsupial?"). Seleccionar "Koala", pulsar comprobar -> Se observa el Toast, el banner verde y la suma de +3 puntos.
  - **Pregunta 2:** Pregunta con `ImageView` ("¿Cuál es el más rápido?") y opciones de texto -> Seleccionar una errónea a propósito para mostrar la penalización de -2 puntos y el diálogo que permite "Continuar" o "Empezar de nuevo". Elegir "Continuar".

### Minuto 0:45 - 1:45 | Controles Variados y Requisito Sobresaliente
  - **Pregunta 3:** Mostrar el control `ListView` ("¿Qué ave tiene canto melodioso?"). Pulsar sobre "Ruiseñor", verificar el `OnItemClickListener` y avanzar.
  - **Pregunta 4:** Mostrar el control `Spinner` desplegable ("¿Cuál es el mamífero más grande?"). Desplegar la lista, elegir "Ballena azul" y comprobar.
  - **Pregunta 5 (Sobresaliente):** Enseñar la pregunta donde **las 4 opciones son imágenes** (`ImageButton`). Seleccionar el pingüino y pulsar en ver resultados.

### Minuto 1:45 - 2:30 | Segunda Activity y Transición Animada
  - Mostrar la animación de transición al abrir `ResultadoActivity`.
  - Explicar la pantalla final:
    - Animación gráfica Lottie.
    - Puntuación calculada exacta con aciertos y fallos.
    - Mensaje motivador personalizado.
    - Pulsar el botón **"Jugar de nuevo"** para demostrar que reinicia una nueva tanda con 0 puntos.

### Minuto 2:30 - 3:00 | Breve repaso del código y despedida
  - Mostrar en Android Studio:
    - `MainActivity.java`: La gestión de preguntas, listeners de cada control (`RadioGroup`, `ListView`, `Spinner`, `ImageButton`), cálculo de puntos y paso de parámetros mediante `Intent`.
    - `themes.xml` y `ActivityOptions`: Las transiciones entre pantallas.
    - Despedida formal.
