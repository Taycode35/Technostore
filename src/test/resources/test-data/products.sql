-- Création de la base de données des tests. --

-- Drop table if exists (pour tests répétés)
DROP TABLE IF EXISTS products CASCADE;

-- Création de la table products
CREATE TABLE products
(
    id            SERIAL PRIMARY KEY,
    product_type  VARCHAR(100) NOT NULL,
    brand         VARCHAR(100) NOT NULL,
    model         VARCHAR(100) NOT NULL,
    price         NUMERIC(10, 2) NOT NULL CHECK (price > 0),
    product_year  INT NOT NULL CHECK (product_year > 1900 AND
                                      product_year <= EXTRACT(YEAR FROM CURRENT_DATE))
);

-- Création des indexes pour la liste déroulante.
CREATE INDEX idx_products_type ON products(product_type);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_year ON products(product_year);

-- Insertion des données de test.
INSERT INTO products (product_type, brand, model, price, product_year)
VALUES
    ('SMARTPHONE', 'Apple', 'iPhone 15 Pro', 1299.99, 2023),
    ('SMARTPHONE', 'Samsung', 'Galaxy S24 Ultra', 1199.50, 2024),
    ('SMARTPHONE', 'Google', 'Pixel 8 Pro', 999.00, 2023),
    ('TABLET', 'Apple', 'iPad Pro 12.9', 1099.00, 2022),
    ('TABLET', 'Samsung', 'Galaxy Tab S9', 899.99, 2023),
    ('TABLET', 'Microsoft', 'Surface Pro 9', 1299.00, 2023),
    ('LAPTOP', 'Dell', 'XPS 13', 1399.00, 2024),
    ('LAPTOP', 'HP', 'Spectre x360', 1249.00, 2023),
    ('LAPTOP', 'Lenovo', 'ThinkPad X1 Carbon', 1599.00, 2024),
    ('LAPTOP', 'Apple', 'MacBook Pro 14', 1999.00, 2023),
    ('SMARTWATCH', 'Apple', 'Watch Series 9', 399.00, 2023),
    ('SMARTWATCH', 'Samsung', 'Galaxy Watch 6', 349.99, 2023),
    ('HEADPHONES', 'Sony', 'WH-1000XM5', 399.00, 2023),
    ('HEADPHONES', 'Apple', 'AirPods Pro 2', 249.00, 2023);
