-- name: retrieve-all-todo-query
-- Returns a list of all todos
SELECT * FROM todo;

-- name: create-todo-query!
-- Creates a todo
INSERT INTO todo
(body)
VALUES (:body);
