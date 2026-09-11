# JobTrack

API REST de suivi de candidatures, sécurisée par authentification JWT, développée avec Spring Boot et PostgreSQL.

🔗 **API en ligne** : [https://jobtrack-9z0p.onrender.com](https://jobtrack-9z0p.onrender.com)
📄 **Documentation interactive** : [https://jobtrack-9z0p.onrender.com/swagger-ui/index.html](https://jobtrack-9z0p.onrender.com/swagger-ui/index.html)

> Le service est hébergé sur le plan gratuit de Render : après une période d'inactivité, la première requête peut prendre 30 à 50 secondes le temps que le service redémarre.

## Fonctionnalités

- Inscription et connexion des utilisateurs (JWT)
- Isolation des données : chaque utilisateur ne voit que ses propres candidatures
- CRUD complet des candidatures (créer, lister, modifier, supprimer)
- Suivi du statut d'une candidature (envoyée, en attente, entretien programmé, acceptée, refusée)
- Pagination et tri de la liste des candidatures
- Validation des données (poste/entreprise/lieu obligatoires, date et statut requis)
- Gestion centralisée des erreurs (404, erreurs de validation) au format JSON
- Séparation entités JPA / DTOs (requêtes et réponses dédiées, aucune entité exposée directement)
- Documentation API interactive (Swagger/OpenAPI)
- Migrations de base de données versionnées (Flyway)
- Conteneurisation complète (backend + base de données)
- Intégration continue (GitHub Actions)

## Stack technique

- **Java 21**
- **Spring Boot** (Spring Web, Spring Data JPA, Spring Security)
- **PostgreSQL** (base de données relationnelle)
- **Flyway** (migrations de schéma versionnées)
- **JWT** (io.jsonwebtoken / JJWT) pour l'authentification stateless
- **Swagger / OpenAPI** (springdoc-openapi) pour la documentation
- **Docker** (conteneurisation du backend et de la base de données)
- **Maven** (gestion des dépendances et du build)
- **JUnit 5 / Mockito** (tests unitaires avec contexte de sécurité simulé)
- **GitHub Actions** (intégration continue)
- **Render** (hébergement du backend et de la base de données)

## Prérequis

- JDK 21
- Docker Desktop
- Un client HTTP pour tester l'API (Postman recommandé), ou directement Swagger UI

## Installation et lancement en local

### 1. Cloner le projet

```bash
git clone https://github.com/BelkaG27/jobTrack.git
cd jobtrack
```

### 2. Lancer l'application complète (backend + base de données)

```bash
docker compose up -d --build
```

L'API est accessible sur `http://localhost:8080`.

> Le projet est configuré pour fonctionner via Docker : les identifiants de base de données et le secret JWT sont fournis comme variables d'environnement dans `docker-compose.yml`, aucune valeur sensible n'est codée en dur dans `application.properties`.

### 3. Documentation interactive (Swagger UI)

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
| GET     | /candidatures           | Lister les candidatures de l'utilisateur connecté (paginé)  |
| GET     | /candidatures/{id}      | Récupérer une candidature (si elle appartient à l'utilisateur) |
| POST    | /candidatures           | Créer une nouvelle candidature                             |
| PUT     | /candidatures/{id}      | Modifier une candidature existante                          |
| DELETE  | /candidatures/{id}      | Supprimer une candidature                                   |

### Pagination et tri (`GET /candidatures`)

Paramètres de requête optionnels :

| Paramètre | Exemple | Description |
|---|---|---|
| `page` | `?page=0` | Numéro de page (commence à 0) |
| `size` | `?size=10` | Nombre d'éléments par page |
| `sort` | `?sort=date,desc` | Champ de tri et sens (`asc` / `desc`) |

Exemple : `GET /candidatures?page=0&size=10&sort=date,desc`

Réponse :
```json
{
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 23,
  "totalPages": 3,
  "last": false,
  "content": [ ... ]
}
```

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

## Gestion des erreurs

Les erreurs sont centralisées et renvoyées au format JSON.

**Candidature non trouvée (404)**
```json
"Candidature(s) not found"
```

**Erreur de validation (400)**
```json
{
  "poste": "Le poste ne peut pas être vide",
  "date": "La date ne peut pas être vide"
}
```

## Migrations de base de données

Le schéma est géré par Flyway. Les scripts se trouvent dans `src/main/resources/db/migration`, nommés `V<numéro>__description.sql`. `spring.jpa.hibernate.ddl-auto` est configuré sur `validate` : Hibernate vérifie que les entités correspondent au schéma, mais ne le modifie jamais lui-même.

## Lancer les tests

```bash
./mvnw test
```

Inclut des tests unitaires sur le contrôleur des candidatures (repositories mockés, contexte de sécurité simulé) ainsi que des tests du gestionnaire d'erreurs centralisé.

## Intégration continue

Un pipeline GitHub Actions (`.github/workflows/ci.yml`) exécute automatiquement les tests à chaque push sur `main`, avec un PostgreSQL temporaire fourni comme service CI.

## Déploiement

Le backend est conteneurisé via `Dockerfile` (build multi-stage) et déployé sur Render, avec une base PostgreSQL managée. Les valeurs sensibles (identifiants de base de données, secret JWT) sont injectées via des variables d'environnement, jamais commitées dans le dépôt.

## Auteur

[BelkaG27]
