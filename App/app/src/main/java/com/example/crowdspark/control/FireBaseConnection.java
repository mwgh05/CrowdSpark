package com.example.crowdspark.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.ventanas.Principal;
import com.example.crowdspark.ventanas.Registrarse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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

import java.util.HashMap;
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
                                if (document.getString("password").equals(password)) {
                                    Intent intent = new Intent(context, Principal.class);
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
    private  void desplegarMensaje(CharSequence text, Context context){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
    public void subirFoto(String nombre, Uri uri){
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
                           /*Imprime algo o hace una accion*/

                        }
                    });
                }

            }
        });
    };
}
