package com.example.crowdspark.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crowdspark.R;
import com.example.crowdspark.componentes.ProyectCard;
import com.example.crowdspark.componentes.ProyectCardAdapterAdmin;
import com.example.crowdspark.control.FireBaseConnection;
import com.example.crowdspark.ventanas.EditarProyecto;
import com.example.crowdspark.ventanas.EditarUsuario;
import com.example.crowdspark.ventanas.Formulario;
import com.example.crowdspark.ventanas.Principal;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class PrincipalAdmin extends AppCompatActivity {

    private ListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal_admin);
        listview = (ListView) findViewById(R.id.listview);

        FireBaseConnection firebase = new FireBaseConnection();
        firebase.mostrarProyecto(listview,this, true);


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