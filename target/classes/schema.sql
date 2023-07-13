CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255),
    price NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS basket (
    id SERIAL PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS basket_item(
    id SERIAL PRIMARY KEY,
    product_id INTEGER REFERENCES product (id),
    product_count INTEGER,
    basket_id INTEGER REFERENCES basket (id)
);

CREATE TABLE IF NOT EXISTS customer_order (
    id SERIAL PRIMARY KEY,
    status VARCHAR(255),
    name VARCHAR(255),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS order_item(
    id SERIAL PRIMARY KEY,
    product_id INTEGER REFERENCES product (id),
    product_count INTEGER,
    order_id INTEGER REFERENCES customer_order (id)
);