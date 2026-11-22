package com.example.practica1.bd;

/** Modelo de una partida guardada en la tabla "partidas" (ranking). */
public class Partida {

    private final String jugador;
    private final int puntos;
    private final int aciertos;
    private final int fallos;
    private final long fecha;

    public Partida(String jugador, int puntos, int aciertos, int fallos, long fecha) {
        this.jugador = jugador;
        this.puntos = puntos;
        this.aciertos = aciertos;
        this.fallos = fallos;
        this.fecha = fecha;
    }

    public String getJugador() {
        return jugador;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getAciertos() {
        return aciertos;
    }

    public int getFallos() {
        return fallos;
    }

    /** Fecha de la partida en milisegundos desde epoch. */
    public long getFecha() {
        return fecha;
    }
}
