package com.example.crowdspark.control;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EnviarCorreo {
    private Session session;
    public EnviarCorreo(){

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Servidor SMTP
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        // Autenticación
        session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("crowdspark58@gmail.com", "enst bmfl lzea nugm");
            }
        });
    }
    public void enviar(String destinatario, String tema, String contenido) throws MessagingException {
        // Crea el mensaje
        MimeMessage mm = new MimeMessage(session);
        mm.setFrom(new InternetAddress("crowdspark58@gmail.com"));
        mm.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
        mm.setSubject(tema);
        mm.setText(contenido);

        // Envía el mensaje
        Transport.send(mm);
    }
}

