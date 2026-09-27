CREATE TABLE candidature_status_history (
    id SERIAL PRIMARY KEY,
    ancien_statut SMALLINT NOT NULL,
    nouveau_statut SMALLINT NOT NULL,
    date_de_changement TIMESTAMP NOT NULL,
    candidature_id INT NOT NULL,
    CONSTRAINT fk_candidature_history FOREIGN KEY (candidature_id) REFERENCES candidature(id)
);