package com.example.crowdspark.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crowdspark.R;
import com.example.crowdspark.componentes.ProyectCard;
import com.example.crowdspark.componentes.ProyectCardAdapterAdmin;
import com.example.crowdspark.ventanas.EditarProyecto;
import com.example.crowdspark.ventanas.EditarUsuario;
import com.example.crowdspark.ventanas.Formulario;
import com.example.crowdspark.ventanas.Principal;

import java.util.ArrayList;

public class PrincipalAdmin extends AppCompatActivity {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal_admin);
        listview = (ListView) findViewById(R.id.listview);

        ArrayList<ProyectCard> proyects = new ArrayList<>();
        proyects.add(new ProyectCard("drawable://" + R.drawable.logo, "Proyecto 1", "10/09/2024", "Descripcion", "100", "1000"));
        proyects.add(new ProyectCard("drawable://" + R.drawable.logo, "Proyecto 2", "20/10/2024", "Descripcion", "500", "16000"));

        ProyectCardAdapterAdmin adapter = new ProyectCardAdapterAdmin(this, R.layout.activity_proyect_card, proyects);
        listview.setAdapter(adapter);

        Button botonUsuarios = findViewById(R.id.usuarios);
        botonUsuarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalAdmin.this, Usuarios.class);
                startActivity(intent);
            }
        });

        Button botonEstadisticas = findViewById(R.id.estadisticas);
        botonEstadisticas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalAdmin.this, Estadisticas.class);
                startActivity(intent);
            }
        });

        Button botonDonaciones = findViewById(R.id.donaciones);
        botonDonaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalAdmin.this, Donaciones.class);
                startActivity(intent);
            }
        });
    }
}