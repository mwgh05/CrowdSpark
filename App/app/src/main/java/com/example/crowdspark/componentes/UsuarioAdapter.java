package com.example.crowdspark.componentes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.crowdspark.R;

import java.util.List;

public class UsuarioAdapter extends ArrayAdapter<User> {

    private Context context;
    private List<User> usuarios;

    public UsuarioAdapter(Context context, List<User> usuarios) {
        super(context, R.layout.usuario, usuarios);
        this.context = context;
        this.usuarios = usuarios;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.usuario, parent, false);
        }


        TextView textViewNombreUsuario = convertView.findViewById(R.id.textViewUsuario);
        TextView textViewCorreo = convertView.findViewById(R.id.textViewCorreo);
        TextView textViewCedula = convertView.findViewById(R.id.textViewCedula);
        TextView textViewArea = convertView.findViewById(R.id.textViewArea);
        TextView textViewEstado = convertView.findViewById(R.id.textViewEstado);


        User usuario = usuarios.get(position);


        textViewNombreUsuario.setText("Usuario: " + usuario.getNombre());
        textViewCorreo.setText("Correo: " + usuario.getCorreo());
        textViewCedula.setText("Cédula: " + usuario.getCedula());
        textViewArea.setText("Área: " + usuario.getArea());
        textViewEstado.setText("Estado: " + usuario.getEstado());

        return convertView;
    }
}
