package co.edu.uniminuto.inventario.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Venta extends Transaccion {

    public static final String TIPO = "VENTA";

    private final int idProducto;
    private final int cantidad;
    private final BigDecimal precioUnitario;

    public Venta(int idSucursal, int idProducto, int cantidad, BigDecimal precioUnitario) {
        super(idSucursal);
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Venta(String id, LocalDateTime fechaHora, int idSucursal,
            int idProducto, int cantidad, BigDecimal precioUnitario) {
        super(id, fechaHora, idSucursal);
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    @Override
    public String getTipo() {
        return TIPO;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    @Override
    public String toString() {
        return TIPO + "{id=" + getId() + ", sucursal=" + getIdSucursal()
                + ", producto=" + idProducto + ", cantidad=" + cantidad
                + ", precioUnitario=" + precioUnitario + ", fecha=" + getFechaHora() + "}";
    }
}
