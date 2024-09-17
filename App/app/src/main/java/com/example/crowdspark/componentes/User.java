package com.example.crowdspark.componentes;

public class User {
    private String nombre;
    private String correo;
    private String cedula;
    private String area;
    private String estado;

    public User(String nombre, String correo, String cedula, String area, String estado) {
        this.nombre = nombre;
        this.correo = correo;
        this.cedula = cedula;
        this.area = area;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getCedula() {
        return cedula;
    }

    public String getArea() {
        return area;
    }

    public String getEstado() {
        return estado;
    }
}

