package co.edu.uniminuto.inventario.cache;

import co.edu.uniminuto.inventario.datos.Conexion;
import co.edu.uniminuto.inventario.datos.Sucursal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.jms.JMSException;

public class CacheBranch extends CacheDistribuido<Sucursal> {

    public CacheBranch() throws JMSException {
        super("sucursales");
    }

    @Override
    protected Sucursal cargarDesdeBaseDeDatos(int id) throws SQLException {
        try (Connection bd = Conexion.abrir();
                PreparedStatement ps = bd.prepareStatement(
                        "SELECT id_sucursal, nombre, direccion, telefono "
                        + "FROM Sucursales WHERE id_sucursal = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                return new Sucursal(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
        }
    }

    public void actualizarTelefono(int id, String telefono) throws SQLException, JMSException {
        try (Connection bd = Conexion.abrir();
                PreparedStatement ps = bd.prepareStatement(
                        "UPDATE Sucursales SET telefono = ? WHERE id_sucursal = ?")) {
            ps.setString(1, telefono);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
        publicarInvalidacion(id);
    }
}
