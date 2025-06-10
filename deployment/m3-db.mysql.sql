-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS desctpersonafrect;

-- Usar la base de datos
USE desctpersonafrect;

-- Eliminar la tabla si existe
DROP TABLE IF EXISTS desctfrecu;

-- Crear tabla según la entidad DesctFrecuEntity
CREATE TABLE desctfrecu (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            minveces INT NOT NULL,
                            maxveces INT NOT NULL,
                            porcentajedesct DOUBLE NOT NULL,
                            PRIMARY KEY (id)
);

-- Insertar datos según la lógica de calculateFrequentCustomerDiscount
INSERT INTO desctfrecu (minveces, maxveces, porcentajedesct) VALUES (2, 4, 0.10);
INSERT INTO desctfrecu (minveces, maxveces, porcentajedesct) VALUES (5, 6, 0.20);
INSERT INTO desctfrecu (minveces, maxveces, porcentajedesct) VALUES (7, 9999, 0.30);