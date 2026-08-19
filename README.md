# SGSC-Workflow

### Développement et Déploiement d'une Application Web dédiée à la Gestion des Compétences et du Workflow des Stages

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![Angular](https://img.shields.io/badge/Angular-%C3%A0%20venir-lightgrey)
![Status](https://img.shields.io/badge/status-en%20d%C3%A9veloppement-yellow)


---

## 📋 Table des matières

- [À propos](#-à-propos)
- [Fonctionnalités](#-fonctionnalités)
- [Stack technique](#-stack-technique)
- [Démarrage](#-démarrage)
- [Structure du projet](#-structure-du-projet)
- [Points d'accès principaux](#-points-daccès-principaux)
- [État d'avancement](#-état-davancement)
- [Améliorations futures](#-améliorations-futures)

---

## 🎯 À propos

Le projet répond à un double besoin :

1. **Digitaliser le workflow de gestion des stages** — du dépôt de la demande par le candidat jusqu'à la génération de l'attestation de fin de stage, en passant par la validation du dossier, l'affectation aux services, le suivi des absences et l'évaluation.
2. **Rapprocher les offres d'emploi des profils disponibles** — en comparant automatiquement les compétences exigées par une offre à celles des employés de l'entreprise, puis, en l'absence de correspondance, à celles des anciens stagiaires évalués.

---

## ✨ Fonctionnalités

### Partie Stage — complète

- Dépôt de la demande de stage avec vérification automatique du dossier (6 pièces obligatoires, dont 2 photos)
- Workflow de validation à deux niveaux : responsable de stage → directeur RH
- Création automatique du `Stage` uniquement si la décision RH est approuvée
- Affectation à un ou plusieurs services (gère nativement le stage d'observation multi-services)
- Gestion des absences et retards, avec traçabilité de qui les a enregistrés
- Évaluation par critères techniques **et** comportementaux
- Dépôt et consultation du rapport de fin de stage
- Génération automatique de l'attestation de stage en PDF (en-tête personnalisé, logo, zone de signature)
- Notifications email à chaque étape clé du workflow, découplées via un système d'événements

### Partie Compétences — complète

- Référentiel de compétences partagé entre offres, employés et stagiaires
- Extraction automatique de compétences depuis le CV (texte, via Apache PDFBox), déclenchée à la validation du stage
- Synchronisation continue entre les notes d'évaluation et le profil de compétences du stagiaire
- Algorithme de matching à priorité stricte : employés d'abord, anciens stagiaires en repli
- Vue détaillée comparant, compétence par compétence, le profil d'un candidat aux exigences d'une offre

---

## 🛠 Stack technique

| Couche | Technologies |
|---|---|
| Backend | Java 21, Spring Boot 4.1, Spring Data JPA, Spring Mail |
| Base de données | MySQL / MariaDB |
| Génération de documents | OpenPDF (attestations), Apache PDFBox (extraction de CV) |
| Frontend | Angular *(à venir)* |
| Outils | Maven, Lombok, Git |

---

**Patterns de conception appliqués :**
- **State** (`service/Workflow`) — centralise les règles de transition du statut d'une demande de stage
- **Strategy** (`service/Matching`) — une source de données interchangeable (`CandidatDataSource`) selon qu'on évalue un employé ou un stagiaire, sans dupliquer la logique de calcul du score
- **Observer** (`event` / `listener`) — les notifications email sont totalement découplées de la logique métier via `ApplicationEventPublisher`

---

## 🚀 Démarrage

### Prérequis

- Java 21 ou supérieur
- Maven (ou le wrapper `./mvnw` fourni)
- MySQL ou MariaDB
- Un compte SMTP (Gmail avec mot de passe d'application, ou [Mailtrap](https://mailtrap.io) pour le développement) pour les notifications

### Installation

```bash
git clone https://github.com/nouhaila0204/Gestion-Competances--Stage.git
cd Gestion-Competances--Stage/Backend_2026
```

Créer la base de données :
```sql
CREATE DATABASE gestion_stages;
```

Copier le modèle de configuration et compléter tes propres identifiants :
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Compiler et lancer l'application :
```bash
./mvnw clean install
./mvnw spring-boot:run
```

L'API est alors accessible sur `http://localhost:8080/api`.

---

## 📁 Structure du projet

```
Backend_2026/
├── src/main/java/com/sbgs/backend_2026/
│   ├── controller/       # Points d'entrée REST
│   ├── service/          # Logique métier
│   │   ├── Workflow/      # Règles de transition (pattern State)
│   │   └── Matching/      # Algorithme de matching (pattern Strategy)
│   ├── repository/       # Accès aux données (Spring Data JPA)
│   ├── entity/           # Entités JPA
│   │   └── enums/
│   ├── dto/               # Objets de transfert (records)
│   ├── event/              # Événements applicatifs
│   ├── listener/           # Écouteurs d'événements (notifications)
│   └── config/              # Configuration Spring
└── pom.xml
```

---

## 🔌 Points d'accès principaux

| Ressource | Endpoints |
|---|---|
| Demandes de stage | `POST /api/demandes` · `GET /api/demandes` · `PATCH /api/demandes/{id}/dossier` · `PATCH /api/demandes/{id}/decision` |
| Documents | `POST /api/demandes/{id}/documents` · `GET /api/documents/{id}/fichier` |
| Stages | `GET /api/stages` · `POST /api/stages/{id}/rapport` · `POST /api/stages/{id}/valider` · `GET /api/stages/{id}/attestation` |
| Affectations | `POST /api/stages/{id}/affectations` |
| Absences | `POST /api/stages/{id}/absences` · `PATCH /api/stages/{id}/absences/{absenceId}` |
| Évaluations | `POST /api/stages/{id}/evaluations` · `PATCH /api/stages/{id}/evaluations/{evaluationId}` |
| Offres d'emploi | `POST /api/offres` · `GET /api/offres/{id}/matching` · `GET /api/offres/{id}/matching/{type}/{candidatId}/details` |

---

## 🗺 État d'avancement

- [x] Partie Stage — workflow complet, testé de bout en bout
- [x] Partie Compétences — extraction, synchronisation, matching
- [x] Sécurité (Spring Security + JWT)
- [x] Frontend Angular
- [x] Tests automatisés

---

## 🔮 Améliorations futures

- Extraction de compétences par NLP plutôt que par simple correspondance de mots-clés
- Dictionnaire de synonymes pour fiabiliser la détection (ex. "esprit d'équipe" ↔ "travail d'équipe")
- Interface graphique dédiée à la Partie Compétences (actuellement accessible via API uniquement)
- Historique des CV successifs par stagiaire
