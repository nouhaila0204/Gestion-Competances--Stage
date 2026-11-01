import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface StatsResponsableStage {
  nouvellesDemandes: number;
  demandesAcceptees: number;
  stagesSansAttestation: number;
  stagiairesNonAffectes: number;
  stagiairesParService: { [key: string]: number };
  absencesParService: { [key: string]: number };
  rapportsDeposes: number;
  rapportsNonDeposes: number;
}

@Injectable({ providedIn: 'root' })
export class StatsService {
  constructor(private http: HttpClient) {}

  getStatsResponsableStage(): Observable<StatsResponsableStage> {
    return this.http.get<StatsResponsableStage>(`${environment.apiUrl}/stats/responsable-stage`);
  }
}
