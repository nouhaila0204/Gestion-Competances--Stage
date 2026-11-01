import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthService, Role } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  // ✅ Variables déclarées
  email = '';
  password = '';
  chargement = false;

  constructor(
    private auth: AuthService,
    private router: Router,
    private snackBar: MatSnackBar,
  ) {}

  // ✅ Méthode de connexion
  seConnecter(): void {
    this.chargement = true;
    this.auth.login(this.email, this.password).subscribe({
      next: (user) => {
        this.chargement = false;
        this.router.navigate([this.rediriger(user.role)]);
      },
      error: () => {
        this.chargement = false;
        this.snackBar.open('Email ou mot de passe incorrect', 'Fermer', { duration: 4000 });
      },
    });
  }

  // ✅ Méthode de redirection
  private rediriger(role: Role): string {
    switch (role) {
      case 'STAGIAIRE':
        return '/stagiaire';
      case 'RESPONSABLE_STAGE':
        return '/responsable-stage';
      case 'DIRECTEUR_RH':
        return '/directeur-rh';
      case 'RESPONSABLE_SERVICE':
        return '/responsable-service';
    }
  }
}
