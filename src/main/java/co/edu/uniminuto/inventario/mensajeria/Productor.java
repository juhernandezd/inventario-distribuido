package co.edu.uniminuto.inventario.mensajeria;

import co.edu.uniminuto.inventario.Config;
import co.edu.uniminuto.inventario.modelo.Compra;
import co.edu.uniminuto.inventario.modelo.Transaccion;
import co.edu.uniminuto.inventario.modelo.Venta;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Productor implements AutoCloseable {

    private final Connection conexion;
    private final Session sesion;
    private final MessageProducer productor;

    public Productor() throws JMSException {
        conexion = new ActiveMQConnectionFactory(Config.BROKER_URL).createConnection();
        conexion.start();
        sesion = conexion.createSession(true, Session.SESSION_TRANSACTED);
        productor = sesion.createProducer(sesion.createQueue(Config.COLA_TRANSACCIONES));
        productor.setDeliveryMode(DeliveryMode.PERSISTENT);
    }

    public void enviar(Transaccion tx) throws JMSException {
        try {
            MapMessage mensaje = sesion.createMapMessage();
            mensaje.setString("tipo", tx.getTipo());
            mensaje.setString("id", tx.getId());
            mensaje.setString("fechaHora", tx.getFechaHora().toString());
            mensaje.setInt("idSucursal", tx.getIdSucursal());
            if (tx instanceof Compra) {
                Compra c = (Compra) tx;
                mensaje.setInt("idProducto", c.getIdProducto());
                mensaje.setInt("cantidad", c.getCantidad());
                mensaje.setString("precioUnitario", c.getPrecioUnitario().toPlainString());
            } else {
                Venta v = (Venta) tx;
                mensaje.setInt("idProducto", v.getIdProducto());
                mensaje.setInt("cantidad", v.getCantidad());
                mensaje.setString("precioUnitario", v.getPrecioUnitario().toPlainString());
            }
            productor.send(mensaje);
            sesion.commit();
            System.out.println("[PRODUCTOR - Sucursal " + tx.getIdSucursal()
                    + "] Mensaje enviado a la cola: " + tx);
        } catch (JMSException e) {
            sesion.rollback();
            throw e;
        }
    }

    @Override
    public void close() throws JMSException {
        conexion.close();
    }
}
