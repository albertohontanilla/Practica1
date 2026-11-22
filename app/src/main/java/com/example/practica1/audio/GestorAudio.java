package com.example.practica1.audio;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.example.practica1.R;

/**
 * Gestión del contenido multimedia (audio) de la aplicación:
 *  - Efectos cortos de acierto/fallo con SoundPool (baja latencia, se cargan una vez en memoria).
 *  - Música de fondo en bucle con MediaPlayer.
 *  - Sonido de victoria/derrota en la pantalla de resultados con MediaPlayer.
 * Las preferencias de música y efectos se guardan en SharedPreferences.
 */
public class GestorAudio {

    private static final String PREFS = "ajustes_audio";
    private static final String KEY_MUSICA = "musica_activada";
    private static final String KEY_EFECTOS = "efectos_activados";
    private static final float VOLUMEN_MUSICA = 0.35f;

    private final Context context;
    private final SharedPreferences prefs;
    private final SoundPool soundPool;
    private final int idSonidoAcierto;
    private final int idSonidoFallo;

    private MediaPlayer musicaFondo;
    private MediaPlayer sonidoResultado;

    public GestorAudio(Context context) {
        this.context = context.getApplicationContext();
        this.prefs = this.context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

        AudioAttributes atributos = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(atributos)
                .build();
        idSonidoAcierto = soundPool.load(this.context, R.raw.sonido_acierto, 1);
        idSonidoFallo = soundPool.load(this.context, R.raw.sonido_fallo, 1);
    }

    // ------------------------------------------------------------------ Preferencias

    public boolean isMusicaActivada() {
        return prefs.getBoolean(KEY_MUSICA, true);
    }

    public boolean isEfectosActivados() {
        return prefs.getBoolean(KEY_EFECTOS, true);
    }

    /** Activa/desactiva la música de fondo y devuelve el nuevo estado. */
    public boolean alternarMusica() {
        boolean activada = !isMusicaActivada();
        prefs.edit().putBoolean(KEY_MUSICA, activada).apply();
        if (activada) {
            iniciarMusica();
        } else {
            pausarMusica();
        }
        return activada;
    }

    /** Activa/desactiva los efectos de sonido y devuelve el nuevo estado. */
    public boolean alternarEfectos() {
        boolean activados = !isEfectosActivados();
        prefs.edit().putBoolean(KEY_EFECTOS, activados).apply();
        return activados;
    }

    // ------------------------------------------------------------------ Efectos

    public void reproducirAcierto() {
        reproducirEfecto(idSonidoAcierto);
    }

    public void reproducirFallo() {
        reproducirEfecto(idSonidoFallo);
    }

    private void reproducirEfecto(int idSonido) {
        if (isEfectosActivados()) {
            soundPool.play(idSonido, 1f, 1f, 1, 0, 1f);
        }
    }

    /** Fanfarria de victoria o sonido de derrota al terminar la partida. */
    public void reproducirResultado(boolean victoria) {
        if (!isEfectosActivados()) {
            return;
        }
        liberarSonidoResultado();
        sonidoResultado = MediaPlayer.create(context,
                victoria ? R.raw.sonido_victoria : R.raw.sonido_derrota);
        if (sonidoResultado != null) {
            sonidoResultado.setOnCompletionListener(mp -> liberarSonidoResultado());
            sonidoResultado.start();
        }
    }

    // ------------------------------------------------------------------ Música de fondo

    public void iniciarMusica() {
        if (!isMusicaActivada()) {
            return;
        }
        if (musicaFondo == null) {
            musicaFondo = MediaPlayer.create(context, R.raw.musica_fondo);
            if (musicaFondo == null) {
                return;
            }
            musicaFondo.setLooping(true);
            musicaFondo.setVolume(VOLUMEN_MUSICA, VOLUMEN_MUSICA);
        }
        if (!musicaFondo.isPlaying()) {
            musicaFondo.start();
        }
    }

    public void pausarMusica() {
        if (musicaFondo != null && musicaFondo.isPlaying()) {
            musicaFondo.pause();
        }
    }

    // ------------------------------------------------------------------ Liberación

    private void liberarSonidoResultado() {
        if (sonidoResultado != null) {
            sonidoResultado.release();
            sonidoResultado = null;
        }
    }

    /** Libera todos los recursos de audio. Llamar en onDestroy() de la Activity. */
    public void liberar() {
        soundPool.release();
        liberarSonidoResultado();
        if (musicaFondo != null) {
            musicaFondo.release();
            musicaFondo = null;
        }
    }
}
