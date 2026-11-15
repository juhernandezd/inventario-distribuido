package co.edu.uniminuto.inventario.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Compra extends Transaccion {

    public static final String TIPO = "COMPRA";

    private final int idProducto;
    private final int cantidad;
    private final BigDecimal precioUnitario;

    public Compra(int idSucursal, int idProducto, int cantidad, BigDecimal precioUnitario) {
        super(idSucursal);
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Compra(String id, LocalDateTime fechaHora, int idSucursal,
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
