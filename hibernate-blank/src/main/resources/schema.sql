DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS albums;
DROP TABLE IF EXISTS photos;
DROP TABLE IF EXISTS photo_likes;
DROP TABLE IF EXISTS friends;

CREATE TABLE users (
    id BIGINT,
    username TEXT,
    joinDate TIMESTAMP,
    PRIMARY KEY (
                id
        )
);

CREATE TABLE albums (
    id BIGINT,
    name TEXT,
    description TEXT,
    user_id BIGINT REFERENCES users (id),
    PRIMARY KEY (
                id
        )
);

CREATE TABLE photos (
    id BIGINT,
    name TEXT,
    date TIMESTAMP,
    album_id BIGINT REFERENCES albums (id),
    PRIMARY KEY (
                 id
        )
);

CREATE TABLE photo_likes (
    user_id BIGINT REFERENCES users (id),
    photo_id BIGINT REFERENCES photos (id),
    PRIMARY KEY (
                user_id, photo_id
        )
);

CREATE TABLE friends (
    user_id BIGINT REFERENCES users (id),
    friend_id BIGINT REFERENCES users (id),
    PRIMARY KEY (
                user_id, friend_id
        )
);

