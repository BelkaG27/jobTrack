ALTER TABLE fichier 
ADD CONSTRAINT uk_fichier_candidature_type
UNIQUE (candidature_id,type_document)