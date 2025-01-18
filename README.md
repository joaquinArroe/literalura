# literalura
Implementacion de un consumidor de apis de una biblioteca

Mediante el presente documento podrás utilizar diferentes apis, antes de utilizar el programa, procura tener las siguientes tablas en tu base de datos y estar conectado


CREATE TABLE authors (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    birth_year INT,
    death_year INT
);

CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    language VARCHAR(10),
    download_count INT,
    author_id INT REFERENCES authors(id) ON DELETE CASCADE
);
