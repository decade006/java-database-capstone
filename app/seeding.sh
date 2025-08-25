mongosh "mongodb://localhost:27017" < ./seeding.mongo
mysql -h 127.0.0.1 -P 3306 -u root -proot < ./seeding.sql
