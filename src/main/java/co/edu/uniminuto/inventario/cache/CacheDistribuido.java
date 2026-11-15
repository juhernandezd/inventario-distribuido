package co.edu.uniminuto.inventario.cache;

import co.edu.uniminuto.inventario.Config;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;

abstract class CacheDistribuido<V> implements AutoCloseable {

    private static final class Entrada<V> {

        final V valor;
        final long vence;

        Entrada(V valor, long vence) {
            this.valor = valor;
            this.vence = vence;
        }
    }

    private final Map<Integer, Entrada<V>> entradas = new ConcurrentHashMap<>();
    private final AtomicInteger aciertos = new AtomicInteger();
    private final AtomicInteger fallos = new AtomicInteger();
    private final String nombre;
    private final Connection conexion;
    private final Session sesionEnvio;
    private final MessageProducer publicador;

    protected CacheDistribuido(String nombre) throws JMSException {
        this.nombre = nombre;
        conexion = new ActiveMQConnectionFactory(Config.BROKER_URL).createConnection();
        Session sesionEscucha = conexion.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topico = sesionEscucha.createTopic(Config.TOPICO_INVALIDACION);
        sesionEscucha.createConsumer(topico, "cache = '" + nombre + "'")
                .setMessageListener(this::alRecibirInvalidacion);
        sesionEnvio = conexion.createSession(false, Session.AUTO_ACKNOWLEDGE);
        publicador = sesionEnvio.createProducer(sesionEnvio.createTopic(Config.TOPICO_INVALIDACION));
        conexion.start();
    }

    protected abstract V cargarDesdeBaseDeDatos(int id) throws SQLException;

    public V obtener(int id) throws SQLException {
        Entrada<V> e = entradas.get(id);
        if (e != null && e.vence > System.currentTimeMillis()) {
            aciertos.incrementAndGet();
            return e.valor;
        }
        fallos.incrementAndGet();
        V valor = cargarDesdeBaseDeDatos(id);
        if (valor != null) {
            entradas.put(id, new Entrada<>(valor, System.currentTimeMillis() + Config.CACHE_TTL_MS));
        }
        return valor;
    }

    protected synchronized void publicarInvalidacion(int id) throws JMSException {
        entradas.remove(id);
        Message aviso = sesionEnvio.createMessage();
        aviso.setStringProperty("cache", nombre);
        aviso.setIntProperty("id", id);
        publicador.send(aviso);
    }

    private void alRecibirInvalidacion(Message aviso) {
        try {
            int id = aviso.getIntProperty("id");
            entradas.remove(id);
            System.out.println("[CACHE " + nombre + "] Entrada " + id + " invalidada");
        } catch (JMSException e) {
            entradas.clear();
        }
    }

    public int getAciertos() {
        return aciertos.get();
    }

    public int getFallos() {
        return fallos.get();
    }

    @Override
    public void close() throws JMSException {
        conexion.close();
    }
}
