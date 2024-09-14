package com.example.crowdspark.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.crowdspark.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetalleProyectoAdmin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_proyecto_admin);

        TextView nombreTextView = findViewById(R.id.nombre);
        TextView fechaTextView = findViewById(R.id.fecha);
        TextView descripcionTextView = findViewById(R.id.descripcion);
        TextView montoTextView = findViewById(R.id.monto);
        TextView objetivoTextView = findViewById(R.id.objetivo);
        ImageView image = findViewById(R.id.image);

        Intent intent = getIntent();
        if (intent != null) {
            try {
                String nombre = intent.getStringExtra("nombre");
                String fecha = intent.getStringExtra("fecha");
                String descripcion = intent.getStringExtra("descripcion");
                String monto = intent.getStringExtra("monto");
                String objetivo = intent.getStringExtra("objetivo");
                String imgUrl = intent.getStringExtra("imgUrl");

                nombreTextView.setText(nombre);
                fechaTextView.setText(fecha);
                descripcionTextView.setText(descripcion);
                montoTextView.setText(monto);
                objetivoTextView.setText(objetivo);

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
                Intent intent = new Intent(DetalleProyectoAdmin.this, PrincipalAdmin.class);
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