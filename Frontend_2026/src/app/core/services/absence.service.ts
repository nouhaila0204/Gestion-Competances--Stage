import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface AbsenceResponse {
  id: string;
  stageId: string;
  date: string;
  motif: string;
  justifiee: boolean;
  retard: boolean;
  marqueParNomComplet: string;
}

@Injectable({ providedIn: 'root' })
export class AbsenceService {
  constructor(private http: HttpClient) {}

  getAbsencesByStage(stageId: string): Observable<AbsenceResponse[]> {
    return this.http.get<AbsenceResponse[]>(`${environment.apiUrl}/stages/${stageId}/absences`);
  }
  marquer(
    stageId: string,
    date: string,
    motif: string,
    justifiee: boolean,
    retard: boolean,
    marqueParId?: string,
  ): Observable<AbsenceResponse> {
    return this.http.post<AbsenceResponse>(`${environment.apiUrl}/stages/${stageId}/absences`, {
      date,
      motif,
      justifiee,
      retard,
      marqueParId,
    });
  }
}
