package com.example.crowdspark.componentes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.crowdspark.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DonacionesAdapter extends ArrayAdapter<Donation> {

    private Context mContext;
    private int mResource;

    public DonacionesAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Donation> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String monto = getItem(position).getMonto();
        String correo = getItem(position).getCorreo();
        String nombreDonante = getItem(position).getNombreDonante();
        String nombreProyecto = getItem(position).getNombreProyecto();
        String telefono = getItem(position).getTelefono();


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);


        TextView textViewMonto = convertView.findViewById(R.id.textViewMonto);
        TextView textViewCorreo = convertView.findViewById(R.id.textViewCorreo);
        TextView textViewNombreDonante = convertView.findViewById(R.id.textViewNombreDonante);
        TextView textViewNombreProyecto = convertView.findViewById(R.id.textViewNombreProyecto);
        TextView textViewTelefono = convertView.findViewById(R.id.textViewTelefono);

        // Asignar los valores a los TextViews
        textViewMonto.setText("Monto: " + monto);
        textViewCorreo.setText("Correo: " + correo);
        textViewNombreDonante.setText("Nombre Donante: " + nombreDonante);
        textViewNombreProyecto.setText("Nombre Proyecto: " + nombreProyecto);
        textViewTelefono.setText("Teléfono: " + telefono);

        return convertView;
    }
}

