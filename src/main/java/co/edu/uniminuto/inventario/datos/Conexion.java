package co.edu.uniminuto.inventario.datos;

import co.edu.uniminuto.inventario.Config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Conexion {

    private Conexion() {
    }

    public static Connection abrir() throws SQLException {
        return DriverManager.getConnection(Config.DB_URL, Config.DB_USUARIO, Config.DB_CLAVE);
    }
}
