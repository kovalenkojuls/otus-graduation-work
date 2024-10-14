-- Создание пользователя (username = admin, password = admin)
INSERT INTO cookhub_user(id, active, username, password, email)
VALUES (1, true, 'admin', '$2a$10$WmgKrFyxw3U/4ELpq6M00eIwrWyC/2d4wA90ZI7FCrbiJQG776U32', 'admin@cookhub.ru');

ALTER SEQUENCE cookhub_user_id_seq RESTART WITH 2;

INSERT INTO cookhub_user_role(cookhub_user_id, role)
VALUES (1, 'USER'), (1, 'ADMIN');