CREATE TABLE todo_list (
       id SERIAL PRIMARY KEY,
       name VARCHAR(100) NOT NULL);

INSERT INTO todo_list (id, name) VALUES (0, 'default');
ALTER TABLE todo
      ADD COLUMN list_id INTEGER NOT NULL DEFAULT 0 REFERENCES  todo_list(id);
