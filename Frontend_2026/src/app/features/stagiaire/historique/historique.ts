import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { finalize } from 'rxjs';
import { DemandeService, DemandeStageResponse } from '../../../core/services/demande.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-historique',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './historique.html',
  styleUrl: './historique.scss',
})
export class Historique implements OnInit {
  demandes: DemandeStageResponse[] = [];
  chargement = true;

  constructor(
    private demandeService: DemandeService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef, // ✅ AJOUTÉ
  ) {}

  ngOnInit(): void {
    console.log('📜 Historique: début de chargement');
    this.demandeService
      .lister()
      .pipe(
        finalize(() => {
          // ✅ FORCER la fin du chargement
          this.chargement = false;
          this.cd.detectChanges(); // ✅ FORCER le rendu
          console.log(
            '📜 Historique: finalize -> chargement = false, demandes =',
            this.demandes.length,
          );
        }),
      )
      .subscribe({
        next: (list) => {
          console.log('📜 Historique: données reçues', list);
          this.demandes = list;
        },
        error: (err) => {
          console.error('📜 Historique: erreur', err);
          this.chargement = false;
          this.cd.detectChanges();
          this.snackBar.open('Erreur de chargement', 'Fermer', { duration: 3000 });
        },
      });
  }

  libelleEtat(etat: string): string {
    const map: Record<string, string> = {
      NOUVELLE: 'En attente',
      ACCEPTEE: 'Dossier accepté',
      REJETEE: 'Rejetée',
    };
    return map[etat] || etat;
  }

  libelleDecision(decision: string): string {
    if (decision === 'APPROUVEE') return 'Approuvé';
    if (decision === 'REJETEE') return 'Rejeté';
    return 'En attente';
  }
}
