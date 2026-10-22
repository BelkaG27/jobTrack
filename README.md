# JobTrack

API REST de suivi de candidatures, sécurisée par authentification JWT, développée avec Spring Boot et PostgreSQL.

🔗 **API en ligne** : [https://jobtrack-9z0p.onrender.com](https://jobtrack-9z0p.onrender.com)
📄 **Documentation interactive** : [https://jobtrack-9z0p.onrender.com/swagger-ui/index.html](https://jobtrack-9z0p.onrender.com/swagger-ui/index.html)

> Le service est hébergé sur le plan gratuit de Render : après une période d'inactivité, la première requête peut prendre 30 à 50 secondes le temps que le service redémarre.

## Fonctionnalités

- Inscription et connexion des utilisateurs (JWT)
- Vérification de l'adresse email à l'inscription, via un lien de confirmation à durée de vie limitée
- Isolation des données : chaque utilisateur ne voit que ses propres candidatures
- CRUD complet des candidatures (créer, lister, modifier, supprimer)
- Suivi du statut d'une candidature (envoyée, en attente, entretien programmé, acceptée, refusée)
- Historique des changements de statut d'une candidature (audit trail)
- Statistiques personnelles sur les candidatures (total, répartition par statut, taux de réponse)
- Détection automatique des candidatures à relancer (en attente depuis plus de 3 jours), via une tâche planifiée
- Pagination et tri de la liste des candidatures
- Recherche et filtrage avancés des candidatures (statut, entreprise, plage de dates), combinables entre eux et avec la pagination et le tri
- Upload de documents PDF (CV, lettre de motivation, autre) rattachés à une candidature
- Téléchargement et suppression des documents d'une candidature
- Suppression automatique des fichiers physiques lorsqu'une candidature est supprimée
- Validation des données (poste/entreprise/lieu obligatoires, date et statut requis)
- Gestion centralisée des erreurs (404, erreurs de validation) au format JSON
- Séparation entités JPA / DTOs (requêtes et réponses dédiées, aucune entité exposée directement)
- Documentation API interactive (Swagger/OpenAPI)
- Migrations de base de données versionnées (Flyway)
- Conteneurisation complète (backend + base de données + serveur mail de développement)
- Intégration continue (GitHub Actions)
- Déploiement conditionné à la réussite des tests (le déploiement sur Render n'est déclenché que si le pipeline CI passe)
- Limitation du nombre de requêtes (rate limiting) par IP ou par utilisateur
- Authentification à deux tokens (access token courte durée + refresh token longue durée), avec rotation et révocation en cascade en cas de vol détecté
- Supervision de l'application via Spring Boot Actuator (santé, métriques, configuration)
- Gestion des rôles (utilisateur / administrateur) avec restriction d'accès aux endpoints sensibles

## Stack technique

- **Java 21**
- **Spring Boot** (Spring Web, Spring Data JPA, Spring Security, Spring Mail)
- **Spring Data JPA Specifications** (`JpaSpecificationExecutor`) pour construire dynamiquement les requêtes de filtrage
- **PostgreSQL** (base de données relationnelle)
- **Flyway** (migrations de schéma versionnées)
- **JWT** (io.jsonwebtoken / JJWT) pour l'authentification stateless
- **Bucket4j** pour le rate limiting (algorithme token bucket)
- **Swagger / OpenAPI** (springdoc-openapi) pour la documentation
- **Stockage de fichiers local** derrière un service dédié (`FileStorageService`), remplaçable par un stockage objet (S3) sans toucher au reste du code
- **Mailpit** (serveur SMTP de développement, pour intercepter les emails en local sans fournisseur réel)
- **Docker** (conteneurisation du backend, de la base de données et du serveur mail de développement)
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

### 2. Lancer l'application complète (backend + base de données + serveur mail)

```bash
docker compose up -d --build
```

L'API est accessible sur `http://localhost:8080`.

> Le projet est configuré pour fonctionner via Docker : les identifiants de base de données, le secret JWT et le mot de passe de l'administrateur (`ADMIN_PASSWORD`) sont fournis comme variables d'environnement dans `docker-compose.yml`, aucune valeur sensible n'est codée en dur dans `application.properties`.

En local, les emails envoyés par l'application (lien de vérification) ne sont pas remis à un vrai fournisseur de messagerie : ils sont interceptés par **Mailpit**, consultable sur `http://localhost:8025`.

### 3. Documentation interactive (Swagger UI)

```
http://localhost:8080/swagger-ui/index.html
```

Pour tester les endpoints protégés depuis Swagger :
1. Récupère un token via `POST /auth/login`
2. Clique sur le bouton **Authorize** en haut à droite
3. Colle uniquement la valeur du token (sans le mot `Bearer`, sans les guillemets ni les accolades du JSON de réponse)

## Authentification

L'API utilise un système à **deux tokens** :

| Token | Durée de vie | Rôle |
|---|---|---|
| **Access token** (JWT) | 15 minutes | Envoyé à chaque requête vers les routes protégées |
| **Refresh token** | 7 jours | Utilisé uniquement pour obtenir un nouvel access token, via `/auth/refresh` |

| Méthode | URL             | Description                                        |
|---------|------------------|------------------------------------------------------|
| POST    | /auth/register   | Créer un compte (non activé), envoie un lien de vérification par email |
| GET     | /auth/verify     | Activer le compte à partir du lien reçu par email |
| POST    | /auth/resend     | Renvoyer un lien de vérification à un compte non activé |
| POST    | /auth/login      | Se connecter (compte activé requis), retourne un access token + un refresh token |
| POST    | /auth/refresh    | Échanger un refresh token valide contre une nouvelle paire de tokens |
| POST    | /auth/logout     | Révoquer tous les refresh tokens actifs de l'utilisateur (déconnexion) |

### Inscription et vérification d'email

`POST /auth/register` crée le compte avec le statut **non activé** (`enabled: false`) et envoie automatiquement un email contenant un lien de vérification, valable **10 minutes**. Aucun token d'authentification n'est renvoyé à ce stade : le compte n'est pas encore utilisable.

Réponse :
```
202 Accepted
"verifiez votre boite mail pour un lien de verification !"
```

Le lien reçu par email pointe vers `GET /auth/verify?token=<token>`. Cliquer dessus (ou appeler l'URL) active le compte. Un lien déjà utilisé ou expiré renvoie une erreur explicite (401).

Si le lien a expiré ou n'a jamais été reçu, `POST /auth/resend` permet d'en générer un nouveau (l'ancien est automatiquement invalidé) :
```json
{
  "username": "monUsername"
}
```
Pour éviter de révéler si un compte existe ou est déjà activé, cet endpoint renvoie systématiquement la même réponse `202`, quel que soit le cas réel (compte inexistant, déjà activé, ou en attente de vérification).

Une fois le compte activé, `POST /auth/login` fonctionne normalement et retourne les tokens d'authentification. Tant que le compte n'est pas activé, une tentative de connexion est refusée.

Réponse type de `/auth/login` et `/auth/refresh` :
```json
{
  "accessToken": "eyJhbGciOi...",
  "refreshToken": "K3f9sQ2m..."
}
```

Toutes les routes `/candidatures/**` nécessitent un header :
```
Authorization: Bearer <access_token>
```

### Rafraîchir un access token expiré

```
POST /auth/refresh
```
```json
{
  "token": "<refresh_token>"
}
```

### Se déconnecter

```
POST /auth/logout
```
```json
{
  "token": "<refresh_token>"
}
```

Révoque immédiatement tous les refresh tokens actifs de l'utilisateur propriétaire du token envoyé. Si le token présenté est invalide, déjà révoqué (signe possible de vol) ou expiré, une erreur 401 est renvoyée — mais dans le cas "déjà révoqué", la révocation en cascade de tous les tokens de l'utilisateur est tout de même déclenchée par précaution.

### Sécurité du refresh token

- Le refresh token n'est jamais stocké en clair en base de données : seul son **hash SHA-256** est conservé.
- **Rotation à chaque utilisation** : à chaque appel à `/auth/refresh`, le refresh token utilisé est invalidé et un nouveau est renvoyé. Un même refresh token ne peut donc servir qu'une seule fois.
- **Détection de vol et révocation en cascade** : si un refresh token déjà utilisé (donc déjà révoqué) est présenté à nouveau, l'API considère qu'il a été volé/intercepté et révoque **immédiatement tous les refresh tokens actifs de l'utilisateur concerné**, le forçant à se reconnecter avec son mot de passe.
- Un refresh token invalide, expiré ou déjà utilisé renvoie une erreur **401 (Unauthorized)**.
- Les refresh tokens révoqués ou expirés sont automatiquement purgés de la base de données par une tâche planifiée (`@Scheduled`), exécutée toutes les 15 minutes.

### Sécurité du lien de vérification d'email

- Le token de vérification n'est jamais stocké en clair : seul son hash SHA-256 est conservé, comme pour le refresh token.
- Il est encodé en **Base64 URL-safe** (sans padding), afin de rester valide une fois inséré dans un lien cliquable.
- Il expire après **10 minutes**.
- Générer un nouveau lien (via `/auth/resend`) invalide automatiquement les précédents : un seul lien est valide à la fois.
- Un lien invalide, expiré ou déjà utilisé renvoie une erreur **401 (Unauthorized)**.
- Les liens révoqués ou expirés sont automatiquement purgés de la base de données par une tâche planifiée (`@Scheduled`), comme pour les refresh tokens.

## Endpoints des candidatures

| Méthode | URL                      | Description                                              |
|---------|---------------------------|------------------------------------------------------------|
| GET     | /candidatures             | Lister les candidatures de l'utilisateur connecté (paginé, triable, filtrable) |
| GET     | /candidatures/{id}        | Récupérer une candidature (si elle appartient à l'utilisateur) |
| POST    | /candidatures             | Créer une nouvelle candidature                             |
| PUT     | /candidatures/{id}        | Modifier une candidature existante                          |
| DELETE  | /candidatures/{id}        | Supprimer une candidature (et ses documents)                |
| GET     | /candidatures/stats       | Récupérer les statistiques des candidatures de l'utilisateur connecté |
| GET     | /candidatures/{id}/history| Récupérer l'historique des changements de statut d'une candidature |

### Pagination, tri et filtrage (`GET /candidatures`)

Tous les paramètres de requête sont **optionnels** et combinables entre eux.

| Paramètre | Exemple | Description |
|---|---|---|
| `page` | `?page=0` | Numéro de page (commence à 0) |
| `size` | `?size=10` | Nombre d'éléments par page |
| `sort` | `?sort=date,desc` | Champ de tri et sens (`asc` / `desc`) |
| `statut` | `?statut=EN_ATTENTE` | Filtre sur le statut (valeur exacte de l'enum) |
| `entreprise` | `?entreprise=cap` | Nom d'entreprise **contenant** la saisie, insensible à la casse |
| `dateDebut` | `?dateDebut=2026-01-01` | Candidatures dont la `date` est postérieure ou égale à cette date |
| `dateFin` | `?dateFin=2026-01-31` | Candidatures dont la `date` est antérieure ou égale à cette date |

Les dates utilisent le format `yyyy-MM-dd` et portent sur le champ `date` (date de candidature), et non sur `derniereMisAJour`. Les deux bornes sont inclusives et indépendantes : on peut n'en fournir qu'une seule.

Exemple :
`GET /candidatures?statut=EN_ATTENTE&entreprise=cap&dateDebut=2026-01-01&dateFin=2026-01-31&page=0&size=10&sort=date,desc`

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

**Règles et erreurs**

| Cas | Code |
|---|---|
| Statut invalide (`?statut=BLABLA`) | 400 |
| Format de date invalide (`?dateDebut=abc`) | 400 |
| `dateDebut` postérieure à `dateFin` | 400 |
| `dateDebut` égale à `dateFin` | 200 (candidatures de ce jour) |
| Aucun résultat | 200 avec `content` vide |

La validation de la plage de dates est effectuée avant tout accès à la base de données.

**Isolation des données** : le filtre sur l'utilisateur connecté est toujours appliqué. Il constitue la base de la requête et ne dépend d'aucun paramètre : un utilisateur ne peut jamais voir les candidatures d'un autre, quels que soient les filtres envoyés.

**Implémentation** : chaque filtre est une `Specification<Candidature>` (classe `CandidatureSpecifications`). La méthode `search` part de la condition sur l'utilisateur, puis ajoute une condition par filtre uniquement si le paramètre est renseigné. `CandidatureService.searchCandidatureBy` exécute la requête via `CandidatureRepository.findAll(spec, pageable)` (interface `JpaSpecificationExecutor`), ce qui permet de conserver la pagination et le tri existants.

**Limite connue** : dans le filtre `entreprise`, les caractères `%` et `_` ne sont pas échappés et sont interprétés comme des jokers par le `LIKE` (par exemple `?entreprise=%` renvoie toutes les candidatures de l'utilisateur). Il n'y a ni fuite de données ni injection SQL (la valeur est paramétrée et le filtre utilisateur reste appliqué), mais le comportement n'est pas celui d'une recherche littérale.

### Statistiques (`GET /candidatures/stats`)

Renvoie un aperçu chiffré des candidatures de l'utilisateur connecté : le nombre total de candidatures, leur répartition par statut, et un taux de réponse (proportion de candidatures ayant reçu une réponse définitive de l'entreprise, acceptée ou refusée, par rapport au total).

Réponse :
```json
{
  "totalCandidatures": 12,
  "groupByStatutCount": {
    "ENVOYE": 2,
    "EN_ATTENTE": 5,
    "ENTRETIEN_PROGRAMME": 1,
    "ACCEPTEE": 2,
    "REFUSEE": 2
  },
  "tauxDeReponse": 0.33
}
```

### Relance recommandée

Chaque candidature possède un champ `relanceRecommandee` (booléen), calculé automatiquement par une tâche planifiée (exécutée toutes les 72 heures). Une candidature passe à `relanceRecommandee: true` si elle est toujours au statut `EN_ATTENTE` depuis plus de 3 jours (basé sur son champ `derniereMisAJour`, mis à jour à chaque création ou modification de la candidature). Le flag est automatiquement remis à `false` dès que le statut de la candidature change.

Ce champ est visible directement dans la réponse des endpoints `GET /candidatures` et `GET /candidatures/{id}`, sans appel supplémentaire nécessaire.

### Historique des changements de statut (`GET /candidatures/{id}/history`)

Chaque changement de statut d'une candidature est automatiquement enregistré dans un historique dédié, permettant de retracer son parcours dans le temps (audit trail). Un nouvel enregistrement est créé uniquement lorsque le statut change réellement lors d'un `PUT /candidatures/{id}` (une modification qui ne touche pas au statut ne génère aucune entrée).

L'historique reste conservé même en cas de suppression de la candidature : il n'est pas lié au cycle de vie de celle-ci.

Réponse (triée du changement le plus récent au plus ancien) :
```json
[
  {
    "id": 2,
    "ancienStatut": "EN_ATTENTE",
    "nouveauStatut": "ACCEPTEE",
    "dateDeChangement": "2026-09-27T17:45:19.990594"
  },
  {
    "id": 1,
    "ancienStatut": "ENVOYE",
    "nouveauStatut": "EN_ATTENTE",
    "dateDeChangement": "2026-09-27T17:45:14.986554"
  }
]
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

## Documents d'une candidature

Chaque candidature peut avoir des documents PDF, **au maximum un par type** : `CV`, `LETTRE_DE_MOTIVATION`, `AUTRE`. Un second envoi du même type est refusé (409) : il faut d'abord supprimer l'ancien.

| Méthode | URL | Description |
|---------|-----|-------------|
| POST    | /candidatures/{id}/files          | Envoyer un document (`multipart/form-data`) |
| GET     | /candidatures/{id}/files          | Lister les métadonnées des documents (du plus récent au plus ancien) |
| GET     | /candidatures/{id}/files/{fileId} | Télécharger un document |
| DELETE  | /candidatures/{id}/files/{fileId} | Supprimer un document (204) |

### Envoi (`POST /candidatures/{id}/files`)

Corps `multipart/form-data` avec deux champs :

| Champ | Description |
|---|---|
| `file` | Le fichier PDF |
| `typeDocument` | `CV`, `LETTRE_DE_MOTIVATION` ou `AUTRE` |

Réponse `201 Created` :
```json
{
  "id": 1,
  "nomOrigine": "cv-2026.pdf",
  "typeMime": "application/pdf",
  "taille": 48213,
  "typeDocument": "CV",
  "dateUpload": "2026-10-12T18:30:12.123456",
  "candidature": 4
}
```

Le nom de stockage interne n'est jamais exposé.

### Règles de validation

- Taille maximale : **5 Mo** par fichier (10 Mo par requête), au-delà : **413**
- Fichier non vide, extension `.pdf`, et contenu réellement PDF : les premiers octets (`%PDF-`) sont vérifiés, le `Content-Type` envoyé par le client n'est pas utilisé
- Un seul document par type et par candidature (**409** sinon), garanti à la fois par un contrôle applicatif et par une contrainte `UNIQUE` en base (protection contre les requêtes simultanées)

### Sécurité des fichiers

- **Aucun nom fourni par le client n'est utilisé sur le disque** : le fichier est stocké sous un nom `UUID` + `.pdf`. Le nom d'origine n'est conservé qu'en base, pour l'affichage et le téléchargement.
- **Protection contre le path traversal** : tout chemin est normalisé puis vérifié (`startsWith`) pour rester dans le dossier de stockage, à l'écriture comme à la lecture et à la suppression.
- **Isolation des données** : l'accès à un fichier exige que la candidature appartienne à l'utilisateur connecté **et** que le fichier appartienne à cette candidature (protection contre l'IDOR). Dans les deux cas d'échec, la réponse est un 404, qui ne révèle pas l'existence de la ressource.
- **Téléchargement** : `Content-Disposition: attachment` généré via l'API Spring (encodage et échappement du nom), et `Content-Type` issu de la base, pas du client.

### Cohérence entre la base et le disque

Le disque n'est pas transactionnel : l'ordre des opérations est choisi pour que l'échec le moins grave soit le plus tolérable.

- **Envoi** : fichier écrit d'abord, puis ligne en base. Si la base échoue, le fichier est supprimé immédiatement (compensation).
- **Suppression** (d'un fichier ou d'une candidature entière) : ligne supprimée d'abord, fichier supprimé **après le commit** (`afterCommit`). Si la suppression disque échoue, l'erreur est loggée sans être renvoyée au client : le fichier devient un orphelin invisible, jamais une ligne pointant vers un fichier absent.
- Une écriture interrompue (disque plein...) supprime le fichier partiel.

### Configuration

| Propriété | Variable d'environnement | Défaut |
|---|---|---|
| `file.storage.dir` | `FILE_STORAGE_DIR` | `uploads` |
| `spring.servlet.multipart.max-file-size` | | `5MB` |
| `spring.servlet.multipart.max-request-size` | | `10MB` |

Dans `docker-compose.yml`, le dossier `/app/uploads` est monté sur le volume nommé `uploads_data` : les fichiers survivent à un `docker compose up --build`.

> **Limite sur Render (offre gratuite)** : le système de fichiers est éphémère. Les documents envoyés sont perdus à chaque redémarrage ou redéploiement, alors que leurs lignes restent en base (le téléchargement répond alors 404). Une solution durable demanderait un stockage objet de type S3 : seul `FileStorageService` aurait à changer.

## Gestion des erreurs

Les erreurs sont centralisées et renvoyées au format JSON.

**Candidature non trouvée (404)**
```json
"Candidature(s) not found"
```

**Refresh token ou lien de vérification invalide, expiré ou déjà utilisé (401)**
```json
"ce token a expiré !"
```

**Compte non activé lors d'une tentative de connexion (401/403, selon le comportement par défaut de Spring Security)**

**Erreur de validation (400)**
```json
{
  "poste": "Le poste ne peut pas être vide",
  "date": "La date ne peut pas être vide"
}
```

**Paramètres de filtrage invalides (400)** : statut inconnu, date mal formatée, ou `dateDebut` postérieure à `dateFin` (voir [Pagination, tri et filtrage](#pagination-tri-et-filtrage-get-candidatures)).

**Documents**

| Cas | Code |
|---|---|
| Fichier trop volumineux | 413 |
| Fichier vide, extension non autorisée, contenu non PDF | 400 |
| Type de document déjà présent pour cette candidature | 409 |
| Candidature ou fichier introuvable (ou appartenant à un autre utilisateur) | 404 |
| Fichier présent en base mais absent du disque | 404 (incident loggé côté serveur) |

## Rate limiting

Chaque client (IP pour les routes `/auth/**`, nom d'utilisateur pour les routes authentifiées) dispose d'un quota de **20 requêtes**, réalimenté à raison de **10 requêtes par minute**. Au-delà, l'API répond avec le code **429 (Too Many Requests)**.

Les envois de fichiers partagent le même quota que les autres routes alors qu'ils coûtent plus cher : un quota dédié aux uploads serait une amélioration possible.

## Migrations de base de données

Le schéma est géré par Flyway. Les scripts se trouvent dans `src/main/resources/db/migration`, nommés `V<numéro>__description.sql` :
- `V1` : schéma initial
- `V2` : table `refresh_token`
- `V3` : ajout de la colonne `role` sur les utilisateurs
- `V4` : colonnes `relance_recommandee` et `derniere_mis_a_jour` sur les candidatures
- `V5` : renommage de la colonne `derniere_mis_a_jour` en `derniere_misajour`
- `V6` : table `candidature_status_history`
- `V7` : colonne `enabled` sur les utilisateurs et table `mail_token`
- `V8` : colonne `suspended` sur les utilisateurs
- `V9` : table `fichier` (métadonnées des documents)
- `V10` : contrainte `UNIQUE (candidature_id, type_document)` sur `fichier`

Le filtrage avancé n'a nécessité aucune migration : il s'appuie sur les colonnes existantes.

`spring.jpa.hibernate.ddl-auto` est configuré sur `validate` : Hibernate vérifie que les entités correspondent au schéma, mais ne le modifie jamais lui-même.

## Lancer les tests

```bash
./mvnw test
```

Inclut des tests unitaires sur le contrôleur des candidatures (repositories mockés, contexte de sécurité simulé), sur le gestionnaire d'erreurs centralisé, ainsi que sur le service de refresh tokens (création, rotation, détection de réutilisation avec révocation en cascade, expiration, logout).

Le filtrage des candidatures est couvert côté contrôleur (service mocké) : appel avec tous les filtres (vérification que chaque paramètre est transmis au bon emplacement), appel sans aucun filtre (paramètres à `null`), et plage de dates inversée (exception levée avant tout appel au service). Les `Specification` elles-mêmes ne sont pas encore testées contre une vraie base (un `@DataJpaTest` serait nécessaire).

Les documents sont aussi couverts : validation des PDF, service de stockage (répertoire temporaire : écriture, lecture, suppression, path traversal, nettoyage après écriture interrompue) et service de fichiers (compensation après échec en base, suppression différée après commit).

## Intégration continue

Un pipeline GitHub Actions (`.github/workflows/ci.yml`) exécute automatiquement les tests à chaque push sur `main`, avec un PostgreSQL temporaire fourni comme service CI.

## Déploiement

Le backend est conteneurisé via `Dockerfile` (build multi-stage) et déployé sur Render, avec une base PostgreSQL managée. Les valeurs sensibles (identifiants de base de données, secret JWT, configuration du serveur mail) sont injectées via des variables d'environnement, jamais commitées dans le dépôt.

Le déploiement est **conditionné à la réussite des tests** : si le pipeline CI échoue (ex : un test casse), le déploiement sur Render n'est pas déclenché, ce qui évite de mettre en production une version défectueuse.

## Supervision (Spring Boot Actuator)

L'application expose des endpoints de supervision via [Spring Boot Actuator](https://docs.spring.io/spring-boot/reference/actuator/index.html), sous `/actuator`.

| Endpoint | Description |
|---|---|
| `/actuator/health` | État de santé de l'application (base de données, etc.) |
| `/actuator/info` | Métadonnées de l'application (nom, version, description) |
| `/actuator/metrics` | Métriques internes (mémoire, requêtes HTTP, temps de réponse...) |
| `/actuator/env` | Variables d'environnement chargées par l'application |
| `/actuator/beans` | Liste des composants (beans) Spring de l'application |

> `/actuator/health` est public (nécessaire pour le monitoring externe, ex : Render). Tous les autres endpoints (`/actuator/info`, `/actuator/metrics`, `/actuator/env`, `/actuator/beans`) sont réservés aux utilisateurs ayant le rôle **ADMIN**, car ils exposent des informations internes sensibles (variables d'environnement, composants internes...).

## Rôles et autorisations

Chaque utilisateur possède un rôle : `ROLE_USER` (par défaut) ou `ROLE_ADMIN`.

- Tout nouvel utilisateur créé via `/auth/register` reçoit automatiquement le rôle `ROLE_USER` — le rôle n'est jamais fourni par le client, pour éviter qu'un utilisateur puisse s'auto-promouvoir administrateur.
- Un compte administrateur est créé automatiquement au premier démarrage de l'application (s'il n'en existe pas déjà), via un `CommandLineRunner`. Son mot de passe est fourni par la variable d'environnement `ADMIN_PASSWORD`, jamais codé en dur dans le dépôt. Ce compte est activé (`enabled: true`) dès sa création, sans passer par la vérification d'email.
- Les endpoints Actuator sensibles (`/actuator/info`, `/actuator/metrics`, `/actuator/env`, `/actuator/beans`) sont restreints au rôle `ROLE_ADMIN` via Spring Security.

## Auteur

[BelkaG27]
