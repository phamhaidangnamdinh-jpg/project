INSERT INTO users (username, password, fullname, email, phone, address, identity_number, age, birthday) VALUES
                                                                                                                ('john_doe', 'hashed_password_1', 'John Doe', 'john@example.com', '1234567890', '123 Elm Street', 'ID123456', 30, '1995-08-15'),
                                                                                                                ('jane_smith', 'hashed_password_2', 'Jane Smith', 'jane@example.com', '0987654321', '456 Oak Avenue', 'ID987654', 28, '1997-01-20'),
                                                                                                                ('admin_user', 'hashed_password_3', 'Admin User', 'admin@example.com', '1122334455', '789 Pine Road', 'ID000001', 35, '1990-12-05');


INSERT INTO categories (code, name, description) VALUES
                                                         ('java', 'Java Programming', 'Books related to the Java language'),
                                                         ('backend', 'Backend Development', 'Books about backend frameworks and systems'),
                                                         ( 'database', 'Databases', 'Books on relational and NoSQL databases'),
                                                         ( 'security', 'Cyber Security', 'Books on network and application security');
INSERT INTO books (code, title, author, publisher, page_count, print_type, language, description, quantity)
VALUES
    ('book001', 'Java for Beginners', 'James Gosling', 'Sun Microsystems', 350, 'BOOK', 'en', 'Introductory Java book', 10),
    ('book002', 'Spring Boot in Action', 'Craig Walls', 'Manning', 420, 'BOOK', 'en', 'Deep dive into Spring Boot', 5),
    ('book003', 'MySQL Cookbook', 'Paul DuBois', 'O''Reilly', 580, 'BOOK', 'en', 'Solutions for working with MySQL', 8);
INSERT INTO article (title, content, book_id, author_id)
VALUES ('Understanding Spring Context', 'Spring Context is essential for DI...', 1, 1);

INSERT INTO article (title, content, book_id, author_id)
VALUES ('Java Best Practices', 'Let’s explore some effective Java habits...', 2, 2);

INSERT INTO comment (content, article_id, author_id)
VALUES ('Very helpful article!', 1, 2);

INSERT INTO comment (content, article_id, author_id)
VALUES ('Thanks, this clarified a lot!', 1, 1);

INSERT INTO comment (content, article_id, author_id)
VALUES ('I love Effective Java!', 2, 1);