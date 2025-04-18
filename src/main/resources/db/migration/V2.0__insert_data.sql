-- Create roles table
CREATE TABLE roles (
                       role_id SERIAL PRIMARY KEY,
                       role_name VARCHAR(20) NOT NULL
);

-- Create users table
CREATE TABLE users (
                       user_id VARCHAR(255) NOT NULL PRIMARY KEY,
                       username VARCHAR(50) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(50) NOT NULL,
                       surname VARCHAR(50) NOT NULL,
                       email VARCHAR(50) NOT NULL,
                       phone_num VARCHAR(20) NOT NULL,
                       role_id INTEGER NOT NULL,
                       FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- Create ad_category table
CREATE TABLE ad_category (
                             category_id BIGSERIAL PRIMARY KEY,
                             category_name VARCHAR(50) NOT NULL UNIQUE,
                             parent_categ_id BIGINT NOT NULL,
                             category_descr VARCHAR(300) NOT NULL
);

-- Create advertisement table
CREATE TABLE advertisement (
                               ad_id BIGSERIAL PRIMARY KEY,
                               category_id BIGINT,
                               user_id VARCHAR(255) NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               description VARCHAR(1000) NOT NULL,
                               price DOUBLE PRECISION NOT NULL,
                               created_at TIMESTAMP NOT NULL,
                               updated_at TIMESTAMP NOT NULL,
                               FOREIGN KEY (category_id) REFERENCES ad_category(category_id),
                               FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create images table
CREATE TABLE images (
                        img_id BIGSERIAL PRIMARY KEY,
                        ad_id BIGINT NOT NULL,
                        img_url VARCHAR(255) NOT NULL,
                        FOREIGN KEY (ad_id) REFERENCES advertisement(ad_id)
);

-- Create comments table
CREATE TABLE comments (
                          id BIGSERIAL PRIMARY KEY,
                          ad_id BIGINT NOT NULL,
                          author_id VARCHAR(255) NOT NULL,
                          text VARCHAR(400) NOT NULL,
                          created_at TIMESTAMP NOT NULL,
                          FOREIGN KEY (ad_id) REFERENCES advertisement(ad_id),
                          FOREIGN KEY (author_id) REFERENCES users(user_id)
);

-- Create user_rating table
CREATE TABLE user_rating (
                             rate_id BIGSERIAL PRIMARY KEY,
                             from_user_id VARCHAR(255) NOT NULL,
                             to_user_id VARCHAR(255) NOT NULL,
                             text VARCHAR(1000),
                             rating INTEGER,
                             created_at TIMESTAMP,
                             FOREIGN KEY (from_user_id) REFERENCES users(user_id),
                             FOREIGN KEY (to_user_id) REFERENCES users(user_id)
);
INSERT INTO roles (role_id, role_name) VALUES
                                  (1,'ROLE_USER'),
                                  (2,'ROLE_MODERATOR'),
                                  (3,'ROLE_ADMIN');