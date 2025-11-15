package com.example.crudfirebase;


public class Gasto {
    public String id;
    public String descripcion;
    public double cantidad;
    public String categoria;

    // Constructor vacío requerido por Firebase
    public Gasto() {
    }

    public Gasto(String id, String descripcion, double cantidad, String categoria) {
        this.id = id;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.categoria = categoria;
    }
}