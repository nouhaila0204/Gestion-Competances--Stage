import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { StatsService, StatsResponsableStage } from '../../../core/services/stats.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-dashboard-responsable-stage',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard implements OnInit {
  stats: StatsResponsableStage | null = null;
  chargement = true;
  nomResponsable = '';

  constructor(
    private authService: AuthService,
    private statsService: StatsService,
    private snackBar: MatSnackBar,
    private cd: ChangeDetectorRef, // ✅ AJOUTÉ
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser();
    this.nomResponsable = user?.email?.split('@')[0] || 'Responsable';

    this.statsService.getStatsResponsableStage().subscribe({
      next: (data) => {
        console.log('✅ Stats reçues :', data);
        this.stats = data;
        this.chargement = false;
        this.cd.detectChanges(); // ✅ FORCER LA DÉTECTION
      },
      error: (err) => {
        console.error('❌ Erreur stats :', err);
        this.chargement = false;
        this.cd.detectChanges();
        this.snackBar.open('Erreur de chargement des statistiques', 'Fermer', { duration: 3000 });
      },
    });
  }

  getPourcentage(part: number, total: number): number {
    return total === 0 ? 0 : Math.round((part / total) * 100);
  }

  getTotalDemandes(): number {
    if (!this.stats) return 0;
    return this.stats.nouvellesDemandes + this.stats.demandesAcceptees;
  }

  getTotalRapports(): number {
    if (!this.stats) return 0;
    return this.stats.rapportsDeposes + this.stats.rapportsNonDeposes;
  }
}
