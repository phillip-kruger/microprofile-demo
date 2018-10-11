CREATE DATABASE membership;
CREATE USER 'payara'@'localhost' IDENTIFIED BY 'payara';
GRANT ALL PRIVILEGES ON membership.* TO 'payara'@'localhost';
FLUSH PRIVILEGES;

