import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface StageResponse {
  id: string;
  stagiaireNomComplet: string;
  dateDebut: string;
  dateFin: string | null;
  dateGenerationAttestation: string | null;
  attestationRemise: boolean;
  rapportNomFichier: string | null;
  rapportTypeContenu: string | null;
  rapportDateDepot: string | null;
  themeStage: string | null;
  responsableStageId: string | null;
  nombreAbsences: number;
  typeStage: string; // ✅ AJOUTÉ
   }

@Injectable({ providedIn: 'root' })
export class StageService {
  constructor(private http: HttpClient) {}

  lister(serviceId?: string): Observable<StageResponse[]> {
    const params: any = {};
    if (serviceId) {
      params.serviceId = serviceId;
    }
    return this.http.get<StageResponse[]>(`${environment.apiUrl}/stages`, { params });
  }

  affecter(
    stageId: string,
    serviceId: string,
    dateDebut: string,
    dateFin: string,
  ): Observable<any> {
    return this.http.post(`${environment.apiUrl}/stages/${stageId}/affectations`, {
      serviceId,
      dateDebut,
      dateFin,
    });
  }

  deposerRapport(stageId: string, themeStage: string, fichier: File): Observable<any> {
    const formData = new FormData();
    formData.append('themeStage', themeStage);
    formData.append('fichier', fichier);
    return this.http.post(`${environment.apiUrl}/stages/${stageId}/rapport`, formData);
  }

  valider(stageId: string): Observable<StageResponse> {
    return this.http.post<StageResponse>(`${environment.apiUrl}/stages/${stageId}/valider`, {});
  }
}
