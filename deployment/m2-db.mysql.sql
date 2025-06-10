-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS desctpersona;

-- Usar la base de datos
USE desctpersona;

-- Eliminar la tabla si existe
DROP TABLE IF EXISTS desctnumber;

-- Crear tabla según la entidad DesctNumberEntity
CREATE TABLE desctnumber (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             minpersonas INT NOT NULL,
                             maxpersonas INT NOT NULL,
                             porcentajedesct DOUBLE NOT NULL,
                             PRIMARY KEY (id)
);

-- Insertar datos según la lógica de calculateGroupSizeDiscount
INSERT INTO desctnumber (minpersonas, maxpersonas, porcentajedesct) VALUES (3, 5, 0.10);
INSERT INTO desctnumber (minpersonas, maxpersonas, porcentajedesct) VALUES (6, 10, 0.20);
INSERT INTO desctnumber (minpersonas, maxpersonas, porcentajedesct) VALUES (11, 15, 0.30);