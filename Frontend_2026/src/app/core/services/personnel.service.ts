import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface PersonnelResponse {
  id: string;
  nomComplet: string;
  serviceId: string | null;
  serviceNom: string | null;
}

@Injectable({ providedIn: 'root' })
export class PersonnelService {
  constructor(private http: HttpClient) {}

  getCurrentPersonnel(): Observable<PersonnelResponse> {
    return this.http.get<PersonnelResponse>(`${environment.apiUrl}/personnel/me`);
  }
}
