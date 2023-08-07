# java-filmorate
Template repository for Filmorate project.
Ссылка на диаграмму таблицы проекта
https://dbdiagram.io/d/64c7712902bd1c4a5ef74a8f
![alt text](https://github.com/fen1x63/java-filmorate/blob/8b2723725166887c8e7dd714f5a7230f7925fe21/DataBase.png)
1. Добавление нового фильма в базу данных:
INSERT INTO films (name, description, realise_date, duration, rating_mpa_id) VALUES ('The Shawshank Redemption', 'Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.', '1994-09-23', 142, 3);

2. Изменение информации о фильме:
UPDATE films SET duration = 150 WHERE film_id = 1;

3. Удаление фильма из базы данных:
DELETE FROM films WHERE film_id = 1;

4. Получение списка всех фильмов:
SELECT * FROM films;

5. Получение списка фильмов, выпущенных после 2010 года:
SELECT * FROM films WHERE realise_date > '2010-01-01';

6. Получение списка фильмов, относящихся к определенному жанру:
SELECT films.name, genre_type.type FROM films INNER JOIN genre_id ON films.film_id = genre_id.film_id INNER JOIN genre_type ON genre_id.genre_id = genre_type.genre_id WHERE genre_type.type = 'Drama';

7. Получение списка пользователей и их лайков:
SELECT users.name, COUNT(likes.film_id) AS likes_count FROM users LEFT JOIN likes ON users.user_id = likes.user_id GROUP BY users.user_id;

8. Получение списка возрастных рейтингов и количества фильмов с каждым рейтингом:
SELECT mpa_type.type, COUNT(films.film_id) AS film_count FROM mpa_type INNER JOIN films ON mpa_type.rating_mpa_id = films.rating_mpa_id GROUP BY mpa_type.rating_mpa_id;
