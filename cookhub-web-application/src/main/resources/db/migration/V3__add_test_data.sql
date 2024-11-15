-- Все пароли - "p"
INSERT INTO cookhub_user (activation_code, active, email, password, username)
VALUES
    (NULL, TRUE, 'masha@cookhub.ru', '$2a$10$sEWHA9uIOBAZ8GL0faSEMOxs47xMrr3aD0upaiFsUSbfx5OlrxZuq', 'masha'),
    (NULL, TRUE, 'dasha@cookhub.ru', '$2a$10$sEWHA9uIOBAZ8GL0faSEMOxs47xMrr3aD0upaiFsUSbfx5OlrxZuq', 'dasha'),
    (NULL, TRUE, 'sasha@cookhub.ru', '$2a$10$sEWHA9uIOBAZ8GL0faSEMOxs47xMrr3aD0upaiFsUSbfx5OlrxZuq', 'sasha'),
    (NULL, TRUE, 'vasya@cookhub.ru', '$2a$10$sEWHA9uIOBAZ8GL0faSEMOxs47xMrr3aD0upaiFsUSbfx5OlrxZuq', 'vasya');

INSERT INTO cookhub_user_role (cookhub_user_id, role)
VALUES
    (2, 'USER'),
    (3, 'USER'),
    (4, 'USER'),
    (5, 'USER');

INSERT INTO recipe (category, filename, text, user_id, created_at)
VALUES
    ('SOUPS', 'sup.jpg', 'Овощи нарезаем кубиками, обжариваем лук и морковь, добавляем картофель, заливаем бульоном, варим до готовности. В конце добавляем зелень.', 1, CURRENT_TIMESTAMP),
    ('SALADS', 'salad.jpg', 'Тунца разминаем вилкой, смешиваем с нарезанными овощами, заправляем оливковым маслом и лимонным соком.', 5, CURRENT_TIMESTAMP),
    ('MAIN_COURSES', NULL, 'Курицу натираем специями, запекаем в духовке с картофелем до готовности.', 3, CURRENT_TIMESTAMP),
    ('MAIN_COURSES', 'main_course.jpg', 'Отвариваем пасту, готовим томатный соус, смешиваем пасту с соусом, посыпаем сыром.', 4, CURRENT_TIMESTAMP),
    ('SOUPS', 'sup_2.jpg', 'Курицу варим до готовности, добавляем овощи, специи, варим до готовности овощей.', 2, CURRENT_TIMESTAMP),
    ('DESSERTS', 'cake.jpg', 'Взбиваем яйца с сахаром, добавляем муку, какао, выпекаем в духовке, украшаем кремом.', 1, CURRENT_TIMESTAMP),
    ('SALADS', 'salad_2.jpg', 'Огурцы, помидоры, лук, оливки, фету нарезаем, заправляем оливковым маслом и бальзамическим уксусом.', 3, CURRENT_TIMESTAMP),
    ('DESSERTS', 'cake_2.jpg', 'Нарезаем фрукты, смешиваем с йогуртом или сметаной, украшаем мятой.', 2, CURRENT_TIMESTAMP),
    ('MAIN_COURSES', 'main_course_2.jpg', 'Обжарьте стейк по 2-3 минуты с каждой стороны, затем доведите до прожарки под крышкой. Дайте отдохнуть перед подачей.', 1, CURRENT_TIMESTAMP),
    ('SOUPS', 'sup.jpg', 'Овощи нарезаем кубиками, обжариваем лук и морковь, добавляем картофель, заливаем бульоном, варим до готовности. В конце добавляем зелень.', 1, CURRENT_TIMESTAMP),
    ('SALADS', 'salad.jpg', 'Тунца разминаем вилкой, смешиваем с нарезанными овощами, заправляем оливковым маслом и лимонным соком.', 5, CURRENT_TIMESTAMP),
    ('MAIN_COURSES', NULL, 'Курицу натираем специями, запекаем в духовке с картофелем до готовности.', 3, CURRENT_TIMESTAMP),
    ('MAIN_COURSES', 'main_course.jpg', 'Отвариваем пасту, готовим томатный соус, смешиваем пасту с соусом, посыпаем сыром.', 4, CURRENT_TIMESTAMP),
    ('SOUPS', 'sup_2.jpg', 'Курицу варим до готовности, добавляем овощи, специи, варим до готовности овощей.', 2, CURRENT_TIMESTAMP),
    ('DESSERTS', 'cake.jpg', 'Взбиваем яйца с сахаром, добавляем муку, какао, выпекаем в духовке, украшаем кремом.', 1, CURRENT_TIMESTAMP),
    ('SALADS', 'salad_2.jpg', 'Огурцы, помидоры, лук, оливки, фету нарезаем, заправляем оливковым маслом и бальзамическим уксусом.', 3, CURRENT_TIMESTAMP),
    ('DESSERTS', 'cake_2.jpg', 'Нарезаем фрукты, смешиваем с йогуртом или сметаной, украшаем мятой.', 2, CURRENT_TIMESTAMP),
    ('MAIN_COURSES', 'main_course_2.jpg', 'Обжарьте стейк по 2-3 минуты с каждой стороны, затем доведите до прожарки под крышкой. Дайте отдохнуть перед подачей.', 1, CURRENT_TIMESTAMP);

INSERT INTO cookhub_user_follower (follower_id, following_id) VALUES
    (1, 3), (1, 5),
    (2, 1), (2, 4),
    (3, 2),
    (4, 5), (4, 1), (4, 3),
    (5, 1), (5, 2);

INSERT INTO recipe_like (recipe_id, user_id) VALUES
    (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
    (2, 1), (2, 3), (2, 5),
    (3, 2), (3, 4),
    (4, 1), (4, 2), (4, 3),
    (5, 1), (5, 4), (5, 5),
    (6, 2), (6, 3), (6, 4),
    (7, 1), (7, 2), (7, 5),
    (8, 3), (8, 4), (8, 5),
    (9, 1), (9, 4),
    (10, 2), (10, 3),
    (11, 1), (11, 2), (11, 5),
    (12, 3), (12, 4),
    (13, 1), (13, 2), (13, 4), (13, 5),
    (14, 2), (14, 3),
    (15, 1), (15, 3), (15, 4),
    (16, 2), (16, 5),
    (17, 1), (17, 4), (17, 5),
    (18, 2), (18, 3), (18, 4);


