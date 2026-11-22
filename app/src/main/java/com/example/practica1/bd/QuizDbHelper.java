package com.example.practica1.bd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de la base de datos SQLite de la aplicación.
 *
 * Tablas:
 *  - preguntas: banco de preguntas del quiz (se precarga al crear la BD).
 *  - partidas:  puntuaciones guardadas por los jugadores (ranking).
 */
public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "quiz_cervecero.db";
    private static final int VERSION_BD = 1;

    // Tabla de preguntas
    private static final String TABLA_PREGUNTAS = "preguntas";
    private static final String COL_ID = "_id";
    private static final String COL_TIPO = "tipo";
    private static final String COL_ENUNCIADO = "enunciado";
    private static final String COL_OPCION_A = "opcion_a";
    private static final String COL_OPCION_B = "opcion_b";
    private static final String COL_OPCION_C = "opcion_c";
    private static final String COL_OPCION_D = "opcion_d";
    private static final String COL_CORRECTA = "correcta";
    private static final String COL_IMAGEN = "imagen";

    // Tabla de partidas (ranking)
    private static final String TABLA_PARTIDAS = "partidas";
    private static final String COL_JUGADOR = "jugador";
    private static final String COL_PUNTOS = "puntos";
    private static final String COL_ACIERTOS = "aciertos";
    private static final String COL_FALLOS = "fallos";
    private static final String COL_FECHA = "fecha";

    private static final String[] COLUMNAS_PREGUNTA = {
            COL_ID, COL_TIPO, COL_ENUNCIADO, COL_OPCION_A, COL_OPCION_B,
            COL_OPCION_C, COL_OPCION_D, COL_CORRECTA, COL_IMAGEN
    };

    /** Cada Activity crea su helper en onCreate() y lo cierra en onDestroy(). */
    public QuizDbHelper(Context context) {
        super(context.getApplicationContext(), NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLA_PREGUNTAS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TIPO + " INTEGER NOT NULL, "
                + COL_ENUNCIADO + " TEXT NOT NULL, "
                + COL_OPCION_A + " TEXT NOT NULL, "
                + COL_OPCION_B + " TEXT NOT NULL, "
                + COL_OPCION_C + " TEXT NOT NULL, "
                + COL_OPCION_D + " TEXT NOT NULL, "
                + COL_CORRECTA + " INTEGER NOT NULL, "
                + COL_IMAGEN + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLA_PARTIDAS + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_JUGADOR + " TEXT NOT NULL, "
                + COL_PUNTOS + " INTEGER NOT NULL, "
                + COL_ACIERTOS + " INTEGER NOT NULL, "
                + COL_FALLOS + " INTEGER NOT NULL, "
                + COL_FECHA + " INTEGER NOT NULL)");

        insertarPreguntasIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PREGUNTAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLA_PARTIDAS);
        onCreate(db);
    }

    // ------------------------------------------------------------------ Preguntas

    /** Devuelve una pregunta aleatoria del tipo indicado, o null si no hay ninguna. */
    @Nullable
    public Pregunta obtenerPreguntaAleatoria(int tipo) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLA_PREGUNTAS, COLUMNAS_PREGUNTA, COL_TIPO + " = ?",
                new String[]{String.valueOf(tipo)}, null, null, "RANDOM()", "1")) {
            return c.moveToFirst() ? leerPregunta(c) : null;
        }
    }

    /** Recupera una pregunta concreta por su id (se usa al restaurar el estado tras girar la pantalla). */
    @Nullable
    public Pregunta obtenerPregunta(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLA_PREGUNTAS, COLUMNAS_PREGUNTA, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? leerPregunta(c) : null;
        }
    }

    public int contarPreguntas() {
        return (int) DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLA_PREGUNTAS);
    }

    private Pregunta leerPregunta(Cursor c) {
        String[] opciones = {
                c.getString(c.getColumnIndexOrThrow(COL_OPCION_A)),
                c.getString(c.getColumnIndexOrThrow(COL_OPCION_B)),
                c.getString(c.getColumnIndexOrThrow(COL_OPCION_C)),
                c.getString(c.getColumnIndexOrThrow(COL_OPCION_D))
        };
        return new Pregunta(
                c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                c.getInt(c.getColumnIndexOrThrow(COL_TIPO)),
                c.getString(c.getColumnIndexOrThrow(COL_ENUNCIADO)),
                opciones,
                c.getInt(c.getColumnIndexOrThrow(COL_CORRECTA)),
                c.getString(c.getColumnIndexOrThrow(COL_IMAGEN)));
    }

    // ------------------------------------------------------------------ Partidas (ranking)

    public long guardarPartida(String jugador, int puntos, int aciertos, int fallos) {
        ContentValues valores = new ContentValues();
        valores.put(COL_JUGADOR, jugador);
        valores.put(COL_PUNTOS, puntos);
        valores.put(COL_ACIERTOS, aciertos);
        valores.put(COL_FALLOS, fallos);
        valores.put(COL_FECHA, System.currentTimeMillis());
        return getWritableDatabase().insert(TABLA_PARTIDAS, null, valores);
    }

    /** Mejores partidas: más puntos primero; a igualdad, la más antigua (la que lo consiguió antes). */
    public List<Partida> obtenerRanking(int limite) {
        List<Partida> partidas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLA_PARTIDAS, null, null, null, null, null,
                COL_PUNTOS + " DESC, " + COL_FECHA + " ASC", String.valueOf(limite))) {
            while (c.moveToNext()) {
                partidas.add(new Partida(
                        c.getString(c.getColumnIndexOrThrow(COL_JUGADOR)),
                        c.getInt(c.getColumnIndexOrThrow(COL_PUNTOS)),
                        c.getInt(c.getColumnIndexOrThrow(COL_ACIERTOS)),
                        c.getInt(c.getColumnIndexOrThrow(COL_FALLOS)),
                        c.getLong(c.getColumnIndexOrThrow(COL_FECHA))));
            }
        }
        return partidas;
    }

    public void borrarHistorial() {
        getWritableDatabase().delete(TABLA_PARTIDAS, null, null);
    }

    // ------------------------------------------------------------------ Datos iniciales

    private void insertarPreguntasIniciales(SQLiteDatabase db) {
        // Tipo 1: texto + RadioButtons
        insertar(db, Pregunta.TIPO_RADIO, "¿Qué marca española nació en Sevilla en 1904?",
                "Mahou", "Cruzcampo", "San Miguel", "Estrella Galicia", 1, null);
        insertar(db, Pregunta.TIPO_RADIO, "¿En qué ciudad se fundó la cervecera Mahou en 1890?",
                "Barcelona", "Valencia", "Madrid", "Bilbao", 2, null);
        insertar(db, Pregunta.TIPO_RADIO, "¿Qué cerveza irlandesa es famosa por su stout negra?",
                "Corona", "Guinness", "Stella Artois", "Heineken", 1, null);
        insertar(db, Pregunta.TIPO_RADIO, "¿Qué ingrediente aporta el amargor característico a la cerveza?",
                "El lúpulo", "La cebada", "El trigo", "El arroz", 0, null);

        // Tipo 2: imagen + RadioButtons
        insertar(db, Pregunta.TIPO_IMAGEN, "¿Qué marca neerlandesa es conocida por la estrella roja de su etiqueta?",
                "Heineken", "Guinness", "Carlsberg", "Budweiser", 0, ImagenesQuiz.JARRA);
        insertar(db, Pregunta.TIPO_IMAGEN, "¿En qué ciudad se fundó la marca de este logotipo?",
                "Madrid", "Granada", "Sevilla", "A Coruña", 3, ImagenesQuiz.LOGO_ESTRELLA_GALICIA);
        insertar(db, Pregunta.TIPO_IMAGEN, "¿A qué grupo cervecero pertenece la marca de este logotipo?",
                "Damm", "Mahou San Miguel", "Hijos de Rivera", "Heineken España", 1, ImagenesQuiz.LOGO_MAHOU);
        insertar(db, Pregunta.TIPO_IMAGEN, "¿En qué país se fundó originalmente la marca de este logotipo?",
                "Filipinas", "España", "México", "Portugal", 0, ImagenesQuiz.LOGO_SAN_MIGUEL);

        // Tipo 3: texto + ListView
        insertar(db, Pregunta.TIPO_LISTA, "¿Cuál de estas marcas pertenece al grupo cervecero Damm?",
                "Estrella Damm", "Mahou", "Alhambra", "Ambar", 0, null);
        insertar(db, Pregunta.TIPO_LISTA, "¿Qué cerveza se elabora originalmente en Granada?",
                "Ambar", "Moritz", "Alhambra", "Turia", 2, null);
        insertar(db, Pregunta.TIPO_LISTA, "¿Qué marca de cerveza es originaria de México?",
                "Peroni", "Kronenbourg", "Leffe", "Corona", 3, null);
        insertar(db, Pregunta.TIPO_LISTA, "¿Qué estilo de cerveza se fermenta a baja temperatura?",
                "Stout", "Lager", "IPA", "Porter", 1, null);

        // Tipo 4: texto + Spinner
        insertar(db, Pregunta.TIPO_SPINNER, "Pilsner Urquell, una de las marcas más conocidas de cerveza pilsner, nació en…",
                "Alemania", "República Checa", "Bélgica", "Irlanda", 1, null);
        insertar(db, Pregunta.TIPO_SPINNER, "¿De qué país es originaria la cerveza Peroni?",
                "Francia", "Grecia", "Italia", "Portugal", 2, null);
        insertar(db, Pregunta.TIPO_SPINNER, "¿De qué país es la cerveza Carlsberg?",
                "Suecia", "Noruega", "Finlandia", "Dinamarca", 3, null);
        insertar(db, Pregunta.TIPO_SPINNER, "¿De qué país es originaria la cerveza Stella Artois?",
                "Bélgica", "Países Bajos", "Alemania", "Austria", 0, null);

        // Tipo 5: texto + opciones con imágenes (las opciones son claves de imagen)
        insertar(db, Pregunta.TIPO_LOGOS, "¿Qué logotipo corresponde a una marca fundada en A Coruña en 1906?",
                ImagenesQuiz.LOGO_MAHOU, ImagenesQuiz.LOGO_ESTRELLA_GALICIA,
                ImagenesQuiz.LOGO_CRUZCAMPO, ImagenesQuiz.LOGO_SAN_MIGUEL, 1, null);
        insertar(db, Pregunta.TIPO_LOGOS, "¿Qué logotipo corresponde a la marca fundada en Madrid en 1890?",
                ImagenesQuiz.LOGO_CRUZCAMPO, ImagenesQuiz.LOGO_SAN_MIGUEL,
                ImagenesQuiz.LOGO_ESTRELLA_GALICIA, ImagenesQuiz.LOGO_MAHOU, 3, null);
        insertar(db, Pregunta.TIPO_LOGOS, "¿Qué logotipo es de la cerveza que toma su nombre del templete sevillano de la Cruz del Campo?",
                ImagenesQuiz.LOGO_CRUZCAMPO, ImagenesQuiz.LOGO_MAHOU,
                ImagenesQuiz.LOGO_SAN_MIGUEL, ImagenesQuiz.LOGO_ESTRELLA_GALICIA, 0, null);
        insertar(db, Pregunta.TIPO_LOGOS, "¿Qué logotipo corresponde a la marca fundada en Manila (Filipinas) en 1890?",
                ImagenesQuiz.LOGO_ESTRELLA_GALICIA, ImagenesQuiz.LOGO_CRUZCAMPO,
                ImagenesQuiz.LOGO_SAN_MIGUEL, ImagenesQuiz.LOGO_MAHOU, 2, null);
    }

    private void insertar(SQLiteDatabase db, int tipo, String enunciado,
                          String a, String b, String c, String d, int correcta, String imagen) {
        ContentValues valores = new ContentValues();
        valores.put(COL_TIPO, tipo);
        valores.put(COL_ENUNCIADO, enunciado);
        valores.put(COL_OPCION_A, a);
        valores.put(COL_OPCION_B, b);
        valores.put(COL_OPCION_C, c);
        valores.put(COL_OPCION_D, d);
        valores.put(COL_CORRECTA, correcta);
        valores.put(COL_IMAGEN, imagen);
        db.insert(TABLA_PREGUNTAS, null, valores);
    }
}
