package com.example.crowdspark.ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crowdspark.R;
import com.example.crowdspark.componentes.ProyectCard;
import com.example.crowdspark.componentes.ProyectCardAdapter;

import java.util.ArrayList;

public class Principal extends AppCompatActivity {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        listview = (ListView) findViewById(R.id.listview);

        ArrayList <ProyectCard> proyects = new ArrayList<>();
        proyects.add(new ProyectCard("drawable://" + R.drawable.logo, "Proyecto 1", "10/09/2024", "Descripcion", "100", "1000"));
        proyects.add(new ProyectCard("drawable://" + R.drawable.logo, "Proyecto 2", "20/10/2024", "Descripcion", "500", "16000"));

        ProyectCardAdapter adapter = new ProyectCardAdapter(this, R.layout.activity_proyect_card, proyects);
        listview.setAdapter(adapter);

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