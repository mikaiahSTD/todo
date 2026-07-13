
CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       CONSTRAINT uk_user_names UNIQUE (first_name, last_name)
);


CREATE TABLE course (
                        id UUID PRIMARY KEY,
                        title VARCHAR(100) NOT NULL,
                        start_date TIMESTAMP WITH TIME ZONE NOT NULL,
                        end_date TIMESTAMP WITH TIME ZONE NOT NULL
);


CREATE TABLE subscription (
                              id UUID PRIMARY KEY,
                              user_id UUID NOT NULL,
                              course_id UUID NOT NULL,
                              subscribed_at TIMESTAMP WITH TIME ZONE NOT NULL,


                              CONSTRAINT fk_subscription_user FOREIGN KEY (user_id) REFERENCES users(id),
                              CONSTRAINT fk_subscription_course FOREIGN KEY (course_id) REFERENCES course(id),


                              CONSTRAINT uk_user_course UNIQUE (user_id, course_id)
);