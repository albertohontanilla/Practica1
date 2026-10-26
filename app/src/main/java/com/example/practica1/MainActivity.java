package com.example.practica1;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    // Marcadores y contadores del juego
    private int puntos = 0;
    private int aciertos = 0;
    private int fallos = 0;
    private int preguntaActual = 1; // 1 a 5
    private final int TOTAL_PREGUNTAS = 5;

    // Estado de la pregunta actual
    private boolean respondida = false;
    private int seleccionActual = -1; // Índice de la opción seleccionada (0..3)

    // Respuestas correctas para cada pregunta (0 = A, 1 = B, 2 = C, 3 = D)
    // P1: Marca fundada en Sevilla -> Cruzcampo (1)
    // P2: Marca neerlandesa de la estrella roja -> Heineken (0)
    // P3: Marca del grupo Damm -> Estrella Damm (0)
    // P4: Origen de Pilsner Urquell -> República Checa (1)
    // P5: Marca fundada en A Coruña en 1906 -> Estrella Galicia (1)
    private final int[] RESPUESTAS_CORRECTAS = {1, 0, 0, 1, 1};

    // Vistas principales
    private TextView tvQuestionProgress;
    private TextView tvScore;
    private TextView tvQuestionTitle;
    private TextView tvQuestionText;

    // Contenedores de cada tipo de pregunta
    private LinearLayout layoutQuestion1;
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
    private ListView listViewQ3;
    private Spinner spinnerQ4;
    private TextView tvSelectedSpinnerOption;

    // Contenedores de opciones de imagen (P5)
    private LinearLayout cardImgOption1, cardImgOption2, cardImgOption3, cardImgOption4;
    private ImageButton ibQ5Option1, ibQ5Option2, ibQ5Option3, ibQ5Option4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vincularVistas();
        configurarListeners();
        cargarPregunta(preguntaActual);
    }

    private void vincularVistas() {
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress);
        tvScore = findViewById(R.id.tvScore);
        tvQuestionTitle = findViewById(R.id.tvQuestionTitle);
        tvQuestionText = findViewById(R.id.tvQuestionText);

        layoutQuestion1 = findViewById(R.id.layoutQuestion1);
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

        // Controles de P2
        radioGroupQ2 = findViewById(R.id.radioGroupQ2);

        // Controles de P3
        listViewQ3 = findViewById(R.id.listViewQ3);

        // Controles de P4
        spinnerQ4 = findViewById(R.id.spinnerQ4);
        tvSelectedSpinnerOption = findViewById(R.id.tvSelectedSpinnerOption);

        // Controles de P5
        cardImgOption1 = findViewById(R.id.cardImgOption1);
        cardImgOption2 = findViewById(R.id.cardImgOption2);
        cardImgOption3 = findViewById(R.id.cardImgOption3);
        cardImgOption4 = findViewById(R.id.cardImgOption4);
        ibQ5Option1 = findViewById(R.id.ibQ5Option1);
        ibQ5Option2 = findViewById(R.id.ibQ5Option2);
        ibQ5Option3 = findViewById(R.id.ibQ5Option3);
        ibQ5Option4 = findViewById(R.id.ibQ5Option4);
    }

    private void configurarListeners() {
        // Listener del botón de reinicio
        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmarReinicio();
            }
        });

        // Listener del botón de acción (Comprobar / Siguiente)
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!respondida) {
                    comprobarRespuesta();
                } else {
                    avanzarPregunta();
                }
            }
        });

        // Listener P1 (RadioGroup)
        radioGroupQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbQ1OptionA) seleccionActual = 0;
                else if (checkedId == R.id.rbQ1OptionB) seleccionActual = 1;
                else if (checkedId == R.id.rbQ1OptionC) seleccionActual = 2;
                else if (checkedId == R.id.rbQ1OptionD) seleccionActual = 3;
            }
        });

        // Listener P2 (RadioGroup con imagen)
        radioGroupQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbQ2OptionA) seleccionActual = 0;
                else if (checkedId == R.id.rbQ2OptionB) seleccionActual = 1;
                else if (checkedId == R.id.rbQ2OptionC) seleccionActual = 2;
                else if (checkedId == R.id.rbQ2OptionD) seleccionActual = 3;
            }
        });

        // Configuración de ListView para P3
        ArrayList<String> opcionesP3 = new ArrayList<>();
        opcionesP3.add(getString(R.string.p3_opcion_a));
        opcionesP3.add(getString(R.string.p3_opcion_b));
        opcionesP3.add(getString(R.string.p3_opcion_c));
        opcionesP3.add(getString(R.string.p3_opcion_d));

        ArrayAdapter<String> adapterP3 = new ArrayAdapter<>(this, R.layout.item_quiz_option, R.id.tvOptionText, opcionesP3);
        listViewQ3.setAdapter(adapterP3);
        listViewQ3.setOnItemClickListener(this);

        // Configuración de Spinner para P4
        ArrayList<String> opcionesP4 = new ArrayList<>();
        opcionesP4.add(getString(R.string.p4_spinner_prompt)); // Posición 0: Placeholder
        opcionesP4.add(getString(R.string.p4_opcion_a)); // Posición 1 -> Opción 0
        opcionesP4.add(getString(R.string.p4_opcion_b)); // Posición 2 -> Opción 1 - Correcta
        opcionesP4.add(getString(R.string.p4_opcion_c)); // Posición 3 -> Opción 2
        opcionesP4.add(getString(R.string.p4_opcion_d)); // Posición 4 -> Opción 3

        ArrayAdapter<String> adapterP4 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opcionesP4);
        spinnerQ4.setAdapter(adapterP4);

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
                    tvSelectedSpinnerOption.setText("Seleccionado: " + opcionesP4.get(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                seleccionActual = -1;
            }
        });

        // Configuración de Opciones con Imágenes para P5 (Sobresaliente)
        View.OnClickListener imgOptionListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (respondida) return;
                int id = v.getId();
                if (id == R.id.cardImgOption1 || id == R.id.ibQ5Option1) {
                    seleccionarOpcionImagen(0);
                } else if (id == R.id.cardImgOption2 || id == R.id.ibQ5Option2) {
                    seleccionarOpcionImagen(1);
                } else if (id == R.id.cardImgOption3 || id == R.id.ibQ5Option3) {
                    seleccionarOpcionImagen(2);
                } else if (id == R.id.cardImgOption4 || id == R.id.ibQ5Option4) {
                    seleccionarOpcionImagen(3);
                }
            }
        };

        cardImgOption1.setOnClickListener(imgOptionListener);
        ibQ5Option1.setOnClickListener(imgOptionListener);
        cardImgOption2.setOnClickListener(imgOptionListener);
        ibQ5Option2.setOnClickListener(imgOptionListener);
        cardImgOption3.setOnClickListener(imgOptionListener);
        ibQ5Option3.setOnClickListener(imgOptionListener);
        cardImgOption4.setOnClickListener(imgOptionListener);
        ibQ5Option4.setOnClickListener(imgOptionListener);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (respondida) return;
        seleccionActual = position;

        // Marcar visualmente el elemento seleccionado en la lista (Diapositiva ListView)
        for (int i = 0; i < parent.getChildCount(); i++) {
            View item = parent.getChildAt(i);
            item.setBackgroundResource(R.drawable.bg_option_card);
        }
        view.setBackgroundResource(R.drawable.bg_option_selected);
    }

    private void seleccionarOpcionImagen(int opcion) {
        seleccionActual = opcion;
        cardImgOption1.setBackgroundResource(opcion == 0 ? R.drawable.bg_option_selected : R.drawable.bg_option_card);
        cardImgOption2.setBackgroundResource(opcion == 1 ? R.drawable.bg_option_selected : R.drawable.bg_option_card);
        cardImgOption3.setBackgroundResource(opcion == 2 ? R.drawable.bg_option_selected : R.drawable.bg_option_card);
        cardImgOption4.setBackgroundResource(opcion == 3 ? R.drawable.bg_option_selected : R.drawable.bg_option_card);
    }

    private void cargarPregunta(int numero) {
        respondida = false;
        seleccionActual = -1;
        actualizarMarcadores();

        // Ocultar todos los contenedores
        layoutQuestion1.setVisibility(View.GONE);
        layoutQuestion2.setVisibility(View.GONE);
        layoutQuestion3.setVisibility(View.GONE);
        layoutQuestion4.setVisibility(View.GONE);
        layoutQuestion5.setVisibility(View.GONE);
        layoutFeedback.setVisibility(View.GONE);

        // Restaurar estado del botón de acción
        btnAction.setText(R.string.btn_comprobar);
        btnAction.setBackgroundColor(getResources().getColor(R.color.accent));

        switch (numero) {
            case 1:
                tvQuestionTitle.setText(R.string.p1_titulo);
                tvQuestionText.setText(R.string.p1_enunciado);
                layoutQuestion1.setVisibility(View.VISIBLE);
                radioGroupQ1.clearCheck();
                habilitarRadioGroup(radioGroupQ1, true);
                break;

            case 2:
                tvQuestionTitle.setText(R.string.p2_titulo);
                tvQuestionText.setText(R.string.p2_enunciado);
                layoutQuestion2.setVisibility(View.VISIBLE);
                radioGroupQ2.clearCheck();
                habilitarRadioGroup(radioGroupQ2, true);
                break;

            case 3:
                tvQuestionTitle.setText(R.string.p3_titulo);
                tvQuestionText.setText(R.string.p3_enunciado);
                layoutQuestion3.setVisibility(View.VISIBLE);
                // Restablecer fondos de ListView
                for (int i = 0; i < listViewQ3.getChildCount(); i++) {
                    listViewQ3.getChildAt(i).setBackgroundResource(R.drawable.bg_option_card);
                }
                break;

            case 4:
                tvQuestionTitle.setText(R.string.p4_titulo);
                tvQuestionText.setText(R.string.p4_enunciado);
                layoutQuestion4.setVisibility(View.VISIBLE);
                spinnerQ4.setEnabled(true);
                spinnerQ4.setSelection(0);
                seleccionActual = -1; // Obliga a seleccionar una opción válida
                tvSelectedSpinnerOption.setVisibility(View.GONE);
                break;

            case 5:
                tvQuestionTitle.setText(R.string.p5_titulo);
                tvQuestionText.setText(R.string.p5_enunciado);
                layoutQuestion5.setVisibility(View.VISIBLE);
                // Restablecer estados de las 4 opciones de imagen
                cardImgOption1.setBackgroundResource(R.drawable.bg_option_card);
                cardImgOption2.setBackgroundResource(R.drawable.bg_option_card);
                cardImgOption3.setBackgroundResource(R.drawable.bg_option_card);
                cardImgOption4.setBackgroundResource(R.drawable.bg_option_card);
                break;
        }
    }

    private void comprobarRespuesta() {
        if (seleccionActual == -1) {
            Toast.makeText(this, R.string.msg_debe_seleccionar, Toast.LENGTH_SHORT).show();
            return;
        }

        respondida = true;
        int correcta = RESPUESTAS_CORRECTAS[preguntaActual - 1];

        if (seleccionActual == correcta) {
            // Acierto (+3 puntos)
            puntos += 3;
            aciertos++;
            mostrarFeedback(true, getString(R.string.msg_acierto));
            Toast.makeText(this, R.string.msg_acierto, Toast.LENGTH_SHORT).show();

            deshabilitarControlesPregunta();
            actualizarMarcadores();
            btnAction.setText(preguntaActual == TOTAL_PREGUNTAS ? "Ver Resultados" : getString(R.string.btn_siguiente));
            btnAction.setBackgroundColor(getResources().getColor(R.color.primary));
        } else {
            // Fallo (-2 puntos)
            puntos -= 2;
            fallos++;
            mostrarFeedback(false, getString(R.string.msg_fallo));
            Toast.makeText(this, R.string.msg_fallo, Toast.LENGTH_SHORT).show();

            deshabilitarControlesPregunta();
            actualizarMarcadores();

            // Requisito del enunciado:
            // "Tras un fallo:
            //  a. Se resta dos puntos
            //  b. Se indica mediante un mensaje que ha habido un error.
            //  c. Se puede continuar o empezar de nuevo el juego."
            mostrarDialogoFallo();
        }
    }

    private void mostrarFeedback(boolean esCorrecto, String mensaje) {
        layoutFeedback.setVisibility(View.VISIBLE);
        if (esCorrecto) {
            layoutFeedback.setBackgroundResource(R.drawable.bg_option_selected);
            ivFeedbackIcon.setImageResource(R.drawable.ic_check);
            tvFeedbackMessage.setTextColor(getResources().getColor(R.color.primary_dark));
        } else {
            layoutFeedback.setBackgroundColor(getResources().getColor(R.color.error_bg));
            ivFeedbackIcon.setImageResource(R.drawable.ic_cross);
            tvFeedbackMessage.setTextColor(getResources().getColor(R.color.error));
        }
        tvFeedbackMessage.setText(mensaje);
    }

    private void deshabilitarControlesPregunta() {
        switch (preguntaActual) {
            case 1:
                habilitarRadioGroup(radioGroupQ1, false);
                break;
            case 2:
                habilitarRadioGroup(radioGroupQ2, false);
                break;
            case 3:
                // No permitir más clics en la lista
                break;
            case 4:
                spinnerQ4.setEnabled(false);
                break;
            case 5:
                cardImgOption1.setEnabled(false);
                cardImgOption2.setEnabled(false);
                cardImgOption3.setEnabled(false);
                cardImgOption4.setEnabled(false);
                break;
        }
    }

    private void habilitarRadioGroup(RadioGroup group, boolean habilitar) {
        for (int i = 0; i < group.getChildCount(); i++) {
            group.getChildAt(i).setEnabled(habilitar);
        }
    }

    private void mostrarDialogoFallo() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_fallo_titulo)
                .setMessage(R.string.dialog_fallo_mensaje)
                .setCancelable(false)
                .setPositiveButton(R.string.btn_continuar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        btnAction.setText(preguntaActual == TOTAL_PREGUNTAS ? "Ver Resultados" : getString(R.string.btn_siguiente));
                        btnAction.setBackgroundColor(getResources().getColor(R.color.primary));
                    }
                })
                .setNegativeButton(R.string.btn_empezar_nuevo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        reiniciarJuego();
                    }
                })
                .show();
    }

    private void mostrarDialogoConfirmarReinicio() {
        new AlertDialog.Builder(this)
                .setTitle("Reiniciar Quiz")
                .setMessage("¿Estás seguro de que deseas reiniciar la partida actual? Se perderá el progreso acumulado.")
                .setPositiveButton("Sí, reiniciar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reiniciarJuego();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void avanzarPregunta() {
        if (preguntaActual < TOTAL_PREGUNTAS) {
            preguntaActual++;
            cargarPregunta(preguntaActual);
        } else {
            // Fin del juego -> Abrir ResultadoActivity mediante Intent con transición animada
            abrirPantallaResultados();
        }
    }

    private void abrirPantallaResultados() {
        Intent intent = new Intent(MainActivity.this, ResultadoActivity.class);
        intent.putExtra("PUNTUACION", puntos);
        intent.putExtra("ACIERTOS", aciertos);
        intent.putExtra("FALLOS", fallos);
        intent.putExtra("TOTAL_PREGUNTAS", TOTAL_PREGUNTAS);

        // Transición animada de salida/entrada entre Activities
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this);
        startActivity(intent, options.toBundle());
    }

    private void reiniciarJuego() {
        puntos = 0;
        aciertos = 0;
        fallos = 0;
        preguntaActual = 1;
        cargarPregunta(preguntaActual);
        Toast.makeText(this, "Juego reiniciado. ¡Buena suerte!", Toast.LENGTH_SHORT).show();
    }

    private void actualizarMarcadores() {
        tvScore.setText(getString(R.string.puntos_format, puntos));
        tvQuestionProgress.setText(getString(R.string.pregunta_counter_format, preguntaActual, TOTAL_PREGUNTAS));
    }
}
