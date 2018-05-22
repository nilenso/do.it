CREATE TABLE todo (
       id SERIAL PRIMARY KEY,
       body TEXT,
       created_at timestamptz DEFAULT now()
);
