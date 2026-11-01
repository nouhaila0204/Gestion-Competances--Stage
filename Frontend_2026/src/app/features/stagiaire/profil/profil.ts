import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-profil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profil.html',
  styleUrl: './profil.scss',
})
export class Profil implements OnInit {
  userEmail = '';
  userRole = '';

  constructor(
    private auth: AuthService,
    private snackBar: MatSnackBar,
  ) {}

  ngOnInit(): void {
    const user = this.auth.currentUser();
    if (user) {
      this.userEmail = user.email;
      this.userRole = user.role;
    }
  }

  // À terme, on pourra ajouter des champs modifiables (nom, téléphone, etc.)
  sauvegarder() {
    // Simuler une sauvegarde (à connecter avec le backend)
    this.snackBar.open('Profil mis à jour (simulation)', 'Fermer', { duration: 3000 });
  }
}
