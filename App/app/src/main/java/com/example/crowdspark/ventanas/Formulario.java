package com.example.crowdspark.ventanas;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;
import android.content.pm.PackageManager;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.R;
import com.example.crowdspark.control.FireBaseConnection;
import com.google.android.material.textfield.TextInputLayout;

import androidx.core.app.ActivityCompat;
import java.util.Calendar;

public class Formulario extends AppCompatActivity {
    private Button dateButton;
    private Uri uri = null;
    private ImageView imagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario);

        /*Declaración de componentes*/
        imagen = findViewById(R.id.imagenProyecto);
        dateButton = findViewById(R.id.dateButton);

        /*Petición de permisos*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        /*Funciones de componentes*/
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Formulario.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                dateButton.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        ImageButton botonAtras = findViewById(R.id.back);
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Formulario.this, Principal.class);
                startActivity(intent);
            }
        });
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                abrirGaleria();
            }
        });

        //Subir proyecto
        Button botonSubir = findViewById(R.id.button);
        botonSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputLayout nombreText = findViewById(R.id.nombreProyecto);
                String nombre = String.valueOf(nombreText.getEditText().getText());
                TextInputLayout descripcionText = findViewById(R.id.descripcionProyecto);
                String descripcion = String.valueOf(descripcionText.getEditText().getText());
                EditText objetivoText = findViewById(R.id.editTextNumberDecimal);
                String objetivo = objetivoText.getText().toString();
                TextInputLayout categoriaText = findViewById(R.id.categoriaProyecto);
                String categoria = String.valueOf(categoriaText.getEditText().getText());

                if(!nombre.isEmpty() && !descripcion.isEmpty() && !objetivo.isEmpty() && !categoria.isEmpty()){
                    FireBaseConnection firebase = new FireBaseConnection();
                    firebase.subirFoto(1, nombre, descripcion, objetivo, categoria, dateButton.getText().toString(), uri, MainActivity.getCorreoColaborador(), "",v.getContext());
                }else {
                    desplegarMensaje("Debe ingresar todos los datos");
                }

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /*Abre la galería*/
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);

    }
    /*Selecciona la imagen y la muestra*/
    private ActivityResultLauncher<Intent> galeriaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK){
                    uri = result.getData().getData();
                    imagen.setImageURI(uri);
                }
                else {
                    desplegarMensaje("No se ha seleccionado una imagen");
                }
            });

    private  void desplegarMensaje(CharSequence text){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}