import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import {
  DemandeService,
  DemandeStageResponse,
  DocumentMetadata,
} from '../../../core/services/demande.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import {environment} from '../../../../environments/environment';

@Component({
  selector: 'app-demandes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './demandes.html',
  styleUrl: './demandes.scss',
})
export class Demandes implements OnInit {
  demandes: DemandeStageResponse[] = [];
  chargement = true;
  apiUrl = environment.apiUrl; // ✅ Ajout

  constructor(
    private demandeService: DemandeService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.chargement = true;
    this.demandeService
      .lister()
      .pipe(
        finalize(() => {
          this.chargement = false;
          this.cd.detectChanges();
        }),
      )
      .subscribe({
        next: (d) => {
          this.demandes = d;
        },
        error: (err) => {
          this.snackBar.open(err.error?.erreur ?? 'Erreur de chargement', 'Fermer', {
            duration: 5000,
          });
        },
      });
  }

  documents: DocumentMetadata[] = [];
  afficherDocuments = false;
  demandeIdSelectionnee: string | null = null;

  voirDocuments(demandeId: string): void {
    this.demandeIdSelectionnee = demandeId;
    this.demandeService.getDocuments(demandeId).subscribe({
      next: (docs) => {
        this.documents = docs;
        this.afficherDocuments = true;
      },
      error: () =>
        this.snackBar.open('Erreur de chargement des documents', 'Fermer', { duration: 3000 }),
    });
  }

  fermerDocuments(): void {
    this.afficherDocuments = false;
    this.documents = [];
    this.demandeIdSelectionnee = null;
  }

  traiter(id: string, decision: 'ACCEPTEE' | 'REJETEE'): void {
    this.demandeService.traiterDossier(id, decision).subscribe({
      next: () => {
        this.snackBar.open(
          'Dossier ' + (decision === 'ACCEPTEE' ? 'accepté' : 'rejeté'),
          'Fermer',
          { duration: 3000 },
        );
        this.charger();
      },
      error: (err) => {
        this.snackBar.open(err.error?.erreur ?? 'Erreur', 'Fermer', { duration: 5000 });
      },
    });
  }
}
