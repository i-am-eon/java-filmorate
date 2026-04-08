CREATE TABLE IF NOT EXISTS mpa (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS app_users (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
email VARCHAR(100) NOT NULL UNIQUE,
login VARCHAR(50) NOT NULL UNIQUE,
name VARCHAR(100),
birthday DATE
);

CREATE TABLE IF NOT EXISTS films (
id BIGINT PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(100) NOT NULL,
description VARCHAR(1000),
release_date DATE NOT NULL,
duration BIGINT NOT NULL,
mpa_id BIGINT NOT NULL,
FOREIGN KEY (mpa_id) REFERENCES mpa(id)
);

CREATE TABLE IF NOT EXISTS films_genres (
film_id BIGINT NOT NULL,
genre_id BIGINT NOT NULL,
PRIMARY KEY (film_id, genre_id),
FOREIGN KEY (film_id) REFERENCES films(id),
FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS likes (
user_id BIGINT NOT NULL,
film_id BIGINT NOT NULL,
PRIMARY KEY (user_id, film_id),
FOREIGN KEY (user_id) REFERENCES app_users(id),
FOREIGN KEY (film_id) REFERENCES films(id)
);

CREATE TABLE IF NOT EXISTS friendships (
user_id BIGINT NOT NULL,
friend_id BIGINT NOT NULL,
status VARCHAR(20) NOT NULL,
PRIMARY KEY (user_id, friend_id),
FOREIGN KEY (user_id) REFERENCES app_users(id) ON DELETE CASCADE,
FOREIGN KEY (friend_id) REFERENCES app_users(id)
);

-- Отдельные ограничения для лучшей совместимости с H2
ALTER TABLE friendships ADD CONSTRAINT chk_friendship_status
    CHECK (status IN ('PENDING', 'CONFIRMED', 'BLOCKED'));

ALTER TABLE friendships ADD CONSTRAINT chk_no_self_friendship
    CHECK (user_id != friend_id);
