import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DemandeStageResponse } from './demande.service';

@Injectable({ providedIn: 'root' })
export class CandidatureService {
  constructor(private http: HttpClient) {}

  postuler(formData: FormData): Observable<DemandeStageResponse> {
    return this.http.post<DemandeStageResponse>(`${environment.apiUrl}/candidatures`, formData);
  }
}
