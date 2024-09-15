package com.example.crowdspark.control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.crowdspark.MainActivity;
import com.example.crowdspark.ventanas.Registrarse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

public class FireBaseConnection {
    FirebaseFirestore mFirestore;

    public FireBaseConnection() {
        this.mFirestore = FirebaseFirestore.getInstance();
    }

    public void registrar(String correo, String password, String nombre,String area,
                          String cedula, String dinero, String telefono, Context context){
        mFirestore = FirebaseFirestore.getInstance();
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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                desplegarMensaje("No se ha podido registrar "+e, context);
            }
        });

/*        mAuth.createUserWithEmailAndPassword(correo,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String id= mAuth.getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre",nombre);
                    map.put("correo",correo);
                    map.put("password",password);
                    map.put("area",area);
                    map.put("cedula",cedula);
                    map.put("dinero",dinero);
                    map.put("telefono",telefono);
                    map.put("estado","activo");
                    mFirestore.collection("Usuarios").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(context, MainActivity.class);
                                context.startActivity(intent);
                                desplegarMensaje("Usuario registrado", context);
                            }
                            else {desplegarMensaje("No se ha podido registrar al usuario", context);}
                        }
                    });
                }else{
                    desplegarMensaje("Error", context);
                }
            }
        });

 */

    }
    private  void desplegarMensaje(CharSequence text, Context context){
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
