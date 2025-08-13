CREATE TABLE client (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    profit NUMERIC NOT NULL,
    active BOOLEAN NOT NULL,
    last_activity_timestamp TIMESTAMP NOT NULL
);

CREATE TABLE admin (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    last_activity_timestamp TIMESTAMP NOT NULL
);

CREATE TABLE client_auth_token (
    token VARCHAR(255) NOT NULL PRIMARY KEY,
    expiration_date TIMESTAMP NOT NULL,
    client_id INTEGER NOT NULL
);

CREATE TABLE admin_auth_token (
    token VARCHAR(255) NOT NULL PRIMARY KEY,
    expiration_date TIMESTAMP NOT NULL,
    admin_id INTEGER NOT NULL
);

CREATE TABLE order_data (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    supplier_id INTEGER NOT NULL,
    consumer_id INTEGER,
    price NUMERIC NOT NULL,
    created_at TIMESTAMP NOT NULL,
    closed_at TIMESTAMP,
    db_write_latency NUMERIC NOT NULL
);

ALTER TABLE client_auth_token ADD CONSTRAINT client_auth_token_client_idfk FOREIGN KEY (client_id) REFERENCES client(id);

ALTER TABLE admin_auth_token ADD CONSTRAINT admin_auth_token_client_idfk FOREIGN KEY (admin_id) REFERENCES admin(id);

ALTER TABLE order_data ADD CONSTRAINT order_supplier_idfk FOREIGN KEY (supplier_id) REFERENCES client(id);
ALTER TABLE order_data ADD CONSTRAINT order_consumer_idfk FOREIGN KEY (consumer_id) REFERENCES client(id);
