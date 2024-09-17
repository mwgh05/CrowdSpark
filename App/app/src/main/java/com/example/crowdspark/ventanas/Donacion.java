package com.example.crowdspark.ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.R;
import com.example.crowdspark.control.FireBaseConnection;
import com.google.android.material.textfield.TextInputLayout;

public class Donacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_donacion);

        ImageButton botonAtras = findViewById(R.id.back);
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Donacion.this, Principal.class);
                startActivity(intent);
            }
        });

        Button botonSubir = findViewById(R.id.donarButton);
        botonSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText montoText = findViewById(R.id.editTextNumberDecimal2);
                String monto = montoText.getText().toString();

                FireBaseConnection firebase = new FireBaseConnection();
                firebase.donar(DetalleProyecto.getnombreProyecto(), MainActivity.getCorreoColaborador(), monto, v.getContext());
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}