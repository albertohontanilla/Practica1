package com.example.practica1;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.practica1.bd.QuizDbHelper;
import com.google.android.material.appbar.MaterialToolbar;

/** Manual de usuario: explica cómo se juega y qué hace cada pantalla. */
public class ManualActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.manual_titulo);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Número de preguntas disponibles en la base de datos
        try (QuizDbHelper db = new QuizDbHelper(this)) {
            TextView tvBanco = findViewById(R.id.tvManualBanco);
            tvBanco.setText(getString(R.string.manual_banco_format, db.contarPreguntas()));
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
