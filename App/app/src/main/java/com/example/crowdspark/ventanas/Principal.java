package com.example.crowdspark.ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.widget.Toast;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.R;
import com.example.crowdspark.componentes.ProyectCard;
import com.example.crowdspark.componentes.ProyectCardAdapter;
import com.example.crowdspark.control.FireBaseConnection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class Principal extends AppCompatActivity {

    private ListView listview;
    private ArrayList<ProyectCard> proyects;
    private ProyectCardAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        listview = (ListView) findViewById(R.id.listview);
        FireBaseConnection firebase = new FireBaseConnection();
        proyects = firebase.mostrarProyecto(listview,this, false);
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();

                Comparator<ProyectCard> compareByName = new Comparator<ProyectCard>() {
                    @Override
                    public int compare(ProyectCard o1, ProyectCard o2) {
                        return o1.getNombre().trim().compareToIgnoreCase(o2.getNombre().trim());
                    }
                };

                Comparator<ProyectCard> compareByCategory = new Comparator<ProyectCard>() {
                    @Override
                    public int compare(ProyectCard o1, ProyectCard o2) {
                        return o1.getCategoria().compareTo(o2.getCategoria());
                    }
                };

                Comparator<ProyectCard> compareByDate = new Comparator<ProyectCard>() {
                    @Override
                    public int compare(ProyectCard o1, ProyectCard o2) {
                        return o1.getFecha().compareTo(o2.getFecha());
                    }
                };

                Comparator<ProyectCard> compareByMonto = new Comparator<ProyectCard>() {
                    @Override
                    public int compare(ProyectCard o1, ProyectCard o2) {
                        return Double.compare(Double.parseDouble(o1.getMonto()), Double.parseDouble(o2.getMonto()));
                    }
                };

                if (selectedItem.equals("Nombre")) {
                    Collections.sort(proyects, compareByName);
                } else if (selectedItem.equals("Categoria")) {
                    Collections.sort(proyects, compareByCategory);
                } else if (selectedItem.equals("Monto")) {
                    Collections.sort(proyects, compareByMonto);
                } else if (selectedItem.equals("Fecha")) {
                    Collections.sort(proyects, compareByDate);
                }

                adapter1 = new ProyectCardAdapter(Principal.this, R.layout.activity_proyect_card, proyects);
                listview.setAdapter(adapter1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        Button botonCrearProyecto = findViewById(R.id.crearproyecto);
        botonCrearProyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, Formulario.class);
                startActivity(intent);
            }
        });

        Button botonEditarProyecto = findViewById(R.id.editarproyecto);
        botonEditarProyecto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, EditarProyecto.class);
                startActivity(intent);
            }
        });

        Button botonEditarUsuario = findViewById(R.id.editarusuario);
        botonEditarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, EditarUsuario.class);
                startActivity(intent);
            }
        });

        Button botonHistorial = findViewById(R.id.historial);
        botonHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Principal.this, Historial.class);
                startActivity(intent);
            }
        });

    }
}