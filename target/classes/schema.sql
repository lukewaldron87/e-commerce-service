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