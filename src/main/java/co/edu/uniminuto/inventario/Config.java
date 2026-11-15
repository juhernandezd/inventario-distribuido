package co.edu.uniminuto.inventario;

public final class Config {

    public static final String BROKER_URL =
            System.getProperty("broker.url", "tcp://localhost:61616");
    public static final String DB_URL = System.getProperty("db.url",
            "jdbc:mysql://localhost:3306/DB_distribucionAlimentos?serverTimezone=America/Bogota");
    public static final String DB_USUARIO = System.getProperty("db.usuario", "root");
    public static final String DB_CLAVE = System.getProperty("db.clave", "");

    public static final String COLA_TRANSACCIONES = "inventario.transacciones";
    public static final String TOPICO_INVALIDACION = "inventario.cache.invalidacion";
    public static final long CACHE_TTL_MS = 60_000;

    private Config() {
    }
}
