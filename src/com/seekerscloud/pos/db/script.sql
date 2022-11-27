SHOW DATABASES;

DROP DATABASE IF EXISTS  thogakade;
CREATE DATABASE IF NOT EXISTS  thogakade;
SHOW DATABASES ;
USE thogakade;
#===========

CREATE TABLE IF NOT EXISTS customer(
    id VARCHAR(45),
    name VARCHAR(45),
    address TEXT,
    salary DOUBLE,
    CONSTRAINT PRIMARY KEY(id)

);

#======================

CREATE TABLE IF NOT EXISTS item(
       code VARCHAR(45),
       description VARCHAR(45),
       unitPrice DOUBLE,
       qtyOnHand INT,
       CONSTRAINT PRIMARY KEY(code)

);

CREATE TABLE IF NOT EXISTS `order`(
    orderId VARCHAR(45),
    date VARCHAR(250),
    totalCost DOUBLE,
    customer VARCHAR(45) ,
    CONSTRAINT PRIMARY KEY(orderId),
    CONSTRAINT FOREIGN KEY(customer) REFERENCES customer(id) ON DELETE CASCADE ON UPDATE CASCADE

    );


CREATE TABLE IF NOT EXISTS `order_details`(
      itemCode VARCHAR(45),
      orderId VARCHAR(45),
      unitPrice DOUBLE,
      qty INT ,
      CONSTRAINT PRIMARY KEY(orderId , itemCode),
      CONSTRAINT FOREIGN KEY(itemCode) REFERENCES item(code) ON DELETE CASCADE ON UPDATE CASCADE,
      CONSTRAINT FOREIGN KEY(orderId) REFERENCES `order`(orderId) ON DELETE CASCADE ON UPDATE CASCADE

);

