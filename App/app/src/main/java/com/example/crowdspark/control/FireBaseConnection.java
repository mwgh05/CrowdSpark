package com.example.crowdspark.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.R;
import com.example.crowdspark.admin.PrincipalAdmin;
import com.example.crowdspark.componentes.DonacionesAdapter;
import com.example.crowdspark.componentes.Donation;
import com.example.crowdspark.componentes.ProyectCard;
import com.example.crowdspark.componentes.ProyectCardAdapter;
import com.example.crowdspark.componentes.ProyectCardAdapterAdmin;
import com.example.crowdspark.componentes.User;
import com.example.crowdspark.componentes.UsuarioAdapter;
import com.example.crowdspark.ventanas.Formulario;
import com.example.crowdspark.ventanas.Principal;
import com.example.crowdspark.ventanas.Registrarse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireBaseConnection {
    private FirebaseFirestore mFirestore;
    private static final String TAG = "FirestoreExample";
    private boolean b;
    public FireBaseConnection() {
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public void registrar(String correo, String password, String nombre,String area,
                          String cedula, String dinero, String telefono, Context context){
        mFirestore.collection("Usuarios").whereEqualTo("correo", correo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.size() == 0){
                            Map<String, Object> map = new HashMap<>();
                            map.put("nombre",nombre);
                            map.put("correo",correo);
                            map.put("password",password);
                            map.put("area",area);
                            map.put("cedula",cedula);
                            map.put("dinero",dinero);
                            map.put("telefono",telefono);
                            map.put("estado","activo");
                            mFirestore.collection("Usuarios").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    desplegarMensaje("Usuario registrado", context);
                                    CorreoService correoService = new CorreoService();
                                    correoService.enviarCorreo(correo,"Usuario Registrado","Bienvenido a CrowdSpark "+nombre ,context);
                                    Intent intent = new Intent(context, MainActivity.class);
                                    context.startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    desplegarMensaje("No se ha podido registrar "+e, context);
                                }
                            });
                        }
                        else {
                            desplegarMensaje("Usuario existente", context);

                        }
                    } else {
                        desplegarMensaje("Error",context);
                    }
                });


    }

    public void crearProyecto(String nombre, String descripcion, String objetivo, String categoria,
                              String fecha, String imageURL, String idEncargado, Context context) {
        // Primero, consulta si ya existe un proyecto con el mismo nombre
        mFirestore.collection("Proyecto")
                .whereEqualTo("Nombre", nombre)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Si se encuentra un proyecto con el mismo nombre, muestra un mensaje y no crea el proyecto
                        if (!task.getResult().isEmpty()) {
                            desplegarMensaje("Ya existe un proyecto con este nombre", context);
                        } else {
                            // Si no existe, procede a crear el proyecto
                            Map<String, Object> map = new HashMap<>();
                            map.put("Nombre", nombre);
                            map.put("Descripcion", descripcion);
                            map.put("Objetivo", objetivo);
                            map.put("Categoria", categoria);
                            map.put("Fecha", fecha);
                            map.put("Imagen", imageURL);
                            map.put("idEncargado", idEncargado);
                            map.put("Monto", "0");

                            // Agrega el nuevo proyecto a la colección "Proyecto"
                            mFirestore.collection("Proyecto").add(map)
                                    .addOnSuccessListener(documentReference -> {
                                        desplegarMensaje("Proyecto registrado", context);
                                        CorreoService correoService = new CorreoService();
                                        correoService.enviarCorreo(idEncargado,"Proyecto Registrado","Su proyecto, "+nombre+", se ha colocado en CrowdSpark" ,context);
                                        Intent intent = new Intent(context, Principal.class);
                                        context.startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        desplegarMensaje("No se ha podido registrar el proyecto: " + e.getMessage(), context);
                                    });
                        }
                    } else {
                        // Si hay un error en la consulta, mostrar un mensaje
                        desplegarMensaje("Error al verificar el nombre del proyecto: " + task.getException().getMessage(), context);
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejo de error en la consulta
                    desplegarMensaje("Error al consultar los proyectos: " + e.getMessage(), context);
                });
    }


    public void donar(String nombreProyecto, String idDonante, String monto, Context context) {
        // Consulta Firestore para obtener el usuario con el idDonante (correo)
        mFirestore.collection("Usuarios")
                .whereEqualTo("correo", idDonante)  // Busca en la colección Usuarios donde "correo" sea igual a idDonante
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Si se encuentra un usuario, obtiene el primer documento
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        // Extrae los valores de "nombre" y "telefono" del documento
                        String nombreDonante = document.getString("nombre");
                        String telefono = document.getString("telefono");
                        String dinero = document.getString("dinero");

                        if(Integer.parseInt(dinero) >= Integer.parseInt(monto)){
                            Map<String, Object> map = new HashMap<>();
                            map.put("Monto", monto);
                            map.put("correo", idDonante);
                            map.put("nombreDonante", nombreDonante);  // Asigna el nombre del donante
                            map.put("nombreProyecto", nombreProyecto);
                            map.put("telefono", telefono);  // Asigna el teléfono del donante

                            actualizarMontoProyecto(monto, nombreProyecto, context);
                            actualizarMontoUsuario(monto, idDonante, context);
                            // Agrega la donación a la colección "Proyecto"
                            mFirestore.collection("Donacion").add(map)
                                    .addOnSuccessListener(documentReference -> {
                                        desplegarMensaje("Donación registrada con éxito", context);
                                        CorreoService correoService = new CorreoService();
                                        correoService.enviarCorreo(idDonante,"Donación exitosa","Su donación de "+monto+" en el proyecto "+nombreProyecto+" se ha realizado con éxito" ,context);
                                        Intent intent = new Intent(context, Principal.class);
                                        context.startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        desplegarMensaje("No se ha podido registrar la donación: " + e.getMessage(), context);
                                    });
                        }else{
                            desplegarMensaje("No posee los fondos necesarios", context);
                        }




                    } else {
                        // Si no se encuentra el usuario, muestra un mensaje de error
                        desplegarMensaje("No se ha encontrado el usuario con el correo proporcionado", context);
                    }
                })
                .addOnFailureListener(e -> {
                    // Manejo de error en la consulta
                    desplegarMensaje("Error al buscar el usuario: " + e.getMessage(), context);
                });
    }
    public void actualizarMontoProyecto(String monto, String nombreProyecto, Context context) {
        // Busca el proyecto con el nombre proporcionado
        mFirestore.collection("Proyecto")
                .whereEqualTo("Nombre", nombreProyecto)  // Busca por el nombre proporcionado
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        try{
                            if (!querySnapshot.isEmpty()) {
                                // Si se encontró un proyecto con el nombre proporcionado
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // Obtén el primer resultado
                                String documentId = documentSnapshot.getId(); // Obtiene el ID del documento
                                String montoActual = documentSnapshot.getString("Monto");

                                String nuevoMonto = String.valueOf(Integer.parseInt(montoActual) + Integer.parseInt(monto));

                                // Crea el mapa con la nueva información
                                Map<String, Object> map = new HashMap<>();
                                map.put("Monto", nuevoMonto);

                                // Actualiza el documento con los nuevos datos

                                mFirestore.collection("Proyecto").document(documentId)
                                        .update(map)
                                        .addOnSuccessListener(aVoid -> {
                                            desplegarMensaje("Proyecto modificado con éxito", context);
                                            Intent intent = new Intent(context, Principal.class);
                                            context.startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            desplegarMensaje("Error al modificar el proyecto: " + e.getMessage(), context);
                                        });

                            } else {
                                // Si no se encontró ningún proyecto con ese nombre
                                desplegarMensaje("No se encontró ningún proyecto con ese nombre", context);
                            }
                        } catch ( Exception e){
                            desplegarMensaje(e.getMessage().toString(), context);
                        }
                    } else {
                        // Si hubo un error al realizar la consulta
                        desplegarMensaje("Error en la consulta: " + task.getException().getMessage(), context);
                    }
                });
    }
    public void actualizarMontoUsuario(String monto, String idEncargado, Context context) {
        // Busca el proyecto con el nombre proporcionado
        mFirestore.collection("Usuarios")
                .whereEqualTo("correo", idEncargado)  // Busca por el nombre proporcionado
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        try{
                            if (!querySnapshot.isEmpty()) {
                                // Si se encontró un proyecto con el nombre proporcionado
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // Obtén el primer resultado
                                String documentId = documentSnapshot.getId(); // Obtiene el ID del documento
                                String montoActual = documentSnapshot.getString("dinero");

                                String nuevoMonto = String.valueOf(Integer.parseInt(montoActual) - Integer.parseInt(monto));

                                // Crea el mapa con la nueva información
                                Map<String, Object> map = new HashMap<>();
                                map.put("dinero", nuevoMonto);

                                // Actualiza el documento con los nuevos datos

                                mFirestore.collection("Usuarios").document(documentId)
                                        .update(map)
                                        .addOnSuccessListener(aVoid -> {
                                            desplegarMensaje("Proyecto modificado con éxito", context);
                                            Intent intent = new Intent(context, Principal.class);
                                            context.startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            desplegarMensaje("Error al modificar el proyecto: " + e.getMessage(), context);
                                        });

                            } else {
                                // Si no se encontró ningún proyecto con ese nombre
                                desplegarMensaje("No se encontró ningún proyecto con ese nombre", context);
                            }
                        } catch ( Exception e){
                            desplegarMensaje(e.getMessage().toString(), context);
                        }
                    } else {
                        // Si hubo un error al realizar la consulta
                        desplegarMensaje("Error en la consulta: " + task.getException().getMessage(), context);
                    }
                });
    }
    public void modificarProyecto(String nombre, String descripcion, String objetivo, String categoria,
                                  String fecha, String imageURL, String idEncargado, String nombreProyecto, Context context) {
        // Busca el proyecto con el nombre proporcionado
        mFirestore.collection("Proyecto")
                .whereEqualTo("Nombre", nombreProyecto)  // Busca por el nombre proporcionado
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        try{
                            if (!querySnapshot.isEmpty()) {
                                // Si se encontró un proyecto con el nombre proporcionado
                                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // Obtén el primer resultado
                                String documentId = documentSnapshot.getId(); // Obtiene el ID del documento

                                // Crea el mapa con la nueva información
                                Map<String, Object> map = new HashMap<>();
                                if(!nombre.isEmpty()){
                                    map.put("Nombre", nombre);
                                }
                                if(!descripcion.isEmpty()){
                                    map.put("Descripcion", descripcion);
                                }
                                if(!objetivo.isEmpty()){
                                    map.put("Objetivo", objetivo);
                                }
                                if(!categoria.isEmpty()){
                                    map.put("Categoria", categoria);
                                }
                                if(!categoria.isEmpty()){
                                    map.put("Fecha", fecha);
                                }
                                if(!categoria.isEmpty()){
                                    map.put("Imagen", imageURL);
                                }
                                if(!categoria.isEmpty()){
                                    map.put("idEncargado", idEncargado);
                                }
                                // Actualiza el documento con los nuevos datos

                                mFirestore.collection("Proyecto").document(documentId)
                                        .update(map)
                                        .addOnSuccessListener(aVoid -> {
                                            desplegarMensaje("Proyecto modificado con éxito", context);
                                            CorreoService correoService = new CorreoService();
                                            correoService.enviarCorreo(idEncargado,"Proyecto Modificado","Su proyecto, "+nombre+", ha sido modificado" ,context);
                                            Intent intent = new Intent(context, Principal.class);
                                            context.startActivity(intent);
                                        })
                                        .addOnFailureListener(e -> {
                                            desplegarMensaje("Error al modificar el proyecto: " + e.getMessage(), context);
                                        });

                            } else {
                                // Si no se encontró ningún proyecto con ese nombre
                                desplegarMensaje("No se encontró ningún proyecto con ese nombre", context);
                            }
                        } catch ( Exception e){
                            desplegarMensaje(e.getMessage().toString(), context);
                        }
                    } else {
                        // Si hubo un error al realizar la consulta
                        desplegarMensaje("Error en la consulta: " + task.getException().getMessage(), context);
                    }
                });
    }


    /*Verifica que el usuario esté Registrado y la contraseña sea correcta*/
    public void verificarUsuario(Context context, String correo, String password){
        mFirestore.collection("Usuarios").whereEqualTo("correo", correo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.size() == 0){
                            desplegarMensaje("Usuario no encontrado",context);
                        }
                        else {
                            // Iterar sobre cada documento en QuerySnapshot
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                if (document.getString("estado").equals("inactivo")){
                                    desplegarMensaje("Usuario inactivo", context);
                                }
                                else if (document.getString("password").equals(password) && document.getString("estado").equals("activo")) {
                                    Intent intent = new Intent(context, Principal.class);
                                    MainActivity.setCorreoColaborador(correo);
                                    context.startActivity(intent);
                                } else {
                                    desplegarMensaje("Contraseña incorrecta", context);
                                }
                            }
                        }
                    } else {
                        desplegarMensaje("Error",context);
                    }
                });
    }
    public void subirFoto(int tipo, String nombre, String descripcion, String objetivo, String categoria,
                          String fecha, Uri uri, String idEncargado, String nombreProyecto, Context context){
        StorageReference storageReference;
        String storagePath = "/*";
        storageReference = FirebaseStorage.getInstance().getReference();
        String rute = storagePath+ ""+nombre;
        StorageReference reference = storageReference.child(rute);
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                if (uriTask.isSuccessful()){
                    uriTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(tipo == 1){
                                crearProyecto(nombre, descripcion, objetivo, categoria, fecha, String.valueOf(uri), idEncargado, context);
                            }else{
                                modificarProyecto(nombre, descripcion, objetivo, categoria, fecha, String.valueOf(uri), idEncargado, nombreProyecto, context);
                            }
                        }
                    });
                }
            }
        });
    };
    /*Muestra los proyectos*/
    public ArrayList<ProyectCard> mostrarProyecto(ListView listView, Context context, Boolean modo){
        ArrayList<ProyectCard> proyects = new ArrayList<>();
        mFirestore.collection("Proyecto")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        // Iterar sobre cada documento en QuerySnapshot
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Usamos el objeto QueryDocumentSnapshot para acceder a los datos del documento
                            String categoria = document.getString("Categoria"); // Obtener el ID del documento
                            String descripcion = document.getString("Descripcion"); // Obtener el campo "nombre"
                            String fecha = document.getString("Fecha"); // Obtener el campo "correo"
                            String imagen = document.getString("Imagen"); // Obtener el ID del documento
                            String nombre = document.getString("Nombre"); // Obtener el campo "nombre"
                            String objetivo = document.getString("Objetivo"); // Obtener el campo "correo"
                            String monto = document.getString("Monto"); // Obtener el campo "correo"
                            proyects.add(new ProyectCard(imagen, nombre, fecha, descripcion, monto, objetivo, categoria));
                        }
                        if(modo){
                            ProyectCardAdapterAdmin adapter = new ProyectCardAdapterAdmin(context, R.layout.activity_proyect_card, proyects);
                            listView.setAdapter(adapter);
                        }
                        else {
                            ProyectCardAdapter adapter = new ProyectCardAdapter(context, R.layout.activity_proyect_card, proyects);
                            listView.setAdapter(adapter);
                        }

                    } else {
                        desplegarMensaje("Error",context);
                    }
                });
        return proyects;
    }
    /*Muestra todas las donaciones*/
    public void mostrarDonaciones(ListView listView, Context context){
        mFirestore.collection("Donacion")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        ArrayList<Donation> listaDonaciones = new ArrayList<>();
                        // Iterar sobre cada documento en QuerySnapshot
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Usamos el objeto QueryDocumentSnapshot para acceder a los datos del documento

                            String monto = document.getString("Monto"); // Obtener el campo "correo"
                            String correo = document.getString("correo"); // Obtener el campo "correo"
                            String nombreDonante = document.getString("nombreDonante"); // Obtener el campo "correo"
                            String nombreProyecto = document.getString("nombreProyecto"); // Obtener el campo "correo"
                            String telefono = document.getString("telefono"); // Obtener el campo "correo"

                            listaDonaciones.add(new Donation(monto, correo, nombreDonante, nombreProyecto, telefono));
                        }
                        DonacionesAdapter adapter = new DonacionesAdapter(context, R.layout.elemento, listaDonaciones);
                        listView.setAdapter(adapter);



                    } else {
                        desplegarMensaje("Error",context);
                    }
                });
    }
    /*Muestra todas las donaciones de un usuario en especifico*/
    public void mostrarDonacionesUsuario(ListView listView, Context context, String usuario){
        mFirestore.collection("Donacion").whereEqualTo("correo", usuario)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        ArrayList<Donation> listaDonaciones = new ArrayList<>();
                        // Iterar sobre cada documento en QuerySnapshot
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Usamos el objeto QueryDocumentSnapshot para acceder a los datos del documento

                            String monto = document.getString("Monto"); // Obtener el campo "correo"
                            String correo = document.getString("correo"); // Obtener el campo "correo"
                            String nombreDonante = document.getString("nombreDonante"); // Obtener el campo "correo"
                            String nombreProyecto = document.getString("nombreProyecto"); // Obtener el campo "correo"
                            String telefono = document.getString("telefono"); // Obtener el campo "correo"
                            listaDonaciones.add(new Donation(monto, correo, nombreDonante, nombreProyecto, telefono));
                        }
                        DonacionesAdapter adapter = new DonacionesAdapter(context, R.layout.elemento, listaDonaciones);
                        listView.setAdapter(adapter);



                    } else {
                        desplegarMensaje("Error",context);
                    }
                });

    }
    public void leerDatos(Context context){
        mFirestore.collection("Usuarios")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();

                        // Iterar sobre cada documento en QuerySnapshot
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Usamos el objeto QueryDocumentSnapshot para acceder a los datos del documento
                            String id = document.getId(); // Obtener el ID del documento
                            String nombre = document.getString("nombre"); // Obtener el campo "nombre"
                            String correo = document.getString("correo"); // Obtener el campo "correo"

                            // Mostrar los datos obtenidos en Logcat
                            desplegarMensaje(" Nombre: " + nombre + ", Correo: " + correo,context);
                        }
                    } else {
                        Log.w(TAG, "Error al obtener documentos.", task.getException());
                    }
                });

    }

    /*Estadisticas*/
    public void estadisticas(Context context, TextView Usuarios, TextView Proyectos, TextView Donaciones){
        mFirestore.collection("Usuarios").whereEqualTo("estado", "activo")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        Usuarios.setText(querySnapshot.size()+"");
                    } else {
                        Log.w(TAG, "Error al obtener documentos.", task.getException());
                    }
                });
        mFirestore.collection("Donacion")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        Donaciones.setText(querySnapshot.size()+"");
                    } else {
                        Log.w(TAG, "Error al obtener documentos.", task.getException());
                    }
                });
        mFirestore.collection("Proyecto")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        Proyectos.setText(querySnapshot.size()+"");
                    } else {
                        Log.w(TAG, "Error al obtener documentos.", task.getException());
                    }
                });
    }
    /*Despliega un mensaje con un toast*/
    private  void desplegarMensaje(CharSequence text, Context context){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void mostrarUsuario(Context context, String usuario, TextInputLayout nombreText, EditText cedulaText, EditText correoText, TextInputLayout areaText, EditText dineroText, EditText telefonoText){
        mFirestore.collection("Usuarios").whereEqualTo("correo", usuario)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);

                        String dinero = document.getString("dinero");
                        String correo = document.getString("correo");
                        String nombre = document.getString("nombre");
                        String telefono = document.getString("telefono");
                        String cedula = document.getString("cedula");
                        String area = document.getString("area");

                        nombreText.setHint(nombre);
                        cedulaText.setHint(cedula);
                        correoText.setHint(correo);
                        areaText.setHint(area);
                        dineroText.setHint(dinero);
                        telefonoText.setHint(telefono);

                    } else {
                        desplegarMensaje("Error",context);
                    }
                });
    }





    /*Despliega una lista de usuarios*/
    public void mostrarUsuarios(ListView listView, Context context){
        mFirestore.collection("Usuarios")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        List<User> usuarios = new ArrayList<>();
                        // Iterar sobre cada documento en QuerySnapshot
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // Usamos el objeto QueryDocumentSnapshot para acceder a los datos del documento

                            String nombre = document.getString("nombre");
                            String correo = document.getString("correo");
                            String cedula = document.getString("cedula");
                            String area = document.getString("area");
                            String estado = document.getString("estado");

                            usuarios.add(new User(nombre, correo, cedula, area, estado));
                        }
                        UsuarioAdapter adapter = new UsuarioAdapter(context, usuarios);
                        listView.setAdapter(adapter);



                    } else {
                        desplegarMensaje("Error",context);
                    }
                });

    }
    /*Desactiva */
    public void activarDesactivarUsuario(Context context, String correo, ListView listView){
        mFirestore.collection("Usuarios").whereEqualTo("correo", correo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // El resultado de la consulta es un objeto QuerySnapshot
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.size() == 0){
                            desplegarMensaje("Usuario no encontrado",context);
                        }
                        else {
                            // Iterar sobre cada documento en QuerySnapshot
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0); // Obtén el primer resultado
                            String documentId = documentSnapshot.getId(); // Obtiene el ID del documento

                            // Crea el mapa con la nueva información
                            Map<String, Object> map = new HashMap<>();
                            if(documentSnapshot.getString("estado").equals("activo")){
                                map.put("estado", "inactivo");
                            }
                            else{
                                map.put("estado", "activo");
                            }
                            // Actualiza el documento con los nuevos datos

                            mFirestore.collection("Usuarios").document(documentId)
                                    .update(map)
                                    .addOnSuccessListener(aVoid -> {
                                        desplegarMensaje("El estado del usuario ha sido modificado", context);
                                        mostrarUsuarios(listView, context);
                                    })
                                    .addOnFailureListener(e -> {
                                        desplegarMensaje("Error al modificar el estado del usuario: " + e.getMessage(), context);
                                    });
                        }
                    } else {
                        desplegarMensaje("Error",context);
                    }
                });
    }
    /*Verifica que el usuario sea el administrador y la contraseña sea correcta*/
    public void verificarAdministrador(Context context, String correo, String password){
        if (correo.equals("crowdspark58@gmail.com")){
            mFirestore.collection("Usuarios").whereEqualTo("correo", "crowdspark58@gmail.com")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // El resultado de la consulta es un objeto QuerySnapshot
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot.size() == 0){
                                desplegarMensaje("Usuario no encontrado",context);
                            }
                            else {
                                // Iterar sobre cada documento en QuerySnapshot
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    if (document.getString("estado").equals("inactivo")){
                                        desplegarMensaje("Usuario inactivo", context);
                                    }
                                    else if (document.getString("password").equals(password) && document.getString("estado").equals("activo")) {
                                        Intent intent = new Intent(context, PrincipalAdmin.class);
                                        MainActivity.setCorreoColaborador(correo);
                                        context.startActivity(intent);
                                    } else {
                                        desplegarMensaje("Contraseña incorrecta", context);
                                    }
                                }
                            }
                        } else {
                            desplegarMensaje("Error",context);
                        }
                    });

            }
        else {
            desplegarMensaje("Usted no es administrador",context);
        }
    }
}

