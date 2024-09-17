package com.example.crowdspark.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.crowdspark.R;
import com.example.crowdspark.control.FireBaseConnection;

public class Usuarios extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_usuarios);

        ImageButton botonAtras = findViewById(R.id.back);
        ListView listView = findViewById(R.id.lista);
        FireBaseConnection firebase = new FireBaseConnection();
        firebase.mostrarUsuarios(listView, this);
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Usuarios.this, PrincipalAdmin.class);
                startActivity(intent);
            }
        });
        Button activarDesactivar = findViewById(R.id.activar);
        activarDesactivar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText correoText = findViewById(R.id.correo);
                String correo = correoText.getText().toString();
                FireBaseConnection firebase = new FireBaseConnection();
                firebase.activarDesactivarUsuario(v.getContext(), correo, listView);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}