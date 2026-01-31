SELECT model, speed, hd 
FROM PC 
WHERE price < 500;

SELECT DISTINCT maker 
FROM Product 
WHERE type = 'Printer';

SELECT model, ram, screen 
FROM Laptop 
WHERE price > 1000;

SELECT * 
FROM Printer 
WHERE color = 'y';

SELECT model, speed, hd 
FROM PC 
WHERE cd IN ('12x', '24x') AND price < 600;

SELECT p.maker, l.speed 
FROM Laptop l
JOIN Product p ON l.model = p.model 
WHERE l.hd >= 100;

SELECT p.model, 
       COALESCE(pc.price, lp.price, pr.price) as price
FROM Product p
LEFT JOIN PC pc ON p.model = pc.model AND p.type = 'PC'
LEFT JOIN Laptop lp ON p.model = lp.model AND p.type = 'Laptop'
LEFT JOIN Printer pr ON p.model = pr.model AND p.type = 'Printer'
WHERE p.maker = 'B';

SELECT DISTINCT maker 
FROM Product 
WHERE type = 'PC' 
  AND maker NOT IN (
    SELECT DISTINCT maker 
    FROM Product 
    WHERE type = 'Laptop'
  );

SELECT DISTINCT p.maker 
FROM Product p
JOIN PC ON p.model = PC.model 
WHERE PC.speed >= 450;

SELECT model, price 
FROM Printer 
WHERE price = (SELECT MAX(price) FROM Printer);

SELECT AVG(speed) as avg_speed 
FROM PC;

SELECT AVG(speed) as avg_speed 
FROM Laptop 
WHERE price > 1000;

SELECT AVG(pc.speed) as avg_speed 
FROM PC pc
JOIN Product p ON pc.model = p.model 
WHERE p.maker = 'A';

SELECT speed, AVG(price) as avg_price 
FROM PC 
GROUP BY speed 
ORDER BY speed;

SELECT hd 
FROM PC 
GROUP BY hd 
HAVING COUNT(*) >= 2;

SELECT DISTINCT p1.model as model1, p2.model as model2, p1.speed, p1.ram
FROM PC p1
JOIN PC p2 ON p1.speed = p2.speed AND p1.ram = p2.ram AND p1.model < p2.model;

SELECT p.type, l.model, l.speed 
FROM Laptop l
JOIN Product p ON l.model = p.model 
WHERE l.speed < (SELECT MIN(speed) FROM PC);

SELECT DISTINCT p.maker, pr.price 
FROM Printer pr
JOIN Product p ON pr.model = p.model 
WHERE pr.color = 'y' 
  AND pr.price = (SELECT MIN(price) FROM Printer WHERE color = 'y');

SELECT p.maker, AVG(l.screen) as avg_screen 
FROM Laptop l
JOIN Product p ON l.model = p.model 
GROUP BY p.maker;

SELECT p.maker, COUNT(*) as model_count 
FROM Product p
WHERE p.type = 'PC' 
GROUP BY p.maker 
HAVING COUNT(*) >= 3;

SELECT p.maker, MAX(pc.price) as max_price 
FROM Product p
JOIN PC pc ON p.model = pc.model 
GROUP BY p.maker;

SELECT speed, AVG(price) as avg_price 
FROM PC 
WHERE speed > 600 
GROUP BY speed 
ORDER BY speed;

SELECT DISTINCT p.maker 
FROM Product p
WHERE p.maker IN (
    SELECT DISTINCT p1.maker 
    FROM Product p1
    JOIN PC ON p1.model = PC.model 
    WHERE PC.speed >= 750
) AND p.maker IN (
    SELECT DISTINCT p2.maker 
    FROM Product p2
    JOIN Laptop l ON p2.model = l.model 
    WHERE l.speed >= 750
);

WITH AllProducts AS (
    SELECT model, price FROM PC
    UNION ALL
    SELECT model, price FROM Laptop
    UNION ALL
    SELECT model, price FROM Printer
)
SELECT model 
FROM AllProducts 
WHERE price = (SELECT MAX(price) FROM AllProducts);

WITH MinRAMPCs AS (
    SELECT model, speed 
    FROM PC 
    WHERE ram = (SELECT MIN(ram) FROM PC)
),
FastestMinRAM AS (
    SELECT model 
    FROM MinRAMPCs 
    WHERE speed = (SELECT MAX(speed) FROM MinRAMPCs)
)
SELECT DISTINCT p.maker 
FROM Product p
JOIN FastestMinRAM f ON p.model = f.model 
WHERE p.maker IN (
    SELECT DISTINCT maker 
    FROM Product 
    WHERE type = 'Printer'
);