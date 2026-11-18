-- Runs automatically the FIRST time the MySQL container starts (empty data volume)

CREATE DATABASE IF NOT EXISTS catalog_db;
CREATE DATABASE IF NOT EXISTS order_db;
CREATE DATABASE IF NOT EXISTS rating_db;
CREATE DATABASE IF NOT EXISTS notification_db;

CREATE USER IF NOT EXISTS 'catalog_user'@'%'      IDENTIFIED BY 'catalog_pass';
CREATE USER IF NOT EXISTS 'order_user'@'%'        IDENTIFIED BY 'order_pass';
CREATE USER IF NOT EXISTS 'rating_user'@'%'       IDENTIFIED BY 'rating_pass';
CREATE USER IF NOT EXISTS 'notification_user'@'%' IDENTIFIED BY 'notification_pass';

GRANT ALL PRIVILEGES ON catalog_db.*      TO 'catalog_user'@'%';
GRANT ALL PRIVILEGES ON order_db.*        TO 'order_user'@'%';
GRANT ALL PRIVILEGES ON rating_db.*       TO 'rating_user'@'%';
GRANT ALL PRIVILEGES ON notification_db.* TO 'notification_user'@'%';

FLUSH PRIVILEGES;