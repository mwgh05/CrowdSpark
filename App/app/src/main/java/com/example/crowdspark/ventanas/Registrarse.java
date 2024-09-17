package com.example.crowdspark.ventanas;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.Toast;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.R;
import com.example.crowdspark.control.CorreoService;
import com.example.crowdspark.control.FireBaseConnection;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registrarse extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
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
                String emailPattern = "^[a-zA-Z0-9._%+-]+@(itcr\\.ac\\.cr|estudiantec\\.cr)$";
                if (correo.length() == 0 || cedula.length() == 0 || nombre.length() == 0 ||
                        area.length() == 0 || dinero.length() == 0 || telefono.length() == 0 ||
                        password.length() == 0 || password2.length() == 0)  {
                    desplegarMensaje("Debe llenar todos los campos");}
                else if (!correo.matches(emailPattern)) {
                    desplegarMensaje("El correo es inválido");
                } else if(!password2.equals(password)){
                    desplegarMensaje("Las contraseñas deben ser iguales");
                } else {
                    FireBaseConnection firebase = new FireBaseConnection();
                    firebase.registrar(correo,password,nombre,area,cedula,dinero,telefono,Registrarse.this);

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