-- MySQL database dump for m1 service

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS tartifasbd;

-- Usar la base de datos
USE tartifasbd;

-- Eliminar tabla si existe para evitar conflictos
DROP TABLE IF EXISTS tariffs;

-- Crear tabla de tarifas
CREATE TABLE tariffs (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         laps INT NOT NULL,
                         max_minutes INT NOT NULL,
                         regular_price DOUBLE NOT NULL,
                         total_duration INT NOT NULL,
                         PRIMARY KEY (id)
);

-- Insertar datos iniciales
INSERT INTO tariffs (laps, max_minutes, regular_price, total_duration) VALUES
                                                                           (10, 10, 15000.0, 30),
                                                                           (15, 15, 20000.0, 35),
                                                                           (20, 20, 25000.0, 40);