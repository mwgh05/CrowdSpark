package com.example.crowdspark.componentes;

public class Donation {
    private String monto;
    private String correo;
    private String nombreDonante;
    private String nombreProyecto;
    private String telefono;

    public Donation(String monto, String correo, String nombreDonante, String nombreProyecto, String telefono) {
        this.monto = monto;
        this.correo = correo;
        this.nombreDonante = nombreDonante;
        this.nombreProyecto = nombreProyecto;
        this.telefono = telefono;
    }

    public String getMonto() {
        return monto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombreDonante() {
        return nombreDonante;
    }

    public String getNombreProyecto() {
        return nombreProyecto;
    }

    public String getTelefono() {
        return telefono;
    }
}
