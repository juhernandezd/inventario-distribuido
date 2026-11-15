package co.edu.uniminuto.inventario;

import co.edu.uniminuto.inventario.cache.CacheBranch;
import co.edu.uniminuto.inventario.cache.CacheProduct;
import co.edu.uniminuto.inventario.datos.Producto;
import co.edu.uniminuto.inventario.datos.Sucursal;
import co.edu.uniminuto.inventario.mensajeria.Consumidor;
import co.edu.uniminuto.inventario.mensajeria.Productor;
import co.edu.uniminuto.inventario.modelo.Compra;
import co.edu.uniminuto.inventario.modelo.Venta;
import java.math.BigDecimal;

public class App {

    public static void main(String[] args) throws Exception {
        try (Consumidor sedeCentral = new Consumidor();
                Productor sucursal = new Productor();
                CacheProduct productos = new CacheProduct();
                CacheBranch sucursales = new CacheBranch()) {

            int idSucursal = 2;
            int idProducto = 1;

            long t0 = System.nanoTime();
            Sucursal origen = sucursales.obtener(idSucursal);
            Producto producto = productos.obtener(idProducto);
            long sinCache = System.nanoTime() - t0;

            t0 = System.nanoTime();
            sucursales.obtener(idSucursal);
            productos.obtener(idProducto);
            long conCache = System.nanoTime() - t0;

            System.out.println("[CACHE] " + origen + " | " + producto);
            System.out.printf("[CACHE] Primera lectura (MySQL): %d us | segunda (caché): %d us%n",
                    sinCache / 1000, conCache / 1000);

            sucursal.enviar(new Compra(idSucursal, idProducto, 100, producto.getPrecio()));
            sucursal.enviar(new Venta(idSucursal, idProducto, 30, producto.getPrecio()));
            sucursal.enviar(new Venta(idSucursal, idProducto, 1_000_000, producto.getPrecio()));

            Thread.sleep(2000);

            productos.actualizarPrecio(idProducto, producto.getPrecio().add(new BigDecimal("100")));
            Thread.sleep(500);
            System.out.println("[CACHE] Tras invalidar: " + productos.obtener(idProducto));

            System.out.printf("[RESUMEN] procesadas=%d rechazadas=%d aciertos=%d fallos=%d%n",
                    sedeCentral.getProcesadas(), sedeCentral.getRechazadas(),
                    productos.getAciertos() + sucursales.getAciertos(),
                    productos.getFallos() + sucursales.getFallos());
        }
    }
}
