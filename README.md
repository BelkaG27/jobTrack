# JobTrack
 
API REST de suivi de candidatures, sécurisée par authentification JWT, développée avec Spring Boot et PostgreSQL.
 
## Fonctionnalités
 
- Inscription et connexion des utilisateurs (JWT)
- Isolation des données : chaque utilisateur ne voit que ses propres candidatures
- CRUD complet des candidatures (créer, lister, modifier, supprimer)
- Suivi du statut d'une candidature (envoyée, en attente, entretien programmé, acceptée, refusée)
- Validation des données (poste/entreprise/lieu obligatoires, date et statut requis)
- Documentation API interactive (Swagger/OpenAPI)
## Stack technique
 
- **Java 21**
- **Spring Boot** (Spring Web, Spring Data JPA, Spring Security)
- **PostgreSQL** (base de données relationnelle)
- **JWT** (io.jsonwebtoken / JJWT) pour l'authentification stateless
- **Swagger / OpenAPI** (springdoc-openapi) pour la documentation
- **Docker Compose** (conteneurisation de la base de données)
- **Maven** (gestion des dépendances et du build)
- **JUnit 5 / Mockito** (tests unitaires avec contexte de sécurité simulé)
## Prérequis
 
- JDK 21
- Docker Desktop
- Un client HTTP pour tester l'API (Postman recommandé), ou directement Swagger UI
## Installation et lancement
 
### 1. Cloner le projet
 
```bash
git clone https://github.com/TON_USERNAME/jobtrack.git
cd jobtrack
```
 
### 2. Lancer la base de données PostgreSQL
 
```bash
docker compose up -d
```
 
### 3. Lancer l'application
 
```bash
./mvnw spring-boot:run
```
 
L'API est accessible sur `http://localhost:8080`.
 
### 4. Documentation interactive (Swagger UI)
 
```
http://localhost:8080/swagger-ui/index.html
```
 
Pour tester les endpoints protégés depuis Swagger :
1. Récupère un token via `POST /auth/login`
2. Clique sur le bouton **Authorize** en haut à droite
3. Colle uniquement la valeur du token (sans le mot `Bearer`, sans les guillemets ni les accolades du JSON de réponse)
## Authentification
 
| Méthode | URL             | Description                            |
|---------|------------------|------------------------------------------|
| POST    | /auth/register   | Créer un compte, retourne un token JWT   |
| POST    | /auth/login      | Se connecter, retourne un token JWT      |
 
Toutes les routes `/candidatures/**` nécessitent un header :
```
Authorization: Bearer <votre_token>
```
 
## Endpoints des candidatures
 
| Méthode | URL                    | Description                                              |
|---------|-------------------------|------------------------------------------------------------|
| GET     | /candidatures           | Lister les candidatures de l'utilisateur connecté          |
| GET     | /candidatures/{id}      | Récupérer une candidature (si elle appartient à l'utilisateur) |
| POST    | /candidatures           | Créer une nouvelle candidature                             |
| PUT     | /candidatures/{id}      | Modifier une candidature existante                          |
| DELETE  | /candidatures/{id}      | Supprimer une candidature                                   |
 
### Statuts possibles
 
`ENVOYE`, `EN_ATTENTE`, `ENTRETIEN_PROGRAMME`, `ACCEPTEE`, `REFUSEE`
 
### Exemple de requête POST /candidatures
 
```json
{
  "poste": "Développeur Java",
  "entreprise": "Capgemini",
  "date": "2026-03-01",
  "lieu": "Lille",
  "statut": "ENVOYE"
}
```
 
## Lancer les tests
 
```bash
./mvnw test
```
 
Inclut des tests unitaires sur le contrôleur des candidatures, avec repositories mockés et contexte de sécurité (`SecurityContextHolder`) simulé pour reproduire un utilisateur authentifié.
 

## Auteur
 
[BelkaG27]