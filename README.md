# Gestion d’une Compagnie Aérienne

Application **Spring Boot (Java 17)** de gestion d’une compagnie aérienne (vols, avions, passagers, réservations, cargo, paiements, tarification, promotions, publicités) avec une interface **Thymeleaf** et une base **PostgreSQL**.

## Stack technique

- **Backend**: Spring Boot 3.3.5
- **Web/UI**: Spring MVC + Thymeleaf
- **Persistance**: Spring Data JPA (Hibernate)
- **Base de données**: PostgreSQL
- **Validation**: Jakarta Validation
- **Exports PDF**: OpenPDF (`com.github.librepdf:openpdf`)

## Prérequis

- **Java**: 17
- **Maven**: 3.8+
- **PostgreSQL**: 13+

## Configuration

Le fichier de configuration principal est:

- `src/main/resources/application.properties`

Paramètres importants (valeurs actuelles dans le projet):

- **Port**: `server.port=8082`
- **Base PostgreSQL**: `spring.datasource.url=jdbc:postgresql://localhost:5432/compagnie_aerienne`
- **JPA**: `spring.jpa.hibernate.ddl-auto=none` (le schéma est créé **manuellement** via SQL)

Important:

- Le mot de passe DB est actuellement présent en clair dans `application.properties`. Pour un usage réel, il est recommandé d’utiliser des variables d’environnement / un fichier non versionné.

## Base de données (scripts SQL)

Les scripts se trouvent dans le dossier `sql/`:

- `sql/complet.sql`: création/évolutions du schéma (tables principales + migrations)
- `sql/base.sql`: schéma de base (tables principales + historiques d’états)
- `sql/insert.sql`: données de démonstration (états, avions, aéroports, passagers, devises, taux)
- `sql/reset.sql`: script de remise à zéro (si applicable)

### Création de la base

1. Créer la base `compagnie_aerienne` dans PostgreSQL
2. Exécuter un des scripts de schéma:

- Option recommandée: exécuter `sql/complet.sql`
- Puis (optionnel): exécuter `sql/insert.sql`

Comme `spring.jpa.hibernate.ddl-auto=none`, l’application suppose que les tables existent déjà.

## Lancer l’application

Depuis la racine du projet:

```bash
mvn spring-boot:run
```

Puis ouvrir:

- `http://localhost:8082/` (redirige vers le dashboard)
- `http://localhost:8082/dashboard`

## Fonctionnalités principales (aperçu)

- **Dashboard**
  - KPIs (CA total, CA du jour, CA du mois, volumes)
  - Graphiques (CA par jour, répartition par devise, top vols)
- **Gestion des vols**: création / modification / suppression, changement d’état
- **Gestion des avions**: CRUD + états/historique
- **Gestion des aéroports**: CRUD
- **Passagers**: CRUD
- **Réservations**
  - Création/édition/suppression
  - Détails + attribution de sièges
  - Calcul de prix (tarification, promotions, types de passagers)
  - **Export PDF** du billet
- **Cargo**: gestion des cargos, chargements et paiements
- **Paiements**
  - Paiements réservation
  - Paiements cargo
  - **Export PDF** de reçus
- **Devises & taux de change**
- **Tarifs par classe** + promotions
- **Publicités / diffusion / paiements publicitaires** (gestion “gestionpub”)

## Principales routes (UI)

Les routes sont gérées par des contrôleurs Spring MVC (Thymeleaf). Exemples:

- `/dashboard`
- `/vols`
- `/reservations`
  - `/reservations/details/{id}`
  - `/reservations/details/{id}/pdf`
  - `/reservations/prix` (endpoint JSON pour le calcul de prix)
- `/paiements`
  - `/paiements/reservation/details/{id}/pdf`
  - `/paiements/cargo/details/{id}/pdf`

## Structure du projet

- `src/main/java/com/compagnie/controller`: contrôleurs web (routes)
- `src/main/java/com/compagnie/service`: logique métier
- `src/main/java/com/compagnie/repository`: accès base (Spring Data JPA)
- `src/main/java/com/compagnie/model`: entités JPA
- `src/main/resources/templates`: vues Thymeleaf
- `src/main/resources/static`: assets (CSS/JS/images)
- `sql/`: scripts PostgreSQL (schéma, migrations, seed)

## Notes

- Le projet inclut un dossier `target/` (artifacts Maven). En général, ce dossier est généré et n’a pas besoin d’être versionné.

## Dépannage

- **Erreur connexion DB**: vérifier que PostgreSQL tourne, que la DB `compagnie_aerienne` existe, et que les identifiants dans `application.properties` sont corrects.
- **Tables manquantes**: exécuter `sql/complet.sql` (puis `sql/insert.sql`).
- **Port déjà utilisé**: changer `server.port` dans `application.properties`.
