package com.example.crowdspark.control;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import javax.mail.MessagingException;

public class CorreoService {

    private ExecutorService executorService;
    private Handler handler;

    public CorreoService() {
        executorService = Executors.newSingleThreadExecutor(); // Crear un hilo de ejecución
        handler = new Handler(Looper.getMainLooper()); // Para actualizar la interfaz de usuario
    }

    public void enviarCorreo(final String destinatario, final String asunto, final String mensaje, Context context) {
        // Ejecutar la tarea en un hilo de fondo
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EnviarCorreo sm = new EnviarCorreo();
                    sm.enviar(destinatario, asunto, mensaje);

                    // Si el envío es exitoso, mostrar un Toast en el hilo principal
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Correo enviado", Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (final MessagingException e) {
                    e.printStackTrace();
                    // Si hay error, también lo manejamos en el hilo principal
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Error al enviar correo: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}
