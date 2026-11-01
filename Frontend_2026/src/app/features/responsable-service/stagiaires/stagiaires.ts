import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize, forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { StageService, StageResponse } from '../../../core/services/stage.service';
import { AbsenceService, AbsenceResponse } from '../../../core/services/absence.service';
import { PersonnelService } from '../../../core/services/personnel.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from '../../../../environments/environment';

interface StagiaireData {
  stageId: string;
  stagiaireNomComplet: string;
  ecoleNom?: string; // ✅ Nouveau (si disponible dans StageResponse)
  typeStage?: string; // ✅ Nouveau
  dateDebut: string;
  dateFin: string | null;
  rapportNomFichier: string | null;
  themeStage: string | null;
  absences: AbsenceResponse[];
  // Champs pour le formulaire d'absence
  absenceDate: string;
  absenceMotif: string;
  absenceJustifiee: boolean;
  absenceRetard: boolean;
  // Champs pour le rapport
  rapportFile: File | null;
}

@Component({
  selector: 'app-stagiaires',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stagiaires.html',
  styleUrl: './stagiaires.scss',
})
export class Stagiaires implements OnInit {
  stagiairesData: StagiaireData[] = [];
  chargement = true;
  apiUrl = environment.apiUrl;

  responsableId: string | null = null;
  serviceId: string | null = null;

  // Gestion de l'onglet actif par stagiaire (Absences ou Rapport)
  activeTab: { [stageId: string]: 'absences' | 'rapport' } = {};

  constructor(
    private stageService: StageService,
    private absenceService: AbsenceService,
    private personnelService: PersonnelService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.personnelService.getCurrentPersonnel().subscribe({
      next: (personnel) => {
        this.responsableId = personnel.id;
        this.serviceId = personnel.serviceId;
        this.chargerStagiaires(this.serviceId ?? undefined);
      },
      error: () => {
        this.snackBar.open('Impossible de récupérer votre profil', 'Fermer', { duration: 3000 });
        this.chargerStagiaires(undefined);
      },
    });
  }

  chargerStagiaires(serviceId?: string): void {
    this.chargement = true;
    this.stageService
      .lister(serviceId)
      .pipe(
        finalize(() => {
          this.chargement = false;
          this.cd.detectChanges();
        }),
      )
      .subscribe({
        next: (stages) => {
          this.chargerDetailsDesStages(stages);
        },
        error: () =>
          this.snackBar.open('Erreur de chargement des stages', 'Fermer', { duration: 3000 }),
      });
  }

  chargerDetailsDesStages(stages: StageResponse[]): void {
    if (stages.length === 0) {
      this.stagiairesData = [];
      this.cd.detectChanges();
      return;
    }

    const observables = stages.map((stage) => {
      return this.absenceService.getAbsencesByStage(stage.id).pipe(
        catchError(() => of([])),
        map((absences) => {
          return {
            stageId: stage.id,
            stagiaireNomComplet: stage.stagiaireNomComplet,
            ecoleNom: (stage as any).ecoleNom || 'École inconnue', // À adapter selon votre DTO backend
            typeStage: stage.typeStage || 'N/A',
            dateDebut: stage.dateDebut,
            dateFin: stage.dateFin,
            rapportNomFichier: stage.rapportNomFichier,
            themeStage: stage.themeStage,
            absences: absences,
            absenceDate: '',
            absenceMotif: '',
            absenceJustifiee: false,
            absenceRetard: false,
            rapportFile: null,
          } as StagiaireData;
        }),
      );
    });

    forkJoin(observables).subscribe({
      next: (results) => {
        this.stagiairesData = results;
        // Initialiser l'onglet par défaut sur 'absences'
        results.forEach((r) => (this.activeTab[r.stageId] = 'absences'));
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Erreur :', err);
        this.snackBar.open('Erreur de chargement des détails', 'Fermer', { duration: 3000 });
      },
    });
  }

  // ========== ACTIONS ==========

  marquerAbsence(stagiaire: StagiaireData): void {
    if (!stagiaire.absenceDate || !stagiaire.absenceMotif) {
      this.snackBar.open('Veuillez saisir la date et le motif', 'Fermer', { duration: 3000 });
      return;
    }
    if (!this.responsableId) return;

    this.absenceService
      .marquer(
        stagiaire.stageId,
        stagiaire.absenceDate,
        stagiaire.absenceMotif,
        stagiaire.absenceJustifiee,
        stagiaire.absenceRetard,
        this.responsableId,
      )
      .subscribe({
        next: () => {
          this.snackBar.open('✅ Absence marquée', 'Fermer', { duration: 3000 });
          this.chargerStagiaires(this.serviceId ?? undefined);
        },
        error: (err) =>
          this.snackBar.open(err.error?.erreur ?? 'Erreur', 'Fermer', { duration: 5000 }),
      });
  }

  deposerRapport(stagiaire: StagiaireData, file: File | null): void {
    if (!file) {
      this.snackBar.open('Veuillez sélectionner un fichier', 'Fermer', { duration: 3000 });
      return;
    }
    this.stageService
      .deposerRapport(stagiaire.stageId, stagiaire.themeStage || '', file)
      .subscribe({
        next: () => {
          this.snackBar.open('✅ Rapport déposé', 'Fermer', { duration: 3000 });
          this.chargerStagiaires(this.serviceId ?? undefined);
        },
        error: (err) =>
          this.snackBar.open(err.error?.erreur ?? 'Erreur', 'Fermer', { duration: 5000 }),
      });
  }

  onRapportFileChange(stagiaire: StagiaireData, event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      stagiaire.rapportFile = input.files[0];
    }
  }
}
