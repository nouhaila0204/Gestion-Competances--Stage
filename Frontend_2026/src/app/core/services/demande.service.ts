import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface DemandeStageResponse {
  id: string;
  stagiaireNomComplet: string;
  ecoleNom: string;
  typeStage: string;
  etatDossier: string;
  decisionRh: string;
  dateSoumission: string;
  dateDebutSouhaitee: string;
  dureeSouhaitee: string;
  stageId: string | null;
}

export interface DocumentMetadata {
  id: string;
  typeDocument: string;
  nomFichier: string;
  present: boolean;
}

export interface VerifPiecesResult {
  complet: boolean;
  manquants: string[];
  details: {
    cv: boolean;
    cni: boolean;
    attestationAssurance: boolean;
    conventionStage: boolean;
    demandeManuscrite: boolean;
    photos: boolean;
    nombrePhotos: number;
  };
}

@Injectable({ providedIn: 'root' })
export class DemandeService {
  constructor(private http: HttpClient) {}

  lister(): Observable<DemandeStageResponse[]> {
    return this.http.get<DemandeStageResponse[]>(`${environment.apiUrl}/demandes`);
  }

  soumettre(formData: FormData): Observable<DemandeStageResponse> {
    return this.http.post<DemandeStageResponse>(`${environment.apiUrl}/demandes`, formData);
  }

  traiterDossier(id: string, decision: string): Observable<DemandeStageResponse> {
    return this.http.patch<DemandeStageResponse>(`${environment.apiUrl}/demandes/${id}/dossier`, {
      decision,
    });
  }

  obtenirDerniere(): Observable<DemandeStageResponse> {
    console.log('📡 Appel à /demandes/derniere');
    return this.http.get<DemandeStageResponse>(`${environment.apiUrl}/demandes/derniere`);
  }

  getDocuments(demandeId: string): Observable<DocumentMetadata[]> {
    return this.http.get<DocumentMetadata[]>(
      `${environment.apiUrl}/demandes/${demandeId}/documents`,
    );
  }

  decider(
    id: string,
    decision: string,
    responsableStageId?: string,
  ): Observable<DemandeStageResponse> {
    return this.http.patch<DemandeStageResponse>(`${environment.apiUrl}/demandes/${id}/decision`, {
      decision,
      responsableStageId,
    });
  }
  // ✅ VÉRIFICATION DES PIÈCES (100% Frontend)
  verifierPieces(
    cv: File | null,
    cni: File | null,
    attestationAssurance: File | null,
    conventionStage: File | null,
    demandeManuscrite: File | null,
    photos: File[],
  ): VerifPiecesResult {
    const details = {
      cv: cv !== null,
      cni: cni !== null,
      attestationAssurance: attestationAssurance !== null,
      conventionStage: conventionStage !== null,
      demandeManuscrite: demandeManuscrite !== null,
      photos: photos.length >= 2,
      nombrePhotos: photos.length,
    };

    const manquants: string[] = [];
    if (!details.cv) manquants.push('CV');
    if (!details.cni) manquants.push('CNI');
    if (!details.attestationAssurance) manquants.push("Attestation d'assurance");
    if (!details.conventionStage) manquants.push('Convention de stage');
    if (!details.demandeManuscrite) manquants.push('Demande manuscrite');
    if (!details.photos) {
      manquants.push(`Photos (${photos.length}/2 sélectionnées)`);
    }

    return {
      complet: manquants.length === 0,
      manquants: manquants,
      details: details,
    };
  }

  // ✅ VÉRIFICATION D'UN SEUL FICHIER
  verifierFichier(file: File | null, type: string): { valide: boolean; message: string } {
    if (!file) {
      return { valide: false, message: 'Aucun fichier sélectionné' };
    }

    // Vérifier la taille (max 10MB)
    const maxSize = 10 * 1024 * 1024; // 10 Mo
    if (file.size > maxSize) {
      return {
        valide: false,
        message: `Le fichier ${type} dépasse 10 Mo (${(file.size / (1024 * 1024)).toFixed(1)} Mo)`,
      };
    }

    // Vérifier le type (PDF pour les documents, image pour les photos)
    if (type === 'PHOTO') {
      if (!file.type.startsWith('image/')) {
        return { valide: false, message: 'Le fichier doit être une image' };
      }
    } else {
      if (file.type !== 'application/pdf' && !file.name.toLowerCase().endsWith('.pdf')) {
        return { valide: false, message: `Le fichier ${type} doit être au format PDF` };
      }
    }

    return { valide: true, message: '✅ Fichier valide' };
  }
}
