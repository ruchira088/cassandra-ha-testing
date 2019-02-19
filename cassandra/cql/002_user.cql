CREATE TABLE cassandra_ha.user (
  id UUID PRIMARY KEY,
  created_at TIMESTAMP,
  first_name TEXT,
  last_name TEXT,
  age INT,
  email TEXT,
  is_married BOOLEAN
);

CREATE MATERIALIZED VIEW cassandra_ha.user_by_email
AS SELECT id, created_at, first_name, last_name, age, email, is_married
FROM cassandra_ha.user
WHERE email IS NOT NULL AND id IS NOT NULL
PRIMARY KEY (email, id);

CREATE MATERIALIZED VIEW cassandra_ha.user_by_age
AS SELECT id, created_at, first_name, last_name, age, email, is_married
FROM cassandra_ha.user
WHERE age IS NOT NULL AND id IS NOT NULL
PRIMARY KEY (age, id);

CREATE MATERIALIZED VIEW cassandra_ha.user_by_first_name
AS SELECT id, created_at, first_name, last_name, age, email, is_married
FROM cassandra_ha.user
WHERE first_name IS NOT NULL AND id IS NOT NULL
PRIMARY KEY (first_name, id);