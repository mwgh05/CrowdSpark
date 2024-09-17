package com.example.crowdspark.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.crowdspark.R;
import com.example.crowdspark.control.FireBaseConnection;
import com.google.firebase.Firebase;

public class Estadisticas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_estadisticas);
        TextView usuarios = findViewById(R.id.usuarios);
        TextView proyectos = findViewById(R.id.proyectos);
        TextView donaciones = findViewById(R.id.lista);
        ImageButton botonAtras = findViewById(R.id.back);
        FireBaseConnection firebase = new FireBaseConnection();
        firebase.estadisticas(this,usuarios,proyectos,donaciones);
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Estadisticas.this, PrincipalAdmin.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}