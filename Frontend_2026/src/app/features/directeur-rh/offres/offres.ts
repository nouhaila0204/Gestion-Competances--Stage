import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import {
  OffreService,
  OffreEmploiResponse,
  MatchingResult,
  CompetenceRequise,
} from '../../../core/services/offre.service';
import { ReferenceService, CompetenceOption } from '../../../core/services/reference.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-offres',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './offres.html',
  styleUrl: './offres.scss',
})
export class Offres implements OnInit {
  offres: OffreEmploiResponse[] = [];
  chargement = true;
  afficherFormulaire = false;

  titre = '';
  description = '';
  datePublication = '';
  statut = 'OUVERTE';
  competencesRequises: CompetenceRequise[] = [{ competenceId: '', niveauRequis: 'INTERMEDIAIRE' }];

  offreSelectionneeId: string | null = null;
  resultatMatching: MatchingResult | null = null;

  competencesDisponibles: CompetenceOption[] = [];

  constructor(
    private offreService: OffreService,
    private referenceService: ReferenceService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.referenceService.listerCompetences().subscribe((c) => {
      this.competencesDisponibles = c;
      this.cd.detectChanges();
    });
    this.charger();
  }

  charger(): void {
    this.chargement = true;
    this.offreService
      .lister()
      .pipe(
        finalize(() => {
          this.chargement = false;
          this.cd.detectChanges();
        }),
      )
      .subscribe({
        next: (o) => {
          this.offres = o;
        },
        error: () => {
          this.snackBar.open('Erreur de chargement des offres', 'Fermer', { duration: 3000 });
        },
      });
  }

  ajouterCompetence(): void {
    this.competencesRequises.push({ competenceId: '', niveauRequis: 'INTERMEDIAIRE' });
  }

  retirerCompetence(index: number): void {
    this.competencesRequises.splice(index, 1);
  }

  creer(): void {
    this.offreService
      .creer(
        this.titre,
        this.description,
        this.datePublication,
        this.statut,
        this.competencesRequises,
      )
      .subscribe({
        next: () => {
          this.snackBar.open('Offre créée', 'Fermer', { duration: 3000 });
          this.afficherFormulaire = false;
          this.titre = '';
          this.description = '';
          this.datePublication = '';
          this.competencesRequises = [{ competenceId: '', niveauRequis: 'INTERMEDIAIRE' }];
          this.charger();
        },
        error: (err) => {
          this.snackBar.open(err.error?.erreur ?? 'Erreur', 'Fermer', { duration: 5000 });
        },
      });
  }

  voirMatching(offreId: string): void {
    this.offreSelectionneeId = offreId;
    this.resultatMatching = null;
    this.offreService.matcher(offreId).subscribe({
      next: (r) => {
        this.resultatMatching = r;
        this.cd.detectChanges();
      },
      error: (err) => {
        this.snackBar.open(err.error?.erreur ?? 'Erreur', 'Fermer', { duration: 5000 });
      },
    });
  }
  // Variables pour le détail du candidat
  detailCandidatId: string | null = null;
  detailCandidatType: 'employe' | 'stagiaire' | null = null;

  // Méthode pour basculer l'affichage des détails
  toggleDetail(candidatId: string, type: 'employe' | 'stagiaire'): void {
    if (this.detailCandidatId === candidatId && this.detailCandidatType === type) {
      this.detailCandidatId = null;
      this.detailCandidatType = null;
    } else {
      this.detailCandidatId = candidatId;
      this.detailCandidatType = type;
    }
  }

  fermerDetail(): void {
    this.detailCandidatId = null;
    this.detailCandidatType = null;
  }
}
