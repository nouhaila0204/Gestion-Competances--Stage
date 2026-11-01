import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { StageService, StageResponse } from '../../../core/services/stage.service';
import { EvaluationService } from '../../../core/services/evaluation.service';
import { PersonnelService } from '../../../core/services/personnel.service';
import { AuthService } from '../../../core/services/auth.service';

interface StagiaireDashboard extends StageResponse {
  hasRapport: boolean;
  hasEvaluation: boolean;
}

@Component({
  selector: 'app-dashboard-service',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  stagiaires: StagiaireDashboard[] = [];
  chargement = true;
  nomResponsable = '';

  stats = {
    total: 0,
    rapportsManquants: 0,
    nonEvalues: 0,
  };

  constructor(
    private stageService: StageService,
    private evaluationService: EvaluationService,
    private personnelService: PersonnelService,
    private authService: AuthService,
    private cd: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser();
    this.nomResponsable = user?.email?.split('@')[0] || 'Responsable';

    this.personnelService.getCurrentPersonnel().subscribe({
      next: (personnel) => {
        this.chargerDonnees(personnel.serviceId);
      },
      error: () => {
        this.chargerDonnees(null);
      },
    });
  }

  chargerDonnees(serviceId: string | null): void {
    this.stageService.lister(serviceId ?? undefined).subscribe({
      next: (stages) => {
        if (stages.length === 0) {
          this.chargement = false;
          this.cd.detectChanges();
          return;
        }

        const obs = stages.map((stage) =>
          this.evaluationService.lister(stage.id).pipe(
            catchError(() => of([])),
            map((evals) => {
              const hasEval = evals.length > 0;
              const hasRapport = !!stage.rapportNomFichier;
              return {
                ...stage,
                hasEvaluation: hasEval,
                hasRapport: hasRapport,
              } as StagiaireDashboard;
            }),
          ),
        );

        forkJoin(obs).subscribe({
          next: (results) => {
            this.stagiaires = results;
            this.stats.total = results.length;
            this.stats.rapportsManquants = results.filter((r) => !r.hasRapport).length;
            this.stats.nonEvalues = results.filter((r) => !r.hasEvaluation).length;
            this.chargement = false;
            this.cd.detectChanges();
          },
          error: () => {
            this.chargement = false;
            this.cd.detectChanges();
          },
        });
      },
      error: () => {
        this.chargement = false;
        this.cd.detectChanges();
      },
    });
  }
}
