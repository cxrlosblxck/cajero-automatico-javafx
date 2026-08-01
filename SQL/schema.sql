CREATE DATABASE cajero_automatico;
USE cajero_automatico;

-- =============================================
-- Tabla: clientes
-- =============================================
CREATE TABLE IF NOT EXISTS clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    telefono VARCHAR(20),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Tabla: cuentas
-- =============================================
CREATE TABLE IF NOT EXISTS cuentas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id INT NOT NULL,
    tipo_cuenta VARCHAR(50) NOT NULL DEFAULT 'debito',
    nombre_cuenta VARCHAR(100) DEFAULT 'Cuenta Axios',
    saldo DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    token VARCHAR(50) NOT NULL UNIQUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Tabla: movimientos
-- =============================================
CREATE TABLE IF NOT EXISTS movimientos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cuenta_id INT NOT NULL,
    tipo_movimiento ENUM('RETIRO', 'DEPOSITO', 'TRANSFERENCIA_ENVIADA', 'TRANSFERENCIA_RECIBIDA', 'RECARGA', 'PAGO_SERVICIO') NOT NULL,
    cantidad DECIMAL(15,2) NOT NULL,
    saldo_anterior DECIMAL(15,2) NOT NULL,
    saldo_posterior DECIMAL(15,2) NOT NULL,
    descripcion VARCHAR(255),
    cuenta_destino_id INT NULL,
    referencia VARCHAR(50) NOT NULL,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cuenta_id) REFERENCES cuentas(id) ON DELETE CASCADE,
    FOREIGN KEY (cuenta_destino_id) REFERENCES cuentas(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- DATOS DE EJEMPLO
-- =============================================

-- Insertar un cliente de ejemplo
INSERT INTO clientes (nombre, apellido_paterno, apellido_materno, email, telefono)
VALUES ('Javier', 'Pérez', 'Gómez', 'javier@example.com', '5512345678');

-- Insertar una cuenta asociada a ese cliente con token de prueba
INSERT INTO cuentas (cliente_id, tipo_cuenta, nombre_cuenta, saldo, token)
VALUES (1, 'nomina', 'Cuenta de nómina', 1500.00, '1234567890');

-- Insertar movimientos de ejemplo
INSERT INTO movimientos (cuenta_id, tipo_movimiento, cantidad, saldo_anterior, saldo_posterior, descripcion, referencia)
VALUES (1, 'DEPOSITO', 500.00, 1000.00, 1500.00, 'Depósito inicial', 'DEP20240101001');

INSERT INTO movimientos (cuenta_id, tipo_movimiento, cantidad, saldo_anterior, saldo_posterior, descripcion, referencia)
VALUES (1, 'RETIRO', 100.00, 1500.00, 1400.00, 'Retiro en cajero', 'RET20240101002');

INSERT INTO movimientos (cuenta_id, tipo_movimiento, cantidad, saldo_anterior, saldo_posterior, descripcion, referencia)
VALUES (1, 'TRANSFERENCIA_ENVIADA', 200.00, 1400.00, 1200.00, 'Transferencia a cuenta 2', 'TRF20240101003');

INSERT INTO movimientos (cuenta_id, tipo_movimiento, cantidad, saldo_anterior, saldo_posterior, descripcion, referencia)
VALUES (1, 'RECARGA', 50.00, 1200.00, 1150.00, 'Recarga telefónica a 5512345678', 'REC20240101004');

INSERT INTO movimientos (cuenta_id, tipo_movimiento, cantidad, saldo_anterior, saldo_posterior, descripcion, referencia)
VALUES (1, 'PAGO_SERVICIO', 150.00, 1150.00, 1000.00, 'Pago de servicio - 123456789012', 'PAG20240101005');

-- =============================================
-- FIN DEL SCRIPT
-- =============================================
