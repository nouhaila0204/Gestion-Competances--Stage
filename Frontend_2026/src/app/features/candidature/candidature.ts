import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CandidatureService } from '../../core/services/candidature.service';
import { ReferenceService, EcoleOption } from '../../core/services/reference.service';

@Component({
  selector: 'app-candidature',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './candidature.html',
  styleUrl: './candidature.scss',
})
export class Candidature implements OnInit {
  // Informations personnelles
  nom = '';
  prenom = '';
  email = '';
  telephone = '';
  cni = '';
  motDePasse = '';

  // Informations académiques
  ecoles: EcoleOption[] = [];
  ecoleId = '';
  niveauEtude = '';
  filiere = '';

  // Informations stage
  typeStage = 'PFA';
  dateDebutSouhaitee = '';
  dureeSouhaitee = '';

  // Fichiers
  cv: File | null = null;
  cniFile: File | null = null;
  attestationAssurance: File | null = null;
  conventionStage: File | null = null;
  demandeManuscrite: File | null = null;
  photos: File[] = [];

  // États
  chargement = false;
  verification: { complet: boolean; manquants: string[] } | null = null;

  constructor(
    private candidatureService: CandidatureService,
    private referenceService: ReferenceService,
    private snackBar: MatSnackBar,
    private router: Router,
  ) {}

  // Vérification des fichiers
  onFichierUnique(
    event: Event,
    champ: 'cv' | 'cniFile' | 'attestationAssurance' | 'conventionStage' | 'demandeManuscrite',
  ): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.[0]) {
      const file = input.files[0];
      const resultat = this.verifierFichier(file, champ);
      if (!resultat.valide) {
        this.snackBar.open(resultat.message, 'Fermer', { duration: 5000 });
        input.value = '';
        this[champ] = null;
        return;
      }
      this[champ] = file;
      this.verifierToutesPieces();
    }
  }

  onPhotos(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      const fichiers = Array.from(input.files);
      for (const file of fichiers) {
        const resultat = this.verifierFichier(file, 'PHOTO');
        if (!resultat.valide) {
          this.snackBar.open(`Photo invalide : ${resultat.message}`, 'Fermer', { duration: 5000 });
          input.value = '';
          return;
        }
      }
      this.photos = fichiers;
      this.verifierToutesPieces();
    }
  }

  verifierFichier(file: File, type: string): { valide: boolean; message: string } {
    const maxSize = 10 * 1024 * 1024;
    if (file.size > maxSize) {
      return {
        valide: false,
        message: `Le fichier dépasse 10 Mo (${(file.size / (1024 * 1024)).toFixed(1)} Mo)`,
      };
    }
    if (type === 'PHOTO') {
      if (!file.type.startsWith('image/')) {
        return { valide: false, message: 'Le fichier doit être une image' };
      }
    } else {
      if (file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf')) {
        return { valide: false, message: 'Le fichier doit être au format PDF' };
      }
    }
    return { valide: true, message: '✅ Valide' };
  }

  verifierToutesPieces(): void {
    const manquants: string[] = [];
    if (!this.cv) manquants.push('CV');
    if (!this.cniFile) manquants.push('CNI');
    if (!this.attestationAssurance) manquants.push("Attestation d'assurance");
    if (!this.conventionStage) manquants.push('Convention de stage');
    if (!this.demandeManuscrite) manquants.push('Demande manuscrite');
    if (this.photos.length < 1) manquants.push(`Photos (${this.photos.length}/1)`);

    this.verification = {
      complet: manquants.length === 0,
      manquants,
    };
  }

  soumettre(): void {
    // Vérification finale
    this.verifierToutesPieces();
    if (!this.verification?.complet) {
      this.snackBar.open(
        `Pièces manquantes : ${this.verification?.manquants.join(', ')}`,
        'Fermer',
        { duration: 5000 },
      );
      return;
    }

    const formData = new FormData();
    formData.append('nom', this.nom);
    formData.append('prenom', this.prenom);
    formData.append('email', this.email);
    formData.append('telephone', this.telephone);
    formData.append('cni', this.cni);
    formData.append('motDePasse', this.motDePasse);
    formData.append('ecoleId', this.ecoleId);
    formData.append('niveauEtude', this.niveauEtude);
    formData.append('filiere', this.filiere);
    formData.append('typeStage', this.typeStage);
    formData.append('dateDebutSouhaitee', this.dateDebutSouhaitee);
    formData.append('dureeSouhaitee', this.dureeSouhaitee);
    formData.append('cv', this.cv!);
    formData.append('cniFile', this.cniFile!);
    formData.append('attestationAssurance', this.attestationAssurance!);
    formData.append('conventionStage', this.conventionStage!);
    formData.append('demandeManuscrite', this.demandeManuscrite!);
    this.photos.forEach((p) => formData.append('photos', p));

    this.chargement = true;
    this.candidatureService.postuler(formData).subscribe({
      next: () => {
        this.chargement = false;
        this.snackBar.open('✅ Candidature soumise avec succès !', 'Fermer', { duration: 6000 });
        this.router.navigate(['/confirmation']);
      },
      error: (err) => {
        this.chargement = false;
        this.snackBar.open(err.error?.erreur ?? '❌ Erreur lors de la soumission', 'Fermer', {
          duration: 6000,
        });
      },
    });
  }
  ngOnInit(): void {
    this.referenceService.listerEcoles().subscribe({
      next: (e) => {
        this.ecoles = e;
        console.log('Écoles chargées:', e);
      },
      error: (err) => {
        console.error('Erreur chargement écoles:', err);
        // ✅ Ne pas bloquer l'affichage, juste afficher un message
        this.snackBar.open('Impossible de charger la liste des écoles', 'Fermer', {
          duration: 3000,
        });
      },
    });
  }
}
