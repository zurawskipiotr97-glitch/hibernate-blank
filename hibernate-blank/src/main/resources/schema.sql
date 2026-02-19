DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS photo_likes;
DROP TABLE IF EXISTS photos;
DROP TABLE IF EXISTS albums;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INTEGER PRIMARY KEY,
    username TEXT,
    joinDate TIMESTAMP,
    UNIQUE (username)
);

CREATE TABLE albums (
    id INTEGER PRIMARY KEY,
    name TEXT,
    description TEXT,
    user_id INTEGER REFERENCES users (id)
);

CREATE TABLE photos (
    id INTEGER PRIMARY KEY,
    name TEXT,
    date TIMESTAMP,
    album_id INTEGER REFERENCES albums (id)
);

CREATE TABLE photo_likes (
    user_id INTEGER REFERENCES users (id),
    photo_id INTEGER REFERENCES photos (id),
    PRIMARY KEY (
                user_id, photo_id
        )
);

CREATE TABLE friends (
    user_id INTEGER REFERENCES users (id),
    friend_id INTEGER REFERENCES users (id),
    PRIMARY KEY (
                user_id, friend_id
        )
);

