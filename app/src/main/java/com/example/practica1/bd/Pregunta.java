package com.example.practica1.bd;

/**
 * Modelo de una pregunta del quiz tal y como se almacena en la tabla "preguntas".
 * El tipo indica qué control de la interfaz se utiliza para mostrarla.
 */
public class Pregunta {

    public static final int TIPO_RADIO = 1;    // Texto + RadioButtons
    public static final int TIPO_IMAGEN = 2;   // Imagen + RadioButtons
    public static final int TIPO_LISTA = 3;    // Texto + ListView
    public static final int TIPO_SPINNER = 4;  // Texto + Spinner
    public static final int TIPO_LOGOS = 5;    // Texto + opciones con imágenes

    public static final int NUM_OPCIONES = 4;

    private final long id;
    private final int tipo;
    private final String enunciado;
    private final String[] opciones;
    private final int correcta;
    private final String imagen;

    public Pregunta(long id, int tipo, String enunciado, String[] opciones, int correcta, String imagen) {
        this.id = id;
        this.tipo = tipo;
        this.enunciado = enunciado;
        this.opciones = opciones;
        this.correcta = correcta;
        this.imagen = imagen;
    }

    public long getId() {
        return id;
    }

    public int getTipo() {
        return tipo;
    }

    public String getEnunciado() {
        return enunciado;
    }

    /** Para las preguntas de tipo LOGOS cada opción es la clave de una imagen (ver {@link ImagenesQuiz}). */
    public String getOpcion(int indice) {
        return opciones[indice];
    }

    /** Índice (0..3) de la opción correcta. */
    public int getCorrecta() {
        return correcta;
    }

    /** Clave de la imagen de apoyo (solo preguntas de tipo IMAGEN), o null. */
    public String getImagen() {
        return imagen;
    }
}
