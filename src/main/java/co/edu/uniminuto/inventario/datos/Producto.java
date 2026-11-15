package co.edu.uniminuto.inventario.datos;

import java.math.BigDecimal;

public class Producto {

    private final int id;
    private final String nombre;
    private final String descripcion;
    private final BigDecimal precio;

    public Producto(int id, String nombre, String descripcion, BigDecimal precio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return "Producto{id=" + id + ", nombre=" + nombre + ", precio=" + precio + "}";
    }
}
