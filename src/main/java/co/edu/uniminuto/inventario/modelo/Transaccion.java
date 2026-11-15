package co.edu.uniminuto.inventario.modelo;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Transaccion {

    private final String id;
    private final LocalDateTime fechaHora;
    private final int idSucursal;

    protected Transaccion(int idSucursal) {
        this(UUID.randomUUID().toString(), LocalDateTime.now().withNano(0), idSucursal);
    }

    protected Transaccion(String id, LocalDateTime fechaHora, int idSucursal) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.idSucursal = idSucursal;
    }

    public abstract String getTipo();

    public String getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getIdSucursal() {
        return idSucursal;
    }
}
