ALTER TABLE app_user ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT true;

CREATE TABLE mail_token (
    id SERIAL PRIMARY KEY,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    user_id INT NOT NULL,
    CONSTRAINT fk_mail_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);