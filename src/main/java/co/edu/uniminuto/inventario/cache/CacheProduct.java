package co.edu.uniminuto.inventario.cache;

import co.edu.uniminuto.inventario.datos.Conexion;
import co.edu.uniminuto.inventario.datos.Producto;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.jms.JMSException;

public class CacheProduct extends CacheDistribuido<Producto> {

    public CacheProduct() throws JMSException {
        super("productos");
    }

    @Override
    protected Producto cargarDesdeBaseDeDatos(int id) throws SQLException {
        try (Connection bd = Conexion.abrir();
                PreparedStatement ps = bd.prepareStatement(
                        "SELECT id_producto, nombre, descripcion, precio "
                        + "FROM Productos WHERE id_producto = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Producto(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getBigDecimal(4));
            }
        }
    }

    public void actualizarPrecio(int id, BigDecimal precio) throws SQLException, JMSException {
        try (Connection bd = Conexion.abrir();
                PreparedStatement ps = bd.prepareStatement(
                        "UPDATE Productos SET precio = ? WHERE id_producto = ?")) {
            ps.setBigDecimal(1, precio);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
        publicarInvalidacion(id);
    }
}
