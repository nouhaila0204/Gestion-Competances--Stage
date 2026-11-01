import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

// =============================================
// INTERFACES
// =============================================

export interface EvaluationNoteRequest {
  critereId: string;
  note: number;
}

export interface EvaluationBatchRequest {
  responsableId: string;
  notes: EvaluationNoteRequest[];
}

export interface EvaluationResponse {
  id: string;
  stageId: string;
  critereId: string;
  critereNom: string;
  note: number;
  responsableNomComplet: string;
}

export interface EvaluationUpdateRequest {
  note: number;
}

// =============================================
// SERVICE
// =============================================

@Injectable({ providedIn: 'root' })
export class EvaluationService {
  constructor(private http: HttpClient) {}

  /**
   * Envoyer plusieurs évaluations pour un stage (batch)
   * POST /api/stages/{stageId}/evaluations
   */
  noter(stageId: string, payload: EvaluationBatchRequest): Observable<EvaluationResponse[]> {
    return this.http.post<EvaluationResponse[]>(
      `${environment.apiUrl}/stages/${stageId}/evaluations`,
      payload,
    );
  }

  /**
   * Récupérer toutes les évaluations d'un stage
   * GET /api/stages/{stageId}/evaluations
   */
  lister(stageId: string): Observable<EvaluationResponse[]> {
    return this.http.get<EvaluationResponse[]>(
      `${environment.apiUrl}/stages/${stageId}/evaluations`,
    );
  }

  /**
   * Modifier la note d'une évaluation existante
   * PATCH /api/stages/{stageId}/evaluations/{evaluationId}
   */
  modifierNote(
    stageId: string,
    evaluationId: string,
    payload: EvaluationUpdateRequest,
  ): Observable<EvaluationResponse> {
    return this.http.patch<EvaluationResponse>(
      `${environment.apiUrl}/stages/${stageId}/evaluations/${evaluationId}`,
      payload,
    );
  }
}
