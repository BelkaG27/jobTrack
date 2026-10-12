CREATE TABLE fichier (
    id SERIAL PRIMARY KEY,
    nom_origine VARCHAR(255) NOT NULL,
    nom_stockage VARCHAR(255) NOT NULL UNIQUE,
    type_mime VARCHAR(255) NOT NULL,
    taille BIGINT NOT NULL,
    type_document SMALLINT NOT NULL,
    date_upload TIMESTAMP NOT NULL,
    candidature_id INT NOT NULL,
    CONSTRAINT fk_candidature_fichier FOREIGN KEY (candidature_id) REFERENCES candidature(id)
);