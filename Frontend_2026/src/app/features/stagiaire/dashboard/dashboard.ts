import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { DemandeService, DemandeStageResponse } from '../../../core/services/demande.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-dashboard-stagiaire',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  demande: DemandeStageResponse | null = null;
  chargement = true;

  constructor(
    private demandeService: DemandeService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef, // ✅ AJOUT
  ) {}

  ngOnInit(): void {
    console.log('Dashboard: début de chargement');
    this.demandeService
      .obtenirDerniere()
      .pipe(
        finalize(() => {
          // ✅ Force la fin
          this.chargement = false;
          this.cd.detectChanges(); // ✅ Force le rendu
          console.log('Dashboard: finalize -> chargement = false');
        }),
      )
      .subscribe({
        next: (demande) => {
          console.log('Dashboard: demande reçue', demande);
          this.demande = demande;
        },
        error: (err) => {
          console.error('Dashboard: erreur', err);
          if (err.status !== 404) {
            this.snackBar.open('Erreur de chargement de la demande', 'Fermer', { duration: 3000 });
          }
          this.chargement = false;
          this.cd.detectChanges();
        },
      });
  }

  libelleEtat(etat: string): string {
    const map: Record<string, string> = {
      NOUVELLE: '⏳ En attente',
      ACCEPTEE: '✅ Dossier accepté',
      REJETEE: '❌ Demande rejetée',
    };
    return map[etat] || etat;
  }

  libelleDecision(decision: string): string {
    if (decision === 'APPROUVEE') return '✅ Stage approuvé';
    if (decision === 'REJETEE') return '❌ Demande rejetée';
    return '⏳ En attente de décision';
  }
}
