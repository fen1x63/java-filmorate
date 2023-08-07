# java-filmorate
Template repository for Filmorate project.
Ссылка на диаграмму таблицы проекта
https://dbdiagram.io/d/64c7712902bd1c4a5ef74a8f
![alt text](https://github.com/fen1x63/java-filmorate/blob/8b2723725166887c8e7dd714f5a7230f7925fe21/DataBase.png)
Получить все фильмы, которые были выпущены после определенной даты:

SELECT * FROM films
WHERE realise_date > '2022-01-01';


Получить список всех пользователей, поставивших лайк конкретному фильму:

SELECT users.*
FROM users
JOIN likes ON users.user_id = likes.user_id
WHERE likes.film_id = <film_id>;


Получить количество лайков, полученных каждым фильмом:

SELECT films.name, COUNT(likes.like_id) AS like_count
FROM films
LEFT JOIN likes ON films.film_id = likes.film_id
GROUP BY films.film_id;


Получить список друзей пользователя по его идентификатору:

SELECT users.name AS friend_name
FROM users
JOIN friends ON users.user_id = friends.friend_id
WHERE friends.user_id = <user_id>;


Получить название всех жанров фильмов:

SELECT genre_type.name
FROM genre_type
JOIN genre ON genre_type.genre_id = genre.genre_id;


Получить список всех фильмов определенного жанра:

SELECT films.name, genre_type.name AS genre
FROM films
JOIN genre ON films.film_id = genre.film_id
JOIN genre_type ON genre.genre_id = genre_type.genre_id
WHERE genre_type.name = '<genre_name>';


Получить список пользователей, которые ставили лайк какому-либо фильму после определенной даты рождения:

SELECT users.name
FROM users
JOIN likes ON users.user_id = likes.user_id
WHERE users.birthday > '1990-01-01';


Получить список фильмов, имеющих определенный рейтинг MPAA:

SELECT films.name
FROM films
JOIN mpa_type ON films.rating_mpa_id = mpa_type.rating_mpa_id
WHERE mpa_type.name = '<rating_name>';
