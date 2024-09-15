package com.example.crowdspark.ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Toast;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.R;
import com.google.android.material.textfield.TextInputLayout;

public class Registrarse extends AppCompatActivity {
    private  void desplegarMensaje(CharSequence text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrarse);

        ImageButton botonAtras = findViewById(R.id.back);
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registrarse.this, MainActivity.class);
                startActivity(intent);
            }
        });
        Button botonRegistrarse = findViewById(R.id.button);
        botonRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText correoText = findViewById(R.id.editTextTextEmailAddress);
                String correo = correoText.getText().toString().trim();
                EditText cedulaText = findViewById(R.id.editTextNumber);
                String cedula = cedulaText.getText().toString();
                TextInputLayout nombreText = findViewById(R.id.nombreCompleto);
                String nombre = String.valueOf(nombreText.getEditText().getText());
                TextInputLayout areaText = findViewById(R.id.areaTrabajo);
                String area = String.valueOf(areaText.getEditText().getText());
                EditText dineroText = findViewById(R.id.editTextNumberDecimal);
                String dinero = dineroText.getText().toString();
                EditText telefonoText = findViewById(R.id.editTextPhone);
                String telefono = telefonoText.getText().toString();
                EditText passwordText = findViewById(R.id.editTextTextPassword);
                String password = passwordText.getText().toString();
                EditText password2Text = findViewById(R.id.editTextTextPassword2);
                String password2 = password2Text.getText().toString();
                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
                if (correo.length() == 0 || cedula.length() == 0 || nombre.length() == 0 ||
                        area.length() == 0 || dinero.length() == 0 || telefono.length() == 0 ||
                        password.length() == 0 || password2.length() == 0)  {
                    desplegarMensaje("Debe llenar todos los campos");}
                else if (!correo.matches(emailPattern)) {
                    desplegarMensaje("El correo es inválido");
                } else if(!password2.equals(password)){
                    desplegarMensaje("Las contraseñas deben ser iguales");
                } else {
                    desplegarMensaje("Usuario registrado");
                    correoText.setText("");
                    cedulaText.setText("");
                    nombreText.getEditText().setText("");
                    areaText.getEditText().setText("");
                    dineroText.setText("");
                    telefonoText.setText("");
                    passwordText.setText("");
                    password2Text.setText("");
                }

            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}