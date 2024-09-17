package com.example.crowdspark.ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.R;
import com.example.crowdspark.control.FireBaseConnection;
import com.google.android.material.textfield.TextInputLayout;

public class EditarUsuario extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_usuario);

        EditText cedulaText = findViewById(R.id.editTextNumber2);
        TextInputLayout nombreText = findViewById(R.id.nombreCompleto2);
        TextInputLayout areaText = findViewById(R.id.area2);
        EditText dineroText = findViewById(R.id.editTextNumberDecimal);
        EditText telefonoText = findViewById(R.id.editTextPhone);


        FireBaseConnection firebase = new FireBaseConnection();
        firebase.mostrarUsuario(this, MainActivity.getCorreoColaborador(), nombreText, cedulaText, areaText, dineroText, telefonoText);



        ImageButton botonAtras = findViewById(R.id.back);
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditarUsuario.this, Principal.class);
                startActivity(intent);
            }
        });
        Button botonEditar = findViewById(R.id.button);
        botonEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cedula = cedulaText.getText().toString();
                String nombre = String.valueOf(nombreText.getEditText().getText());
                String area = String.valueOf(areaText.getEditText().getText());
                String dinero = dineroText.getText().toString();
                String telefono = telefonoText.getText().toString();
                EditText passwordText = findViewById(R.id.editTextTextPassword);
                String password = passwordText.getText().toString();
                EditText password2Text = findViewById(R.id.editTextTextPassword2);
                String password2 = password2Text.getText().toString();
                if (!password2.equals(password)) {
                    desplegarMensaje("Las contraseñas deben ser iguales");
                } else {
                    boolean cambio = false;
                    if (cedula.length() > 0) {
                        cambio = true;
                    }

                    if (nombre.length() > 0) {
                        cambio = true;
                    }

                    if (area.length() > 0) {
                        cambio = true;
                    }

                    if (dinero.length() > 0) {
                        cambio = true;
                    }

                    if (telefono.length() > 0) {
                        cambio = true;
                    }

                    if (password.length() > 0) {
                        cambio = true;
                    }

                    if (cambio) {
                        desplegarMensaje("Información del usuario modificada");
                        FireBaseConnection firebase = new FireBaseConnection();
                        firebase.modificarUsuario(MainActivity.getCorreoColaborador(), nombre, area, cedula, dinero, telefono, v.getContext());


                    }
                }
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void desplegarMensaje(CharSequence text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}