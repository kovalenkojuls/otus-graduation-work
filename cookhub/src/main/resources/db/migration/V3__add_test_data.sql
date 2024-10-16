-- Все пароли "p"
INSERT INTO cookhub_user (activation_code, active, email, password, username)
VALUES
    (NULL, TRUE, 'masha@cookhub.ru', '$2a$10$sEWHA9uIOBAZ8GL0faSEMOxs47xMrr3aD0upaiFsUSbfx5OlrxZuq', 'Masha'),
    (NULL, TRUE, 'dasha@cookhub.ru', '$2a$10$sEWHA9uIOBAZ8GL0faSEMOxs47xMrr3aD0upaiFsUSbfx5OlrxZuq', 'Dasha'),
    (NULL, TRUE, 'sasha@cookhub.ru', '$2a$10$sEWHA9uIOBAZ8GL0faSEMOxs47xMrr3aD0upaiFsUSbfx5OlrxZuq', 'Sasha'),
    (NULL, TRUE, 'vasya@cookhub.ru', '$2a$10$sEWHA9uIOBAZ8GL0faSEMOxs47xMrr3aD0upaiFsUSbfx5OlrxZuq', 'Vasya');

INSERT INTO cookhub_user_role (cookhub_user_id, role)
VALUES
    (2, 'USER'),
    (3, 'USER'),
    (4, 'USER'),
    (5, 'USER');

INSERT INTO recipe (category, filename, text, user_id, created_at)
VALUES
    ('SOUPS', 'cat_sup.jpg', 'Вкусный суп с овощами', 1, CURRENT_TIMESTAMP),
    ('SALADS', 'cat_salad.jpg', 'Свежий салат с тунцом', 5, CURRENT_TIMESTAMP),
    ('MAIN_COURSES', 'cat_kotleta.jpg', 'Паста с томатным соусом', 4, CURRENT_TIMESTAMP),
    ('SOUPS', 'cat_sup.jpg', 'Классический куриный бульон', 2, CURRENT_TIMESTAMP),
    ('DESSERTS', 'cat_cake.jpg', 'Шоколадный торт', 1, CURRENT_TIMESTAMP),
    ('SALADS', 'cat_salad.jpg', 'Греческий салат', 3, CURRENT_TIMESTAMP),
    ('MAIN_COURSES', NULL, 'Запеченная курица с картофелем', 3, CURRENT_TIMESTAMP),
    ('DESSERTS', 'cat_cake.jpg', 'Фруктовый салат', 2, CURRENT_TIMESTAMP),
    ('MAIN_COURSES', 'cat_kotleta.jpg', 'Рыба под лимонным соусом', 1, CURRENT_TIMESTAMP);

