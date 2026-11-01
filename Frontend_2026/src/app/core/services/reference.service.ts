import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface EcoleOption {
  id: string;
  nom: string;
  ville: string;
}
export interface ServiceOption {
  id: string;
  nom: string;
}
export interface PersonnelOption {
  id: string;
  nomComplet: string;
}
export interface CompetenceOption {
  id: string;
  nom: string;
  categorie: string;
}

export interface PersonnelOption {
  id: string;
  nomComplet: string;
  serviceId?: string;
  serviceNom?: string;
  // supprimer capaciteMax
}

@Injectable({ providedIn: 'root' })
export class ReferenceService {
  constructor(private http: HttpClient) {}

  listerEcoles(): Observable<EcoleOption[]> {
    return this.http.get<EcoleOption[]>(`${environment.apiUrl}/ecoles`);
  }
  listerServices(): Observable<ServiceOption[]> {
    return this.http.get<ServiceOption[]>(`${environment.apiUrl}/services`);
  }
  listerPersonnel(role: string): Observable<PersonnelOption[]> {
    return this.http.get<PersonnelOption[]>(`${environment.apiUrl}/personnel`, {
      params: { role },
    });
  }
  listerCompetences(): Observable<CompetenceOption[]> {
    return this.http.get<CompetenceOption[]>(`${environment.apiUrl}/competences`);
  }
}
