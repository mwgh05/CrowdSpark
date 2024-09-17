package com.example.crowdspark;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.crowdspark.admin.PrincipalAdmin;
import com.example.crowdspark.control.FireBaseConnection;
import com.example.crowdspark.ventanas.Principal;
import com.example.crowdspark.ventanas.Registrarse;

public class MainActivity extends AppCompatActivity {
    private static String correoColaborador;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button botonRegistrar = findViewById(R.id.button2);
        correoColaborador = "";
        botonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Registrarse.class);
                startActivity(intent);
            }
        });

        Button botonIngresar = findViewById(R.id.button3);
        botonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText correoText = findViewById(R.id.editTextTextEmailAddress2);
                String correo = correoText.getText().toString();
                EditText passwordText = findViewById(R.id.editTextTextPassword3);
                String password = passwordText.getText().toString();
                FireBaseConnection firebase = new FireBaseConnection();
                firebase.verificarUsuario(v.getContext(),correo,password);
                passwordText.setText("");
            }
        });

        Button botonAdmin = findViewById(R.id.admin);
        botonAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText correoText = findViewById(R.id.editTextTextEmailAddress2);
                String correo = correoText.getText().toString();
                EditText passwordText = findViewById(R.id.editTextTextPassword3);
                String password = passwordText.getText().toString();
                FireBaseConnection firebase = new FireBaseConnection();
                firebase.verificarAdministrador(v.getContext(),correo,password);
                passwordText.setText("");
            }
        });
    }
    public static String getCorreoColaborador() {
        return correoColaborador;
    }

    public static void setCorreoColaborador(String correoColaborador) {
        MainActivity.correoColaborador = correoColaborador;
    }
}