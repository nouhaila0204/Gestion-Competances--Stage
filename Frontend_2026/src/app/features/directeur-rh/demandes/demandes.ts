import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize, forkJoin } from 'rxjs';
import { DemandeService, DemandeStageResponse } from '../../../core/services/demande.service';
import {
  ReferenceService,
  PersonnelOption,
  ServiceOption,
} from '../../../core/services/reference.service';
import { StageService } from '../../../core/services/stage.service';
import { MatSnackBar } from '@angular/material/snack-bar';

// Interface pour une affectation en cours de saisie
interface AffectationTemp {
  serviceId: string;
  dateDebut: string;
  dateFin: string;
}

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

  demandeEnEditionId: string | null = null;
  serviceSelectionne = '';
  responsableStageId = '';
  responsableStageNom = '';

  services: ServiceOption[] = [];
  responsablesService: PersonnelOption[] = [];
  responsablesFiltres: PersonnelOption[] = [];

  // ✅ Gestion des affectations (une ou plusieurs)
  affectations: { [stageId: string]: AffectationTemp[] } = {};
  affectationsLoading = false;

  constructor(
    private demandeService: DemandeService,
    private stageService: StageService,
    private referenceService: ReferenceService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    // Charger les services
    this.referenceService.listerServices().subscribe({
      next: (s) => {
        this.services = s;
        this.cd.detectChanges();
      },
      error: () =>
        this.snackBar.open('Erreur de chargement des services', 'Fermer', { duration: 3000 }),
    });

    // Charger tous les responsables de service (RESPONSABLE_SERVICE)
    this.referenceService.listerPersonnel('RESPONSABLE_SERVICE').subscribe({
      next: (personnel) => {
        this.responsablesService = personnel;
        this.responsablesFiltres = personnel;
        this.cd.detectChanges();
        console.log('✅ Responsables de service chargés :', personnel);
      },
      error: (err) => {
        console.error('❌ Erreur :', err);
        this.snackBar.open('Erreur de chargement des responsables', 'Fermer', { duration: 3000 });
      },
    });

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
          this.demandes = d.filter(
            (x) => x.etatDossier === 'ACCEPTEE' && x.decisionRh === 'REJETEE',
          );
        },
        error: (err) => {
          this.snackBar.open(err.error?.erreur ?? 'Erreur de chargement', 'Fermer', {
            duration: 5000,
          });
        },
      });
  }

  ouvrirDecision(id: string): void {
    this.demandeEnEditionId = id;
    this.serviceSelectionne = '';
    this.responsableStageId = '';
    this.responsablesFiltres = this.responsablesService;
    // ✅ Initialiser les affectations avec une ligne vide
    this.affectations[id] = [{ serviceId: '', dateDebut: '', dateFin: '' }];
    this.cd.detectChanges();
  }

  // ✅ Filtrer les responsables par service
  chargerResponsablesParService(): void {
    if (!this.serviceSelectionne) {
      this.responsablesFiltres = this.responsablesService;
    } else {
      this.responsablesFiltres = this.responsablesService.filter(
        (r) => r.serviceId === this.serviceSelectionne,
      );
    }
    this.cd.detectChanges();
  }

  // ✅ Ajouter une affectation (pour les stages OBSERVATION)
  ajouterAffectation(stageId: string): void {
    if (!this.affectations[stageId]) {
      this.affectations[stageId] = [];
    }
    this.affectations[stageId].push({ serviceId: '', dateDebut: '', dateFin: '' });
    this.cd.detectChanges();
  }

  // ✅ Supprimer une affectation
  supprimerAffectation(stageId: string, index: number): void {
    const affs = this.affectations[stageId];
    if (affs && affs.length > 1) {
      affs.splice(index, 1);
      this.cd.detectChanges();
    }
  }

  // ✅ Approuver + Créer les affectations
  approuver(id: string): void {
    if (!this.responsableStageId) {
      this.snackBar.open('Veuillez sélectionner un responsable', 'Fermer', { duration: 3000 });
      return;
    }

    const affs = this.affectations[id];
    if (!affs || affs.length === 0) {
      this.snackBar.open('Veuillez ajouter au moins une affectation', 'Fermer', { duration: 3000 });
      return;
    }

    // Vérifier que toutes les affectations sont remplies
    const invalide = affs.some((a) => !a.serviceId || !a.dateDebut || !a.dateFin);
    if (invalide) {
      this.snackBar.open('Tous les services et dates doivent être renseignés', 'Fermer', {
        duration: 3000,
      });
      return;
    }

    this.affectationsLoading = true;

    // Étape 1 : Approuver la demande (crée le stage)
    this.demandeService.decider(id, 'APPROUVEE', this.responsableStageId).subscribe({
      next: (demandeApprouvee) => {
        const stageId = demandeApprouvee.stageId;
        if (!stageId) {
          this.snackBar.open('Erreur : stage non créé', 'Fermer', { duration: 3000 });
          this.affectationsLoading = false;
          return;
        }

        // Étape 2 : Créer toutes les affectations
        const observables = affs.map((a) =>
          this.stageService.affecter(stageId, a.serviceId, a.dateDebut, a.dateFin),
        );

        forkJoin(observables).subscribe({
          next: () => {
            this.snackBar.open('✅ Stage approuvé et affectations créées', 'Fermer', {
              duration: 3000,
            });
            this.demandeEnEditionId = null;
            delete this.affectations[id];
            this.affectationsLoading = false;
            this.charger();
          },
          error: (err) => {
            this.snackBar.open(err.error?.erreur ?? '❌ Erreur lors des affectations', 'Fermer', {
              duration: 5000,
            });
            this.affectationsLoading = false;
          },
        });
      },
      error: (err) => {
        this.snackBar.open(err.error?.erreur ?? "❌ Erreur lors de l'approbation", 'Fermer', {
          duration: 5000,
        });
        this.affectationsLoading = false;
      },
    });
  }

  rejeter(id: string): void {
    this.demandeService.decider(id, 'REJETEE').subscribe({
      next: () => {
        this.snackBar.open('❌ Demande rejetée', 'Fermer', { duration: 3000 });
        this.charger();
      },
      error: (err) => {
        this.snackBar.open(err.error?.erreur ?? 'Erreur', 'Fermer', { duration: 5000 });
      },
    });
  }
}
