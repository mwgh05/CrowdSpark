package com.example.crowdspark.ventanas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.R;
import com.example.crowdspark.componentes.ProyectCard;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetalleProyecto extends AppCompatActivity {
    private static String nombreProyecto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_proyecto);

        TextView nombreTextView = findViewById(R.id.nombre);
        TextView fechaTextView = findViewById(R.id.fecha);
        TextView descripcionTextView = findViewById(R.id.descripcion);
        TextView montoTextView = findViewById(R.id.monto);
        TextView objetivoTextView = findViewById(R.id.objetivo);
        TextView categoriaTextView = findViewById(R.id.categoria);
        ImageView image = findViewById(R.id.image);

        Intent intent = getIntent();

        nombreProyecto = "";
        if (intent != null) {
            try {
                String nombre = intent.getStringExtra("nombre");
                String fecha = intent.getStringExtra("fecha");
                String descripcion = intent.getStringExtra("descripcion");
                String monto = intent.getStringExtra("monto");
                String objetivo = intent.getStringExtra("objetivo");
                String categoria = intent.getStringExtra("categoria");
                String imgUrl = intent.getStringExtra("imgUrl");

                nombreProyecto = nombre;
                nombreTextView.setText(nombre);
                fechaTextView.setText(fecha);
                descripcionTextView.setText(descripcion);
                montoTextView.setText(monto);
                objetivoTextView.setText(objetivo);
                categoriaTextView.setText(categoria);

                ImageLoader imageLoader = ImageLoader.getInstance();

                int defaultImage = this.getResources().getIdentifier("@drawable/image_failed", null, this.getPackageName());

                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                        .cacheOnDisc(true).resetViewBeforeLoading(true)
                        .showImageForEmptyUri(defaultImage)
                        .showImageOnFail(defaultImage)
                        .showImageOnLoading(defaultImage).build();

                imageLoader.displayImage(imgUrl, image, options);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        ImageButton botonAtras = findViewById(R.id.back);
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalleProyecto.this, Principal.class);
                startActivity(intent);
            }
        });

        Button botonDonacion = findViewById(R.id.donar);
        botonDonacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetalleProyecto.this, Donacion.class);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public static String getnombreProyecto() {
        return nombreProyecto;
    }
    public static void setnombreProyecto(String nombreProyecto) {
        DetalleProyecto.nombreProyecto = nombreProyecto;
    }
}