import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet, RouterLink, RouterLinkActive, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { NavItem } from '../models/nav-item';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './layout.html',
  styleUrl: './layout.scss',
})
export class Layout {
  navItems: NavItem[] = [];

  constructor(
    private route: ActivatedRoute,
    public auth: AuthService,
  ) {
    this.navItems = this.route.snapshot.data['navItems'] ?? [];
  }

  initiales(): string {
    const email = this.auth.currentUser()?.email ?? '';
    return email.substring(0, 2).toUpperCase();
  }

  // ✅ Méthode pour récupérer le rôle actuel
  getCurrentRole(): string {
    const user = this.auth.currentUser();
    if (!user) return '';
    switch (user.role) {
      case 'STAGIAIRE':
        return 'stagiaire';
      case 'RESPONSABLE_STAGE':
        return 'responsable-stage';
      case 'DIRECTEUR_RH':
        return 'directeur-rh';
      case 'RESPONSABLE_SERVICE':
        return 'responsable-service';
      default:
        return '';
    }
  }

  seDeconnecter(): void {
    this.auth.logout();
  }
}
