package com.example.practica1;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.practica1.audio.GestorAudio;
import com.example.practica1.bd.QuizDbHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ResultadoActivity extends AppCompatActivity {

    private static final String PREFS_JUGADOR = "jugador";
    private static final String KEY_ULTIMO_NOMBRE = "ultimo_nombre";
    private static final String ESTADO_GUARDADA = "partida_guardada";
    private static final int PUNTOS_ACIERTO = 3;
    private static final int PUNTOS_FALLO = 2;

    private TextView tvPuntuacionFinal;
    private TextView tvResumenAciertos;
    private TextView tvResumenFallos;
    private TextView tvMensajeVeredicto;
    private EditText etNombreJugador;
    private MaterialButton btnGuardarPuntuacion;
    private MaterialButton btnJugarDeNuevo;
    private MaterialButton btnVerRanking;
    private MaterialButton btnSalir;

    private QuizDbHelper db;
    private GestorAudio audio;

    private int puntos;
    private int aciertos;
    private int fallos;
    private boolean partidaGuardada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new QuizDbHelper(this);
        audio = new GestorAudio(this);

        vincularVistas();
        mostrarResultados();
        configurarListeners();

        if (savedInstanceState != null) {
            partidaGuardada = savedInstanceState.getBoolean(ESTADO_GUARDADA);
        } else {
            // Solo la primera vez (no al girar la pantalla)
            audio.reproducirResultado(aciertos > fallos);
            SharedPreferences prefs = getSharedPreferences(PREFS_JUGADOR, MODE_PRIVATE);
            etNombreJugador.setText(prefs.getString(KEY_ULTIMO_NOMBRE, ""));
        }
        actualizarEstadoGuardado();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ESTADO_GUARDADA, partidaGuardada);
    }

    @Override
    protected void onDestroy() {
        audio.liberar();
        db.close();
        super.onDestroy();
    }

    private void vincularVistas() {
        tvPuntuacionFinal = findViewById(R.id.tvPuntuacionFinal);
        tvResumenAciertos = findViewById(R.id.tvResumenAciertos);
        tvResumenFallos = findViewById(R.id.tvResumenFallos);
        tvMensajeVeredicto = findViewById(R.id.tvMensajeVeredicto);
        etNombreJugador = findViewById(R.id.etNombreJugador);
        btnGuardarPuntuacion = findViewById(R.id.btnGuardarPuntuacion);
        btnJugarDeNuevo = findViewById(R.id.btnJugarDeNuevo);
        btnVerRanking = findViewById(R.id.btnVerRanking);
        btnSalir = findViewById(R.id.btnSalir);
    }

    private void mostrarResultados() {
        Intent intent = getIntent();
        puntos = intent.getIntExtra(MainActivity.EXTRA_PUNTUACION, 0);
        aciertos = intent.getIntExtra(MainActivity.EXTRA_ACIERTOS, 0);
        fallos = intent.getIntExtra(MainActivity.EXTRA_FALLOS, 0);
        int total = intent.getIntExtra(MainActivity.EXTRA_TOTAL_PREGUNTAS, 5);

        // Puntuación obtenida
        tvPuntuacionFinal.setText(getString(
                puntos > 0 ? R.string.resultado_puntos_positivo_format : R.string.resultado_puntos_format, puntos));

        // Resumen detallado de aciertos y fallos
        tvResumenAciertos.setText(getString(R.string.resultado_aciertos_format, aciertos, total, aciertos * PUNTOS_ACIERTO));
        tvResumenFallos.setText(getString(R.string.resultado_fallos_format, fallos, total, fallos * PUNTOS_FALLO));

        // Mensaje de veredicto según los puntos obtenidos
        int texto;
        int color;
        if (puntos >= 12) {
            texto = R.string.resultado_excelente;
            color = R.color.primary_dark;
        } else if (puntos >= 6) {
            texto = R.string.resultado_notable;
            color = R.color.primary_dark;
        } else if (puntos >= 0) {
            texto = R.string.resultado_aprobado;
            color = R.color.accent_dark;
        } else {
            texto = R.string.resultado_suspenso;
            color = R.color.error;
        }
        tvMensajeVeredicto.setText(texto);
        tvMensajeVeredicto.setTextColor(ContextCompat.getColor(this, color));
    }

    private void configurarListeners() {
        btnGuardarPuntuacion.setOnClickListener(v -> guardarPuntuacion());
        etNombreJugador.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                guardarPuntuacion();
                return true;
            }
            return false;
        });

        // Opción de comenzar una nueva tanda del juego
        btnJugarDeNuevo.setOnClickListener(v -> {
            Intent intent = new Intent(ResultadoActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResultadoActivity.this);
            startActivity(intent, options.toBundle());
            finish();
        });

        btnVerRanking.setOnClickListener(v -> startActivity(new Intent(this, RankingActivity.class)));

        btnSalir.setOnClickListener(v -> finishAffinity());
    }

    /** Inserta la partida en la tabla "partidas" de la base de datos. */
    private void guardarPuntuacion() {
        if (partidaGuardada) {
            return;
        }
        String nombre = etNombreJugador.getText().toString().trim();
        if (nombre.isEmpty()) {
            nombre = getString(R.string.jugador_anonimo);
        } else {
            getSharedPreferences(PREFS_JUGADOR, MODE_PRIVATE).edit()
                    .putString(KEY_ULTIMO_NOMBRE, nombre).apply();
        }

        if (db.guardarPartida(nombre, puntos, aciertos, fallos) != -1) {
            partidaGuardada = true;
            actualizarEstadoGuardado();
            ocultarTeclado();
            Toast.makeText(this, R.string.msg_puntuacion_guardada, Toast.LENGTH_SHORT).show();
        }
    }

    /** Evita guardar la misma partida dos veces. */
    private void actualizarEstadoGuardado() {
        etNombreJugador.setEnabled(!partidaGuardada);
        btnGuardarPuntuacion.setEnabled(!partidaGuardada);
    }

    private void ocultarTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etNombreJugador.getWindowToken(), 0);
        }
    }
}
