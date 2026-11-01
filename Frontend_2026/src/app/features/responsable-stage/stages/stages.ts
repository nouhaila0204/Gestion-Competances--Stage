import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { StageService, StageResponse } from '../../../core/services/stage.service';
import { ReferenceService, ServiceOption } from '../../../core/services/reference.service';
import { AbsenceService } from '../../../core/services/absence.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-stages',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './stages.html',
  styleUrl: './stages.scss',
})
export class Stages implements OnInit {
  stages: StageResponse[] = [];
  chargement = true;
  stageEnEditionId: string | null = null;
  apiUrl = environment.apiUrl;

  services: ServiceOption[] = [];
  serviceId = '';
  dateDebut = '';
  dateFin = '';

  constructor(
    private stageService: StageService,
    private referenceService: ReferenceService,
    private absenceService: AbsenceService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.referenceService.listerServices().subscribe((s) => {
      this.services = s;
      this.cd.detectChanges();
    });
    this.charger();
  }

  charger(): void {
    this.chargement = true;
    this.stageService
      .lister()
      .pipe(
        finalize(() => {
          this.chargement = false;
          this.cd.detectChanges();
        }),
      )
      .subscribe({
        next: (stages) => {
          this.stages = stages;
          this.chargerAbsencesPourStages(stages);
        },
        error: () => {
          this.snackBar.open('Erreur de chargement des stages', 'Fermer', { duration: 3000 });
        },
      });
  }

  chargerAbsencesPourStages(stages: StageResponse[]): void {
    const observables = stages.map((stage) => this.absenceService.getAbsencesByStage(stage.id));

    forkJoin(observables).subscribe({
      next: (results) => {
        stages.forEach((stage, index) => {
          const absences = results[index];
          const nombre = absences.filter((a) => !a.justifiee).length;
          (stage as any).nombreAbsences = nombre;
        });
        this.cd.detectChanges();
      },
      error: () => {
        stages.forEach((stage) => ((stage as any).nombreAbsences = 0));
        this.cd.detectChanges();
      },
    });
  }

  ouvrirAffectation(stageId: string): void {
    this.stageEnEditionId = stageId;
    this.serviceId = '';
    this.dateDebut = '';
    this.dateFin = '';
  }

  affecter(): void {
    if (!this.stageEnEditionId) return;
    this.stageService
      .affecter(this.stageEnEditionId, this.serviceId, this.dateDebut, this.dateFin)
      .subscribe({
        next: () => {
          this.snackBar.open('Affectation créée', 'Fermer', { duration: 3000 });
          this.stageEnEditionId = null;
          this.charger();
        },
        error: (err) => {
          this.snackBar.open(err.error?.erreur ?? 'Erreur', 'Fermer', { duration: 5000 });
        },
      });
  }

  valider(stageId: string): void {
    this.stageService.valider(stageId).subscribe({
      next: () => {
        this.snackBar.open('Fin de stage validée, attestation générée', 'Fermer', {
          duration: 4000,
        });
        this.charger();
      },
      error: (err) => {
        this.snackBar.open(err.error?.erreur ?? 'Erreur', 'Fermer', { duration: 5000 });
      },
    });
  }
}
