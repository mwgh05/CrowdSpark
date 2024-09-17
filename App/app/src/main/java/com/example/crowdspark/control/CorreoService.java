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
        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }

    public void enviarCorreo(final String destinatario, final String asunto, final String mensaje, Context context) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EnviarCorreo sm = new EnviarCorreo();
                    sm.enviar(destinatario, asunto, mensaje);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                } catch (final MessagingException e) {
                    e.printStackTrace();
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
