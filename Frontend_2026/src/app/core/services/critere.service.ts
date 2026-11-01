import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CritereEvaluation {
  id: string;
  nom: string;
  competenceId?: string | null;
competenceName?: string | null;
categorie: string | null;
}

@Injectable({ providedIn: 'root' })
export class CritereService {
  constructor(private http: HttpClient) {}

  lister(): Observable<CritereEvaluation[]> {
    return this.http.get<CritereEvaluation[]>(`${environment.apiUrl}/critere-evaluations`);
  }
}
