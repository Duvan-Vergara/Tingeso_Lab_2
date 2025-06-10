-- MySQL database dump for m4 service

SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;

-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS tartiffspecialbd CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE tartiffspecialbd;

-- Eliminar tablas si existen para evitar conflictos
DROP TABLE IF EXISTS special_days;
DROP TABLE IF EXISTS tariff_special;

-- Crear tabla de días especiales
CREATE TABLE special_days (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              date DATE NOT NULL,
                              description VARCHAR(255) NOT NULL,
                              PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Crear tabla de tarifas especiales
CREATE TABLE tariff_special (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                weekend_discount_percentage DOUBLE NOT NULL,
                                holiday_increase_percentage DOUBLE NOT NULL,
                                PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Poblar tabla 'special_days' con fechas actualizadas a 2024
INSERT INTO special_days (date, description) VALUES ('2024-01-01', 'Año Nuevo');
INSERT INTO special_days (date, description) VALUES ('2024-03-29', 'Viernes Santo');
INSERT INTO special_days (date, description) VALUES ('2024-03-30', 'Sábado Santo');
INSERT INTO special_days (date, description) VALUES ('2024-05-01', 'Día del Trabajo');
INSERT INTO special_days (date, description) VALUES ('2024-05-21', 'Día de las Glorias Navales');
INSERT INTO special_days (date, description) VALUES ('2024-06-20', 'Día Nacional de los Pueblos Indígenas');
INSERT INTO special_days (date, description) VALUES ('2024-06-29', 'San Pedro y San Pablo');
INSERT INTO special_days (date, description) VALUES ('2024-07-16', 'Día de la Virgen del Carmen');
INSERT INTO special_days (date, description) VALUES ('2024-08-15', 'Asunción de la Virgen');
INSERT INTO special_days (date, description) VALUES ('2024-09-18', 'Independencia Nacional');
INSERT INTO special_days (date, description) VALUES ('2024-09-19', 'Día de las Glorias del Ejército');
INSERT INTO special_days (date, description) VALUES ('2024-10-12', 'Encuentro de Dos Mundos');
INSERT INTO special_days (date, description) VALUES ('2024-10-31', 'Día de las Iglesias Evangélicas y Protestantes');
INSERT INTO special_days (date, description) VALUES ('2024-11-01', 'Día de Todos los Santos');
INSERT INTO special_days (date, description) VALUES ('2024-12-08', 'Inmaculada Concepción');
INSERT INTO special_days (date, description) VALUES ('2024-12-25', 'Navidad');

-- Insertar datos iniciales para tarifas especiales
INSERT INTO tariff_special (weekend_discount_percentage, holiday_increase_percentage) VALUES
    (10.0, 15.0);