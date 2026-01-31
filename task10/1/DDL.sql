CREATE TABLE Product (
    maker VARCHAR(50),
    model VARCHAR(50) PRIMARY KEY,
    type VARCHAR(50) CHECK (type IN ('PC', 'Laptop', 'Printer'))
);

CREATE TABLE PC (
    code SERIAL PRIMARY KEY,
    model VARCHAR(50) REFERENCES Product(model),
    speed INTEGER,
    ram INTEGER,
    hd NUMERIC(5,1),
    cd VARCHAR(10),
    price NUMERIC(10,2)
);

CREATE TABLE Laptop (
    code SERIAL PRIMARY KEY,
    model VARCHAR(50) REFERENCES Product(model),
    speed INTEGER,
    ram INTEGER,
    hd NUMERIC(5,1),
    screen NUMERIC(4,1),
    price NUMERIC(10,2)
);

CREATE TABLE Printer (
    code SERIAL PRIMARY KEY,
    model VARCHAR(50) REFERENCES Product(model),
    color CHAR(1) CHECK (color IN ('y', 'n')),
    type VARCHAR(50) CHECK (type IN ('Laser', 'Jet', 'Matrix')),
    price NUMERIC(10,2)
);