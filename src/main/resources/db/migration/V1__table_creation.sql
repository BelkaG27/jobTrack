CREATE TABLE app_user (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    specialty VARCHAR(255) NOT NULL,
    years_of_experience INT NOT NULL
);

CREATE TABLE candidature (
    id SERIAL PRIMARY KEY,
    poste VARCHAR(255) NOT NULL,
    entreprise VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    lieu VARCHAR(255) NOT NULL,
    statut SMALLINT NOT NULL,
    user_id INT,
    CONSTRAINT fk_candidature_user FOREIGN KEY (user_id) REFERENCES app_user(id)
);