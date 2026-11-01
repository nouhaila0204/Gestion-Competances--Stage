# SGSC-Workflow

### Développement et Déploiement d'une Application Web full-stack dédiée à la Gestion des Compétences et du Workflow des Stages

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![Angular](https://img.shields.io/badge/Angular-DD0031)
![Docker](https://img.shields.io/badge/Docker-2496ED)
![Nginx](https://img.shields.io/badge/Nginx-009639)
![Status](https://img.shields.io/badge/status-Termin%C3%A9-success)

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

- Dépôt de la demande de stage avec vérification automatique du dossier (6 pièces obligatoires, dont 2 photos, stockées en BDD `LONGBLOB`).
- Workflow de validation à deux niveaux : responsable de stage → directeur RH.
- Création automatique du `Stage` uniquement si la décision RH est approuvée.
- Affectation à un ou plusieurs services (gère nativement le stage d'observation multi-services).
- Gestion des absences et retards, avec traçabilité de qui les a enregistrés.
- Évaluation par critères techniques **et** comportementaux.
- Dépôt et consultation du rapport de fin de stage.
- Génération automatique de l'attestation de stage en PDF (en-tête personnalisé, logo, zone de signature).
- Notifications email à chaque étape clé du workflow, découplées via un système d'événements.

### Partie Compétences — complète

- Référentiel de compétences partagé entre offres, employés et stagiaires.
- Extraction automatique de compétences depuis le CV (texte, via Apache PDFBox), déclenchée à la validation du stage.
- Synchronisation continue entre les notes d'évaluation et le profil de compétences du stagiaire.
- Algorithme de matching à priorité stricte : employés d'abord, anciens stagiaires en repli.
- Vue détaillée comparant, compétence par compétence, le profil d'un candidat aux exigences d'une offre.

### Partie Frontend (Angular) — complète

- **Single Page Application (SPA)** : Interface réactive et adaptée à la charte graphique de l'entreprise (SBGS).
- **Sécurité & Routage** : Utilisation de `Guards` pour restreindre l'accès aux tableaux de bord selon le rôle de l'utilisateur (Stagiaire, RH, Responsable).
- **Intercepteur HTTP** : Injection automatique du cookie JWT (`withCredentials: true`) pour sécuriser toutes les requêtes vers l'API.
- **Formulaires réactifs** : Validation dynamique en temps réel (ex: vérification stricte des 6 pièces jointes pour postuler).
- **Interfaces dédiées** : Tableaux de bord interactifs, suivi des stages, gestion des absences, et interface visuelle de matching des compétences pour le Directeur RH.

### DevOps & Déploiement — complets

- **Sécurité robuste** : Authentification JWT (stocké dans un cookie `HttpOnly` et `SameSite=Lax`), hachage des mots de passe avec BCrypt.
- **Conteneurisation** : Application entièrement dockerisée (Backend, Frontend, MySQL) via `docker-compose`.
- **Reverse Proxy** : Configuration Nginx pour servir l'application Angular et rediriger les requêtes API vers Spring Boot.

---

## 🛠 Stack technique

| Couche | Technologies |
|---|---|
| Backend | Java 21, Spring Boot 4.1, Spring Data JPA, Spring Security, Spring Mail |
| Base de données | MySQL 8 / MariaDB (Volume persistant Docker) |
| Génération de documents | OpenPDF (attestations), Apache PDFBox (extraction de CV) |
| Frontend | Angular, TypeScript, RxJS, Reactive Forms |
| DevOps & Déploiement | Docker, Docker Compose, Nginx (Reverse Proxy) |
| Outils | Maven, Lombok, Git, IntelliJ IDEA |

---

**Patterns de conception appliqués :**
- **State** (`service/Workflow`) — centralise les règles de transition du statut d'une demande de stage.
- **Strategy** (`service/Matching`) — une source de données interchangeable (`CandidatDataSource`) selon qu'on évalue un employé ou un stagiaire, sans dupliquer la logique de calcul du score.
- **Observer** (`event` / `listener`) — les notifications email sont totalement découplées de la logique métier via `ApplicationEventPublisher`.
- **Interceptor** (Frontend) — gestion centralisée de l'injection du token JWT dans les requêtes HTTP.

---

## 🚀 Démarrage

### Option 1 : Lancement rapide via Docker (Recommandé)

Tout l'environnement (Base de données, Backend, Frontend) est pré-configuré.

```bash
# 1. Cloner le projet
git clone https://github.com/nouhaila0204/Gestion-Competances--Stage.git
cd Gestion-Competences--Stage

# 2. Lancer les conteneurs
docker compose up -d --build
