import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StageService, StageResponse } from '../../../core/services/stage.service';
import {
  EvaluationService,
  EvaluationResponse,
  EvaluationBatchRequest,
  EvaluationNoteRequest,
} from '../../../core/services/evaluation.service';
import { CritereService, CritereEvaluation } from '../../../core/services/critere.service';
import { PersonnelService } from '../../../core/services/personnel.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-evaluation',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './evaluation.html',
  styleUrls: ['./evaluation.scss'],
})
export class Evaluation implements OnInit {
  stages: StageResponse[] = [];
  selectedStageId: string | null = null;

  criteres: CritereEvaluation[] = [];
  evaluationsExistantes: EvaluationResponse[] = [];
  notes: { [critereId: string]: number | null } = {};

  chargementStages = true;
  chargementCriteres = false;
  envoiEnCours = false;

  responsableId: string | null = null;
  serviceId: string | null = null;

  constructor(
    private stageService: StageService,
    private evaluationService: EvaluationService,
    private critereService: CritereService,
    private personnelService: PersonnelService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.personnelService.getCurrentPersonnel().subscribe({
      next: (personnel) => {
        this.responsableId = personnel.id;
        this.serviceId = personnel.serviceId;
        this.chargerStages();
      },
      error: () => {
        this.snackBar.open('Impossible de récupérer votre profil', 'Fermer', { duration: 3000 });
        this.chargerStages();
      },
    });
  }

  chargerStages(): void {
    this.chargementStages = true;
    this.stageService.lister(this.serviceId ?? undefined).subscribe({
      next: (stages) => {
        this.stages = stages;
        this.chargementStages = false;
        if (this.stages.length > 0) {
          this.selectedStageId = this.stages[0].id;
          this.chargerCriteresEtEvaluations(this.selectedStageId);
        }
      },
      error: () => {
        this.chargementStages = false;
        this.snackBar.open('Erreur de chargement des stages', 'Fermer', { duration: 3000 });
      },
    });
  }

  onStageChangeModel(stageId: string | null): void {
    if (stageId) {
      this.selectedStageId = stageId;
      this.chargerCriteresEtEvaluations(stageId);
    } else {
      this.selectedStageId = null;
      this.criteres = [];
      this.notes = {};
    }
  }

  chargerCriteresEtEvaluations(stageId: string): void {
    this.chargementCriteres = true;

    this.critereService.lister().subscribe({
      next: (criteres) => {
        this.criteres = criteres;
        this.evaluationService.lister(stageId).subscribe({
          next: (evaluations) => {
            this.evaluationsExistantes = evaluations;
            this.notes = {};
            this.criteres.forEach((c) => {
              const evalExistante = evaluations.find((e) => e.critereId === c.id);
              this.notes[c.id] = evalExistante ? evalExistante.note : null;
            });
            this.chargementCriteres = false;
            this.cd.detectChanges();
          },
          error: () => {
            this.chargementCriteres = false;
            this.snackBar.open('Erreur de chargement des évaluations', 'Fermer', {
              duration: 3000,
            });
          },
        });
      },
      error: () => {
        this.chargementCriteres = false;
        this.snackBar.open('Erreur de chargement des critères', 'Fermer', { duration: 3000 });
      },
    });
  }

  soumettreEvaluations(): void {
    if (!this.selectedStageId) return;

    const notesManquantes = this.criteres.filter(
      (c) => this.notes[c.id] === null || this.notes[c.id] === undefined,
    );
    if (notesManquantes.length > 0) {
      this.snackBar.open('Veuillez saisir toutes les notes', 'Fermer', { duration: 3000 });
      return;
    }

    if (!this.responsableId) {
      this.snackBar.open('Identifiant du responsable non trouvé', 'Fermer', { duration: 3000 });
      return;
    }

    const notesPayload: EvaluationNoteRequest[] = this.criteres.map((c) => ({
      critereId: c.id,
      note: this.notes[c.id] as number,
    }));

    const payload: EvaluationBatchRequest = {
      responsableId: this.responsableId,
      notes: notesPayload,
    };

    this.envoiEnCours = true;
    this.evaluationService.noter(this.selectedStageId, payload).subscribe({
      next: () => {
        this.envoiEnCours = false;
        this.snackBar.open('✅ Évaluations enregistrées', 'Fermer', { duration: 3000 });
        this.chargerCriteresEtEvaluations(this.selectedStageId!);
      },
      error: (err) => {
        this.envoiEnCours = false;
        this.snackBar.open(err.error?.erreur || "❌ Erreur lors de l'envoi", 'Fermer', {
          duration: 5000,
        });
      },
    });
  }
}
