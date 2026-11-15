CREATE DATABASE IF NOT EXISTS DB_distribucionAlimentos
CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE DB_distribucionAlimentos;

DROP TABLE IF EXISTS Transacciones;
DROP TABLE IF EXISTS Inventario;
DROP TABLE IF EXISTS Productos;
DROP TABLE IF EXISTS Sucursales;

CREATE TABLE Sucursales (
  id_sucursal INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  direccion VARCHAR(150) NOT NULL,
  telefono VARCHAR(20) NOT NULL
  ) ENGINE = InnoDB;

CREATE TABLE Productos (
  id_producto INT PRIMARY KEY AUTO_INCREMENT,
  nombre VARCHAR(100) NOT NULL,
  descripcion TEXT,
  precio DECIMAL(10, 2) NOT NULL CHECK (precio >= 0)
  ) ENGINE = InnoDB;

CREATE TABLE Inventario (
  id_sucursal INT NOT NULL,
  id_producto INT NOT NULL,
  stock INT NOT NULL DEFAULT 0 CHECK (stock >= 0),
  PRIMARY KEY (id_sucursal, id_producto),
  FOREIGN KEY (id_sucursal) REFERENCES Sucursales (id_sucursal),
  FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
  ) ENGINE = InnoDB;

CREATE TABLE Transacciones (
  id_transaccion CHAR(36) PRIMARY KEY,
  tipo ENUM ('COMPRA', 'VENTA') NOT NULL,
  fecha_hora DATETIME NOT NULL,
  id_sucursal INT NOT NULL,
  id_producto INT NOT NULL,
  cantidad INT NOT NULL CHECK (cantidad > 0),
  precio_unitario DECIMAL(10, 2) NOT NULL,
  FOREIGN KEY (id_sucursal) REFERENCES Sucursales (id_sucursal),
  FOREIGN KEY (id_producto) REFERENCES Productos (id_producto)
  ) ENGINE = InnoDB;

INSERT INTO Sucursales (nombre, direccion, telefono) VALUES
('Sede Central Bogotá', 'Av. Calle 26 # 68-50, Bogotá', '601 555 0100'),
('Sucursal Medellín', 'Cra. 43A # 1-50, Medellín', '604 555 0101'),
('Sucursal Cali', 'Av. 6N # 23-45, Cali', '602 555 0102'),
('Sucursal Barranquilla', 'Cra. 53 # 75-120, Barranquilla', '605 555 0103');

INSERT INTO Productos (nombre, descripcion, precio) VALUES
('Arroz blanco 500 g', 'Arroz de grano largo, bolsa de 500 g', 2400.00),
('Aceite vegetal 1 L', 'Aceite de girasol, botella de 1 litro', 9800.00),
('Fríjol rojo 500 g', 'Fríjol cargamanto seleccionado', 5200.00),
('Leche entera 1 L', 'Leche UHT entera, caja de 1 litro', 4100.00),
('Café molido 250 g', 'Café de origen, tostión media', 11500.00);

INSERT INTO Inventario (id_sucursal, id_producto, stock) VALUES
(1, 1, 5000), (1, 2, 3000), (1, 3, 2500), (1, 4, 4000), (1, 5, 1500),
(2, 1, 400), (2, 2, 250), (2, 3, 180), (2, 4, 300), (2, 5, 120),
(3, 1, 350), (3, 2, 200), (3, 3, 150), (3, 4, 280), (3, 5, 100),
(4, 1, 300), (4, 2, 180), (4, 3, 140), (4, 4, 260), (4, 5, 90);
