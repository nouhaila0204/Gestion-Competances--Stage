import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CompetenceRequise {
  competenceId: string;
  niveauRequis: string;
}
export interface OffreEmploiResponse {
  id: string;
  titre: string;
  description: string;
  datePublication: string;
  statut: string;
  competencesRequisesLibelle: string[];
}
export interface CandidatMatch {
  id: string;
  nomComplet: string;
  scoreTechnique: number;
  competencesCouvertes: string[];
  competencesManquantes: string[];
  possedeCompetencesNonNotees: boolean;
}
export interface MatchingResult {
  employes: CandidatMatch[];
  stagiaires: CandidatMatch[];
}

@Injectable({ providedIn: 'root' })
export class OffreService {
  constructor(private http: HttpClient) {}

  lister(): Observable<OffreEmploiResponse[]> {
    return this.http.get<OffreEmploiResponse[]>(`${environment.apiUrl}/offres`);
  }

  creer(
    titre: string,
    description: string,
    datePublication: string,
    statut: string,
    competencesRequises: CompetenceRequise[],
  ): Observable<OffreEmploiResponse> {
    return this.http.post<OffreEmploiResponse>(`${environment.apiUrl}/offres`, {
      titre,
      description,
      datePublication,
      statut,
      competencesRequises,
    });
  }

  matcher(offreId: string): Observable<MatchingResult> {
    return this.http.get<MatchingResult>(`${environment.apiUrl}/offres/${offreId}/matching`);
  }
}
