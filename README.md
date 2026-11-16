# Inventario distribuido

Trabajo de Sistemas Transaccionales (semana 4), UNIMINUTO.

Es una simulación de una empresa de distribución de alimentos con una sede central y varias sucursales. Las sucursales mandan compras y ventas por una cola de ActiveMQ y la sede central las guarda en MySQL.

## Qué hay en el repositorio

- `sql/DB_distribucionAlimentos.sql`: script para crear la base de datos con datos de prueba.
- `pom.xml` y `src/`: proyecto de NetBeans (Maven) con el código en Java.

## Cómo probarlo

1. Ejecutar el script SQL en MySQL.
2. Iniciar ActiveMQ (`bin\activemq start`).
3. Abrir el proyecto en NetBeans (File > Open Project) y correr `App.java`.

El usuario de MySQL es `root` sin clave. Si tiene clave, hay que cambiarla en `Config.java`.

Al final debería salir `procesadas=2 rechazadas=1`.

Victor Manuel Hernández David
