COPY Users
FROM '/home/csmajs/sbala025/CS166Project/data/users.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE users_userID_seq RESTART 101;

COPY Store
FROM '/home/csmajs/sbala025/CS166Project/data/stores.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Product
FROM '/home/csmajs/sbala025/CS166Project/data/products.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Warehouse
FROM '/home/csmajs/sbala025/CS166Project/data/warehouse.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Orders
FROM '/home/csmajs/sbala025/CS166Project/data/orders.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE orders_orderNumber_seq RESTART 501;


COPY ProductSupplyRequests
FROM '/home/csmajs/sbala025/CS166Project/data/productSupplyRequests.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productsupplyrequests_requestNumber_seq RESTART 11;

COPY ProductUpdates
FROM '/home/csmajs/sbala025/CS166Project/data/productUpdates.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productupdates_updateNumber_seq RESTART 51;
