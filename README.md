# inventario-distribuido

Actividad: Analisis de caso. Transacciones distribuidas (Sistemas Transaccionales, UNIMINUTO). Empresa de distribucion de alimentos con MySQL, ActiveMQ (JMS) y cache distribuido.

Archivos del repositorio:

inventario-distribuido.zip: proyecto NetBeans/Maven completo (codigo Java, pom.xml, README y script SQL).

DB_distribucionAlimentos.sql: script de la base de datos (Sucursales, Productos, Inventario, Transacciones).

Como ejecutarlo: 1) ejecutar el script SQL en MySQL; 2) iniciar ActiveMQ (bin\activemq start); 3) abrir el proyecto en NetBeans, ajustar la clave de MySQL en Config.java (DB_CLAVE) y ejecutar el proyecto. Resultado esperado: procesadas=2 rechazadas=1.
