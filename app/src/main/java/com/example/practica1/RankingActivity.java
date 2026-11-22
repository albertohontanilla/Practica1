package com.example.practica1;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.practica1.bd.Partida;
import com.example.practica1.bd.QuizDbHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** Muestra las mejores partidas guardadas en la base de datos. */
public class RankingActivity extends AppCompatActivity {

    private static final int LIMITE_RANKING = 20;

    private QuizDbHelper db;
    private final List<Partida> partidas = new ArrayList<>();
    private RankingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.ranking_titulo);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        db = new QuizDbHelper(this);

        ListView listRanking = findViewById(R.id.listRanking);
        listRanking.setEmptyView(findViewById(R.id.tvRankingVacio));
        adapter = new RankingAdapter(this, partidas);
        listRanking.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarRanking();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }

    private void cargarRanking() {
        partidas.clear();
        partidas.addAll(db.obtenerRanking(LIMITE_RANKING));
        adapter.notifyDataSetChanged();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ranking, menu);
        menu.findItem(R.id.action_borrar_historial).setVisible(!partidas.isEmpty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_borrar_historial) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_borrar_titulo)
                    .setMessage(R.string.dialog_borrar_mensaje)
                    .setPositiveButton(R.string.btn_borrar, (dialog, which) -> {
                        db.borrarHistorial();
                        cargarRanking();
                        Toast.makeText(this, R.string.msg_historial_borrado, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.btn_cancelar, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /** Adaptador que pinta cada partida con el layout item_ranking. */
    private static class RankingAdapter extends ArrayAdapter<Partida> {

        private final LayoutInflater inflater;
        private final java.text.DateFormat formatoFecha;

        RankingAdapter(Context context, List<Partida> partidas) {
            super(context, 0, partidas);
            inflater = LayoutInflater.from(context);
            formatoFecha = DateFormat.getDateFormat(context);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            View fila = convertView;
            ViewHolder holder;
            if (fila == null) {
                fila = inflater.inflate(R.layout.item_ranking, parent, false);
                holder = new ViewHolder(fila);
                fila.setTag(holder);
            } else {
                holder = (ViewHolder) fila.getTag();
            }

            Partida partida = getItem(position);
            if (partida != null) {
                Context ctx = getContext();
                holder.posicion.setText(ctx.getString(R.string.ranking_posicion_format, position + 1));
                holder.jugador.setText(partida.getJugador());
                holder.detalle.setText(ctx.getString(R.string.ranking_detalle_format,
                        partida.getAciertos(), partida.getFallos(),
                        formatoFecha.format(new Date(partida.getFecha()))));
                holder.puntos.setText(ctx.getString(R.string.resultado_puntos_format, partida.getPuntos()));
            }
            return fila;
        }

        private static class ViewHolder {
            final TextView posicion;
            final TextView jugador;
            final TextView detalle;
            final TextView puntos;

            ViewHolder(View fila) {
                posicion = fila.findViewById(R.id.tvPosicion);
                jugador = fila.findViewById(R.id.tvJugador);
                detalle = fila.findViewById(R.id.tvDetalle);
                puntos = fila.findViewById(R.id.tvPuntos);
            }
        }
    }
}
