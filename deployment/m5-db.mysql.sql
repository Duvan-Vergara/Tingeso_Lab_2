-- Crear base de datos si no existe
CREATE DATABASE IF NOT EXISTS reserve;

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
);

-- Crear tabla de reservas
CREATE TABLE reserves (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          reserveday DATE NOT NULL,
                          begin TIME NOT NULL,
                          finish TIME NOT NULL,
                          tariff_id BIGINT NOT NULL,
                          final_price DOUBLE NOT NULL,
                          PRIMARY KEY (id)
);

-- Tabla intermedia para la relación muchos a muchos entre reservas y usuarios
CREATE TABLE reserves_users (
                                reserve_id BIGINT NOT NULL,
                                user_id BIGINT NOT NULL,
                                PRIMARY KEY (reserve_id, user_id),
                                FOREIGN KEY (reserve_id) REFERENCES reserves(id) ON DELETE CASCADE,
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);