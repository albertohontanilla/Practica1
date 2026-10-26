package com.example.practica1;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

public class ResultadoActivity extends AppCompatActivity {

    private TextView tvPuntuacionFinal;
    private TextView tvResumenAciertos;
    private TextView tvResumenFallos;
    private TextView tvMensajeVeredicto;
    private MaterialButton btnJugarDeNuevo;
    private MaterialButton btnSalir;
    private LottieAnimationView lottieCelebration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        vincularVistas();
        mostrarResultados();
        configurarListeners();
    }

    private void vincularVistas() {
        tvPuntuacionFinal = findViewById(R.id.tvPuntuacionFinal);
        tvResumenAciertos = findViewById(R.id.tvResumenAciertos);
        tvResumenFallos = findViewById(R.id.tvResumenFallos);
        tvMensajeVeredicto = findViewById(R.id.tvMensajeVeredicto);
        btnJugarDeNuevo = findViewById(R.id.btnJugarDeNuevo);
        btnSalir = findViewById(R.id.btnSalir);
        lottieCelebration = findViewById(R.id.lottieCelebration);
    }

    private void mostrarResultados() {
        Intent intent = getIntent();
        int puntos = intent.getIntExtra("PUNTUACION", 0);
        int aciertos = intent.getIntExtra("ACIERTOS", 0);
        int fallos = intent.getIntExtra("FALLOS", 0);
        int total = intent.getIntExtra("TOTAL_PREGUNTAS", 5);

        // Puntuación obtenida
        String signo = puntos > 0 ? "+" : "";
        tvPuntuacionFinal.setText(String.format(Locale.getDefault(), "%s%d pts", signo, puntos));

        // Resumen detallado de aciertos y fallos
        int puntosGanados = aciertos * 3;
        int puntosRestados = fallos * 2;
        tvResumenAciertos.setText(getString(R.string.resultado_aciertos_format, aciertos, total, puntosGanados));
        tvResumenFallos.setText(getString(R.string.resultado_fallos_format, fallos, total, puntosRestados));

        // Mensaje de veredicto según los puntos obtenidos
        if (puntos >= 12) {
            tvMensajeVeredicto.setText(R.string.resultado_excelente);
            tvMensajeVeredicto.setTextColor(getResources().getColor(R.color.primary_dark));
        } else if (puntos >= 6) {
            tvMensajeVeredicto.setText(R.string.resultado_notable);
            tvMensajeVeredicto.setTextColor(getResources().getColor(R.color.primary_dark));
        } else if (puntos >= 0) {
            tvMensajeVeredicto.setText(R.string.resultado_aprobado);
            tvMensajeVeredicto.setTextColor(getResources().getColor(R.color.accent_dark));
        } else {
            tvMensajeVeredicto.setText(R.string.resultado_suspenso);
            tvMensajeVeredicto.setTextColor(getResources().getColor(R.color.error));
        }
    }

    private void configurarListeners() {
        // Opción de comenzar una nueva tanda del juego 
        btnJugarDeNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultadoActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ResultadoActivity.this);
                startActivity(intent, options.toBundle());
                finish();
            }
        });

        // Botón Salir
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }
}
