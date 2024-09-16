package com.example.crowdspark.ventanas;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.crowdspark.MainActivity;
import com.example.crowdspark.R;
import com.example.crowdspark.control.FireBaseConnection;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class EditarProyecto extends AppCompatActivity {
    private Uri uri = null;
    private ImageView imagen;
    private Button dateButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_editar_proyecto);

        /*Declaración de componentes*/
        imagen = findViewById(R.id.imagenProyecto2);
        dateButton = findViewById(R.id.dateButton);
        Spinner spinner = findViewById(R.id.spinner);

        List<String> nombresProyectos = new ArrayList<>();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("Proyecto")
                .whereEqualTo("idEncargado",  MainActivity.getCorreoColaborador())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Extrae el campo "Nombre" de cada documento y añádelo a la lista
                            String nombreProyecto = document.getString("Nombre");
                            nombresProyectos.add(nombreProyecto);
                        }

                        // Configura el adaptador para el Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresProyectos);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);  // Establece el adaptador al Spinner
                    }
                });

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditarProyecto.this,
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
                Intent intent = new Intent(EditarProyecto.this, Principal.class);
                startActivity(intent);
            }
        });
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                abrirGaleria();

            }
        });

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
                String elementoSeleccionado = spinner.getSelectedItem().toString();

                FireBaseConnection firebase = new FireBaseConnection();
                firebase.subirFoto(0, nombre, descripcion, objetivo, categoria, dateButton.getText().toString(), uri, MainActivity.getCorreoColaborador(), elementoSeleccionado,EditarProyecto.this);
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