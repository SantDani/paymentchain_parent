#!/bin/bash
set -e

echo "Creating database springboot in $POSTGRES_DB"

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER springboot WITH PASSWORD 'qwerty';
    CREATE DATABASE springboot;
    GRANT ALL PRIVILEGES ON DATABASE springboot TO springboot;
    \connect springboot
    GRANT ALL PRIVILEGES ON SCHEMA public TO springboot;

EOSQL

echo "Database springboot created successfully"
