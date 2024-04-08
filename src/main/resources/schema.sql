
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    login VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255) UNIQUE NOT NULL,
    amount INTEGER NOT NULL,
    price DOUBLE (10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS sales (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    user_id INTEGER NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    discount INTEGER NOT NULL,
    total DOUBLE (10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS sales_items (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    sale_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    amount INTEGER NOT NULL,
    price DOUBLE(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME,
    FOREIGN KEY (sale_id) REFERENCES sales(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS returns (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    total DOUBLE(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS returns_items (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    return_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    amount INTEGER NOT NULL,
    price DOUBLE (10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME,
    FOREIGN KEY (return_id) REFERENCES returns(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS exchanges (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    total DOUBLE(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS old_exchanges_items (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    exchange_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    amount INTEGER NOT NULL,
    price DOUBLE(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME,
    FOREIGN KEY (exchange_id) REFERENCES exchanges(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS new_exchanges_items (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    exchange_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    amount INTEGER NOT NULL,
    price DOUBLE(10, 2) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME,
    FOREIGN KEY (exchange_id) REFERENCES exchanges(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS customers (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50) UNIQUE NOT NULL,
    purchases INTEGER NOT NULL,
    birth_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME
);

CREATE TABLE IF NOT EXISTS addresses (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    customer_id INTEGER NOT NULL,
    house_number MEDIUMINT NOT NULL,
    street VARCHAR(255) NOT NULL,
    neighborhood VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    state VARCHAR(2) NOT NULL,
    created_at DATETIME NOT NULL,
    deleted_at DATETIME,
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);