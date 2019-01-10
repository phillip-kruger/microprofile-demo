DROP USER IF EXISTS 'membershipuser'@'localhost';
DROP DATABASE IF EXISTS membership;
CREATE DATABASE membership;
CREATE USER 'membershipuser'@'localhost' IDENTIFIED BY 'membershipuser';
GRANT ALL PRIVILEGES ON membership.* TO 'membershipuser'@'localhost';
FLUSH PRIVILEGES;

