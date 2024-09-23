# Приложение Filmorate

---

## ER-диаграмма для БД приложения Filmorate

![ER-диаграмма](filmorate_ER_diagram.png)

---

## Структура БД

БД приложения Filmorate имеет следующую структуру:

- **films**: описывает добавленные фильмы.
- **users**: описывает список пользователей.
- **вспомогательные таблицы**:
  - **mpa_rating**
  - **genre**
  - **films_genre**
  - **liked_films**
  - **friendship**

### Вспомогательные таблицы

#### Таблица `mpa_rating`

Таблица, содержащая информацию о возможных возрастных рейтингах фильмов.

- **Связь**: Один фильм – один рейтинг, но у рейтинга может быть множество фильмов – связь один-ко-многим.

#### Таблица `genre`

Таблица, содержащая список возможных жанров у фильмов.

- **Связь**: Один фильм может иметь несколько жанров, у одного жанра может быть несколько фильмов. Для описания данной связи (многие-ко-многим) используем соединительную таблицу `films_genre`.

#### Таблица `films_genre`

Описывает связь между существующими фильмами и их возможными жанрами.

- **Первичный ключ**: Составной первичный ключ `PK = (film_id, genre_id)` для избегания повторов в ячейках.

#### Таблица `liked_films`

Соединительная таблица. Описывает взаимоотношение между пользователями и лайками к фильмам.

- **Связь**: У одного фильма может быть несколько лайков, один пользователь может ставить лайки нескольким фильмам – связь многие-ко-многим.
- **Первичный ключ**: Составной первичный ключ `PK = (film_id, user_id)` для избегания повторов в ячейках: пользователь не может поставить лайк одному фильму более одного раза.

#### Таблица `friendship`

Соединительная таблица. Описывает взаимоотношение между пользователем и его списком друзей.

- **Связь**: У одного пользователя может быть несколько друзей, любой друг может добавить в друзья еще несколько пользователей.
- **Первичный ключ**: Составной первичный ключ `PK = (following_user_id, followed_user_id)` для избегания повторов в ячейках: пользователь не может добавить в друзья другого пользователя более одного раза.
- **Дополнительное поле**: `friendship_status` с типом данных `boolean`, описывает текущий статус заявки в друзья (`true` – заявка одобрена, `false` – заявка на данный момент не одобрена).

---

### Примеры SQL-запросов

#### Получение списка пользователей с датой рождения позже 01.01.2000

```sql
SELECT *  
FROM users
WHERE birthday IS AFTER '2000-01-01'
ORDER BY user_id;
```

#### Получение списка всех фильмов с длительностью более 120 минут в порядке убывания длительности

```sql
SELECT * 
FROM films
WHERE duration > 120
ORDER BY duration DESC;
```

#### Получение списка имен 10 самых популярных фильмов

```sql
SELECT f.name, lf.likes AS likes_count
FROM films AS f
JOIN (
    SELECT film_id, COUNT(user_id) AS likes
    FROM liked_films
    GROUP BY film_id
) AS lf ON f.film_id = lf.film_id
ORDER BY likes_count DESC
LIMIT 10;
```

#### Получение таблицы со всеми фильмами с рейтингом R

```sql
SELECT f.*
FROM films AS f
JOIN mpa_rating AS mr ON f.rating_id = mr.rating_id
WHERE mr.name = 'R';
```

#### Получение таблицы с фильмами, у которых встречается жанр comedy

```sql
SELECT *
FROM films
WHERE film_id IN (
    SELECT film_id
    FROM films_genre
    WHERE genre_id = (
        SELECT genre_id
        FROM genre
        WHERE name = 'comedy'
    )
);
```

#### Получение таблицы всех пользователей, которые являются общими друзьями пользователя с user_id = 1 и пользователя user_id = 3
```sql
SELECT u.*
FROM users AS u
WHERE u.user_id IN (
    SELECT f1.following_user_id
    FROM friendship AS f1
    WHERE f1.followed_user_id = 1 AND f1.friendship_status = true
)
AND u.user_id IN (
    SELECT f2.following_user_id
    FROM friendship AS f2
    WHERE f2.followed_user_id = 3 AND f2.friendship_status = true
);
```

#### Получение таблицы всех пользователей, которые являются подписчиками пользователя с user_id = 1

```sql
SELECT u.*
FROM users AS u
JOIN friendship AS f ON u.user_id = f.following_user_id
WHERE f.followed_user_id = 1 AND f.friendship_status = false;
```

