package com.example.practica1;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.practica1.audio.GestorAudio;
import com.example.practica1.bd.ImagenesQuiz;
import com.example.practica1.bd.Pregunta;
import com.example.practica1.bd.QuizDbHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // Claves de los extras enviados a ResultadoActivity
    public static final String EXTRA_PUNTUACION = "PUNTUACION";
    public static final String EXTRA_ACIERTOS = "ACIERTOS";
    public static final String EXTRA_FALLOS = "FALLOS";
    public static final String EXTRA_TOTAL_PREGUNTAS = "TOTAL_PREGUNTAS";

    // Claves para conservar el estado al girar la pantalla
    private static final String ESTADO_IDS = "ids_preguntas";
    private static final String ESTADO_PREGUNTA = "pregunta_actual";
    private static final String ESTADO_PUNTOS = "puntos";
    private static final String ESTADO_ACIERTOS = "aciertos";
    private static final String ESTADO_FALLOS = "fallos";
    private static final String ESTADO_RESPONDIDA = "respondida";
    private static final String ESTADO_ULTIMO_ACIERTO = "ultimo_acierto";

    private static final int TOTAL_PREGUNTAS = 5;
    private static final int PUNTOS_ACIERTO = 3;
    private static final int PUNTOS_FALLO = 2;

    // Orden de los tipos de pregunta en cada partida (uno de cada control)
    private static final int[] TIPOS_POR_POSICION = {
            Pregunta.TIPO_RADIO, Pregunta.TIPO_IMAGEN, Pregunta.TIPO_LISTA,
            Pregunta.TIPO_SPINNER, Pregunta.TIPO_LOGOS
    };

    // Base de datos y audio
    private QuizDbHelper db;
    private GestorAudio audio;

    // Preguntas de la partida actual, elegidas al azar de la BD
    private final Pregunta[] preguntas = new Pregunta[TOTAL_PREGUNTAS];

    // Marcadores y contadores del juego
    private int puntos = 0;
    private int aciertos = 0;
    private int fallos = 0;
    private int preguntaActual = 1; // 1 a 5

    // Estado de la pregunta actual
    private boolean respondida = false;
    private boolean ultimoAcierto = false;
    private int seleccionActual = -1; // Índice de la opción seleccionada (0..3)

    // Vistas principales
    private TextView tvQuestionProgress;
    private TextView tvScore;
    private TextView tvQuestionTitle;
    private TextView tvQuestionText;

    // Contenedores de cada tipo de pregunta
    private LinearLayout layoutQuestion2;
    private LinearLayout layoutQuestion3;
    private LinearLayout layoutQuestion4;
    private LinearLayout layoutQuestion5;

    // Feedback
    private LinearLayout layoutFeedback;
    private ImageView ivFeedbackIcon;
    private TextView tvFeedbackMessage;

    // Botones inferiores
    private MaterialButton btnRestart;
    private MaterialButton btnAction;

    // Controles específicos de preguntas
    private RadioGroup radioGroupQ1;
    private RadioGroup radioGroupQ2;
    private final RadioButton[] opcionesQ1 = new RadioButton[Pregunta.NUM_OPCIONES];
    private final RadioButton[] opcionesQ2 = new RadioButton[Pregunta.NUM_OPCIONES];
    private ImageView ivQuestion2Image;
    private ListView listViewQ3;
    private Spinner spinnerQ4;
    private TextView tvSelectedSpinnerOption;

    // Adaptadores con las opciones cargadas desde la BD
    private final List<String> opcionesLista = new ArrayList<>();
    private final List<String> opcionesSpinner = new ArrayList<>();
    private ArrayAdapter<String> adapterLista;
    private ArrayAdapter<String> adapterSpinner;

    // Opciones con imagen (P5)
    private final LinearLayout[] cardsImagen = new LinearLayout[Pregunta.NUM_OPCIONES];
    private final ImageButton[] botonesImagen = new ImageButton[Pregunta.NUM_OPCIONES];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new QuizDbHelper(this);
        audio = new GestorAudio(this);

        vincularVistas();
        configurarListeners();

        if (savedInstanceState == null || !restaurarEstado(savedInstanceState)) {
            nuevaPartida();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        audio.iniciarMusica();
    }

    @Override
    protected void onPause() {
        super.onPause();
        audio.pausarMusica();
    }

    @Override
    protected void onDestroy() {
        audio.liberar();
        db.close();
        super.onDestroy();
    }

    // ------------------------------------------------------------------ Toolbar / menú

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_musica).setChecked(audio.isMusicaActivada());
        menu.findItem(R.id.action_efectos).setChecked(audio.isEfectosActivados());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_ranking) {
            startActivity(new Intent(this, RankingActivity.class));
            return true;
        } else if (id == R.id.action_manual) {
            startActivity(new Intent(this, ManualActivity.class));
            return true;
        } else if (id == R.id.action_musica) {
            item.setChecked(audio.alternarMusica());
            return true;
        } else if (id == R.id.action_efectos) {
            item.setChecked(audio.alternarEfectos());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ------------------------------------------------------------------ Estado (giro de pantalla)

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        long[] ids = new long[TOTAL_PREGUNTAS];
        for (int i = 0; i < TOTAL_PREGUNTAS; i++) {
            ids[i] = preguntas[i] != null ? preguntas[i].getId() : -1;
        }
        outState.putLongArray(ESTADO_IDS, ids);
        outState.putInt(ESTADO_PREGUNTA, preguntaActual);
        outState.putInt(ESTADO_PUNTOS, puntos);
        outState.putInt(ESTADO_ACIERTOS, aciertos);
        outState.putInt(ESTADO_FALLOS, fallos);
        outState.putBoolean(ESTADO_RESPONDIDA, respondida);
        outState.putBoolean(ESTADO_ULTIMO_ACIERTO, ultimoAcierto);
    }

    /** Recupera la partida en curso. Devuelve false si no se puede restaurar. */
    private boolean restaurarEstado(Bundle estado) {
        long[] ids = estado.getLongArray(ESTADO_IDS);
        if (ids == null || ids.length != TOTAL_PREGUNTAS) {
            return false;
        }
        for (int i = 0; i < TOTAL_PREGUNTAS; i++) {
            preguntas[i] = db.obtenerPregunta(ids[i]);
            if (preguntas[i] == null) {
                return false;
            }
        }
        puntos = estado.getInt(ESTADO_PUNTOS);
        aciertos = estado.getInt(ESTADO_ACIERTOS);
        fallos = estado.getInt(ESTADO_FALLOS);
        preguntaActual = estado.getInt(ESTADO_PREGUNTA, 1);
        cargarPregunta(preguntaActual);

        if (estado.getBoolean(ESTADO_RESPONDIDA)) {
            respondida = true;
            ultimoAcierto = estado.getBoolean(ESTADO_ULTIMO_ACIERTO);
            mostrarFeedback(ultimoAcierto, getString(ultimoAcierto ? R.string.msg_acierto : R.string.msg_fallo));
            deshabilitarControlesPregunta();
            prepararBotonSiguiente();
        }
        return true;
    }

    // ------------------------------------------------------------------ Inicialización de vistas

    private void vincularVistas() {
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress);
        tvScore = findViewById(R.id.tvScore);
        tvQuestionTitle = findViewById(R.id.tvQuestionTitle);
        tvQuestionText = findViewById(R.id.tvQuestionText);

        layoutQuestion2 = findViewById(R.id.layoutQuestion2);
        layoutQuestion3 = findViewById(R.id.layoutQuestion3);
        layoutQuestion4 = findViewById(R.id.layoutQuestion4);
        layoutQuestion5 = findViewById(R.id.layoutQuestion5);

        layoutFeedback = findViewById(R.id.layoutFeedback);
        ivFeedbackIcon = findViewById(R.id.ivFeedbackIcon);
        tvFeedbackMessage = findViewById(R.id.tvFeedbackMessage);

        btnRestart = findViewById(R.id.btnRestart);
        btnAction = findViewById(R.id.btnAction);

        // Controles de P1
        radioGroupQ1 = findViewById(R.id.radioGroupQ1);
        opcionesQ1[0] = findViewById(R.id.rbQ1OptionA);
        opcionesQ1[1] = findViewById(R.id.rbQ1OptionB);
        opcionesQ1[2] = findViewById(R.id.rbQ1OptionC);
        opcionesQ1[3] = findViewById(R.id.rbQ1OptionD);

        // Controles de P2
        radioGroupQ2 = findViewById(R.id.radioGroupQ2);
        ivQuestion2Image = findViewById(R.id.ivQuestion2Image);
        opcionesQ2[0] = findViewById(R.id.rbQ2OptionA);
        opcionesQ2[1] = findViewById(R.id.rbQ2OptionB);
        opcionesQ2[2] = findViewById(R.id.rbQ2OptionC);
        opcionesQ2[3] = findViewById(R.id.rbQ2OptionD);

        // Controles de P3 y P4
        listViewQ3 = findViewById(R.id.listViewQ3);
        spinnerQ4 = findViewById(R.id.spinnerQ4);
        tvSelectedSpinnerOption = findViewById(R.id.tvSelectedSpinnerOption);

        // Controles de P5
        cardsImagen[0] = findViewById(R.id.cardImgOption1);
        cardsImagen[1] = findViewById(R.id.cardImgOption2);
        cardsImagen[2] = findViewById(R.id.cardImgOption3);
        cardsImagen[3] = findViewById(R.id.cardImgOption4);
        botonesImagen[0] = findViewById(R.id.ibQ5Option1);
        botonesImagen[1] = findViewById(R.id.ibQ5Option2);
        botonesImagen[2] = findViewById(R.id.ibQ5Option3);
        botonesImagen[3] = findViewById(R.id.ibQ5Option4);
    }

    private void configurarListeners() {
        btnRestart.setOnClickListener(v -> mostrarDialogoConfirmarReinicio());

        // Botón de acción (Comprobar / Siguiente)
        btnAction.setOnClickListener(v -> {
            if (!respondida) {
                comprobarRespuesta();
            } else {
                avanzarPregunta();
            }
        });

        // P1 y P2 (RadioGroup): la opción elegida es la posición del RadioButton marcado
        radioGroupQ1.setOnCheckedChangeListener((group, checkedId) -> seleccionarRadio(opcionesQ1, checkedId));
        radioGroupQ2.setOnCheckedChangeListener((group, checkedId) -> seleccionarRadio(opcionesQ2, checkedId));

        // P3 (ListView)
        adapterLista = new ArrayAdapter<>(this, R.layout.item_quiz_option, R.id.tvOptionText, opcionesLista);
        listViewQ3.setAdapter(adapterLista);
        listViewQ3.setOnItemClickListener(this);

        // P4 (Spinner): la posición 0 es el texto "Selecciona una opción"
        adapterSpinner = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesSpinner);
        spinnerQ4.setAdapter(adapterSpinner);
        spinnerQ4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (respondida) return;
                if (position == 0) {
                    seleccionActual = -1;
                    tvSelectedSpinnerOption.setVisibility(View.GONE);
                } else {
                    seleccionActual = position - 1;
                    tvSelectedSpinnerOption.setVisibility(View.VISIBLE);
                    tvSelectedSpinnerOption.setText(getString(R.string.spinner_seleccionado_format,
                            opcionesSpinner.get(position)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                seleccionActual = -1;
            }
        });

        // P5 (opciones con imágenes): tanto la tarjeta como el ImageButton seleccionan la opción
        for (int i = 0; i < Pregunta.NUM_OPCIONES; i++) {
            final int opcion = i;
            View.OnClickListener listener = v -> {
                if (!respondida) seleccionarOpcionImagen(opcion);
            };
            cardsImagen[i].setOnClickListener(listener);
            botonesImagen[i].setOnClickListener(listener);
        }
    }

    private void seleccionarRadio(RadioButton[] botones, int checkedId) {
        for (int i = 0; i < botones.length; i++) {
            if (botones[i].getId() == checkedId) {
                seleccionActual = i;
                return;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (respondida) return;
        seleccionActual = position;

        // Marcar visualmente el elemento seleccionado en la lista
        for (int i = 0; i < parent.getChildCount(); i++) {
            parent.getChildAt(i).setBackgroundResource(R.drawable.bg_option_card);
        }
        view.setBackgroundResource(R.drawable.bg_option_selected);
    }

    private void seleccionarOpcionImagen(int opcion) {
        seleccionActual = opcion;
        for (int i = 0; i < Pregunta.NUM_OPCIONES; i++) {
            cardsImagen[i].setBackgroundResource(i == opcion ? R.drawable.bg_option_selected : R.drawable.bg_option_card);
        }
    }

    // ------------------------------------------------------------------ Lógica del juego

    /** Elige al azar de la base de datos una pregunta de cada tipo y empieza desde la primera. */
    private void nuevaPartida() {
        puntos = 0;
        aciertos = 0;
        fallos = 0;
        preguntaActual = 1;
        for (int i = 0; i < TOTAL_PREGUNTAS; i++) {
            preguntas[i] = db.obtenerPreguntaAleatoria(TIPOS_POR_POSICION[i]);
            if (preguntas[i] == null) {
                Toast.makeText(this, R.string.msg_sin_preguntas, Toast.LENGTH_LONG).show();
                btnAction.setEnabled(false);
                return;
            }
        }
        btnAction.setEnabled(true);
        cargarPregunta(preguntaActual);
    }

    private void cargarPregunta(int numero) {
        Pregunta pregunta = preguntas[numero - 1];
        respondida = false;
        seleccionActual = -1;
        actualizarMarcadores();

        // Ocultar todos los contenedores
        radioGroupQ1.setVisibility(View.GONE);
        layoutQuestion2.setVisibility(View.GONE);
        layoutQuestion3.setVisibility(View.GONE);
        layoutQuestion4.setVisibility(View.GONE);
        layoutQuestion5.setVisibility(View.GONE);
        layoutFeedback.setVisibility(View.GONE);

        // Restaurar estado del botón de acción
        btnAction.setText(R.string.btn_comprobar);
        btnAction.setBackgroundColor(ContextCompat.getColor(this, R.color.accent));

        tvQuestionTitle.setText(getString(R.string.pregunta_titulo_format, numero));
        tvQuestionText.setText(pregunta.getEnunciado());

        switch (pregunta.getTipo()) {
            case Pregunta.TIPO_RADIO:
                radioGroupQ1.setVisibility(View.VISIBLE);
                prepararRadioGroup(radioGroupQ1, opcionesQ1, pregunta);
                break;

            case Pregunta.TIPO_IMAGEN:
                layoutQuestion2.setVisibility(View.VISIBLE);
                ivQuestion2Image.setImageResource(ImagenesQuiz.obtener(pregunta.getImagen()));
                prepararRadioGroup(radioGroupQ2, opcionesQ2, pregunta);
                break;

            case Pregunta.TIPO_LISTA:
                layoutQuestion3.setVisibility(View.VISIBLE);
                opcionesLista.clear();
                for (int i = 0; i < Pregunta.NUM_OPCIONES; i++) {
                    opcionesLista.add(pregunta.getOpcion(i));
                }
                adapterLista.notifyDataSetChanged();
                for (int i = 0; i < listViewQ3.getChildCount(); i++) {
                    listViewQ3.getChildAt(i).setBackgroundResource(R.drawable.bg_option_card);
                }
                listViewQ3.setEnabled(true);
                break;

            case Pregunta.TIPO_SPINNER:
                layoutQuestion4.setVisibility(View.VISIBLE);
                opcionesSpinner.clear();
                opcionesSpinner.add(getString(R.string.spinner_placeholder));
                for (int i = 0; i < Pregunta.NUM_OPCIONES; i++) {
                    opcionesSpinner.add(pregunta.getOpcion(i));
                }
                adapterSpinner.notifyDataSetChanged();
                spinnerQ4.setEnabled(true);
                spinnerQ4.setSelection(0);
                tvSelectedSpinnerOption.setVisibility(View.GONE);
                break;

            case Pregunta.TIPO_LOGOS:
                layoutQuestion5.setVisibility(View.VISIBLE);
                for (int i = 0; i < Pregunta.NUM_OPCIONES; i++) {
                    botonesImagen[i].setImageResource(ImagenesQuiz.obtener(pregunta.getOpcion(i)));
                    cardsImagen[i].setBackgroundResource(R.drawable.bg_option_card);
                    cardsImagen[i].setEnabled(true);
                    botonesImagen[i].setEnabled(true);
                }
                break;

            default:
                break;
        }
    }

    private void prepararRadioGroup(RadioGroup grupo, RadioButton[] botones, Pregunta pregunta) {
        grupo.clearCheck();
        for (int i = 0; i < Pregunta.NUM_OPCIONES; i++) {
            botones[i].setText(pregunta.getOpcion(i));
            botones[i].setEnabled(true);
        }
        seleccionActual = -1;
    }

    private void comprobarRespuesta() {
        if (seleccionActual == -1) {
            Toast.makeText(this, R.string.msg_debe_seleccionar, Toast.LENGTH_SHORT).show();
            return;
        }

        respondida = true;
        deshabilitarControlesPregunta();
        boolean acierto = seleccionActual == preguntas[preguntaActual - 1].getCorrecta();
        ultimoAcierto = acierto;

        if (acierto) {
            puntos += PUNTOS_ACIERTO;
            aciertos++;
            audio.reproducirAcierto();
            mostrarFeedback(true, getString(R.string.msg_acierto));
            actualizarMarcadores();
            prepararBotonSiguiente();
        } else {
            puntos -= PUNTOS_FALLO;
            fallos++;
            audio.reproducirFallo();
            mostrarFeedback(false, getString(R.string.msg_fallo));
            actualizarMarcadores();
            mostrarDialogoFallo();
        }
    }

    private void prepararBotonSiguiente() {
        btnAction.setText(preguntaActual == TOTAL_PREGUNTAS ? R.string.btn_ver_resultados : R.string.btn_siguiente);
        btnAction.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
    }

    private void mostrarFeedback(boolean esCorrecto, String mensaje) {
        layoutFeedback.setVisibility(View.VISIBLE);
        if (esCorrecto) {
            layoutFeedback.setBackgroundResource(R.drawable.bg_option_selected);
            ivFeedbackIcon.setImageResource(R.drawable.ic_check);
            tvFeedbackMessage.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
        } else {
            layoutFeedback.setBackgroundColor(ContextCompat.getColor(this, R.color.error_bg));
            ivFeedbackIcon.setImageResource(R.drawable.ic_cross);
            tvFeedbackMessage.setTextColor(ContextCompat.getColor(this, R.color.error));
        }
        tvFeedbackMessage.setText(mensaje);
    }

    private void deshabilitarControlesPregunta() {
        switch (preguntas[preguntaActual - 1].getTipo()) {
            case Pregunta.TIPO_RADIO:
                habilitarBotones(opcionesQ1, false);
                break;
            case Pregunta.TIPO_IMAGEN:
                habilitarBotones(opcionesQ2, false);
                break;
            case Pregunta.TIPO_LISTA:
                listViewQ3.setEnabled(false);
                break;
            case Pregunta.TIPO_SPINNER:
                spinnerQ4.setEnabled(false);
                break;
            case Pregunta.TIPO_LOGOS:
                for (int i = 0; i < Pregunta.NUM_OPCIONES; i++) {
                    cardsImagen[i].setEnabled(false);
                    botonesImagen[i].setEnabled(false);
                }
                break;
            default:
                break;
        }
    }

    private void habilitarBotones(RadioButton[] botones, boolean habilitar) {
        for (RadioButton boton : botones) {
            boton.setEnabled(habilitar);
        }
    }

    private void mostrarDialogoFallo() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_fallo_titulo)
                .setMessage(R.string.dialog_fallo_mensaje)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_continuar, (dialog, which) -> prepararBotonSiguiente())
                .setNegativeButton(R.string.btn_empezar_nuevo, (dialog, which) -> reiniciarJuego())
                .show();
    }

    private void mostrarDialogoConfirmarReinicio() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_reinicio_titulo)
                .setMessage(R.string.dialog_reinicio_mensaje)
                .setPositiveButton(R.string.btn_confirmar_reinicio, (dialog, which) -> reiniciarJuego())
                .setNegativeButton(R.string.btn_cancelar, null)
                .show();
    }

    private void avanzarPregunta() {
        if (preguntaActual < TOTAL_PREGUNTAS) {
            preguntaActual++;
            cargarPregunta(preguntaActual);
        } else {
            abrirPantallaResultados();
        }
    }

    private void abrirPantallaResultados() {
        Intent intent = new Intent(MainActivity.this, ResultadoActivity.class);
        intent.putExtra(EXTRA_PUNTUACION, puntos);
        intent.putExtra(EXTRA_ACIERTOS, aciertos);
        intent.putExtra(EXTRA_FALLOS, fallos);
        intent.putExtra(EXTRA_TOTAL_PREGUNTAS, TOTAL_PREGUNTAS);

        // Transición animada de salida/entrada entre Activities
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(intent, options.toBundle());
    }

    private void reiniciarJuego() {
        nuevaPartida();
        Toast.makeText(this, R.string.msg_juego_reiniciado, Toast.LENGTH_SHORT).show();
    }

    private void actualizarMarcadores() {
        tvScore.setText(getString(R.string.puntos_format, puntos));
        tvQuestionProgress.setText(getString(R.string.pregunta_counter_format, preguntaActual, TOTAL_PREGUNTAS));
    }
}
