-- Criação da tabela Users
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    profile VARCHAR(50) NOT NULL
);

-- Criação da tabela Courses
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL
);

-- Criação da tabela Topics
CREATE TABLE topics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    creation_date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    author_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- Criação da tabela Responses
CREATE TABLE responses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message TEXT NOT NULL,
    creation_date DATETIME NOT NULL,
    topic_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    solution BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (topic_id) REFERENCES topics(id),
    FOREIGN KEY (author_id) REFERENCES users(id)
);