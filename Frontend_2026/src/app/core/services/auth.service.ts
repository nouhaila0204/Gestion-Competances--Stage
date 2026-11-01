import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export type Role = 'STAGIAIRE' | 'RESPONSABLE_STAGE' | 'DIRECTEUR_RH' | 'RESPONSABLE_SERVICE';

export interface LoginResponse {
  email: string;
  role: Role;
}

const USER_STORAGE = 'sbgs_user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // ✅ Initialiser à null, puis charger après
  currentUser = signal<LoginResponse | null>(null);

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {
    // ✅ Charger seulement dans le navigateur
    this.chargerUtilisateur();
  }

  private chargerUtilisateur(): void {
    // Vérifier si on est dans un navigateur
    if (typeof window !== 'undefined' && window.localStorage) {
      const saved = localStorage.getItem(USER_STORAGE);
      if (saved) {
        try {
          this.currentUser.set(JSON.parse(saved));
        } catch (e) {
          console.error('Erreur de chargement:', e);
        }
      }
    }
  }

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(
        `${environment.apiUrl}/auth/login`,
        { email, password },
        { withCredentials: true },
      )
      .pipe(
        tap((user) => {
          this.currentUser.set(user);
          if (typeof window !== 'undefined' && window.localStorage) {
            localStorage.setItem(USER_STORAGE, JSON.stringify(user));
          }
        }),
      );
  }

  logout(): void {
    this.http.post(`${environment.apiUrl}/auth/logout`, {}, { withCredentials: true }).subscribe({
      next: () => {
        this.currentUser.set(null);
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.removeItem(USER_STORAGE);
        }
        this.router.navigate(['/login']);
      },
      error: () => {
        this.currentUser.set(null);
        if (typeof window !== 'undefined' && window.localStorage) {
          localStorage.removeItem(USER_STORAGE);
        }
        this.router.navigate(['/login']);
      },
    });
  }

  isAuthenticated(): boolean {
    return this.currentUser() !== null;
  }

  hasRole(...roles: Role[]): boolean {
    const user = this.currentUser();
    return user !== null && roles.includes(user.role);
  }
}
