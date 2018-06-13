CREATE TABLE app_user (
       id SERIAL PRIMARY KEY,
        -- email max length reference https://stackoverflow.com/a/574698
       email VARCHAR(255) NOT NULL,
       token TEXT,
       token_exp INTEGER,
       created_at timestamptz DEFAULT now() NOT NULL
);
