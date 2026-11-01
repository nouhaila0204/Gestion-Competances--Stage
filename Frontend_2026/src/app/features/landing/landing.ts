import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-landing',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing.html',
  styleUrl: './landing.scss',
})
export class Landing implements OnInit {
  // ✅ Vérifier si l'utilisateur est connecté
  estConnecte = false;

  // ✅ Données pour la section avantages
  benefits = [
    {
      icon: '🏢',
      title: 'Leader au Maroc',
      description: 'Rejoignez le leader des boissons gazeuses au Maroc',
    },
    {
      icon: '📚',
      title: 'Formation de qualité',
      description: "Bénéficiez d'un encadrement professionnel de haut niveau",
    },
    {
      icon: '🚀',
      title: "Perspectives d'évolution",
      description: "De nombreux stagiaires intègrent l'entreprise après leur stage",
    },
    {
      icon: '🌍',
      title: 'Culture internationale',
      description: 'Une marque mondiale avec des valeurs locales',
    },
  ];

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // ✅ Vérifier si l'utilisateur est connecté
    this.estConnecte = this.authService.isAuthenticated();
  }

  // ✅ Récupérer le lien du dashboard selon le rôle
  getDashboardLink(): string {
    const user = this.authService.currentUser();
    if (!user) return '/login';

    switch (user.role) {
      case 'STAGIAIRE':
        return '/stagiaire';
      case 'RESPONSABLE_STAGE':
        return '/responsable-stage';
      case 'DIRECTEUR_RH':
        return '/directeur-rh';
      case 'RESPONSABLE_SERVICE':
        return '/responsable-service';
      default:
        return '/login';
    }
  }

  // ✅ Récupérer le nom de l'utilisateur
  getNomUtilisateur(): string {
    const user = this.authService.currentUser();
    return user?.email?.split('@')[0] || 'Utilisateur';
  }
}
