package co.edu.uniminuto.inventario.mensajeria;

import co.edu.uniminuto.inventario.Config;
import co.edu.uniminuto.inventario.datos.Conexion;
import co.edu.uniminuto.inventario.modelo.Compra;
import co.edu.uniminuto.inventario.modelo.Transaccion;
import co.edu.uniminuto.inventario.modelo.Venta;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumidor implements MessageListener, AutoCloseable {

    public static final int ID_SEDE_CENTRAL = 1;

    private final Connection conexion;
    private final Session sesion;
    private final AtomicInteger procesadas = new AtomicInteger();
    private final AtomicInteger rechazadas = new AtomicInteger();

    public Consumidor() throws JMSException {
        conexion = new ActiveMQConnectionFactory(Config.BROKER_URL).createConnection();
        sesion = conexion.createSession(true, Session.SESSION_TRANSACTED);
        sesion.createConsumer(sesion.createQueue(Config.COLA_TRANSACCIONES))
                .setMessageListener(this);
        conexion.start();
    }

    @Override
    public void onMessage(Message mensaje) {
        try {
            Transaccion tx = leer((MapMessage) mensaje);
            boolean aplicada = procesarEnBaseDeDatos(tx);
            sesion.commit();
            if (aplicada) {
                procesadas.incrementAndGet();
                System.out.println("[CONSUMIDOR - Sede Central] Procesada en MySQL -> " + tx);
            } else {
                rechazadas.incrementAndGet();
                System.out.println("[CONSUMIDOR - Sede Central] Rechazada o duplicada -> " + tx);
            }
        } catch (Exception e) {
            System.err.println("[CONSUMIDOR - Sede Central] Error, se reintentará: " + e.getMessage());
            try {
                sesion.rollback();
            } catch (JMSException ex) {
                System.err.println("[CONSUMIDOR - Sede Central] No se pudo revertir: " + ex.getMessage());
            }
        }
    }

    private Transaccion leer(MapMessage m) throws JMSException {
        String id = m.getString("id");
        LocalDateTime fecha = LocalDateTime.parse(m.getString("fechaHora"));
        int idSucursal = m.getInt("idSucursal");
        int idProducto = m.getInt("idProducto");
        int cantidad = m.getInt("cantidad");
        BigDecimal precio = new BigDecimal(m.getString("precioUnitario"));
        if (Compra.TIPO.equals(m.getString("tipo"))) {
            return new Compra(id, fecha, idSucursal, idProducto, cantidad, precio);
        }
        return new Venta(id, fecha, idSucursal, idProducto, cantidad, precio);
    }

    private boolean procesarEnBaseDeDatos(Transaccion tx) throws SQLException {
        try (java.sql.Connection bd = Conexion.abrir()) {
            bd.setAutoCommit(false);
            try {
                boolean aplicada = !yaRegistrada(bd, tx.getId()) && moverInventario(bd, tx);
                if (aplicada) {
                    registrar(bd, tx);
                    bd.commit();
                } else {
                    bd.rollback();
                }
                return aplicada;
            } catch (SQLException e) {
                bd.rollback();
                throw e;
            }
        }
    }

    private boolean yaRegistrada(java.sql.Connection bd, String id) throws SQLException {
        try (PreparedStatement ps = bd.prepareStatement(
                "SELECT 1 FROM Transacciones WHERE id_transaccion = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private boolean moverInventario(java.sql.Connection bd, Transaccion tx) throws SQLException {
        if (tx instanceof Venta) {
            Venta v = (Venta) tx;
            return descontar(bd, v.getIdSucursal(), v.getIdProducto(), v.getCantidad());
        }
        Compra c = (Compra) tx;
        if (c.getIdSucursal() != ID_SEDE_CENTRAL
                && !descontar(bd, ID_SEDE_CENTRAL, c.getIdProducto(), c.getCantidad())) {
            return false;
        }
        sumar(bd, c.getIdSucursal(), c.getIdProducto(), c.getCantidad());
        return true;
    }

    private boolean descontar(java.sql.Connection bd, int idSucursal, int idProducto, int cantidad)
            throws SQLException {
        try (PreparedStatement ps = bd.prepareStatement(
                "UPDATE Inventario SET stock = stock - ? "
                + "WHERE id_sucursal = ? AND id_producto = ? AND stock >= ?")) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idSucursal);
            ps.setInt(3, idProducto);
            ps.setInt(4, cantidad);
            return ps.executeUpdate() == 1;
        }
    }

    private void sumar(java.sql.Connection bd, int idSucursal, int idProducto, int cantidad)
            throws SQLException {
        try (PreparedStatement ps = bd.prepareStatement(
                "UPDATE Inventario SET stock = stock + ? WHERE id_sucursal = ? AND id_producto = ?")) {
            ps.setInt(1, cantidad);
            ps.setInt(2, idSucursal);
            ps.setInt(3, idProducto);
            if (ps.executeUpdate() == 1) {
                return;
            }
        }
        try (PreparedStatement ps = bd.prepareStatement(
                "INSERT INTO Inventario (id_sucursal, id_producto, stock) VALUES (?, ?, ?)")) {
            ps.setInt(1, idSucursal);
            ps.setInt(2, idProducto);
            ps.setInt(3, cantidad);
            ps.executeUpdate();
        }
    }

    private void registrar(java.sql.Connection bd, Transaccion tx) throws SQLException {
        int idProducto;
        int cantidad;
        BigDecimal precio;
        if (tx instanceof Compra) {
            Compra c = (Compra) tx;
            idProducto = c.getIdProducto();
            cantidad = c.getCantidad();
            precio = c.getPrecioUnitario();
        } else {
            Venta v = (Venta) tx;
            idProducto = v.getIdProducto();
            cantidad = v.getCantidad();
            precio = v.getPrecioUnitario();
        }
        try (PreparedStatement ps = bd.prepareStatement(
                "INSERT INTO Transacciones (id_transaccion, tipo, fecha_hora, id_sucursal, "
                + "id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, tx.getId());
            ps.setString(2, tx.getTipo());
            ps.setTimestamp(3, Timestamp.valueOf(tx.getFechaHora()));
            ps.setInt(4, tx.getIdSucursal());
            ps.setInt(5, idProducto);
            ps.setInt(6, cantidad);
            ps.setBigDecimal(7, precio);
            ps.executeUpdate();
        }
    }

    public int getProcesadas() {
        return procesadas.get();
    }

    public int getRechazadas() {
        return rechazadas.get();
    }

    @Override
    public void close() throws JMSException {
        conexion.close();
    }
}
