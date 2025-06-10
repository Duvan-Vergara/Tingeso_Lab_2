-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS reserve CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar la base de datos
USE reserve;

-- Eliminar tablas si existen para evitar conflictos
DROP TABLE IF EXISTS reserves_users;
DROP TABLE IF EXISTS reserves;
DROP TABLE IF EXISTS users;

-- Crear tabla de usuarios
CREATE TABLE users (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       rut VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       lastname VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       birthdate DATE NOT NULL,
                       PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Crear tabla de reservas
CREATE TABLE reserves (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          reserveday DATE NOT NULL,
                          begin TIME NOT NULL,
                          finish TIME NOT NULL,
                          tariff_id BIGINT NOT NULL,
                          final_price DOUBLE NOT NULL,
                          PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla intermedia para la relación muchos a muchos entre reservas y usuarios
CREATE TABLE reserves_users (
                                reserve_id BIGINT NOT NULL,
                                user_id BIGINT NOT NULL,
                                PRIMARY KEY (reserve_id, user_id),
                                FOREIGN KEY (reserve_id) REFERENCES reserves(id) ON DELETE CASCADE,
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Poblar tabla 'users'
INSERT INTO users (rut, name, lastname, email, birthdate) VALUES
('12345678-9', 'José', 'Muñoz', 'jose.munoz@email.com', '1990-05-15'),
('23456789-0', 'María', 'García', 'maria.garcia@email.com', '1985-08-22'),
('34567890-1', 'Álvaro', 'Peña', 'alvaro.pena@email.com', '1992-12-03'),
('45678901-2', 'Anaís', 'López', 'anais.lopez@email.com', '2000-01-30'),
('56789012-3', 'René', 'Sánchez', 'rene.sanchez@email.com', '1998-07-19'),
('67890123-4', 'Camila', 'Espiñeira', 'camila.espineira@email.com', '1995-03-10'),
('78901234-5', 'Julián', 'Pérez', 'julian.perez@email.com', '1997-11-25'),
('89012345-6', 'Lucía', 'Quiñones', 'lucia.quinones@email.com', '2002-04-14'),
('90123456-7', 'Tomás', 'Gutiérrez', 'tomas.gutierrez@email.com', '1993-09-08'),
('01234567-8', 'Paula', 'Ríos', 'paula.rios@email.com', '1999-06-21');

-- Poblar tabla 'reserves'
INSERT INTO reserves (reserveday, begin, finish, tariff_id, final_price) VALUES
('2024-12-01', '10:00', '12:00', 1, 50000.00),   -- id 1
('2024-12-05', '15:00', '17:00', 2, 75000.00),   -- id 2
('2024-12-10', '18:00', '20:00', 3, 90000.00),   -- id 3
('2024-12-15', '09:00', '11:00', 1, 60000.00),   -- id 4
('2024-12-20', '14:00', '16:00', 2, 80000.00),   -- id 5
('2024-12-22', '17:00', '19:00', 3, 95000.00),   -- id 6
('2024-12-25', '11:00', '13:00', 1, 55000.00),   -- id 7
('2024-12-27', '16:00', '18:00', 2, 78000.00),   -- id 8
('2024-12-29', '19:00', '21:00', 3, 99000.00),   -- id 9
('2025-01-02', '08:00', '10:00', 1, 52000.00);   -- id 10

-- Poblar tabla 'reserves_users'
-- Reserva 1: José, María, Álvaro
INSERT INTO reserves_users (reserve_id, user_id) VALUES (1, 1);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (1, 2);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (1, 3);

-- Reserva 2: Anaís, René, Camila, Julián
INSERT INTO reserves_users (reserve_id, user_id) VALUES (2, 4);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (2, 5);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (2, 6);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (2, 7);

-- Reserva 3: Lucía, Tomás, Paula
INSERT INTO reserves_users (reserve_id, user_id) VALUES (3, 8);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (3, 9);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (3, 10);

-- Reserva 4: José, Anaís
INSERT INTO reserves_users (reserve_id, user_id) VALUES (4, 1);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (4, 4);

-- Reserva 5: María, René, Lucía
INSERT INTO reserves_users (reserve_id, user_id) VALUES (5, 2);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (5, 5);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (5, 8);

-- Reserva 6: Álvaro, Camila, Tomás
INSERT INTO reserves_users (reserve_id, user_id) VALUES (6, 3);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (6, 6);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (6, 9);

-- Reserva 7: Julián, Paula
INSERT INTO reserves_users (reserve_id, user_id) VALUES (7, 7);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (7, 10);

-- Reserva 8: José, Lucía, Tomás
INSERT INTO reserves_users (reserve_id, user_id) VALUES (8, 1);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (8, 8);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (8, 9);

-- Reserva 9: María, Anaís, Camila, Paula
INSERT INTO reserves_users (reserve_id, user_id) VALUES (9, 2);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (9, 4);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (9, 6);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (9, 10);

-- Reserva 10: René, Julián, Paula
INSERT INTO reserves_users (reserve_id, user_id) VALUES (10, 5);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (10, 7);
INSERT INTO reserves_users (reserve_id, user_id) VALUES (10, 10);