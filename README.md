# Inventario distribuido

Actividad: Análisis de caso. Transacciones distribuidas (Sistemas Transaccionales, UNIMINUTO). Empresa de distribución de alimentos con MySQL, ActiveMQ (JMS) y caché distribuido.

## Contenido

| Ruta | Qué es |
|---|---|
| `sql/DB_distribucionAlimentos.sql` | Script de la base de datos: Sucursales, Productos, Inventario, Transacciones y datos de prueba |
| `pom.xml` | Proyecto Maven (abre directo en NetBeans) |
| `src/main/java/co/edu/uniminuto/inventario/App.java` | Demostración completa |
| `.../modelo/` | Transaccion, Compra, Venta |
| `.../mensajeria/` | Productor (sucursal) y Consumidor (sede central), cola `inventario.transacciones` |
| `.../cache/` | CacheProduct y CacheBranch, invalidación por el tópico `inventario.cache.invalidacion` |
| `.../datos/` | Conexion, Producto, Sucursal |
| `.../Config.java` | URL del broker, conexión MySQL y TTL de la caché |

## Requisitos

- JDK 11 o superior
- MySQL 8.0.16 o superior
- Apache ActiveMQ Classic 5.18.x o 6.x

## Ejecución

1. Descargar el repositorio (Code > Download ZIP) y descomprimirlo.
2. Crear la base de datos: `mysql -u root -p < sql/DB_distribucionAlimentos.sql`
3. Iniciar el broker: `bin\activemq start` (consola en http://localhost:8161, admin/admin).
4. En NetBeans: File > Open Project, elegir la carpeta, y ejecutar el proyecto (Run Project). Por consola: `mvn compile exec:java`.
5. Si MySQL tiene clave para root: editar `DB_CLAVE` en `Config.java` o ejecutar `mvn compile exec:java -Ddb.clave=TU_CLAVE`.

Salida esperada al final: `[RESUMEN] procesadas=2 rechazadas=1 aciertos=2 fallos=3`.
