package com.example.crowdspark.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crowdspark.R;
import com.example.crowdspark.componentes.DonacionesAdapter;
import com.example.crowdspark.componentes.Donation;
import com.example.crowdspark.control.FireBaseConnection;
import com.example.crowdspark.ventanas.Historial;
import com.example.crowdspark.ventanas.Principal;

import java.util.ArrayList;

public class Donaciones extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donaciones);

        ListView listView = findViewById(R.id.lista);

        ImageButton botonAtras = findViewById(R.id.back);
        FireBaseConnection firebase = new FireBaseConnection();
        firebase.mostrarDonaciones(listView, this);
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Donaciones.this, PrincipalAdmin.class);
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