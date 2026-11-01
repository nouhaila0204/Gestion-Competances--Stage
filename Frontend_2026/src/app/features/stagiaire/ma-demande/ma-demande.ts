import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  DemandeService,
  DemandeStageResponse,
  VerifPiecesResult,
} from '../../../core/services/demande.service';
import { ReferenceService, EcoleOption } from '../../../core/services/reference.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-ma-demande',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ma-demande.html',
  styleUrl: './ma-demande.scss',
})
export class MaDemande implements OnInit {
  // ✅ Données
  demande: DemandeStageResponse | null = null;
  chargementInitial = true;
  soumission = false;

  // ✅ Formulaire
  ecoles: EcoleOption[] = [];
  ecoleId = '';
  typeStage = 'PFA';
  dateDebutSouhaitee = '';
  dureeSouhaitee = '';

  // ✅ Fichiers
  cv: File | null = null;
  cni: File | null = null;
  attestationAssurance: File | null = null;
  conventionStage: File | null = null;
  demandeManuscrite: File | null = null;
  photos: File[] = [];

  // ✅ Résultat de vérification
  verification: VerifPiecesResult | null = null;

  constructor(
    private demandeService: DemandeService,
    private referenceService: ReferenceService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    this.referenceService.listerEcoles().subscribe((e) => (this.ecoles = e));

    // ✅ Vérifier si une demande existe déjà
    this.demandeService.lister().subscribe({
      next: (demandes) => {
        this.demande = demandes.length > 0 ? demandes[0] : null;
        this.chargementInitial = false;
      },
      error: () => {
        this.chargementInitial = false;
      },
    });
  }

  // ✅ Méthode générique pour les fichiers
  onFichierUnique(
    event: Event,
    champ: 'cv' | 'cni' | 'attestationAssurance' | 'conventionStage' | 'demandeManuscrite',
  ): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.[0]) {
      const file = input.files[0];

      // ✅ Vérifier le fichier
      const typeMap: Record<string, string> = {
        cv: 'CV',
        cni: 'CNI',
        attestationAssurance: "Attestation d'assurance",
        conventionStage: 'Convention de stage',
        demandeManuscrite: 'Demande manuscrite',
      };

      const resultat = this.demandeService.verifierFichier(file, typeMap[champ]);
      if (!resultat.valide) {
        this.snackBar.open(resultat.message, 'Fermer', { duration: 5000 });
        input.value = ''; // Réinitialiser l'input
        this[champ] = null;
        return;
      }

      this[champ] = file;
      this.verifierToutesPieces(); // ✅ Vérification automatique
    }
  }

  // ✅ Méthode pour les photos
  onPhotos(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const fichiers = Array.from(input.files);

      // ✅ Vérifier chaque photo
      for (const file of fichiers) {
        const resultat = this.demandeService.verifierFichier(file, 'PHOTO');
        if (!resultat.valide) {
          this.snackBar.open(`Photo invalide : ${resultat.message}`, 'Fermer', { duration: 5000 });
          input.value = '';
          return;
        }
      }

      this.photos = fichiers;
      this.verifierToutesPieces(); // ✅ Vérification automatique
    }
  }

  // ✅ VÉRIFIER TOUTES LES PIÈCES
  verifierToutesPieces(): void {
    this.verification = this.demandeService.verifierPieces(
      this.cv,
      this.cni,
      this.attestationAssurance,
      this.conventionStage,
      this.demandeManuscrite,
      this.photos,
    );
  }

  // ✅ VÉRIFIER AVANT SOUMISSION
  verifierAvantSoumission(): boolean {
    this.verifierToutesPieces();

    if (!this.verification || !this.verification.complet) {
      const message = this.verification
        ? `Pièces manquantes : ${this.verification.manquants.join(', ')}`
        : 'Veuillez vérifier toutes les pièces';

      this.snackBar.open('❌ ' + message, 'Fermer', { duration: 5000 });
      return false;
    }
    return true;
  }

  // ✅ SOUMISSION
  soumettre(): void {
    // ✅ Vérification avant soumission
    if (!this.verifierAvantSoumission()) {
      return;
    }

    const formData = new FormData();
    formData.append('ecoleId', this.ecoleId);
    formData.append('typeStage', this.typeStage);
    formData.append('dateDebutSouhaitee', this.dateDebutSouhaitee);
    formData.append('dureeSouhaitee', this.dureeSouhaitee);
    formData.append('cv', this.cv!);
    formData.append('cni', this.cni!);
    formData.append('attestationAssurance', this.attestationAssurance!);
    formData.append('conventionStage', this.conventionStage!);
    formData.append('demandeManuscrite', this.demandeManuscrite!);
    this.photos.forEach((p) => formData.append('photos', p));

    this.soumission = true;
    this.demandeService.soumettre(formData).subscribe({
      next: (d) => {
        this.demande = d;
        this.soumission = false;
        this.snackBar.open('✅ Demande soumise avec succès !', 'Fermer', { duration: 4000 });
      },
      error: (err) => {
        this.soumission = false;
        this.snackBar.open(err.error?.erreur ?? '❌ Erreur lors de la soumission', 'Fermer', {
          duration: 6000,
        });
      },
    });
  }

  // ✅ AFFICHAGE ÉTAT
  libelleEtat(etat: string): string {
    const libelles: Record<string, string> = {
      NOUVELLE: '⏳ En attente de vérification',
      ACCEPTEE: '✅ Dossier accepté — en attente de la décision RH',
      REJETEE: '❌ Dossier rejeté',
    };
    return libelles[etat] ?? etat;
  }

  // ✅ VÉRIFIER SI TOUT EST COMPLET
  get estComplet(): boolean {
    return this.verification?.complet ?? false;
  }

  // ✅ NOMBRE DE PIÈCES MANQUANTES
  get nombreManquants(): number {
    return this.verification?.manquants.length ?? 0;
  }
}
